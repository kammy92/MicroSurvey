package com.resultier.crux.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.resultier.crux.R;
import com.resultier.crux.activity.SurveyActivity;
import com.resultier.crux.helper.DatabaseHandler;
import com.resultier.crux.listeners.OnItemClickListener;
import com.resultier.crux.models.Option;
import com.resultier.crux.models.Question;
import com.resultier.crux.utils.AppConfigTags;
import com.resultier.crux.utils.SetTypeFace;
import com.resultier.crux.utils.Utils;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnItemClickListener mItemClickListener;
    SurveyActivity surveyActivity;
    ProgressDialog progressDialog;
    DatabaseHandler db;
    int survey_id = 0;
    private Activity activity;
    private ArrayList<Question> questionList = new ArrayList<> ();
    
    public QuestionAdapter (Activity activity, ArrayList<Question> questionList, int survey_id) {
        this.activity = activity;
        this.questionList = questionList;
        this.survey_id = survey_id;
        surveyActivity = (SurveyActivity) activity;
        db = new DatabaseHandler (activity);
    }
    
    @Override
    public int getItemViewType (int position) {
        Question question = questionList.get (position);
        switch (question.getType ()) {
            case AppConfigTags.TYPE_INPUT:
                return 1;
            case AppConfigTags.TYPE_RADIO:
                return 2;
            case AppConfigTags.TYPE_CHECKBOX:
                return 3;
            case AppConfigTags.TYPE_RATING:
                return 4;
            case AppConfigTags.TYPE_SLIDER:
                return 5;
            default:
                return 0;
        }
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView;
        switch (viewType) {
            case 1:
                sView = mInflater.inflate (R.layout.question_type_input, parent, false);
                return new ViewHolder1 (sView);
            case 2:
                sView = mInflater.inflate (R.layout.question_type_radio, parent, false);
                return new ViewHolder2 (sView);
            case 3:
                sView = mInflater.inflate (R.layout.question_type_checkbox, parent, false);
                return new ViewHolder3 (sView);
            case 4:
                sView = mInflater.inflate (R.layout.question_type_rating, parent, false);
                return new ViewHolder4 (sView);
            case 5:
                sView = mInflater.inflate (R.layout.question_type_slider, parent, false);
                return new ViewHolder5 (sView);
            default:
                sView = mInflater.inflate (R.layout.question_type_plain_text, parent, false);
                return new com.resultier.crux.adapter.QuestionAdapter.ViewHolder3 (sView);
        }
    }
    
    @Override
    public void onBindViewHolder (final RecyclerView.ViewHolder holder, int position) {
        final Question question = questionList.get (position);
        holder.setIsRecyclable (false);
        switch (holder.getItemViewType ()) {
            case 1:
                final ViewHolder1 holder1 = (ViewHolder1) holder;
                holder1.setIsRecyclable (false);
                Utils.setTypefaceToAllViews (activity, holder1.tvQuestion);
                holder1.tvQuestion.setText (question.getText ());
                if (db.isResponseExist (survey_id, question.getId ())) {
                    holder1.etInput.setText (db.getResponseValue (survey_id, question.getId ()));
                }
                holder1.etInput.setInputType (InputType.TYPE_CLASS_TEXT);
                holder1.etInput.addTextChangedListener (new TextWatcher () {
                    @Override
                    public void onTextChanged (CharSequence s, int start, int before, int count) {
                        if (db.isResponseExist (survey_id, question.getId ())) {
                            db.updateResponse (survey_id, question.getId (), question.getType (), "", s.toString ());
                        } else {
                            db.insertResponse (survey_id, question.getId (), question.getType (), "", s.toString ());
                        }
                    }
                    
                    @Override
                    public void beforeTextChanged (CharSequence s, int start, int count, int after) {
                    }
                    
                    @Override
                    public void afterTextChanged (Editable s) {
                    }
                });
                break;
            case 2:
                ViewHolder2 holder2 = (ViewHolder2) holder;
                holder2.setIsRecyclable (false);
                Utils.setTypefaceToAllViews (activity, holder2.tvQuestion);
                holder2.tvQuestion.setText (question.getText ());
                holder2.rgOptions.removeAllViews ();
                int option_id2 = 0;
                if (db.isResponseExist (survey_id, question.getId ())) {
                    option_id2 = Integer.parseInt (db.getResponseID (survey_id, question.getId ()));
                }
                
                String selected_option = "";
                holder2.radioButtons = new RadioButton[question.getOptions ().size ()];
                for (int i = 0; i < question.getOptions ().size (); i++) {
                    Option option = question.getOptions ().get (i);
                    holder2.radioButtons[i] = new RadioButton (activity);
                    holder2.radioButtons[i].setId (option.getId ());
                    holder2.radioButtons[i].setText (option.getValue ());
                    holder2.radioButtons[i].setTag (i);
                    holder2.radioButtons[i].setTextSize (TypedValue.COMPLEX_UNIT_SP, 14);
                    holder2.radioButtons[i].setTypeface (SetTypeFace.getTypeface (activity));
                    if (option.getId () == option_id2) {
                        holder2.radioButtons[i].setChecked (true);
                        selected_option = option.getValue ();
                    }
    
                    holder2.rgOptions.addView (holder2.radioButtons[i]);
                }
    
                holder2.rgOptions.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener () {
                    @Override
                    public void onCheckedChanged (RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) group.findViewById (checkedId);
                        String selected_option = radioButton.getText ().toString ();
                        if (db.isResponseExist (survey_id, question.getId ())) {
                            db.updateResponse (survey_id, question.getId (), question.getType (), String.valueOf (checkedId), selected_option);
                        } else {
                            db.insertResponse (survey_id, question.getId (), question.getType (), String.valueOf (checkedId), selected_option);
                        }
                    }
                });
                break;
            case 3:
                ViewHolder3 holder3 = (ViewHolder3) holder;
                holder3.setIsRecyclable (false);
                Utils.setTypefaceToAllViews (activity, holder3.tvQuestion);
                holder3.tvQuestion.setText (question.getText ());
    
                String option_id3 = "";
                String option_value3 = "";
                if (db.isResponseExist (survey_id, question.getId ())) {
                    option_id3 = db.getResponseID (survey_id, question.getId ());
                    option_value3 = db.getResponseValue (survey_id, question.getId ());
                }
    
                String[] ops = option_id3.split (";;");
                String[] ops2 = option_value3.split (";;");
                
                final ArrayList<String> optionList = new ArrayList<> ();
                final ArrayList<String> valueList = new ArrayList<> ();
                try {
                    for (int i = 0; i < ops.length; i++) {
                        if (ops[i].length () > 0) {
                            optionList.add (ops[i]);
                        }
                    }
                    for (int j = 0; j < ops2.length; j++) {
                        if (ops2[j].length () > 0) {
                            valueList.add (ops2[j]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace ();
                }
    
                holder3.llOptions.removeAllViews ();
                for (int i = 0; i < question.getOptions ().size (); i++) {
                    final Option option = question.getOptions ().get (i);
                    final CheckBox cbOption = new CheckBox (activity);
                    cbOption.setId (option.getId ());
                    cbOption.setText (option.getValue ());
                    cbOption.setTypeface (SetTypeFace.getTypeface (activity));
                    cbOption.setTextSize (TypedValue.COMPLEX_UNIT_SP, 14);
                    if (optionList.contains (String.valueOf (option.getId ()))) {
                        cbOption.setChecked (true);
                    } else {
                        cbOption.setChecked (false);
                    }
                    
                    cbOption.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                        @Override
                        public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                if (! optionList.contains (String.valueOf (buttonView.getId ()))) {
                                    optionList.add (String.valueOf (buttonView.getId ()));
                                }
                                if (! valueList.contains (buttonView.getText ().toString ())) {
                                    valueList.add (buttonView.getText ().toString ());
                                }
                            } else {
                                if (optionList.contains (String.valueOf (buttonView.getId ()))) {
                                    optionList.remove (String.valueOf (buttonView.getId ()));
                                }
                                if (valueList.contains (buttonView.getText ().toString ())) {
                                    valueList.remove (buttonView.getText ().toString ());
                                }
                            }
                            
                            StringBuilder str = new StringBuilder ();
                            for (int i = 0; i < optionList.size (); i++) {
                                if (optionList.get (i).length () > 0) {
                                    str.append (optionList.get (i));
                                    if (i != optionList.size () - 1) {
                                        str.append (";;");
                                    }
                                }
                            }
                            
                            StringBuilder str2 = new StringBuilder ();
                            for (int i = 0; i < valueList.size (); i++) {
                                if (valueList.get (i).length () > 0) {
                                    str2.append (valueList.get (i));
                                    if (i != valueList.size () - 1) {
                                        str2.append (";;");
                                    }
                                }
                            }
    
                            if (db.isResponseExist (survey_id, question.getId ())) {
                                db.updateResponse (survey_id, question.getId (), question.getType (), str.toString (), str2.toString ());
                            } else {
                                db.insertResponse (survey_id, question.getId (), question.getType (), str.toString (), str2.toString ());
                            }
                        }
                    });
                    holder3.llOptions.addView (cbOption);
                }
                break;
            case 4:
                ViewHolder4 holder4 = (ViewHolder4) holder;
                Utils.setTypefaceToAllViews (activity, holder4.tvQuestion);
                holder4.tvQuestion.setText (question.getText ());
                if (db.isResponseExist (survey_id, question.getId ())) {
                    holder4.rbRating.setRating (Float.parseFloat (db.getResponseValue (survey_id, question.getId ())));
                }
        
                holder4.rbRating.setOnRatingBarChangeListener (new RatingBar.OnRatingBarChangeListener () {
                    @Override
                    public void onRatingChanged (RatingBar ratingBar, float rating, boolean fromUser) {
                        if (db.isResponseExist (survey_id, question.getId ())) {
                            db.updateResponse (survey_id, question.getId (), question.getType (), "", String.valueOf (rating));
                        } else {
                            db.insertResponse (survey_id, question.getId (), question.getType (), "", String.valueOf (rating));
                        }
                    }
                });
                break;
            case 5:
                ViewHolder5 holder5 = (ViewHolder5) holder;
                Utils.setTypefaceToAllViews (activity, holder5.tvQuestion);
                holder5.tvQuestion.setText (question.getText ());
                if (db.isResponseExist (survey_id, question.getId ())) {
                    holder5.seekBar.setProgress (Float.parseFloat (db.getResponseValue (survey_id, question.getId ())));
                }
        
                holder5.seekBar.setOnSeekChangeListener (new OnSeekChangeListener () {
                    @Override
                    public void onSeeking (SeekParams seekParams) {
                
                    }
            
                    @Override
                    public void onStartTrackingTouch (IndicatorSeekBar seekBar) {
                
                    }
            
                    @Override
                    public void onStopTrackingTouch (IndicatorSeekBar seekBar) {
                        if (db.isResponseExist (survey_id, question.getId ())) {
                            db.updateResponse (survey_id, question.getId (), question.getType (), "", String.valueOf (seekBar.getProgress ()));
                        } else {
                            db.insertResponse (survey_id, question.getId (), question.getType (), "", String.valueOf (seekBar.getProgress ()));
                        }
                    }
                });
                break;
            default:
                ViewHolder0 holder0 = (ViewHolder0) holder;
                Utils.setTypefaceToAllViews (activity, holder0.tvQuestion);
                holder0.tvQuestion.setText (question.getText ());
                break;
        }
    }
    
    @Override
    public int getItemCount () {
        return questionList.size ();
    }
    
    public void SetOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public class ViewHolder0 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        
        public ViewHolder0 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
    
    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        EditText etInput;
        
        public ViewHolder1 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            etInput = (EditText) view.findViewById (R.id.etInput);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
    
    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        RadioGroup rgOptions;
        RadioButton[] radioButtons;
        
        public ViewHolder2 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            rgOptions = (RadioGroup) view.findViewById (R.id.rgOptions);
            radioButtons = new RadioButton[100];
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
    
    public class ViewHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        LinearLayout llOptions;
        
        public ViewHolder3 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            llOptions = (LinearLayout) view.findViewById (R.id.llOptions);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
    
    public class ViewHolder4 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        RatingBar rbRating;
        
        public ViewHolder4 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            rbRating = (RatingBar) view.findViewById (R.id.rbRating);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
    
    public class ViewHolder5 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        IndicatorSeekBar seekBar;
        
        public ViewHolder5 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            seekBar = (IndicatorSeekBar) view.findViewById (R.id.seekBar);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
}