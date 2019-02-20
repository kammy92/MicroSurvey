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

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    OnItemClickListener mItemClickListener;
    SurveyActivity surveyActivity;
    ProgressDialog progressDialog;
    DatabaseHandler db;
    int poll_id = 0;
    private Activity activity;
    private ArrayList<Question> questionList = new ArrayList<> ();
    
    public QuestionAdapter (Activity activity, ArrayList<Question> questionList, int poll_id) {
        this.activity = activity;
        this.questionList = questionList;
        this.poll_id = poll_id;
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
                return new ViewHolder7 (sView);
            case 2:
                sView = mInflater.inflate (R.layout.question_type_radio, parent, false);
                return new ViewHolder8 (sView);
            case 3:
                sView = mInflater.inflate (R.layout.question_type_checkbox, parent, false);
                return new ViewHolder9 (sView);
            default:
                sView = mInflater.inflate (R.layout.question_type_plain_text, parent, false);
                return new ViewHolder3 (sView);
        }
    }
    
    @Override
    public void onBindViewHolder (final RecyclerView.ViewHolder holder, int position) {
        final Question question = questionList.get (position);
        holder.setIsRecyclable (false);
        switch (holder.getItemViewType ()) {
            case 1:
                final ViewHolder7 holder7 = (ViewHolder7) holder;
                holder7.setIsRecyclable (false);
                Utils.setTypefaceToAllViews (activity, holder7.tvQuestion);
                holder7.tvQuestion.setText (question.getText ());
                
                if (db.isResponseExist (poll_id, question.getId ())) {
                    holder7.etInput.setText (db.getResponseValue (poll_id, question.getId ()));
                }
                
                holder7.etInput.setInputType (InputType.TYPE_CLASS_TEXT);
                holder7.etInput.addTextChangedListener (new TextWatcher () {
                    @Override
                    public void onTextChanged (CharSequence s, int start, int before, int count) {
                        if (db.isResponseExist (poll_id, question.getId ())) {
                            db.updateResponse (poll_id, question.getId (), question.getType (), "", s.toString ());
                        } else {
                            db.insertResponse (poll_id, question.getId (), question.getType (), "", s.toString ());
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
                ViewHolder8 holder8 = (ViewHolder8) holder;
                holder8.setIsRecyclable (false);
                Utils.setTypefaceToAllViews (activity, holder8.tvQuestion);
                holder8.tvQuestion.setText (question.getText ());
                holder8.rgOptions.removeAllViews ();
                int option_id8 = 0;
                if (db.isResponseExist (poll_id, question.getId ())) {
                    option_id8 = Integer.parseInt (db.getResponseID (poll_id, question.getId ()));
                }
                
                String selected_option = "";
                holder8.radioButtons = new RadioButton[question.getOptions ().size ()];
                for (int i = 0; i < question.getOptions ().size (); i++) {
                    Option option = question.getOptions ().get (i);
                    holder8.radioButtons[i] = new RadioButton (activity);
                    holder8.radioButtons[i].setId (option.getId ());
                    holder8.radioButtons[i].setText (option.getValue ());
                    holder8.radioButtons[i].setTag (i);
                    holder8.radioButtons[i].setTextSize (TypedValue.COMPLEX_UNIT_SP, 14);
                    holder8.radioButtons[i].setTypeface (SetTypeFace.getTypeface (activity));
                    if (option.getId () == option_id8) {
                        holder8.radioButtons[i].setChecked (true);
                        selected_option = option.getValue ();
                    }
                    
                    holder8.rgOptions.addView (holder8.radioButtons[i]);
                }
                
                holder8.rgOptions.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener () {
                    @Override
                    public void onCheckedChanged (RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) group.findViewById (checkedId);
                        String selected_option = radioButton.getText ().toString ();
                        
                        if (db.isResponseExist (poll_id, question.getId ())) {
                            db.updateResponse (poll_id, question.getId (), question.getType (), String.valueOf (checkedId), selected_option);
                        } else {
                            db.insertResponse (poll_id, question.getId (), question.getType (), String.valueOf (checkedId), selected_option);
                        }
                    }
                });
                break;
            case 3:
                ViewHolder9 holder9 = (ViewHolder9) holder;
                holder9.setIsRecyclable (false);
                Utils.setTypefaceToAllViews (activity, holder9.tvQuestion);
                holder9.tvQuestion.setText (question.getText ());
                
                String option_id9 = "";
                String option_value9 = "";
                if (db.isResponseExist (poll_id, question.getId ())) {
                    option_id9 = db.getResponseID (poll_id, question.getId ());
                    option_value9 = db.getResponseValue (poll_id, question.getId ());
                }
                
                String[] ops = option_id9.split (";;");
                String[] ops2 = option_value9.split (";;");
                
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
                
                holder9.llOptions.removeAllViews ();
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
                            
                            if (db.isResponseExist (poll_id, question.getId ())) {
                                db.updateResponse (poll_id, question.getId (), question.getType (), str.toString (), str2.toString ());
                            } else {
                                db.insertResponse (poll_id, question.getId (), question.getType (), str.toString (), str2.toString ());
                            }
                        }
                    });
                    holder9.llOptions.addView (cbOption);
                }
                break;
            default:
                ViewHolder3 holder3d = (ViewHolder3) holder;
                Utils.setTypefaceToAllViews (activity, holder3d.tvQuestion);
                holder3d.tvQuestion.setText (question.getText ());
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
    
    public class ViewHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        
        public ViewHolder3 (View view) {
            super (view);
            tvQuestion = (TextView) view.findViewById (R.id.tvQuestion);
            view.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
    
    public class ViewHolder7 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        EditText etInput;
        
        public ViewHolder7 (View view) {
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
    
    public class ViewHolder8 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        RadioGroup rgOptions;
        RadioButton[] radioButtons;
        
        public ViewHolder8 (View view) {
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
    
    public class ViewHolder9 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvQuestion;
        LinearLayout llOptions;
        
        public ViewHolder9 (View view) {
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
}