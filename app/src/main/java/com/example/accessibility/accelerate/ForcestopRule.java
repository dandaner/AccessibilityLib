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
 * time: 16/4/25 下午2:23
 */
public class ForcestopRule extends BaseEventRule {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "ForcestopRule";

    private AccessibilityNodeInfo mTargetNode;

    @Override
    public boolean accept(AccessibilityEvent event) {
        // 如果父元素不接受该事件，则子元素直接抛弃
        if (!super.accept(event)) {
            return false;
        }
        List<AccessibilityNodeInfo> infos = AccessibilityEventUtils.getAccessibilityNodeInfos(event,
                getTargetNodeId(), getTargetNodeText());
        if (infos == null || infos.size() == 0) {
            return false;
        }
        for (AccessibilityNodeInfo info : infos) {
            // 如果不判断info.isEnabled()，经常会导致强停按钮点击以后无法弹窗，导致后续event无法触发
            if ("android.widget.Button".equals(info.getClassName()) && info.isEnabled()) {
                mTargetNode = info;
                setApplyed();
                return true;
            }
        }
        return false;
    }

    @Override
    public void format(AccessibilityEvent event) {
        if (DEBUG) {
            LogHelper.d(TAG, "format event!");
        }
        if (mTargetNode != null) {
            mTargetNode.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }
    }

    @Override
    public String[] getTargetNodeId() {
        return new String[]{"left_button"};
    }

    @Override
    public String[] getTargetNodeText() {
        return new String[]{"强行停止"};
    }

    @Override
    public String getTargetPackageName() {
        return "com.android.settings";
    }

    @Override
    public String getTargetClassName() {
        return "com.android.settings.applications.InstalledAppDetailsTop";
    }
}
