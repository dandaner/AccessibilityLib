package com.example.accessibility.base;

import android.content.Context;
import android.os.Process;
import android.os.RemoteException;

import com.demon.lib.BuildConfig;
import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.utils.LogHelper;
import com.example.accessibility.accelerate.AccHandler;
import com.example.accessibility.accelerate.IAccessibility;
import com.example.accessibility.accelerate.IAccessibilityCallback;
import com.example.accessibility.automatic.install.AutoInstallHandler;

import java.util.List;

/**
 * author: demon.zhang
 * time: 16/5/18 下午6:52
 */
public class AccessibilityService extends IAccessibility.Stub {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "AccessibilityService";

    private Context mContext;

    public AccessibilityService(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void start(int type, List<String> params, IAccessibilityCallback callback) throws RemoteException {
        if (DEBUG) {
            LogHelper.d(TAG, "AccessibilityService start() called in Pid = " + Process.myPid() + " and Thread id = " + Thread.currentThread().getId());
        }
        if (params == null || params.size() == 0) {
            if (DEBUG) {
                throw new RuntimeException("AccessibilityService#start params is null!");
            }
            return;
        }
        AccessibilityServiceManager manager = AccessibilityServiceManager.getInstance();
        if (manager.isWorking()) {
            if (DEBUG) {
                throw new RuntimeException("AccessibilityService is working!");
            }
            return;
        }
        startInternal(type, params, callback);
    }

    private void startInternal(int type, List<String> params, IAccessibilityCallback callback) {
        BaseEventHandler handler;
        BaseEventHandlerStateListener listener = new BaseEventHandlerStateListener(mContext, params, callback);
        switch (type) {
            case AccessibilityHelper.TYPE_ACC:
                handler = new AccHandler(listener);
                break;
            case AccessibilityHelper.TYPE_AUTO_INSTALL:
                handler = new AutoInstallHandler(listener);
                break;
            default:
                if (DEBUG) {
                    throw new RuntimeException("no match handler to handle request!");
                }
                break;
        }
        AccessibilityServiceManager.getInstance().start(handler);
    }

    @Override
    public void stop() throws RemoteException {
        AccessibilityServiceManager.getInstance().stop();
    }
}
