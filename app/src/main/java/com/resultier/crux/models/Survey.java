package com.resultier.crux.models;

/**
 * Created by Admin on 11-01-2017.
 */

public class Survey {
    int survey_id, group_id, assignment_id, survey_status;
    String survey_title, survey_question, survey_date;
    
    public Survey (int survey_id, int group_id, int assignment_id, int survey_status, String survey_title, String survey_question, String survey_date) {
        this.survey_id = survey_id;
        this.group_id = group_id;
        this.assignment_id = assignment_id;
        this.survey_status = survey_status;
        this.survey_title = survey_title;
        this.survey_question = survey_question;
        this.survey_date = survey_date;
    }
    
    public int getGroup_id () {
        return group_id;
    }
    
    public void setGroup_id (int group_id) {
        this.group_id = group_id;
    }
    
    public int getAssignment_id () {
        return assignment_id;
    }
    
    public void setAssignment_id (int assignment_id) {
        this.assignment_id = assignment_id;
    }
    
    public String getSurvey_date () {
        return survey_date;
    }
    
    public void setSurvey_date (String survey_date) {
        this.survey_date = survey_date;
    }
    
    public int getSurvey_id () {
        return survey_id;
    }
    
    public void setSurvey_id (int survey_id) {
        this.survey_id = survey_id;
    }
    
    public int getSurvey_status () {
        return survey_status;
    }
    
    public void setSurvey_status (int survey_status) {
        this.survey_status = survey_status;
    }
    
    public String getSurvey_title () {
        return survey_title;
    }
    
    public void setSurvey_title (String survey_title) {
        this.survey_title = survey_title;
    }
    
    public String getSurvey_question () {
        return survey_question;
    }
    
    public void setSurvey_question (String survey_question) {
        this.survey_question = survey_question;
    }
}
