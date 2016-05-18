package com.example.accessibility.automatic.install;

import android.os.SystemClock;
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
 * time: 16/5/10 下午3:25
 */
public class InstallOkRule extends BaseEventRule {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "InstallOkRule";

    /**
     * 等待应用安装最长时间
     */
    private static final long MAX_WAIT_TIME = 60 * 1000;

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
        long elapsedRealtime = SystemClock.elapsedRealtime();
        List<AccessibilityNodeInfo> nodeInfos = null;
        int loopCount = 1;
        while ((nodeInfos == null || nodeInfos.size() == 0)
                && ((SystemClock.elapsedRealtime() - elapsedRealtime) < MAX_WAIT_TIME)) {
            nodeInfos = AccessibilityEventUtils
                    .getAccessibilityNodeInfos(event, getTargetNodeId(), getTargetNodeText());
            SystemClock.sleep(1000);
            if (DEBUG) {
                LogHelper.d(TAG, "loop " + (loopCount++) + " times!");
            }
        }
        if (nodeInfos != null && nodeInfos.size() > 0) {
            for (AccessibilityNodeInfo info : nodeInfos) {
                info.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            }
        }
    }

    @Override
    public String[] getTargetNodeId() {
        return new String[]{"done_button"};
    }

    @Override
    public String[] getTargetNodeText() {
        return new String[]{"完成"};
    }

    @Override
    public String getTargetPackageName() {
        return "com.android.packageinstaller";
    }

    @Override
    public String getTargetClassName() {
        return "com.android.packageinstaller.InstallAppProgress";
    }
}
