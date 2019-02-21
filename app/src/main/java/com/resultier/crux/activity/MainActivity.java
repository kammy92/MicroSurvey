package com.resultier.crux.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.resultier.crux.R;
import com.resultier.crux.adapter.SurveyAdapter;
import com.resultier.crux.listeners.OnItemClickListener;
import com.resultier.crux.models.Survey;
import com.resultier.crux.utils.AppConfigTags;
import com.resultier.crux.utils.AppConfigURL;
import com.resultier.crux.utils.AppDetailsPref;
import com.resultier.crux.utils.Constants;
import com.resultier.crux.utils.DashDivider;
import com.resultier.crux.utils.NetworkConnection;
import com.resultier.crux.utils.RecyclerViewMargin;
import com.resultier.crux.utils.UserDetailsPref;
import com.resultier.crux.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener {
    private UserDetailsPref userDetailsPref;
    private AppDetailsPref appDetailsPref;
    private RelativeLayout rlBack;
    private CoordinatorLayout clMain;
    private EditText etSearch;
    private RecyclerView rvPolls;
    private ImageView ivRefresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout rlMain;
    private RelativeLayout rlNoResultFound;
    private RelativeLayout rlNoInternet;
    private RelativeLayout rlLoading;
    
    SurveyAdapter surveyAdapter;
    private ArrayList<Survey> surveyList = new ArrayList<> ();
    
    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        initListeners ();
        isLogin ();
        getPollList ();
    }
    
    public void initData () {
        userDetailsPref = UserDetailsPref.getInstance ();
        appDetailsPref = AppDetailsPref.getInstance ();
        Utils.setTypefaceToAllViews (this, clMain);
        swipeRefreshLayout.setColorSchemeColors (getResources ().getColor (R.color.primary_dark));
        surveyAdapter = new SurveyAdapter (this, surveyList);
        rvPolls.setAdapter (surveyAdapter);
        rvPolls.setHasFixedSize (true);
        rvPolls.setLayoutManager (new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false));
        rvPolls.setItemAnimator (new DefaultItemAnimator ());
        rvPolls.addItemDecoration (new RecyclerViewMargin (this, (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), 1, 0, RecyclerViewMargin.LAYOUT_MANAGER_LINEAR, RecyclerViewMargin.ORIENTATION_VERTICAL));
//        rvPolls.addItemDecoration (new DividerItemDecoration (ContextCompat.getDrawable (this, R.drawable.line_divider)));
//        rvPolls.addItemDecoration (new SimpleDividerItemDecoration (this, (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16), (int) Utils.pxFromDp (this, 16)));
    
        rvPolls.addItemDecoration (new DashDivider.Builder (this)
                .dashGap (5)
                .dashLength (15)
                .dashThickness (3)
                .color (ContextCompat.getColor (this, R.color.primary))
                .orientation (LinearLayoutManager.VERTICAL)
                .marginTop ((int) Utils.pxFromDp (this, 16))
                .marginBottom ((int) Utils.pxFromDp (this, 16))
                .marginLeft ((int) Utils.pxFromDp (this, 16))
                .marginRight ((int) Utils.pxFromDp (this, 16))
                .build ());
        
    }
    
    public void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById (R.id.swipe_refresh_layout);
        rvPolls = (RecyclerView) findViewById (R.id.rvPolls);
        ivRefresh = (ImageView) findViewById (R.id.ivRefresh);
        rlMain = (RelativeLayout) findViewById (R.id.rlMain);
        rlNoResultFound = (RelativeLayout) findViewById (R.id.rlNoResultFound);
        rlLoading = (RelativeLayout) findViewById (R.id.rlLoading);
        rlNoInternet = (RelativeLayout) findViewById (R.id.rlNoInternet);
    }
    
    public void initListeners () {
        ivRefresh.setOnClickListener (this);
        swipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh () {
                getPollList ();
                swipeRefreshLayout.setRefreshing (false);
            }
        });
        surveyAdapter.setOnItemClickListener (this);
    }
    
    public void isLogin () {
        if (userDetailsPref.getIntPref (this, UserDetailsPref.USER_ID) == 0) {
            Intent intent = new Intent (MainActivity.this, LoginActivity.class);
            startActivity (intent);
            finish ();
        }
    }
    
    @Override
    public void onClick (View v) {
        switch (v.getId ()) {
            case R.id.ivRefresh:
                getPollList ();
                break;
        }
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    private void startLoading () {
        rlNoResultFound.setVisibility (View.GONE);
        rlNoInternet.setVisibility (View.GONE);
        rlMain.setVisibility (View.GONE);
        rlLoading.setVisibility (View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation (this, R.anim.rotate);
        rotation.setFillAfter (true);
        ivRefresh.startAnimation (rotation);
    }
    
    private void stopLoading () {
        ivRefresh.clearAnimation ();
        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run () {
                if (surveyList.size () > 0) {
                    rlMain.setVisibility (View.VISIBLE);
                } else {
                    rlNoResultFound.setVisibility (View.VISIBLE);
                }
            }
        }, 500);
        rlLoading.setVisibility (View.GONE);
    }
    
    public void getPollList () {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            startLoading ();
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.GET_POLLS, true);
            StringRequest strRequest = new StringRequest (Request.Method.GET, AppConfigURL.GET_POLLS,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! is_error) {
                                        appDetailsPref.putStringPref (MainActivity.this, AppDetailsPref.POLLS, response);
                                        JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEYS);
                                        surveyList.clear ();
                                        for (int i = 0; i < jsonArray.length (); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject (i);
                                            surveyList.add (new Survey (
                                                    jsonObject.getInt (AppConfigTags.SURVEY_ID),
                                                    jsonObject.getInt (AppConfigTags.SURVEY_STATUS),
                                                    jsonObject.getString (AppConfigTags.SURVEY_TITLE),
                                                    jsonObject.getString (AppConfigTags.SURVEY_QUESTION),
                                                    jsonObject.getString (AppConfigTags.SURVEY_DATE)));
                                        }
                                        if (surveyList.size () > 0) {
                                            rlNoResultFound.setVisibility (View.GONE);
                                        } else {
                                            rlNoResultFound.setVisibility (View.VISIBLE);
                                        }
                                        surveyAdapter.notifyDataSetChanged ();
                                    } else {
                                        if (! showOfflineData ()) {
                                            Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                    if (! showOfflineData ()) {
                                        Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
                                }
                            } else {
                                if (! showOfflineData ()) {
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                }
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            stopLoading ();
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            stopLoading ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                            }
                            if (! showOfflineData ()) {
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                            }
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    AppDetailsPref appDetailsPref = AppDetailsPref.getInstance ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_ID, "1555");
//                    params.put (AppConfigTags.HEADER_AUTHORIZATION, "Basic " + appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.USER_TOKEN));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
                
                @Override
                public byte[] getBody () throws AuthFailureError {
//                    JSONObject jsonObject = new JSONObject ();
//                    try {
//                        jsonObject.put (AppConfigTags.USERNAME, username);
//                        jsonObject.put (AppConfigTags.PASSWORD, password);
//                    } catch (JSONException e) {
//                        e.printStackTrace ();
//                    }
                    String str = "";
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, str, true);
                    return str.getBytes ();
                }
                
                public String getBodyContentType () {
                    return "application/json; charset=utf-8";
                }
            };
            Utils.sendRequest (strRequest, 30);
        } else {
            if (! showOfflineData ()) {
                swipeRefreshLayout.setRefreshing (false);
                rlNoInternet.setVisibility (View.VISIBLE);
                rlMain.setVisibility (View.GONE);
                rlNoResultFound.setVisibility (View.GONE);
                rlLoading.setVisibility (View.GONE);
                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
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
    
    private boolean showOfflineData () {
        stopLoading ();
        String response = null;
        response = appDetailsPref.getStringPref (MainActivity.this, AppDetailsPref.POLLS);
        if (response != null) {
            try {
                JSONObject jsonObj = new JSONObject (response);
                boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                String message = jsonObj.getString (AppConfigTags.MESSAGE);
                if (! is_error) {
                    appDetailsPref.putStringPref (MainActivity.this, AppDetailsPref.POLLS, response);
                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEYS);
                    surveyList.clear ();
                    for (int i = 0; i < jsonArray.length (); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                        surveyList.add (new Survey (
                                jsonObject.getInt (AppConfigTags.SURVEY_ID),
                                jsonObject.getInt (AppConfigTags.SURVEY_STATUS),
                                jsonObject.getString (AppConfigTags.SURVEY_TITLE),
                                jsonObject.getString (AppConfigTags.SURVEY_QUESTION),
                                jsonObject.getString (AppConfigTags.SURVEY_DATE)));
                    }
                    if (surveyList.size () > 0) {
                        rlNoResultFound.setVisibility (View.GONE);
                    } else {
                        rlNoResultFound.setVisibility (View.VISIBLE);
                    }
                    surveyAdapter.notifyDataSetChanged ();
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
            stopLoading ();
            return true;
        } else {
            stopLoading ();
            return false;
        }
        
        /*
        if (response != null) {
            try {
                JSONObject jsonObj = new JSONObject (response);
                boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                String message = jsonObj.getString (AppConfigTags.MESSAGE);
                if (! is_error) {
                    rlNoInternet.setVisibility (View.GONE);
                    rlNoResultFound.setVisibility (View.GONE);
                    rlLoading.setVisibility (View.GONE);
                    JSONArray jsonArray = jsonObj.getJSONArray (AppConfigTags.SURVEYS);
                    surveyList.clear ();
                    for (int i = 0; i < jsonArray.length (); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject (i);
                        surveyList.add (new Survey (i, 0, "Title " + i, "Question " + i, "2019-02-13 12:12:12"));
                    }
                    if (surveyList.size () > 0) {
                        rlMain.setVisibility (View.VISIBLE);
                        rlNoResultFound.setVisibility (View.GONE);
                    } else {
                        rlMain.setVisibility (View.GONE);
                        rlNoResultFound.setVisibility (View.VISIBLE);
                    }
                    surveyAdapter.notifyDataSetChanged ();
                } else {
                    Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
            return true;
        } else {
            return false;
        }
        */
    }
    
    @Override
    public void onItemClick (View view, int position) {
        Survey survey = surveyList.get (position);
        Intent intent = new Intent (MainActivity.this, SurveyActivity.class);
        intent.putExtra (AppConfigTags.SURVEY_ID, survey.getSurvey_id ());
        intent.putExtra (AppConfigTags.SURVEY_TITLE, survey.getSurvey_title ());
        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity (intent);
    }
}