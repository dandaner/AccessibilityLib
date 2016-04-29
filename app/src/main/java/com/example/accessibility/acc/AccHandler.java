package com.example.accessibility.acc;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
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

    public AccHandler(Context context) {
        super(context);
    }

    @Override
    public List<BaseEventRule> getRules() {
        ForcestopRule forcestopRule = new ForcestopRule();
        ForcestopOkRule forcestopOkRule = new ForcestopOkRule();
        ArrayList<BaseEventRule> rules = new ArrayList<>(2);
        rules.add(forcestopRule);
        rules.add(forcestopOkRule);
        return rules;
    }

    @Override
    public AccessibilityServiceInfo getTarget() {
        AccessibilityServiceInfo target = new AccessibilityServiceInfo();
        target.packageNames = new String[]{"com.android.settings"};
        target.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        return target;
    }
}
