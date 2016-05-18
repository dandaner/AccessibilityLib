package com.example.accessibility.accelerate;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.demon.lib.BuildConfig;
import com.demon.lib.base.BaseEventRule;
import com.demon.lib.utils.AccessibilityEventUtils;
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
    public boolean accept(AccessibilityEvent event) {
        // 如果父元素不接受该事件，则子元素直接抛弃
        if (!super.accept(event)) {
            return false;
        }
        setApplyed();
        return true;
    }

    @Override
    public void format(AccessibilityEvent event) {
        if (DEBUG) {
            LogHelper.d(TAG, "format");
        }
        List<AccessibilityNodeInfo> infos = AccessibilityEventUtils
                .getAccessibilityNodeInfos(event, getTargetNodeId(), getTargetNodeText());
        if (infos == null || infos.size() == 0) {
            return;
        }
        for (AccessibilityNodeInfo info : infos) {
            info.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }
    }

    @Override
    public String[] getTargetNodeId() {
        return new String[]{"button1"};
    }

    @Override
    public String[] getTargetNodeText() {
        return new String[]{"确定"};
    }

    @Override
    public String getTargetPackageName() {
        return "com.android.settings";
    }

    @Override
    public String getTargetClassName() {
        return "android.app.AlertDialog";
    }
}
