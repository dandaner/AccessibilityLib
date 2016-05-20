package com.example.accessibility.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.demon.lib.BuildConfig;
import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.utils.LogHelper;
import com.example.accessibility.accelerate.IAccessibility;
import com.example.accessibility.accelerate.IAccessibilityCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * author: demon.zhang
 * time: 16/5/18 下午6:19
 */
public class AccessibilityHelper {

    private static final boolean DEBUG = BuildConfig.LOG_ENABLE;
    private static final String TAG = "AccessibilityHelper";

    /**
     * 加速功能
     */
    public static final int TYPE_ACC = 1;
    /**
     * 自动安装
     */
    public static final int TYPE_AUTO_INSTALL = 2;
    /**
     * 自动卸载
     */
    public static final int TYPE_AUTO_UNINSTALL = 3;

    @IntDef({
            TYPE_ACC,
            TYPE_AUTO_INSTALL,
            TYPE_AUTO_UNINSTALL
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AccessibilityType {
    }

    /**
     * 开启辅助功能
     */
    public static void openAccessibilityService(Context context, IAccessibilityCallback callback) {
        IBinder binder = getAccessibilityService(context);
        if (binder != null && binder.isBinderAlive()) {
            IAccessibility service = IAccessibility.Stub.asInterface(binder);
            try {
                service.open(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 启动指定类型的辅助功能
     */
    public static void start(@NonNull Context context, @AccessibilityType int type,
            @NonNull List<String> params, IAccessibilityCallback callback) {
        if (DEBUG) {
            LogHelper.d(TAG, "AccessibilityHelper start() called in Pid = " + Process.myPid() + " and Thread id = " + Thread.currentThread().getId());
        }
        IBinder binder = getAccessibilityService(context);
        if (binder != null && binder.isBinderAlive()) {
            IAccessibility service = IAccessibility.Stub.asInterface(binder);
            try {
                service.start(type, params, callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止辅助功能
     */
    public static void stop(Context context) {
        IBinder binder = getAccessibilityService(context);
        if (binder != null && binder.isBinderAlive()) {
            IAccessibility service = IAccessibility.Stub.asInterface(binder);
            try {
                service.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断当前辅助服务是否可用
     */
    public static boolean isAccessibilityEnable(Context ctx) {
        return AccessibilityServiceManager.isAccessibilityEnable(ctx);
    }

    /**
     * 判断当前系统是否支持辅助服务功能
     * 尽管Android在ICE_CREAM_SANDWICH中已经提供辅助服务，但是它
     * 的功能只有在JELLY_BEAN才能满足我们的需求
     */
    public static boolean isAccessibilitySupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 这里简单通过ContentProvider实现一个远端binder service管理类。
     * 由于bundle#getBinder()方法在JELLY_BEAN_MR2之后才提供，所以只能在4.3
     * 及其以上的android 设备上生效，这里仅仅是为了尝试使用ContentProvider来提供
     * 应用内部binder管理，测试使用，没有特殊意义！！！
     * JELLY_BEAN_MR2以下，请自行封装parcel
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static IBinder getAccessibilityService(Context context) {
        Bundle bundle = context.getContentResolver().call(ServiceProvider.GET_SERVICE, "", null, null);
        if (bundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return bundle.getBinder(ServiceProvider.KEY_SERVICE);
            }
        }
        return null;
    }
}
