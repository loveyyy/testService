package com.example.test;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Create By morningsun  on 2020-08-06
 */
public class NoticesHelperService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        startBind();
    }

    @Override
    public void onDestroy() {
        Log.e("NoticesHelperService","onDestroy");
        if (mInnerConnection != null) {
            unbindService(mInnerConnection);
            mInnerConnection = null;
        }
        super.onDestroy();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("NoticesHelperService","onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("NoticesHelperService","onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null) {
            mBinder = new HelperBinder();
        }
        return mBinder;
    }



    private ServiceConnection mInnerConnection;
    private void startBind() {
        mInnerConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("NoticesHelperService","连接断开"+name);
                Intent intent = new Intent(getApplicationContext(), LongTimeRunningService.class);
                startService(intent);
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e("NoticesHelperService","绑定成功"+name);
                INoticeServiceAIDL l = INoticeServiceAIDL.Stub.asInterface(service);
                try {
                    l.onFinishBind();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        Intent intent = new Intent(getApplicationContext(), LongTimeRunningService.class);
        getApplicationContext().bindService(intent, mInnerConnection, Context.BIND_AUTO_CREATE);
        Log.e("LongTimeRunningService","启动服务-----LongTimeRunningService");
    }

    private HelperBinder mBinder;

    private class HelperBinder extends INoticeHelperServiceAIDL.Stub {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onFinishBind(int notiId) {
            Log.e("NoticesHelperService","后台notiecs启动成功"+notiId);
            startForeground(notiId, new Notification());
        }
    }



}
