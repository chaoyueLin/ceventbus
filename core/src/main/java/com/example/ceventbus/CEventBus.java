package com.example.ceventbus;

import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;

/*****************************************************************
 * * File: - FacadeProxy
 * * Description: 
 * * Version: 1.0
 * * Date : 2020/12/8
 * * Author: linchaoyue
 * *
 * * ---------------------- Revision History:----------------------
 * * <author>   <date>     <version>     <desc>
 * * linchaoyue 2020/12/8    1.0         create
 ******************************************************************/

public class CEventBus implements InvocationHandler {
    private static final String TAG = "FacadeProxy";

    private static HashMap<String, HashMap<String, BusMutableLiveData>> map = new HashMap();


    private static void put(String name, String method, BusMutableLiveData data) {
        if (map.get(name) == null) {
            map.put(name, new HashMap<String, BusMutableLiveData>());
        }
        map.get(name).put(method, data);
    }

    private static MutableLiveData get(String module, String method) {
        if (map.get(module) == null) {
            return null;
        }
        return map.get(module).get(method);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Log.d(TAG, "method toGenericString:" + method.toGenericString());
        Log.d(TAG, "method name:" + method.getName());
        String classN = method.getDeclaringClass().getName();
        Log.d(TAG, "class name:" + classN);
        String module = null;
        try {
            Class c = Class.forName(classN);
            module = (String) c.getField(Cons.MODULE).get(null);
            Log.d(TAG, "module=" + module);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (get(module, method.getName()) == null) {
            Type type = method.getReturnType();
            //type.toString 前面会有class
            Log.d(TAG, "type name:" +  type.toString());

            Log.d(TAG, "type name:" + type.getTypeName());
            BusMutableLiveData mutableLiveData = (BusMutableLiveData) Class.forName(type.getTypeName()).newInstance();
            Type returnType = method.getGenericReturnType();
            Log.d(TAG, "returnType name:" + returnType.getTypeName());
            if (returnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) returnType;
                for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
                    Log.d(TAG, "parameter=" + actualTypeArgument.getTypeName());
                }
            }
            put(module, method.getName(), mutableLiveData);
        }

        return get(module, method.getName());
    }


    public static <T> T of(Class<T> mapperInterface) {
        ClassLoader classLoader = mapperInterface.getClassLoader();
        Class<?>[] interfaces = new Class[]{mapperInterface};
        CEventBus proxy = new CEventBus();
        return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }




}
