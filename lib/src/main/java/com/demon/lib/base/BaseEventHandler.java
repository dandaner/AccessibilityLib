package com.demon.lib.base;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Message;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.BuildConfig;
import com.demon.lib.utils.CommonHandler;
import com.demon.lib.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * AccessibilityEvent处理器的基类，提供event响应规则
 * author: demon.zhang
 * time: 16/4/22 下午1:44
 */
public abstract class BaseEventHandler implements CommonHandler.MessageHandler {
    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "BaseEventHandler";

    protected Context mContext;
    private CommonHandler mHandler;

    private EventQueue mQueue;
    private ArrayList<BaseEventRule> mRules;

    private IEventHandlerStateListener mListener;

    /**
     * 提供当前event处理器工作状态监听
     */
    public interface IEventHandlerStateListener {

        /**
         * 当前处理器开始工作，处于监听状态
         */
        void onHandlerStartWork();

        /**
         * 当前处理器停止工作，
         */
        void onHandlerStopWork();

        /**
         * 当前执行完毕的的规则，预留接口，目前没什么用
         */
        void onEventRuleProgress(BaseEventRule rule);

        /**
         * 当处理器中，所有规则都被处理之后，该接口会被回调
         */
        void onAllRulesApplyed();
    }

    public BaseEventHandler(Context context) {
        this.mContext = context.getApplicationContext();

        this.mHandler = new CommonHandler(this);
        this.mQueue = new EventQueue();

        List<BaseEventRule> rules = getRules();
        if (rules != null && rules.size() > 0) {
            this.mRules = new ArrayList<>(rules.size());
            mRules.addAll(rules);
        }
    }

    /**
     * 提供监听规则
     */
    public abstract List<BaseEventRule> getRules();

    /**
     * 提供监听目标
     */
    public abstract AccessibilityServiceInfo getTarget();

    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (mRules == null || mRules.isEmpty()) {
            return;
        }
        mQueue.enqueue(event);
        // TODO HOW TO FIX ???????
        // 所有处理都向UI线程中POST消息，会阻塞UI线程,导致UI线程卡顿
        // 但是event处理必须放在UI线程中执行，目前处于无解状态。通过查看
        // 源码发现在解析event的时候，都需要去校验enforceSealed（）状态
        // 尝试通过反射来修改该标记位，发现虽然可以修改，但是event的内部状态
        // 会被修改，无法正常解析，所以，暂时没有找到好的解决方法
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AccessibilityEvent accessibilityEvent = mQueue.dequeue();
                handleEvent(accessibilityEvent);
                accessibilityEvent.recycle();
            }
        });
    }

    @Override
    public void handleMessage(Message msg) {
    }

    public void handleEvent(final AccessibilityEvent event) {
        final int N = mRules.size();
        BaseEventRule rule;
        int loopCount = 0;
        for (int i = 0; i < N; i++) {
            loopCount = i;
            rule = mRules.get(i);
            if (rule.accept(event) && rule.format(event)) {
                if (mListener != null) {
                    mListener.onEventRuleProgress(rule);
                }
                break;
            }
        }
        // 当一个事件序列处理完毕之后(比如：一条完整的强停应用逻辑执行完毕，包括点击强停按钮和点击确认按钮)，
        // 重置所有规则，以便下一条事件序列继续执行。
        if (loopCount == N - 1) {
            if (DEBUG) {
                LogHelper.d(TAG, "a holonomic event complete, reset all rules.");
            }
            for (int i = 0; i < N; i++) {
                mRules.get(i).resetRule();
            }
            if (mListener != null) {
                mListener.onAllRulesApplyed();
            }
        }
    }

    /**
     * 重写equals的主要目的在于防止相同规则的处理器同时工作。
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseEventHandler) {
            BaseEventHandler target = (BaseEventHandler) o;
            List<BaseEventRule> myRules = getRules();
            List<BaseEventRule> targetRules = target.getRules();
            if (myRules != null && targetRules != null && (myRules.size() == targetRules.size())) {
                final int N = myRules.size();
                boolean isSameRule = true;
                for (int i = 0; i < N; i++) {
                    isSameRule = (myRules.get(i).getClass() == targetRules.get(i).getClass());
                    if (!isSameRule) {
                        break;
                    }
                }
                return isSameRule;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setEventRuleProgressListener(IEventHandlerStateListener listener) {
        this.mListener = listener;
    }

    protected void onStartWork() {
        if (DEBUG) {
            LogHelper.d(TAG, this.getClass().getSimpleName() + ": start work!");
        }
        if (mListener != null) {
            mListener.onHandlerStartWork();
        }
    }

    protected void onStopWork() {
        if (DEBUG) {
            LogHelper.d(TAG, this.getClass().getSimpleName() + ": stop work!");
        }
        if (mListener != null) {
            mListener.onHandlerStopWork();
        }
    }
}
