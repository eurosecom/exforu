package com.eusecom.exforu;


import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Wasabeef on 2015/01/03.
 */
@SuppressWarnings("deprecation")
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mDataSet;


    public HistoryAdapter(Context context, List<String> dataSet, List<String> askPrice, 
    		List<String> bidPrice, List<String> Profit) {
    	
        mContext = context;
        mDataSet = dataSet;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {   	
        
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_list_history, parent, false);
        return new ViewHolder(v);
        
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(mContext).load(R.drawable.add2new).into(holder.image);
        holder.text.setText(mDataSet.get(position));

        
        holder.setClickListener(new HistoryAdapter.ViewHolder.ClickListener() {
            public void onClick(View v, int pos, boolean isLongClick) {
                if (isLongClick) {
         
                    // View v at position pos is long-clicked.
                	String poslx = pos + "";
                	String menax = mDataSet.get(pos);
                	Toast.makeText(mContext, "longclick pos. " + poslx + " pair " + menax, Toast.LENGTH_SHORT).show();
                	
                } else {
                    // View v at position pos is clicked.
                	//String possx = pos + "";
                	String poslx = pos + "";
                	String mena2 = mDataSet.get(pos);
                	Toast.makeText(mContext, "shortclick pos. " + poslx + " pair " + mena2, Toast.LENGTH_SHORT).show();
                	//toggleSelection(pos);
                	
                	Intent i = new Intent(mContext, LearningActivity.class);
                	Bundle extras = new Bundle();
                    extras.putString("pairx", mena2);
                    extras.putInt("whatspage", 0);
                    i.putExtras(extras);
                    v.getContext().startActivity(i);
                    //use localbroadcast or interface for finish activity and asynctask
                    //mActivity.finish(); don't work if i send activity to adapter

                }
            }
        });
        
    }
    


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void remove(int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

    public void add(String text, int position) {
        mDataSet.add(position, text);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView image;
        public TextView text;
        private ClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
            
            // We set listeners to the whole item view, but we could also
            // specify listeners for the title or the icon.
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            
        }
        
        
        /* Interface for handling clicks - both normal and long ones. */
        public interface ClickListener {

            /**
             * Called when the view is clicked.
             *
             * @param v view that is clicked
             * @param position of the clicked item
             * @param isLongClick true if long click, false otherwise
             */
            public void onClick(View v, int position, boolean isLongClick);

        }

        /* Setter for listener. */
        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

    	@Override
        public void onClick(View v) {

            // If not long clicked, pass last variable as false.
            clickListener.onClick(v, getPosition(), false);
        }

    	@Override
        public boolean onLongClick(View v) {

            // If long clicked, passed last variable as true.
            clickListener.onClick(v, getPosition(), true);
            return true;
        }

  
    }
}
