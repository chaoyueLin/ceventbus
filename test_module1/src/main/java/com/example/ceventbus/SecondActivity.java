package com.example.ceventbus;

import android.os.Bundle;
import android.view.View;
import com.example.ceventbus.R;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ceventbus.event.EventsDefineOfSecondEvents;

/*****************************************************************
 * * File: - SecondActivity
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/9
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/9    1.0         create
 ******************************************************************/
public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondTestBean bean=new SecondTestBean();
                bean.v="ad";
                bean.id=2;
                CEventBus.of(EventsDefineOfSecondEvents.class).test().setValue(bean);
            }
        });
    }
}
