package com.resultier.crux.models;

import java.util.ArrayList;

public class Question {
    private boolean validation;
    private int id;
    private String text, type;
    private ArrayList<Option> options;
    
    public Question () {
    }
    
    public Question (boolean validation, int id, String text, String type, ArrayList<Option> options) {
        this.validation = validation;
        this.id = id;
        this.text = text;
        this.type = type;
        this.options = options;
    }
    
    public boolean isValidation () {
        return validation;
    }
    
    public void setValidation (boolean validation) {
        this.validation = validation;
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getText () {
        return text;
    }
    
    public void setText (String text) {
        this.text = text;
    }
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    public ArrayList<Option> getOptions () {
        return options;
    }
    
    public void setOptions (ArrayList<Option> options) {
        this.options = options;
    }
}