package com.example.accessibility.acc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;

import com.example.accessibility.base.MyServiceManger;

import java.util.List;

/**
 * 加速工具类, Public API
 * author: demon.zhang
 * time: 16/4/25 下午2:21
 */
public class AccHelper {

    public static void startAcc(Context context, List<String> targetPkgNames, IAccProgressListener listener) {
        IBinder service = MyServiceManger.getService(context, MyServiceManger.ACC_SERVICE);
        if (service != null && service.isBinderAlive()) {
            IAccManagerService iAccManagerService = IAccManagerService.Stub.asInterface(service);
            try {
                iAccManagerService.startAcc(targetPkgNames, listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void stopAcc(Context context) {
        IBinder service = MyServiceManger.getService(context, MyServiceManger.ACC_SERVICE);
        if (service != null && service.isBinderAlive()) {
            IAccManagerService iAccManagerService = IAccManagerService.Stub.asInterface(service);
            try {
                iAccManagerService.stopAcc();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startTargetDetailsActivity(Context context, String pkgName) {
        Intent target = new Intent(context, TransparentActivity.class);
        target.putExtra(TransparentActivity.EXTRA_TARGET_INTENT, getDetailsSettings(pkgName));
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(target);
    }

    private static Intent getDetailsSettings(String pkgName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", pkgName, null));
        return intent;
    }

    public static void openAccessibilityService(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}
