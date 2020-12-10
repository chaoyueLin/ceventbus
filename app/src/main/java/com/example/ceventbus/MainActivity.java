package com.example.ceventbus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ceventbus.event.EventsDefineOfDemoEvents;
import com.example.ceventbus.event.EventsDefineOfSecondEvents;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CEventBus.of(EventsDefineOfDemoEvents.class).event().observe(this, s -> {
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.button).setOnClickListener(v -> {
            CEventBus.of(EventsDefineOfDemoEvents.class).event().setValue("adfa");
        });
        findViewById(R.id.second).setOnClickListener(v -> {
            ComponentName chatActivity = new ComponentName("com.example.ceventbus", "com.example.ceventbus.SecondActivity");
            Intent intent = new Intent();
            intent.setComponent(chatActivity);
            startActivity(intent);
        });
        CEventBus.of(EventsDefineOfSecondEvents.class).test().observeForever(secondTestBean -> {
            Log.d(TAG, "second test id" + secondTestBean.id + ",v=" + secondTestBean.v);
        });
    }
}