package com.resultier.crux.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.resultier.crux.R;
import com.resultier.crux.adapter.QuestionAdapter;
import com.resultier.crux.helper.DatabaseHandler;
import com.resultier.crux.listeners.OnItemClickListener;
import com.resultier.crux.models.Option;
import com.resultier.crux.models.Question;
import com.resultier.crux.utils.AppConfigTags;
import com.resultier.crux.utils.AppConfigURL;
import com.resultier.crux.utils.AppDetailsPref;
import com.resultier.crux.utils.Constants;
import com.resultier.crux.utils.NetworkConnection;
import com.resultier.crux.utils.SetTypeFace;
import com.resultier.crux.utils.UserDetailsPref;
import com.resultier.crux.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class SurveyActivity extends AppCompatActivity {
    ImageView ivSave;
    CoordinatorLayout clMain;
    AppDetailsPref appDetailsPref;
    ProgressDialog progressDialog;
    UserDetailsPref userDetailsPref;
    
    ArrayList<Question> questionList = new ArrayList<> ();
    
    RecyclerView rvSurvey;
    TextView tvTitle;
    
    QuestionAdapter questionAdapter;
    
    String survey_title = "";
    int survey_id = 0;
    int group_id = 0;
    int assignment_id = 0;
    
    DatabaseHandler db;
    
    RelativeLayout rlBack;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_survey);
        initExtras ();
        initView ();
        initData ();
        initAdapter ();
        initListener ();
        getQuestionList ();
    }
    
    private void initExtras () {
        Intent mIntent = getIntent ();
        survey_id = mIntent.getIntExtra (AppConfigTags.SURVEY_ID, 0);
        group_id = mIntent.getIntExtra (AppConfigTags.GROUP_ID, 0);
        assignment_id = mIntent.getIntExtra (AppConfigTags.ASSIGNMENT_ID, 0);
        survey_title = mIntent.getStringExtra (AppConfigTags.SURVEY_TITLE);
    }
    
    private void initView () {
        ivSave = (ImageView) findViewById (R.id.ivSave);
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        rvSurvey = (RecyclerView) findViewById (R.id.rvSurvey);
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        tvTitle = (TextView) findViewById (R.id.tvTitleEventName);
    }
    
    private void initData () {
        progressDialog = new ProgressDialog (this);
        appDetailsPref = AppDetailsPref.getInstance ();
        userDetailsPref = UserDetailsPref.getInstance ();
        Utils.setTypefaceToAllViews (this, clMain);
        tvTitle.setText (survey_title);
        db = new DatabaseHandler (this);
    }
    
    private void initAdapter () {
        questionAdapter = new QuestionAdapter (this, questionList, survey_id, group_id, assignment_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager (SurveyActivity.this, OrientationHelper.VERTICAL, false);
        rvSurvey.setLayoutManager (linearLayoutManager);
        rvSurvey.setItemAnimator (new DefaultItemAnimator ());
        rvSurvey.setAdapter (questionAdapter);
    }
    
    private void initListener () {
        ivSave.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                submitResponsesToServer ();
            }
        });
        
        questionAdapter.SetOnItemClickListener (new OnItemClickListener () {
            @Override
            public void onItemClick (View view, int position) {
            }
        });
        
        rlBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
    
    public void getQuestionList () {
        if (NetworkConnection.isNetworkAvailable (SurveyActivity.this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.GET_SURVEY_DETAIL, true);
            Utils.showProgressDialog (SurveyActivity.this, progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.GET_SURVEY_DETAIL,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    questionList.clear ();
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! is_error) {
                                        if (db.isSurveyJSONExist (survey_id)) {
                                            db.updateSurveyJSON (survey_id, response);
                                        } else {
                                            db.insertSurveyJSON (survey_id, response);
                                        }
                                        
                                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.QUESTIONS);
                                        for (int i = 0; i < jsonArray.length (); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject (i);
                                            
                                            Question question = new Question ();
                                            question.setId (jsonObject.getInt (AppConfigTags.QUESTION_ID));
                                            question.setText (jsonObject.getString (AppConfigTags.QUESTION_TEXT));
                                            question.setType (jsonObject.getString (AppConfigTags.QUESTION_TYPE));
                                            question.setValidation (jsonObject.getBoolean (AppConfigTags.VALIDATION));
                                            
                                            JSONArray jsonArray2 = jsonObject.getJSONArray (AppConfigTags.OPTIONS);
                                            ArrayList<Option> options = new ArrayList<> ();
                                            for (int j = 0; j < jsonArray2.length (); j++) {
                                                JSONObject jsonObject1 = jsonArray2.getJSONObject (j);
                                                options.add (new Option (
                                                        jsonObject1.getInt (AppConfigTags.OPTION_ID),
                                                        jsonObject1.getString (AppConfigTags.OPTION_VALUE)));
                                            }
                                            question.setOptions (options);
                                            questionList.add (question);
                                        }
                                        questionAdapter.notifyDataSetChanged ();
                                    } else {
                                        if (! showOfflineData (survey_id)) {
                                            Utils.showSnackBar (SurveyActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    if (! showOfflineData (survey_id)) {
                                        Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
                                }
                            } else {
                                if (! showOfflineData (survey_id)) {
                                    Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                }
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                            }
                            if (! showOfflineData (survey_id)) {
                                Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                            progressDialog.dismiss ();
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.USER_ID, String.valueOf (userDetailsPref.getIntPref (SurveyActivity.this, UserDetailsPref.USER_ID)));
                    params.put (AppConfigTags.SURVEY_ID, String.valueOf (survey_id));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest, 50);
        } else {
            if (! showOfflineData (survey_id)) {
                Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                    @Override
                    public void onClick (View v) {
                        Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                        dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity (dialogIntent);
                    }
                });
            }
        }
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    private void submitResponsesToServer () {
        if (NetworkConnection.isNetworkAvailable (SurveyActivity.this)) {
            Utils.showProgressDialog (SurveyActivity.this, progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.SUBMIT_RESPONSES, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.SUBMIT_RESPONSES,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    ArrayList<String> questions = new ArrayList<> ();
                                    if (! error) {
                                        new MaterialDialog.Builder (SurveyActivity.this)
                                                .content (message)
                                                .cancelable (false)
                                                .canceledOnTouchOutside (false)
                                                .positiveText (getResources ().getString (R.string.dialog_action_ok))
                                                .typeface (SetTypeFace.getTypeface (SurveyActivity.this), SetTypeFace.getTypeface (SurveyActivity.this))
                                                .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                    @Override
                                                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss ();
                                                        db.deleteAllSurveyResponses (survey_id, group_id, assignment_id);
                                                        finish ();
                                                        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                                    }
                                                })
                                                .show ();
                                    } else {
/*
                                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.INVALID_QUESTIONS);
                                        if (jsonArray.length () > 0) {
                                            for (int k = 0; k < jsonArray.length (); k++) {
                                                questions.add (jsonArray.getString (k));
                                            }
                                            new MaterialDialog.Builder (SurveyActivity.this)
                                                    .content (message)
                                                    .items (questions)
                                                    .positiveText (getResources ().getString (R.string.dialog_action_ok))
                                                    .typeface (SetTypeFace.getTypeface (SurveyActivity.this), SetTypeFace.getTypeface (SurveyActivity.this))
                                                    .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                        @Override
                                                        public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            dialog.dismiss ();
                                                            db.deleteAllSurveyResponses (job_id, survey_id);
                                                            finish ();
                                                            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                                        }
                                                    })
                                                    .show ();
                                        } else {
*/
                                        Utils.showSnackBar (SurveyActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
//                                        }
                                    }
                                    progressDialog.dismiss ();
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (SurveyActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.USER_ID, String.valueOf (userDetailsPref.getIntPref (SurveyActivity.this, UserDetailsPref.USER_ID)));
                    ArrayList<com.resultier.crux.models.Response> responseList = new ArrayList<> ();
                    responseList = db.getAllResponses (survey_id, group_id, assignment_id);
                    JSONObject jsonObject = new JSONObject ();
                    try {
                        JSONArray jsonArray = new JSONArray ();
                        
                        for (int i = 0; i < responseList.size (); i++) {
                            com.resultier.crux.models.Response response = responseList.get (i);
                            JSONObject jsonObject1 = new JSONObject ();
                            jsonObject1.put (AppConfigTags.QUESTION_TYPE, response.getQuestion_type ());
                            jsonObject1.put (AppConfigTags.QUESTION_ID, response.getQuestion_id ());
                            switch (response.getQuestion_type ()) {
                                case AppConfigTags.TYPE_INPUT:
                                    jsonObject1.put (AppConfigTags.ANSWER, response.getResponse_values ());
                                    break;
                                case AppConfigTags.TYPE_RADIO:
                                    jsonObject1.put (AppConfigTags.ANSWER, response.getResponse_ids ());
                                    break;
                                case AppConfigTags.TYPE_CHECKBOX:
                                    jsonObject1.put (AppConfigTags.ANSWER, response.getResponse_ids ());
                                    break;
                                case AppConfigTags.TYPE_RATING:
                                    jsonObject1.put (AppConfigTags.ANSWER, response.getResponse_values ());
                                    break;
                                case AppConfigTags.TYPE_SLIDER:
                                    jsonObject1.put (AppConfigTags.ANSWER, response.getResponse_values ());
                                    break;
                                default:
                                    jsonObject1.put (AppConfigTags.ANSWER, "");
                                    break;
                            }
                            jsonArray.put (jsonObject1);
                        }
                        jsonObject.put (AppConfigTags.QUESTIONS, jsonArray);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                    
                    Log.e ("karman", jsonObject.toString ());
                    
                    params.put (AppConfigTags.RESPONSES, jsonObject.toString ());
                    params.put (AppConfigTags.SURVEY_ID, String.valueOf (survey_id));
                    params.put (AppConfigTags.GROUP_ID, String.valueOf (group_id));
                    params.put (AppConfigTags.ASSIGNMENT_ID, String.valueOf (assignment_id));
                    params.put (AppConfigTags.USER_ID, String.valueOf (userDetailsPref.getIntPref (SurveyActivity.this, UserDetailsPref.USER_ID)));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 60);
        } else {
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }
    
    private boolean showOfflineData (int survey_id) {
        return false;
    }
}