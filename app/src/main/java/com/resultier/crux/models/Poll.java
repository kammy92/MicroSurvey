package com.resultier.crux.models;

/**
 * Created by Admin on 11-01-2017.
 */

public class Poll {
    int poll_id, poll_status;
    String poll_title, poll_question, poll_date;
    
    public Poll (int poll_id, int poll_status, String poll_title, String poll_question, String poll_date) {
        this.poll_id = poll_id;
        this.poll_status = poll_status;
        this.poll_title = poll_title;
        this.poll_question = poll_question;
        this.poll_date = poll_date;
    }
    
    public String getPoll_date () {
        return poll_date;
    }
    
    public void setPoll_date (String poll_date) {
        this.poll_date = poll_date;
    }
    
    public int getPoll_id () {
        return poll_id;
    }
    
    public void setPoll_id (int poll_id) {
        this.poll_id = poll_id;
    }
    
    public int getPoll_status () {
        return poll_status;
    }
    
    public void setPoll_status (int poll_status) {
        this.poll_status = poll_status;
    }
    
    public String getPoll_title () {
        return poll_title;
    }
    
    public void setPoll_title (String poll_title) {
        this.poll_title = poll_title;
    }
    
    public String getPoll_question () {
        return poll_question;
    }
    
    public void setPoll_question (String poll_question) {
        this.poll_question = poll_question;
    }
}
