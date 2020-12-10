package com.example.ceventbus;

import java.util.List;

public class ModuleBean {
    String fullClassName;
    String className;
    String module;
    List<EventBean> eventList;

    public ModuleBean(String fullClassName, String className, String module) {
        this.fullClassName = fullClassName;
        this.className = className;
        this.module = module;
    }
}