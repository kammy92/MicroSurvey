package com.resultier.crux.models;

import org.json.JSONObject;

/**
 * Created by Admin on 11-01-2017.
 */

public class Notification {
    boolean isBackground;
    int notification_type; // 1=> Normal open app
    int notification_priority; // -2=> PRIORITY_MIN, -1=> PRIORITY_LOW, 0=> PRIORITY_DEFAULT, 1=> PRIORITY_HIGH, 2=> PRIORITY_MAX
    int event_id, offer_id;
    String title, message, image_url, timestamp;
    JSONObject payload;
    
    public Notification () {
    }
    
    public boolean isBackground () {
        return isBackground;
    }
    
    public void setBackground (boolean background) {
        isBackground = background;
    }
    
    public int getNotification_type () {
        return notification_type;
    }
    
    public void setNotification_type (int notification_type) {
        this.notification_type = notification_type;
    }
    
    public int getNotification_priority () {
        return notification_priority;
    }
    
    public void setNotification_priority (int notification_priority) {
        this.notification_priority = notification_priority;
    }
    
    public int getEvent_id () {
        return event_id;
    }
    
    public void setEvent_id (int event_id) {
        this.event_id = event_id;
    }
    
    public int getOffer_id () {
        return offer_id;
    }
    
    public void setOffer_id (int offer_id) {
        this.offer_id = offer_id;
    }
    
    public String getTitle () {
        return title;
    }
    
    public void setTitle (String title) {
        this.title = title;
    }
    
    public String getMessage () {
        return message;
    }
    
    public void setMessage (String message) {
        this.message = message;
    }
    
    public String getImage_url () {
        return image_url;
    }
    
    public void setImage_url (String image_url) {
        this.image_url = image_url;
    }
    
    public String getTimestamp () {
        return timestamp;
    }
    
    public void setTimestamp (String timestamp) {
        this.timestamp = timestamp;
    }
    
    public JSONObject getPayload () {
        return payload;
    }
    
    public void setPayload (JSONObject payload) {
        this.payload = payload;
    }
}
