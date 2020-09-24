package com.example.interpreter.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TranslationBean implements Serializable {
    private List<String> translation;
    private BasicBean basic;
    private String query;
    private int errorCode;
    private List<WebBean> web;
}

@Data
class BasicBean implements Serializable {
    private List<String> explains;
}

@Data
class WebBean implements Serializable {
    private List<String> value;
    private String key;
}