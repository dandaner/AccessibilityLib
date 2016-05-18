package com.example.accessibility.automatic.install;

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
 * time: 16/5/9 下午4:15
 */
public class InstallRule extends BaseEventRule {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "InstallRule";

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
        List<AccessibilityNodeInfo> nodeinfos = AccessibilityEventUtils
                .getAccessibilityNodeInfos(event, getTargetNodeId(), getTargetNodeText());

        if (nodeinfos == null || nodeinfos.size() == 0) {
            return;
        }
        // 直接找到“安装”按钮，就点击一次。
        if (checkInstallButtonAvailable(nodeinfos)) {
            return;
        }
        // 安装界面的下一步按钮可能需要点击多次,最多尝试点击10遍，查找不到，直接跳过
        for (int i = 0; i < 10; i++) {
            if (DEBUG) {
                LogHelper.d(TAG, "try to click install button " + i + " times!");
            }
            for (AccessibilityNodeInfo info : nodeinfos) {
                info.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
            }
            List<AccessibilityNodeInfo> installButtonList = AccessibilityEventUtils
                    .getAccessibilityNodeInfos(event, new String[]{"ok_button"}, new String[]{"安装"});
            if (checkInstallButtonAvailable(installButtonList)) {
                return;
            }
        }
    }

    @Override
    public String[] getTargetNodeId() {
        return new String[]{"ok_button"};
    }

    @Override
    public String[] getTargetNodeText() {
        return new String[]{"下一步", "安装"};
    }

    @Override
    public String getTargetPackageName() {
        return "com.android.packageinstaller";
    }

    @Override
    public String getTargetClassName() {
        return "com.android.packageinstaller.PackageInstallerActivity";
    }

    private boolean checkInstallButtonAvailable(List<AccessibilityNodeInfo> installButtonList) {
        if (installButtonList != null && installButtonList.size() > 0) {
            for (AccessibilityNodeInfo info : installButtonList) {
                if ("android.widget.Button".equals(info.getClassName())
                        && "安装".equals(info.getText())) {
                    info.performAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

}
