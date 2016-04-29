package com.example.accessibility.auto;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;

import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.base.BaseEventRule;

import java.util.List;

/**
 * 应用自动卸载处理器，在没有ROOT情况下，模拟点击应用卸载界面的卸载按钮
 * author: demon.zhang
 * time: 16/4/28 下午3:10
 */
public class AutoUninstallHandler extends BaseEventHandler {
    public AutoUninstallHandler(Context context) {
        super(context);
    }

    @Override
    public List<BaseEventRule> getRules() {
        return null;
    }

    @Override
    public AccessibilityServiceInfo getTarget() {
        return null;
    }
}
