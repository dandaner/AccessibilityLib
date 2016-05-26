package com.example.accessibility.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.utils.LogHelper;
import com.example.accessibility.accelerate.IAccessibilityCallback;

import java.util.List;

/**
 * author: demon.zhang
 * time: 16/5/19 下午4:26
 */
public class BaseEventHandlerStateListener implements BaseEventHandler.IEventHandlerStateListener {

    private static final boolean DEBUG = com.demon.lib.BuildConfig.LOG_ENABLE;
    private static final String TAG = "BaseEventHandlerStateListener";

    protected Context mContext;
    private List<String> mTargets;
    private IAccessibilityCallback mCallback;
    private String mCurTarget;

    public BaseEventHandlerStateListener(@NonNull Context context, @NonNull List<String> targets, IAccessibilityCallback callback) {
        this.mContext = context.getApplicationContext();
        this.mTargets = targets;
        this.mCallback = callback;
    }

    @Override
    public void onHandlerStartWork(BaseEventHandler eventHandler) {
        if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
            try {
                mCallback.onStart();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        startTarget(eventHandler);
    }

    @Override
    public void onHandlerStopWork(BaseEventHandler eventHandler) {
        // 销毁启动界面
        Intent target = new Intent(mContext, TransparentActivity.class);
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(target);

        if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
            try {
                mCallback.onStop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAllRulesApplyed(BaseEventHandler eventHandler) {
        if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
            try {
                mCallback.after(mCurTarget);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mTargets.size() == 0) {
            AccessibilityServiceManager.getInstance().stop();
            return;
        }
        startTarget(eventHandler);
    }

    private void startTarget(BaseEventHandler handler) {
        if (!handler.isWorking()) {
            return;
        }
        if (mTargets.size() > 0) {
            mCurTarget = mTargets.remove(0);
            if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
                try {
                    mCallback.before(mCurTarget);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            Intent targetIntent = handler.getTargetIntent(mCurTarget);
            if (targetIntent != null && isActivityAvailable(targetIntent)) {
                // 所有的界面启动都是通过TransparentActivity来启动，方便我们统一处理销毁界面
                Intent intent = new Intent(mContext, TransparentActivity.class);
                intent.putExtra(TransparentActivity.EXTRA_TARGET_INTENT, targetIntent);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                if (DEBUG) {
                    LogHelper.d(TAG, "*** Error *** : " + mCurTarget + " not Available, skip it!");
                }
                if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
                    try {
                        mCallback.after(mCurTarget);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                startTarget(handler);
            }
        } else {
            // 极端情况，所有传入的参数都是非法的，导致当前handler阻塞
            AccessibilityServiceManager.getInstance().stop();
        }
    }

    private boolean isActivityAvailable(Intent intent) {
        List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }
}
