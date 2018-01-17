package com.phone.listen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by popfisher on 2017/11/6.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = PhoneStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "电话状态变更: " + action);

        String resultData = this.getResultData();
        Log.d(TAG, "onReceive->收到的数据: " + resultData);

        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
            // 去电，可以用定时挂断
            // 双卡双卡双卡的手机可能不走这个Action
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d(TAG, "onReceive->去电号码: " + phoneNumber);
        } else {
            // 来电去电都会走
            // 获取当前电话状态
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, "onReceive->电话状态: " + state);

            // 获取电话号码
            String extraIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "onReceive->来电/去电号码: " + extraIncomingNumber);

            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                Log.d(TAG, "onReceive->主动挂断电话");
                HangUpTelephonyUtil.endCall(context);
            }
        }
    }
}
