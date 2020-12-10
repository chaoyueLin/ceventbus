package com.example.ceventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*****************************************************************
 * * File: - EventType
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/8
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/8    1.0         create
 ******************************************************************/
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface EventType {
    Class value() default Object.class;
}