package com.example.accessibility.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 透明的Activity，专门用来启动指定界面，然后销毁界面使用
 * author: demon.zhang
 * time: 16/4/26 下午2:40
 */
public class TransparentActivity extends Activity {

    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Intent target = intent.getParcelableExtra(EXTRA_TARGET_INTENT);
        if (target != null) {
            startActivity(target);
        } else {
            finish();
        }
    }
}
