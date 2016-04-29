package com.example.accessibility.acc;

import android.content.Context;
import android.os.RemoteException;

import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.utils.LogHelper;

import java.util.List;

/**
 * author: demon.zhang
 * time: 16/4/28 下午5:12
 */
public class AccManagerService extends IAccManagerService.Stub {

    private static final boolean DEBUG = com.demon.lib.BuildConfig.LOG_ENABLE;
    private static final String TAG = "AccManagerService";

    private Context mContext;
    private AccHandler mAccHandler;

    public AccManagerService(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAccHandler = new AccHandler(mContext);
    }

    @Override
    public void startAcc(List<String> targetPkgNames, IAccProgressListener listener) throws RemoteException {
        startAccInternal(targetPkgNames, listener);
    }

    @Override
    public void stopAcc() throws RemoteException {
        stopAccInternal();
    }

    private void startAccInternal(List<String> targetPkgNames, IAccProgressListener callback) {
        if (DEBUG) {
            LogHelper.d(TAG, "startAccInternal = " + targetPkgNames.toString() + " callback = " + callback);
        }
        if (targetPkgNames == null || targetPkgNames.size() == 0) {
            return;
        }
        AccEventHandlerStateListener listener = new AccEventHandlerStateListener(mContext, mAccHandler, targetPkgNames, callback);
        mAccHandler.setEventRuleProgressListener(listener);
        AccessibilityServiceManager.getInstance().registerEventHandler(mAccHandler);
    }

    private void stopAccInternal() {
        if (DEBUG) {
            LogHelper.d(TAG, "stopAccInternal");
        }
        AccessibilityServiceManager.getInstance().unregisterEventHandler(mAccHandler);
    }
}
