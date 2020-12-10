package com.example.ceventbus;

/*****************************************************************
 * * File: - DemoEvents
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/8
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/8    1.0         create
 ******************************************************************/
//可以指定module，若不指定，则使用包名作为module名
@ModuleEvents(module = "app")
public class DemoEvents {
    @EventType(Integer.class)

    public static final String EVENT1 = "event1";

    @EventType(String.class)
    public static final String EVENT = "event";
}
