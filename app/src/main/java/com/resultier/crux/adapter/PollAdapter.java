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
import com.resultier.crux.models.Poll;
import com.resultier.crux.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.ViewHolder> {
    private OnItemClickListener mItemClickListener;
    private ArrayList<Poll> pollList;
    private Activity activity;
    
    public PollAdapter (Activity activity, ArrayList<Poll> pollList) {
        this.pollList = pollList;
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
        Poll poll = pollList.get (position);
        Utils.setTypefaceToAllViews (activity, holder.tvPollQuestion);
        holder.tvPollTitle.setText (poll.getPoll_title ());
        holder.tvPollQuestion.setText (poll.getPoll_question ());
        
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        try {
            Date mDate = sdf.parse (poll.getPoll_date ());
            holder.tvTime.setText ("" + TimeAgo.using (mDate.getTime ()) + " at " + Utils.convertTimeFormat (poll.getPoll_date (), "yyyy-MM-dd HH:mm:ss", "HH:mm"));
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        
        switch (poll.getPoll_status ()) {
            case 0:
                holder.llResults.setAlpha (0.5f);
                break;
            case 1:
                holder.llResults.setAlpha (1.0f);
                break;
        }
    }
    
    public void setOnItemClickListener (final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    @Override
    public int getItemCount () {
        return pollList.size ();
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout llResults;
        private TextView tvPollTitle;
        private TextView tvPollQuestion;
        private TextView tvTime;
        
        public ViewHolder (@NonNull View itemView) {
            super (itemView);
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
