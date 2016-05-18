package com.demon.lib.base;

import android.support.annotation.CallSuper;
import android.view.accessibility.AccessibilityEvent;

import com.demon.lib.BuildConfig;
import com.demon.lib.utils.LogHelper;

/**
 * 事件规则的基类:
 * 需要实现重写accept()和 format()两个方法来实现规则的过滤以及执行过程。
 * 所有子类都应该在重写accept中调用一下代码：
 *
 * if (!super.accept(event)) {
 * return false;
 * }
 *
 * 重写getTargetNodeId() 和 getTargetNodeText() 来适配点击控件的资源ID和文案
 *
 * 默认情况下一条规则只是使用一次，如果想要重复使用，可以重写setApplyed()方法
 * author: demon.zhang
 * time: 16/4/25 下午4:28
 */
public abstract class BaseEventRule {

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
     * 默认情况下，规则只使用一次，如果规则需要使用多次，
     * 可以根据自己的业务，重写该方法
     */
    public void setApplyed() {
        this.mHasApplyed = true;
    }

    /**
     * 处理规则，由于AccessibilityEvent内部节点的点击执行结果并不可靠，所以返回值没有太大的参考意义
     */
    public abstract void format(AccessibilityEvent event);

    /**
     * 适配目标节点的资源ID
     */
    public abstract String[] getTargetNodeId();

    /**
     * 适配目标节点的文案
     */
    public abstract String[] getTargetNodeText();

    /**
     * 适配规则包名
     */
    public abstract String getTargetPackageName();

    /**
     * 适配规则类名
     */
    public abstract String getTargetClassName();

    /**
     * 过滤规则
     */
    @CallSuper
    public boolean accept(AccessibilityEvent event) {
        // 当前规则已经被使用过了，则不在使用该规则
        if (mHasApplyed) {
            if (DEBUG) {
                LogHelper.d(TAG, this.getClass().getSimpleName() + " rule has applyed, skip it!");
            }
            return false;
        }
        int eventType = event.getEventType();
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (DEBUG) {
                LogHelper.d(TAG, "not match window changed event type");
            }
            return false;
        }
        CharSequence packageName = event.getPackageName();
        CharSequence className = event.getClassName();
        if (DEBUG) {
            LogHelper.d(TAG, "[" + this.getClass().getSimpleName() + "] packageName = " + packageName + " className = " + className);
        }
        return getTargetPackageName().equals(packageName) && getTargetClassName().equals(className);
    }

    /**
     * 重置规则，使其可以再次被使用
     */
    public void resetRule() {
        this.mHasApplyed = false;
    }
}
