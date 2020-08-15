package com.example.test;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create By morningsun  on 2020-08-06
 */
public class LongTimeRunningService extends Service {
    private int num;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       return  binder;
    }


    @Override
    public void onCreate() {
        Log.e("NoticesHelperService","onCreate");
        super.onCreate();
        startBindHelperService();
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LongTimeRunningService","onStartCommand");
        //开启长连接
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                num++;
                Log.e("LongTimeRunningService","任务执行"+num);
            }
        },1000,1000*1000*5);
        return  START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("LongTimeRunningService","onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("LongTimeRunningService","onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }




    @Override
    public void onDestroy() {
        LogUtils.e("LongTimeRunningService","onDestroy");
        super.onDestroy();
    }

    //必须继承binder，才能作为中间人对象返回
    private  Binder binder = new INoticeServiceAIDL.Stub(){

        @Override
        public void onFinishBind() {
            //开启长连接
            Log.e("LongTimeRunningService","onFinishBind");
        }
    };


    private INoticeHelperServiceAIDL mHelperAIDL;

    private ServiceConnection connection;

    private void startBindHelperService() {
        connection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //doing nothing
                Log.e("LongTimeRunningService","连接断开"+name);
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e("LongTimeRunningService","连接成功"+name);
                INoticeHelperServiceAIDL l = INoticeHelperServiceAIDL.Stub.asInterface(service);
                mHelperAIDL = l;
                try {
                    l.onFinishBind(1111);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        Intent intent = new Intent(getApplicationContext(), NoticesHelperService.class);
        getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.e("LongTimeRunningService","启动服务-----NoticesHelperService");

    }



}
