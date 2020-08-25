package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    protected Button btn1;
    protected Button btn2;
    private ChartView chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotfix);
        initView();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(TestWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this)
                .enqueue(saveRequest);

    }

    @Override
    public void onClick(View view) {


    }

    private void initView() {
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LongTimeRunningService.class);
                startService(intent);
            }
        });
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Float> floats=new ArrayList<>();
                floats.add(1.0f);
                floats.add(1.0f);
                floats.add(2.0f);
                floats.add(2.0f);
                floats.add(3.0f);
                floats.add(3.0f);
                floats.add(4.0f);
                floats.add(4.0f);
                floats.add(1.0f);
                floats.add(1.0f);
                floats.add(2.0f);
                floats.add(2.0f);
                floats.add(3.0f);
                floats.add(3.0f);
                floats.add(4.0f);
                floats.add(4.0f);


                List<Float> floats1=new ArrayList<>();
                floats1.add(10f);
                floats1.add(20f);
                floats1.add(30f);
                floats1.add(40f);
                floats1.add(50f);
                floats1.add(60f);
                floats1.add(70f);
                floats1.add(80f);
                floats1.add(90f);
                floats1.add(100f);
                floats1.add(110f);
                floats1.add(120f);
                floats1.add(130f);
                floats1.add(140f);
                floats1.add(150f);
                floats1.add(160f);

                chartView.setData(floats1,floats);
            }
        });

        chartView=findViewById(R.id.chart);
    }

}
