package com.example.accessibility.acc;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.demon.lib.BuildConfig;
import com.demon.lib.base.BaseEventRule;
import com.demon.lib.utils.LogHelper;

import java.util.List;

/**
 * author: demon.zhang
 * time: 16/4/25 下午2:36
 */
public class ForcestopOkRule extends BaseEventRule {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "ForcestopOkRule";

    @Override
    public void setApplyed() {
        this.mHasApplyed = true;
    }

    @Override
    public boolean accept(AccessibilityEvent event) {
        // 如果父元素不接受该事件，则子元素直接抛弃
        if (!super.accept(event)) {
            return false;
        }
        int eventType = event.getEventType();
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return false;
        }
        CharSequence packageName = event.getPackageName();
        CharSequence className = event.getClassName();
        if (DEBUG) {
            LogHelper.d(TAG, "packageName = " + packageName + " className = " + className);
        }
        return "com.android.settings".equals(packageName)
                && "android.app.AlertDialog".equals(className);
    }

    @Override
    public boolean format(AccessibilityEvent event) {
        if (DEBUG) {
            LogHelper.d(TAG, "format");
        }
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return false;
        }
        List<AccessibilityNodeInfo> infos = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            infos = source.findAccessibilityNodeInfosByViewId("button1");
        }
        if (infos == null || infos.size() == 0) {
            infos = source.findAccessibilityNodeInfosByText("确定");
        }
        if (infos == null || infos.size() == 0) {
            return false;
        }
        boolean result = false;
        for (AccessibilityNodeInfo info : infos) {
            result = info.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            if (result) {
                break;
            }
        }
        return result;
    }
}
