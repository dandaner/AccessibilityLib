package com.example.accessibility.acc;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.base.BaseEventRule;

import java.util.List;

/**
 * author: demon.zhang
 * time: 16/4/26 下午6:42
 */
public class AccEventHandlerStateListener implements BaseEventHandler.IEventHandlerStateListener {

    private Context mContext;
    private List<String> mTargets;
    private BaseEventHandler mHandler;
    private IAccProgressListener mListener;
    private String mCurPkgName;

    public AccEventHandlerStateListener(Context context, BaseEventHandler handler, List<String> mTargets, IAccProgressListener callback) {
        this.mContext = context.getApplicationContext();
        this.mTargets = mTargets;
        this.mHandler = handler;
        this.mListener = callback;
    }

    @Override
    public void onHandlerStartWork() {
        startTargetDetailsActivity();
    }

    @Override
    public void onHandlerStopWork() {

    }

    @Override
    public void onEventRuleProgress(BaseEventRule rule) {

    }

    @Override
    public void onAllRulesApplyed() {
        if (mListener != null && mListener.asBinder().isBinderAlive()) {
            try {
                mListener.onProgress(mCurPkgName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (mTargets != null && mTargets.size() == 0) {
            AccessibilityServiceManager.getInstance().unregisterEventHandler(mHandler);
            // 销毁应用详情界面
            Intent target = new Intent(mContext, TransparentActivity.class);
            target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(target);
            return;
        }

        startTargetDetailsActivity();
    }

    private void startTargetDetailsActivity() {
        if (mTargets != null && mTargets.size() > 0) {
            mCurPkgName = mTargets.remove(0);
            AccHelper.startTargetDetailsActivity(mContext, mCurPkgName);
        }
    }
}
