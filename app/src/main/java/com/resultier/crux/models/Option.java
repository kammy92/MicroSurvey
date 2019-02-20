package com.resultier.crux.models;

public class Option {
    private int id;
    private String value, text;
    
    public Option (int id, String value, String text) {
        this.id = id;
        this.value = value;
        this.text = text;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getValue () {
        return value;
    }
    
    public void setValue (String value) {
        this.value = value;
    }
    
    public String getText () {
        return text;
    }
    
    public void setText (String text) {
        this.text = text;
    }
}