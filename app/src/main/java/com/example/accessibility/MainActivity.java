package com.example.accessibility;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.demon.lib.base.AccessibilityServiceManager;
import com.demon.lib.utils.LogHelper;
import com.example.accessibility.acc.AccHelper;
import com.example.accessibility.acc.AccProgressListener;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final boolean DEBUG = com.demon.lib.BuildConfig.LOG_ENABLE;
    private static final String TAG = "MainActivity";

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                AccHelper.openAccessibilityService(this);
                break;
            case R.id.start:
                startAcc();
                break;
            case R.id.stop:
                AccHelper.stopAcc(this);
                break;
            default:
                break;
        }
    }

    private void startAcc() {
        if (!AccessibilityServiceManager.isAccessibilityEnable(this)) {
            Toast.makeText(this, "吊炸天服务尚未开启，请点击打开辅助功能按钮！！", Toast.LENGTH_SHORT).show();
            return;
        }
        LinkedList<String> pkgNames = new LinkedList<>();
        pkgNames.add("com.qihoo360.mobilesafe");
        pkgNames.add("com.achievo.vipshop");
        pkgNames.add("cn.j.hers");
        AccHelper.startAcc(this, pkgNames, new AccProgressListener() {
            @Override
            public void onProgress(String pkgName) throws RemoteException {
                if (DEBUG) {
                    LogHelper.d(TAG, "onProgress callback = " + pkgName);
                }
            }
        });
    }
}
