package com.demon.lib.base;

import android.view.accessibility.AccessibilityEvent;

/**
 * 提供Event过滤,处理规则
 * author: demon.zhang
 * time: 16/4/22 下午3:21
 */
public interface IEventRule {

    /**
     * 过滤规则
     */
    boolean accept(AccessibilityEvent event);

    /**
     * 处理规则
     */
    boolean format(AccessibilityEvent event);

}
