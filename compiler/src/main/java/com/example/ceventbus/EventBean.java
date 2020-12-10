package com.example.ceventbus;

/*****************************************************************
 * * File: - EventBean
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/8
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/8    1.0         create
 ******************************************************************/
public class EventBean {
    String aClass;
    String methodName;

    public EventBean(String event, String aClass) {
        this.methodName = event;
        this.aClass = aClass;
    }
}
