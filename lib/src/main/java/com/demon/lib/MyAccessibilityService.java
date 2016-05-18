package com.demon.lib;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.monitor.CallStateMonitor;
import com.demon.lib.utils.LogHelper;


/**
 * author: demon.zhang
 * time: 16/4/18 下午2:40
 */
public class MyAccessibilityService extends AccessibilityService {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "MyAccessibilityService";

    private AccessibilityServiceManager mManager;
    private CallStateMonitor mCallStateMonitor;

    private int mLastWindowId = -1;

    @Override
    public void onCreate() {
        if (DEBUG) {
            LogHelper.d(TAG, "MyAccessibilityService onCreate");
        }
        super.onCreate();
        mManager = AccessibilityServiceManager.getInstance();
        if (CallStateMonitor.isPhoneDevice(this)) {
            mCallStateMonitor = new CallStateMonitor(this);
            mCallStateMonitor.startMonitor();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            LogHelper.d(TAG, "MyAccessibilityService onStartCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        if (DEBUG) {
            LogHelper.d(TAG, "MyAccessibilityService onServiceConnected");
        }
        super.onServiceConnected();
        mManager.onServiceConnected(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (DEBUG) {
            LogHelper.d(TAG, "MyAccessibilityService onAccessibilityEvent id = " + event.getWindowId());
        }
        if (dropEventIfNeeded(event)) {
            return;
        }
        mManager.onAccessibilityEvent(event);
    }

    @Override
    public void onInterrupt() {
        if (DEBUG) {
            LogHelper.d(TAG, "MyAccessibilityService onInterrupt");
        }
        mManager.onServiceDisConnected();
        if (mCallStateMonitor != null) {
            mCallStateMonitor.stopMonitor();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (DEBUG) {
            LogHelper.d(TAG, "MyAccessibilityService onUnbind");
        }
        mManager.onServiceDisConnected();
        if (mCallStateMonitor != null) {
            mCallStateMonitor.stopMonitor();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (mCallStateMonitor != null) {
            mCallStateMonitor.stopMonitor();
        }
        mManager.onServiceDisConnected();
        super.onDestroy();
    }

    /**
     * 源头设置event拦截规则，可以监听电话状态，屏幕状态，手机电量等等，主要由业务决定
     */
    private boolean dropEventIfNeeded(AccessibilityEvent event) {
        int windowId = event.getWindowId();
        // 同一个窗口事件，对我们来说是无用的
        if (windowId < 0 || windowId == mLastWindowId) {
            return true;
        }
        // 无法获取数据源，无用的数据
        if (event.getSource() == null) {
            return true;
        }
        // 通话中，停止处理event
        if (mCallStateMonitor != null && mCallStateMonitor.isPhoneActive()) {
            return true;
        }
        mLastWindowId = windowId;
        return false;
    }
}
