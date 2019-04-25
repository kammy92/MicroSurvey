package com.resultier.crux.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.resultier.crux.R;
import com.resultier.crux.listeners.OnItemClickListener;
import com.resultier.crux.models.Survey;
import com.resultier.crux.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private OnItemClickListener mItemClickListener;
    private ArrayList<Survey> surveyList;
    private Activity activity;
    
    public SurveyAdapter (Activity activity, ArrayList<Survey> surveyList) {
        this.surveyList = surveyList;
        this.activity = activity;
    }
    
    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from (parent.getContext ());
        final View sView = mInflater.inflate (R.layout.list_item_poll, parent, false);
        return new ViewHolder (sView);
    }
    
    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
        Survey survey = surveyList.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvPollQuestion);
        holder.tvPollTitle.setText (survey.getSurvey_title ());
        holder.tvPollQuestion.setText (survey.getSurvey_question ());
    
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse (survey.getSurvey_date ());
            holder.tvTime.setText ("" + TimeAgo.using (mDate.getTime ()));
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    
        switch (survey.getSurvey_status ()) {
            case 0:
                holder.vDot.setVisibility (View.VISIBLE);
                holder.llPoll.setAlpha (1.0f);
                break;
            case 1:
                holder.vDot.setVisibility (View.GONE);
                holder.llPoll.setAlpha (0.5f);
                break;
        }
    }
    
    public void setOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    @Override
    public int getItemCount () {
        return surveyList.size ();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout llPoll;
        private View vDot;
        private LinearLayout llResults;
        private TextView tvPollTitle;
        private TextView tvPollQuestion;
        private TextView tvTime;
        
        public ViewHolder (@NonNull View itemView) {
            super (itemView);
            vDot = (View) itemView.findViewById (R.id.vDot);
            llPoll = (LinearLayout) itemView.findViewById (R.id.llPoll);
            llResults = (LinearLayout) itemView.findViewById (R.id.llResults);
            tvPollTitle = (TextView) itemView.findViewById (R.id.tvPollTitle);
            tvPollQuestion = (TextView) itemView.findViewById (R.id.tvPollQuestion);
            tvTime = (TextView) itemView.findViewById (R.id.tvTime);
            itemView.setOnClickListener (this);
        }
        
        @Override
        public void onClick (View v) {
            mItemClickListener.onItemClick (v, getLayoutPosition ());
        }
    }
}
