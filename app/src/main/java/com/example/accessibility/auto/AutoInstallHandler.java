package com.example.accessibility.auto;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;

import com.demon.lib.base.BaseEventHandler;
import com.demon.lib.base.BaseEventRule;

import java.util.List;

/**
 * 应用自动安装处理器，在没有ROOT情况下，模拟点击安装界面安装按钮
 * author: demon.zhang
 * time: 16/4/28 下午3:09
 */
public class AutoInstallHandler extends BaseEventHandler {
    public AutoInstallHandler(Context context) {
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
