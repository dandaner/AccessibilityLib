package com.demon.lib.base;

import android.support.annotation.CallSuper;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.BuildConfig;
import com.demon.lib.utils.LogHelper;

/**
 * author: demon.zhang
 * time: 16/4/25 下午4:28
 */
public abstract class BaseEventRule implements IEventRule {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "BaseEventRule";

    /**
     * 主要是性能方面考虑：
     * 用来标示当前规则是否已经被使用过，主要用来加快过滤规则。
     * 如果不用该标记，则每次事件响应的时候，会将所有规则循环
     * 再次过滤一遍。具体该标记改变规则，可以自己设计，比如，当前
     * 规则可以使用几次，都可以在#setApplyed（）中重写
     */
    protected boolean mHasApplyed;

    /**
     * 依据自己的业务逻辑设置当前规则是否已经被使用过了
     * 一般情况下，如果规则只使用一次，直接设置mHasApplyed = true即可
     */
    public abstract void setApplyed();

    @CallSuper
    @Override
    public boolean accept(AccessibilityEvent event) {
        // 当前规则已经被使用过了，则不在使用该规则
        if (mHasApplyed) {
            if (DEBUG) {
                LogHelper.d(TAG, this.getClass().getSimpleName() + " rule has applyed, skip it!");
            }
            return false;
        }
        setApplyed();
        return true;
    }

    /**
     * 重置规则，使其可以再次被使用
     */
    public void resetRule() {
        this.mHasApplyed = false;
    }
}
