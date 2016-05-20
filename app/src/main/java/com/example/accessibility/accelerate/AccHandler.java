package com.example.accessibility.accelerate;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.base.BaseEventRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机加速功能处理器，在没有ROOT情况下，可以模拟点击应用详情界面的强行停止按钮
 * author: demon.zhang
 * time: 16/4/22 下午4:10
 */
public class AccHandler extends BaseEventHandler {

    public AccHandler(IEventHandlerStateListener listener) {
        super(listener);
    }

    @Override
    public List<BaseEventRule> getRules() {
        ArrayList<BaseEventRule> rules = new ArrayList<>(2);
        rules.add(new ForcestopRule());
        rules.add(new ForcestopOkRule());
        return rules;
    }

    @Override
    public AccessibilityServiceInfo getTarget() {
        AccessibilityServiceInfo target = new AccessibilityServiceInfo();
        target.packageNames = new String[]{"com.android.settings"};
        target.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        return target;
    }

    @Nullable
    @Override
    public Intent getTargetIntent(String target) {
        // TODO 判断intent是否可用
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", target, null));
        return intent;
    }
}
