package com.example.ceventbus;

/*****************************************************************
 * * File: - SecondEvents
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/9
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/9    1.0         create
 ******************************************************************/
@ModuleEvents(module = "common_second")
public class SecondEvents {
    @EventType(SecondTestBean.class)
    public static final String TEST = "test";

    @EventType()
    public static final String TEST2 = "test2";
}
