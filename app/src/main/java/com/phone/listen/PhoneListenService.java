package com.phone.listen;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 来去电监听服务
 */

public class PhoneListenService extends Service {
    public static final String TAG = PhoneListenService.class.getSimpleName();
    public static final String ACTION_REGISTER_LISTENER = "action_register_listener";
    public static final String ACTION_STOP_LISTEN = "action_stop_listen";
    private CallType currentType = CallType.IDLE;
    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String callNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                    Log.d(TAG, "【待机】");
                    currentType = CallType.IDLE;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                    Log.d(TAG, "【响铃】来电号码：" + callNumber);
                    currentType = CallType.INCOMING_CALL;
                    Log.d(TAG, "【主动挂断电话】");
                    HangUpTelephonyUtil.endCall(PhoneListenService.this);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:    // 来电接通 或者 去电，去电接通  但是没法区分
                    if (currentType == CallType.INCOMING_CALL) {
                        Log.d(TAG, "【接通】来电号码：" + callNumber);
                    } else {
                        currentType = CallType.OUTGOING_CALL;
                        Log.d(TAG, "【呼出】去电号码：" + callNumber);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "来去电监听服务启动:");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service->onStartCommand action: " + intent.getAction() + " flags: " + flags + " startId: " + startId);
        String action = intent.getAction();
        if (null != action) {
            if (action.equals(ACTION_REGISTER_LISTENER)) {
                registerCallStateListener();
            }

            if (action.equals(ACTION_STOP_LISTEN)) {
                stopCallStateListener();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerCallStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void stopCallStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind action: " + intent.getAction());
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind action: " + intent.getAction());
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind action: " + intent.getAction());
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "来去电监听服务结束!🔚");
        super.onDestroy();
    }

    private enum CallType {
        IDLE,
        INCOMING_CALL,
        OUTGOING_CALL
    }
}
