/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.demon.lib.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.demon.lib.MyAccessibilityService;

/**
 * {@link BroadcastReceiver} for detecting incoming calls.
 */
public class CallStateMonitor extends BroadcastReceiver {
    private static final IntentFilter STATE_CHANGED_FILTER = new IntentFilter(
            TelephonyManager.ACTION_PHONE_STATE_CHANGED);

    private final MyAccessibilityService mService;
    private int mLastCallState;
    private boolean mIsStarted;

    public CallStateMonitor(MyAccessibilityService context) {
        mService = context;
        mLastCallState = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                .getCallState();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            mLastCallState = TelephonyManager.CALL_STATE_IDLE;
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            mLastCallState = TelephonyManager.CALL_STATE_OFFHOOK;
        } else if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            mLastCallState = TelephonyManager.CALL_STATE_RINGING;
        }
    }

    public void startMonitor() {
        if (!mIsStarted) {
            mService.registerReceiver(this, STATE_CHANGED_FILTER);
            mIsStarted = true;
        }
    }

    public void stopMonitor() {
        if (mIsStarted) {
            mService.unregisterReceiver(this);
            mIsStarted = false;
        }
    }

    public boolean isPhoneActive() {
        return mLastCallState != TelephonyManager.CALL_STATE_IDLE;
    }

    public static boolean isPhoneDevice(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_TELEPHONY);
    }
}
