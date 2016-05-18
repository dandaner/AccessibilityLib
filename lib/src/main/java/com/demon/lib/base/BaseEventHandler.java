package com.demon.lib.base;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.BuildConfig;
import com.demon.lib.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * AccessibilityEvent处理器的基类，提供event响应规则
 * author: demon.zhang
 * time: 16/4/22 下午1:44
 */
public abstract class BaseEventHandler {
    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "BaseEventHandler";

    private Handler mHandler;
    private HandlerThread mWorker;
    private boolean mIsWorking;

    private ArrayList<BaseEventRule> mRules;
    private EventQueue mEventQueue;
    private IEventHandlerStateListener mListener;

    /**
     * 提供当前event处理器工作状态监听
     */
    public interface IEventHandlerStateListener {

        /**
         * 当前处理器开始工作，处于监听状态
         */
        void onHandlerStartWork(BaseEventHandler eventHandler);

        /**
         * 当前处理器停止工作，
         */
        void onHandlerStopWork(BaseEventHandler eventHandler);

        /**
         * 当处理器中，所有规则都被处理之后，该接口会被回调
         */
        void onAllRulesApplyed(BaseEventHandler eventHandler);

    }

    public BaseEventHandler(IEventHandlerStateListener listener) {
        this.mListener = listener;
        List<BaseEventRule> rules = getRules();
        if (rules != null && rules.size() > 0) {
            this.mRules = new ArrayList<>(rules.size());
            this.mRules.addAll(rules);
        }
        this.mEventQueue = new EventQueue();

        mWorker = new HandlerThread("work_accessibility");
        mWorker.start();
        this.mHandler = new Handler(mWorker.getLooper());
    }

    /**
     * 提供监听规则
     */
    public abstract List<BaseEventRule> getRules();

    /**
     * 提供监听目标
     */
    public abstract AccessibilityServiceInfo getTarget();

    /**
     * 提供启动界面，由于参数的不确定性，返回值可能为Null,比如传递了一个没有安装
     * 的应用包名等
     */
    @Nullable
    public abstract Intent getTargetIntent(String target);

    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (mRules == null || mRules.isEmpty()) {
            onStopWork();
            return;
        }
        mEventQueue.enqueue(event);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    LogHelper.d(TAG, "handle event in Thread : " + Thread.currentThread().getId());
                }
                AccessibilityEvent accessibilityEvent = mEventQueue.dequeue();
                handleEvent(accessibilityEvent);
                accessibilityEvent.recycle();
            }
        });
    }

    public void handleEvent(final AccessibilityEvent event) {
        final int N = mRules.size();
        BaseEventRule rule;
        int loopCount = 0;
        for (int i = 0; i < N; i++) {
            loopCount = i;
            rule = mRules.get(i);
            if (rule.accept(event)) {
                if (DEBUG) {
                    LogHelper.d(TAG, rule.getClass().getSimpleName() + " has accept event!");
                }
                rule.format(event);
                break;
            } else {
                if (DEBUG) {
                    LogHelper.d(TAG, rule.getClass().getSimpleName() + " has refuse event!");
                }
            }
        }
        // 当一个事件序列处理完毕之后(比如：一条完整的强停应用逻辑执行完毕，包括点击强停按钮和点击确认按钮)，
        // 重置所有规则，以便下一条事件序列继续执行。
        if (loopCount == (N - 1)) {
            onAllRulesApplyed();
        }
    }

    private void onAllRulesApplyed() {
        if (DEBUG) {
            LogHelper.d(TAG, "a holonomic event complete, reset all rules.");
        }
        final int N = mRules.size();
        for (int j = 0; j < N; j++) {
            mRules.get(j).resetRule();
        }
        if (mListener != null) {
            mListener.onAllRulesApplyed(this);
        }
    }

    protected void onStartWork() {
        if (DEBUG) {
            LogHelper.d(TAG, this.getClass().getSimpleName() + ": start work!");
        }
        mIsWorking = true;

        if (mListener != null) {
            mListener.onHandlerStartWork(this);
        }
    }

    protected void onStopWork() {
        if (DEBUG) {
            LogHelper.d(TAG, this.getClass().getSimpleName() + ": stop work!");
        }
        mIsWorking = false;

        if (mWorker != null) {
            mWorker.quit();
        }

        if (mListener != null) {
            mListener.onHandlerStopWork(this);
        }
    }

    public boolean isWorking() {
        return mIsWorking;
    }
}
