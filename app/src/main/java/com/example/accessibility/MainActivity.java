package com.example.accessibility;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demon.lib.utils.LogHelper;
import com.example.accessibility.base.AccessibilityCallback;
import com.example.accessibility.base.AccessibilityHelper;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final boolean DEBUG = com.demon.lib.BuildConfig.LOG_ENABLE;
    private static final String TAG = "MainActivity";

    private WindowManager mWindowManager;
    private View mWindow;
    private TextView mTestAnimView;
    private TextView mTips;
    private Button mStop;
    private ObjectAnimator mAnimator;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    showAssitSucView();
                    applyAnim();
                    break;
                case MSG_STOP:
                    stopAnim();
                    dismissWindow();
                    break;
                case MSG_BEFORE:
                    showTips((String) (msg.obj));
                    break;
                case MSG_AFTER:
                    break;
                case MSG_CONNECT:
                    Toast.makeText(MainActivity.this, "辅助服务功能开启成功", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DISCONNECT:
                    Toast.makeText(MainActivity.this, "辅助服务功能关闭", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_BEFORE = 3;
    private static final int MSG_AFTER = 4;
    private static final int MSG_CONNECT = 5;
    private static final int MSG_DISCONNECT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRes();
    }

    private void initRes() {
        findViewById(R.id.open).setOnClickListener(this);
        findViewById(R.id.start).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.auto_install).setOnClickListener(this);
        findViewById(R.id.auto_uninstall).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                open();
                break;
            case R.id.start:
                startAcc();
                break;
            case R.id.stop:
                break;
            case R.id.auto_install:
                autoInstall();
                break;
            case R.id.auto_uninstall:
                autoUninstall();
                break;
            default:
                break;
        }
    }

    private void open() {
        AccessibilityHelper.openAccessibilityService(this, new AccessibilityCallback() {
            @Override
            public void onStart() throws RemoteException {
                mHandler.sendEmptyMessage(MSG_CONNECT);
            }

            @Override
            public void before(String result) throws RemoteException {

            }

            @Override
            public void after(String result) throws RemoteException {

            }

            @Override
            public void onStop() throws RemoteException {
                mHandler.sendEmptyMessage(MSG_DISCONNECT);
            }
        });
    }

    private void autoUninstall() {
        if (!AccessibilityHelper.isAccessibilityEnable(this)) {
            Toast.makeText(this, "吊炸天服务尚未开启，请点击打开辅助功能按钮！！", Toast.LENGTH_SHORT).show();
            return;
        }
        LinkedList<String> pkgNames = new LinkedList<>();
        // need to check intent avaiable
//        pkgNames.add("com.qihoo360.mobilesafe");
        pkgNames.add("fm.qingting.qtradio");
        pkgNames.add("yinyu.toutiiao");
        AccessibilityHelper.start(this, AccessibilityHelper.TYPE_AUTO_UNINSTALL, pkgNames, null);
    }

    private void startAcc() {
        if (!AccessibilityHelper.isAccessibilityEnable(this)) {
            Toast.makeText(this, "吊炸天服务尚未开启，请点击打开辅助功能按钮！！", Toast.LENGTH_SHORT).show();
            return;
        }
        LinkedList<String> pkgNames = new LinkedList<>();
        // need to check intent avaiable
//        pkgNames.add("com.qihoo360.mobilesafe");
        pkgNames.add("com.achievo.vipshop");
        pkgNames.add("cn.j.hers");
        AccessibilityHelper.start(this, AccessibilityHelper.TYPE_ACC, pkgNames, new MyAccessibilityCallback());
    }

    private void autoInstall() {
        if (!AccessibilityHelper.isAccessibilityEnable(this)) {
            Toast.makeText(this, "蛋碎服务尚未开启，请点击打开辅助功能按钮！！", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> paths = new ArrayList<>();
        paths.add("/sdcard/AndroidOptimizer/apkdownloader/appssearch-fm.qingting.qtradio.apk");
        paths.add("/sdcard/AndroidOptimizer/apkdownloader/appssearch-yinyu.toutiiao.apk");
        paths.add("/sdcard/AndroidOptimizer/apkdownloader/predl-com.baidu.browser.apps.pak");
        AccessibilityHelper.start(this, AccessibilityHelper.TYPE_AUTO_INSTALL,
                paths, new MyAccessibilityCallback());
    }


    private void showAssitSucView() {
        if (mWindowManager != null && mWindow != null) {
            mWindowManager.removeView(mWindow);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER;

        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        LayoutInflater inflater = LayoutInflater.from(this);
        // 获取浮动窗口视图所在布局
        mWindow = inflater.inflate(R.layout.float_window, null);
        mTestAnimView = (TextView) mWindow.findViewById(R.id.window_test);
        mTips = (TextView) mWindow.findViewById(R.id.tip);
        mStop = (Button) mWindow.findViewById(R.id.stop);
        mStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AccessibilityHelper.stop(MainActivity.this);
                if (mWindowManager != null) {
                    mWindowManager.removeView(mWindow);
                    mWindowManager = null;
                }
            }
        });
        mWindowManager.addView(mWindow, params);
    }

    private void dismissWindow() {
        if (mWindowManager != null) {
            mWindowManager.removeView(mWindow);
            mWindowManager = null;
        }
    }

    private void applyAnim() {
        if (mTestAnimView != null) {
            mAnimator = ObjectAnimator.ofFloat(mTestAnimView, "translationY", 0, 300);
            mAnimator.setDuration(1000);
            mAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            mAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            mAnimator.start();
        }
    }

    private void stopAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    private void showTips(String msg) {
        if (mTips != null) {
            mTips.setText(getString(R.string.tips, msg));
        }
    }

    private class MyAccessibilityCallback extends AccessibilityCallback {

        @Override
        public void onStart() throws RemoteException {
            if (DEBUG) {
                LogHelper.d(TAG, "Client callback service onStart");
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_START));
        }

        @Override
        public void before(String result) throws RemoteException {
            if (DEBUG) {
                LogHelper.d(TAG, "Client callback service before : " + result);
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_BEFORE, result));
        }

        @Override
        public void after(String result) throws RemoteException {
            if (DEBUG) {
                LogHelper.d(TAG, "Client callback service after : " + result);
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_AFTER));
        }

        @Override
        public void onStop() throws RemoteException {
            if (DEBUG) {
                LogHelper.d(TAG, "Client callback service onStop");
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP));
        }
    }
}
