package com.demon.lib.base;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * author: demon.zhang
 * time: 16/4/19 下午2:26
 */
public class AccessibilityServiceManager {

    /**
     * 由于系统限制，同一时刻，只能有一个处理器工作，无法处理并发情况
     */
    private BaseEventHandler mWorkHandler;
    private AccessibilityService mService;
    private static AccessibilityServiceManager sInstance;

    private AccessibilityServiceManager() {
    }

    public static AccessibilityServiceManager getInstance() {
        if (sInstance == null) {
            synchronized (AccessibilityServiceManager.class) {
                if (sInstance == null) {
                    sInstance = new AccessibilityServiceManager();
                }
            }
        }
        return sInstance;
    }

    public boolean isWorking() {
        return mWorkHandler != null;
    }

    public void start(BaseEventHandler handler) {
        if (handler == null) {
            return;
        }
        if (isWorking()) {
            return;
        }
        this.mWorkHandler = handler;
        addTarget(handler.getTarget());
        handler.onStartWork();
    }

    public void stop() {
        if (!isWorking()) {
            return;
        }
        removeTarget(mWorkHandler.getTarget());
        this.mWorkHandler.onStopWork();
        this.mWorkHandler = null;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addTarget(AccessibilityServiceInfo target) {
        if (mService != null && target != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                AccessibilityServiceInfo serviceInfo = mService.getServiceInfo();
                if (serviceInfo != null) {
                    // 目前只针对包名和eventTypes进行融合。
                    HashSet<String> set = new HashSet<>();
                    serviceInfo.eventTypes |= target.eventTypes;

                    // 将originTarget， newTarget合并并且去重
                    String[] originTarget = serviceInfo.packageNames;
                    String[] newTarget = target.packageNames;
                    if (originTarget != null && originTarget.length > 0) {
                        set.addAll(Arrays.asList(originTarget));
                    }
                    if (newTarget != null && newTarget.length > 0) {
                        set.addAll(Arrays.asList(newTarget));
                    }
                    serviceInfo.packageNames = set.toArray(new String[set.size()]);
                    mService.setServiceInfo(serviceInfo);
                }
            }
        }
    }

    /**
     * TODO
     * 之所以是空实现，是因为：
     * 1、当多个handler同时存在的时候，假如监听的是同一个应用比如，都监听Settings应用，
     * 一个是用来强停加速的，一个是用来自动安装卸载的，如果移除一个handler的target之后，
     * 另外的同样监听该应用的业务无法正常工作，这在逻辑上行不通。
     *
     * 2、测试过程中，发现AccessibilityService#setServiceInfo（）
     * 在删除的监听目标的时候不生效，比如当前监听了微信和QQ，当删除QQ的包名之后，虽然最新的
     * AccessibilityServiceInfo的打印信息中的确不包含QQ的包名了，但是QQ的event仍然会触发，
     * 目前还不清楚是不是系统BUG 还是使用不当造成的。也就是说监听只能多不能少。
     *
     * 基于以上两点，暂时没有提供移除功能的具体实现。
     */
    private void removeTarget(AccessibilityServiceInfo target) {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onServiceConnected(AccessibilityService service) {
        this.mService = service;
        // 这里传入空给系统，用来欺骗系统，如果不设置会监听所有应用的行为，主要是性能方面考量
        // 另外AccessibilityService 具有记忆功能，当关闭辅助功能，再次打开的该功能的时候，
        // 会自动记忆该应用以前所有监听过的对象，这也可能是setServiceInfo本身的BUG造成的，
        // 具体参见#removeTarget()方法注释
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            AccessibilityServiceInfo serviceInfo = service.getServiceInfo();
            serviceInfo.packageNames = new String[]{""};
            service.setServiceInfo(serviceInfo);
        }
    }

    public void onAccessibilityEvent(final AccessibilityEvent event) {
        if (mWorkHandler != null) {
            mWorkHandler.onAccessibilityEvent(event);
        }
    }

    public void onServiceDisConnected() {
    }

    /**
     * 判断当前辅助服务是否可用
     */
    public static boolean isAccessibilityEnable(Context ctx) {
        if (!isAccessibilitySupport()) {
            return false;
        }
        AccessibilityManager systemService = (AccessibilityManager) ctx.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledAccessibilityServiceList =
                systemService.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        String myPackageName = ctx.getPackageName();
        if (enabledAccessibilityServiceList != null) {
            for (AccessibilityServiceInfo info : enabledAccessibilityServiceList) {
                if (myPackageName.equals(info.getResolveInfo().serviceInfo.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断当前系统是否支持辅助服务功能
     * 尽管Android在ICE_CREAM_SANDWICH中已经提供辅助服务，但是它
     * 的功能只有在JELLY_BEAN才能满足我们的需求
     */
    public static boolean isAccessibilitySupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
