package com.example.accessibility.automatic.install;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.base.BaseEventRule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用自动安装处理器，在没有ROOT情况下，模拟点击安装界面安装按钮
 * author: demon.zhang
 * time: 16/4/28 下午3:09
 */
public class AutoInstallHandler extends BaseEventHandler {
    public AutoInstallHandler(IEventHandlerStateListener listener) {
        super(listener);
    }

    @Override
    public List<BaseEventRule> getRules() {
        ArrayList<BaseEventRule> rules = new ArrayList<>(2);
        rules.add(new InstallRule());
        rules.add(new InstallOkRule());
        return rules;
    }

    @Override
    public AccessibilityServiceInfo getTarget() {
        AccessibilityServiceInfo target = new AccessibilityServiceInfo();
        target.packageNames = new String[]{"com.android.packageinstaller"};
        target.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        return target;
    }

    @Nullable
    @Override
    public Intent getTargetIntent(String target) {
        // TODO 判断intent是否可用
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE, Uri.fromFile(new File(target)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
