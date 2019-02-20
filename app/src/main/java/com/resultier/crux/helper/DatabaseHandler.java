package com.resultier.crux.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.resultier.crux.utils.AppConfigTags;
import com.resultier.crux.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "microsurver";
    
    // Table Names
    private static final String TABLE_RESPONSES = "tbl_responses";
    private static final String TABLE_SURVEYS = "tbl_surveys";
    
    // Responses Table - column names
    private static final String RSPNS_ID = "rspns_id";
    private static final String RSPNS_SURVEY_ID = "rspns_survey_id";
    private static final String RSPNS_QUESTION_ID = "rspns_question_id";
    private static final String RSPNS_QUESTION_TYPE = "rspns_question_type";
    private static final String RSPNS_IDS = "rspns_ids";
    private static final String RSPNS_VALUES = "rspns_values";
    
    
    private static final String SRVY_ID = "id";
    private static final String SRVY_JOB_ID = "job_id";
    private static final String SRVY_JSON = "response";
    private static final String TIMESTAMP = "timestamp";
    
    
    // Responses table Create Statements
    private static final String CREATE_TABLE_RESPONSES = "CREATE TABLE "
            + TABLE_RESPONSES + "(" +
            RSPNS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            RSPNS_SURVEY_ID + " INTEGER," +
            RSPNS_QUESTION_ID + " INTEGER," +
            RSPNS_QUESTION_TYPE + " TEXT," +
            RSPNS_IDS + " TEXT," +
            RSPNS_VALUES + " TEXT" + ")";
    
    
    private static final String CREATE_TABLE_SURVEYS = "CREATE TABLE "
            + TABLE_SURVEYS + "(" +
            SRVY_ID + " INTEGER," +
            SRVY_JSON + " TEXT," +
            TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP " + ")";
    
    
    Context mContext;
    private boolean LOG_FLAG = true;
    
    public DatabaseHandler (Context context) {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    
    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL (CREATE_TABLE_RESPONSES);
        db.execSQL (CREATE_TABLE_SURVEYS);
    }
    
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_RESPONSES);
        db.execSQL ("DROP TABLE IF EXISTS " + TABLE_SURVEYS);
        onCreate (db);
    }
    
    public void closeDB () {
        SQLiteDatabase db = this.getReadableDatabase ();
        if (db != null && db.isOpen ())
            db.close ();
    }
    
    private String getDateTime () {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss", Locale.getDefault ());
        Date date = new Date ();
        return dateFormat.format (date);
    }
    
    public boolean isResponseExist (int survey_id, int question_id) {
        String countQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public long insertResponse (int survey_id, int question_id, String question_type, String response_ids, String response_values) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Inserting Response", LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (RSPNS_SURVEY_ID, survey_id);
        values.put (RSPNS_QUESTION_ID, question_id);
        values.put (RSPNS_QUESTION_TYPE, question_type);
        values.put (RSPNS_IDS, response_ids);
        values.put (RSPNS_VALUES, response_values);
        return db.insert (TABLE_RESPONSES, null, values);
    }
    
    public int updateResponse (int survey_id, int question_id, String question_type, String response_ids, String response_values) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update Response in Question ID = " + question_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (RSPNS_IDS, response_ids);
        values.put (RSPNS_VALUES, response_values);
        return db.update (TABLE_RESPONSES, values, RSPNS_SURVEY_ID + " = ? AND " + RSPNS_QUESTION_ID + " = ? AND " + RSPNS_QUESTION_TYPE + " = ?", new String[] {String.valueOf (survey_id), String.valueOf (question_id), question_type});
    }
    
    public String getResponseID (int survey_id, int question_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Response where Question ID = " + question_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (RSPNS_IDS));
    }
    
    public String getResponseValue (int survey_id, int question_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Response where Question ID = " + question_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (RSPNS_VALUES));
    }
    
    public boolean isSurveyJSONExist (int survey_id) {
        String countQuery = "SELECT * FROM " + TABLE_SURVEYS + " WHERE " + SRVY_ID + " = " + survey_id;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public int updateSurveyJSON (int survey_id, String response) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update Response in Survey ID = " + survey_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (SRVY_ID, survey_id);
        values.put (SRVY_JSON, response);
        return db.update (TABLE_SURVEYS, values, SRVY_ID + " = ?", new String[] {String.valueOf (survey_id)});
    }
    
    public long insertSurveyJSON (int survey_id, String response) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Inserting Survey", LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (SRVY_ID, survey_id);
        values.put (SRVY_JSON, response);
        values.put (TIMESTAMP, getDateTime ());
        return db.insert (TABLE_SURVEYS, null, values);
    }

    
    /*
    public boolean isSurveyResponseExist (int job_id, int survey_id) {
        String countQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isResponseExist2 (int job_id, int survey_id, int question_id, int option_id) {
        String countQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id + " AND " + RSPNS_IDS + " = " + option_id;
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isResponseExist3 (int job_id, int survey_id, int question_id, String option_id) {
        String countQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id + " AND " + RSPNS_IDS + " = \"" + option_id + "\"";
        SQLiteDatabase db = this.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public int updateResponse2 (int job_id, int survey_id, int question_id, String question_type, String response_ids, String response_values) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Update Response in Question ID = " + question_id, LOG_FLAG);
        ContentValues values = new ContentValues ();
        values.put (RSPNS_IDS, response_ids);
        values.put (RSPNS_VALUES, response_values);
        return db.update (TABLE_RESPONSES, values, RSPNS_JOB_ID + " = ? AND " + RSPNS_SURVEY_ID + " = ? AND " + RSPNS_QUESTION_ID + " = ? AND " + RSPNS_QUESTION_TYPE + " = ? AND " + RSPNS_IDS + " = ?", new String[] {String.valueOf (job_id), String.valueOf (survey_id), String.valueOf (question_id), question_type, response_ids});
    }
    
    public String getResponseValue2 (int job_id, int survey_id, int question_id, int option_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id + " AND " + RSPNS_IDS + " = " + option_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get Response where Question ID = " + question_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (RSPNS_VALUES));
    }
    
    public ArrayList<Image> getAllImageResponses (int job_id, int survey_id, int question_id) {
        ArrayList<Image> imageList = new ArrayList<Image> ();
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all images", LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c.moveToFirst ()) {
            do {
                imageList.add (new Image (
                        c.getString (c.getColumnIndex (RSPNS_IDS)),
                        c.getString (c.getColumnIndex (RSPNS_VALUES))));
            } while (c.moveToNext ());
        }
        return imageList;
    }
    
    public void deleteImageResponse (int job_id, int survey_id, int question_id, String image_name, String image_path) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete Response where Question ID = " + question_id, LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id + " AND " + RSPNS_IDS + " = '" + image_name + "' AND " + RSPNS_VALUES + " = '" + image_path + "'");
    }
    
    public void deleteResponse (int job_id, int survey_id, int question_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete Response where Question ID = " + question_id, LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id + " AND " + RSPNS_QUESTION_ID + " = " + question_id);
    }
    
    public void deleteAllSurveyResponses (int job_id, int survey_id) {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete Response where Job ID = " + job_id + " AND Survey ID = " + survey_id, LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id);
    }
    
    public ArrayList<Response> getAllResponses (int job_id, int survey_id) {
        ArrayList<Response> responseList = new ArrayList<Response> ();
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT  * FROM " + TABLE_RESPONSES + " WHERE " + RSPNS_JOB_ID + " = " + job_id + " AND " + RSPNS_SURVEY_ID + " = " + survey_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get all Responses", LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c.moveToFirst ()) {
            do {
                responseList.add (new Response (
                        c.getInt (c.getColumnIndex (RSPNS_JOB_ID)),
                        c.getInt (c.getColumnIndex (RSPNS_SURVEY_ID)),
                        c.getInt (c.getColumnIndex (RSPNS_QUESTION_ID)),
                        c.getString (c.getColumnIndex (RSPNS_QUESTION_TYPE)),
                        c.getString (c.getColumnIndex (RSPNS_IDS)),
                        c.getString (c.getColumnIndex (RSPNS_VALUES))));
            } while (c.moveToNext ());
        }
        return responseList;
    }
    
    
    
    
    
    public String getResponseSurveyJSON (int survey_id, int job_id) {
        SQLiteDatabase db = this.getReadableDatabase ();
        String selectQuery = "SELECT * FROM " + TABLE_SURVEYS + " WHERE " + SRVY_JOB_ID + " = " + job_id + " AND " + SRVY_ID + " = " + survey_id;
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Get ResponseSurveyDetail where job ID = " + job_id, LOG_FLAG);
        Cursor c = db.rawQuery (selectQuery, null);
        if (c != null)
            c.moveToFirst ();
        return c.getString (c.getColumnIndex (SRVY_JSON));
    }
    
    public void deleteAllSurveyJSON () {
        SQLiteDatabase db = this.getWritableDatabase ();
        Utils.showLog (Log.DEBUG, AppConfigTags.DATABASE_LOG, "Delete all surveys", LOG_FLAG);
        db.execSQL ("DELETE FROM " + TABLE_SURVEYS);
    }
    
    */
}