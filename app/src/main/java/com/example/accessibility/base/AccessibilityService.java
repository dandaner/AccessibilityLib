package com.example.accessibility.base;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.os.RemoteException;
import android.provider.Settings;

import com.demon.lib.BuildConfig;
import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.utils.LogHelper;
import com.example.accessibility.accelerate.AccHandler;
import com.example.accessibility.accelerate.IAccessibility;
import com.example.accessibility.accelerate.IAccessibilityCallback;
import com.example.accessibility.automatic.install.AutoInstallHandler;
import com.example.accessibility.automatic.uninstall.AutoUninstallHandler;

import java.util.List;

/**
 * author: demon.zhang
 * time: 16/5/18 下午6:52
 */
public class AccessibilityService extends IAccessibility.Stub {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "AccessibilityService";

    private Context mContext;
    private MyAccessibilityServiceStateListener mAccessibilityServiceStateListener;

    private class MyAccessibilityServiceStateListener implements AccessibilityServiceManager.IAccessibilityServiceStateListener {

        private IAccessibilityCallback mCallback;

        public void setCallback(IAccessibilityCallback callback) {
            this.mCallback = callback;
        }

        @Override
        public void onServiceConnected() {
            if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
                try {
                    mCallback.onStart();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                mCallback = null;
            }
        }

        @Override
        public void onServiceDisConnected() {
            if (mCallback != null && mCallback.asBinder().isBinderAlive()) {
                try {
                    mCallback.onStop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                mCallback = null;
            }
        }
    }

    public AccessibilityService(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAccessibilityServiceStateListener = new MyAccessibilityServiceStateListener();
    }

    @Override
    public void open(IAccessibilityCallback callback) throws RemoteException {
        AccessibilityServiceManager manager = AccessibilityServiceManager.getInstance();
        manager.setStateListener(mAccessibilityServiceStateListener);
        mAccessibilityServiceStateListener.setCallback(callback);

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
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
            case AccessibilityHelper.TYPE_AUTO_UNINSTALL:
                handler = new AutoUninstallHandler(listener);
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
