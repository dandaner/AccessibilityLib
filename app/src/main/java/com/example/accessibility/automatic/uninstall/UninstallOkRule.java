package com.example.accessibility.automatic.uninstall;

import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.BuildConfig;
import com.demon.lib.base.BaseEventRule;
import com.demon.lib.utils.LogHelper;

/**
 * author: demon.zhang
 * time: 16/5/20 下午5:35
 */
public class UninstallOkRule extends BaseEventRule {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "UninstallOkRule";

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
    }

    @Override
    public String[] getTargetNodeId() {
        return new String[0];
    }

    @Override
    public String[] getTargetNodeText() {
        return new String[0];
    }

    @Override
    public String getTargetPackageName() {
        return "com.android.packageinstaller";
    }

    @Override
    public String getTargetClassName() {
        return "com.android.packageinstaller.UninstallAppProgress";
    }
}
