package com.resultier.crux.models;

public class Response {
    private int survey_id, group_id, assignment_id, question_id;
    private String question_type, response_ids, response_values;
    
    public Response (int survey_id, int group_id, int assignment_id, int question_id, String question_type, String response_ids, String response_values) {
        this.survey_id = survey_id;
        this.group_id = group_id;
        this.assignment_id = assignment_id;
        this.question_id = question_id;
        this.question_type = question_type;
        this.response_ids = response_ids;
        this.response_values = response_values;
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
    
    public String getQuestion_type () {
        return question_type;
    }
    
    public void setQuestion_type (String question_type) {
        this.question_type = question_type;
    }
    
    public int getSurvey_id () {
        return survey_id;
    }
    
    public void setSurvey_id (int survey_id) {
        this.survey_id = survey_id;
    }
    
    public int getQuestion_id () {
        return question_id;
    }
    
    public void setQuestion_id (int question_id) {
        this.question_id = question_id;
    }
    
    public String getResponse_ids () {
        return response_ids;
    }
    
    public void setResponse_ids (String response_ids) {
        this.response_ids = response_ids;
    }
    
    public String getResponse_values () {
        return response_values;
    }
    
    public void setResponse_values (String response_values) {
        this.response_values = response_values;
    }
}