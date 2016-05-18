package com.example.accessibility.automatic.uninstall;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.base.BaseEventRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用自动卸载处理器，在没有ROOT情况下，模拟点击应用卸载界面的卸载按钮
 * author: demon.zhang
 * time: 16/4/28 下午3:10
 */
public class AutoUninstallHandler extends BaseEventHandler {
    public AutoUninstallHandler(IEventHandlerStateListener listener) {
        super(listener);
    }

    @Override
    public List<BaseEventRule> getRules() {
        ArrayList<BaseEventRule> rules = new ArrayList<>();
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
        return null;
    }
}
