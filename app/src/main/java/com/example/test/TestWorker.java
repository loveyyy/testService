package com.example.test;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Create By morningsun  on 2020-07-23
 */
public class TestWorker extends Worker {
    private Context context;

    public TestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("tag","任务进行中");
        if(isRunService(getApplicationContext(),"LongTimeRunningService")){
            Intent intent = new Intent(getApplicationContext(), LongTimeRunningService.class);
            context.startService(intent);
        }
        return Result.success();
    }

    /**
     * 判断服务是否在运行
     * @param context
     * @param serviceName
     * @return
     * 服务名称为全路径 例如com.ghost.WidgetUpdateService
     */
    public boolean isRunService(Context context,String serviceName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
