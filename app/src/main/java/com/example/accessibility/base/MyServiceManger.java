package com.example.accessibility.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 这里简单通过ContentProvider实现一个远端binder service管理类。
 * 由于bundle#getBinder()方法在JELLY_BEAN_MR2之后才提供，所以只能在4.3
 * 及其以上的android 设备上生效，这里仅仅是为了尝试使用ContentProvider来提供
 * 应用内部binder管理，测试使用，没有特殊意义！！！
 *
 * author: demon.zhang
 * time: 16/4/28 下午7:04
 */
public class MyServiceManger {

    public static final String ACC_SERVICE = "acc";
    public static final String AUTO_INSTALL_SERVICE = "auto_install";
    public static final String AUTO_UNINSTALL_SERVICE = "acauto_uninstallc";

    @StringDef({
            ACC_SERVICE,
            AUTO_INSTALL_SERVICE,
            AUTO_UNINSTALL_SERVICE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceName {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static IBinder getService(Context context, @ServiceName @NonNull String serviceName) {
        Bundle bundle = context.getContentResolver().call(ServiceProvider.GET_SERVICE, "", serviceName, null);
        if (bundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return bundle.getBinder(ServiceProvider.KEY_SERVICE);
            }
        }
        return null;
    }
}
