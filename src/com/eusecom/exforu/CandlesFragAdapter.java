package com.eusecom.exforu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Wasabeef on 2015/01/03.
 */
@SuppressLint("SimpleDateFormat")
public class CandlesFragAdapter extends RecyclerView.Adapter<CandlesFragAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mTime;
    private List<String> mopenPrice;
    private List<String> mclosePrice;
    private List<String> mhighPrice;
    private List<String> mlowPrice;
    
    private String oplos="";
    private String ophis="";
    private String periodxy="D1";

    public CandlesFragAdapter(Context context, List<String> timex, List<String> openPrice, 
    		List<String> closePrice, List<String> highPrice, List<String> lowPrice
    		, String oplosx, String ophisx, String period) {
        mContext = context;
        mTime = timex;
        mopenPrice = openPrice;
        mclosePrice = closePrice;
        mhighPrice = highPrice;
        mlowPrice = lowPrice;
        oplos=oplosx;
        ophis=ophisx;
        periodxy=period;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_list_candles, parent, false);
        return new ViewHolder(v);
    }


	@Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.openprice.setText(mopenPrice.get(position));
        holder.closeprice.setText(mclosePrice.get(position));
        holder.highprice.setText(mhighPrice.get(position));
        holder.lowprice.setText(mlowPrice.get(position));
        
        long dv = Long.valueOf(mTime.get(position));
    	Date df = new java.util.Date(dv);
    	//String vv = new SimpleDateFormat("dd.MM.yyyy hh:mma").format(df);
    	//String vv = new SimpleDateFormat("dd.MM. HH:mm").format(df);
    	String vv = new SimpleDateFormat("dd.MM").format(df);
    	if( periodxy.equals("M1")) { vv = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("M5")) { vv = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("M15")) { vv = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("M30")) { vv = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("H1")) { vv = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("H4")) { vv = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("D1")) { vv = new SimpleDateFormat("dd.MM").format(df); }
        if( periodxy.equals("W1")) { vv = new SimpleDateFormat("dd.MM.yy").format(df); }
        if( periodxy.equals("MN1")) { vv = new SimpleDateFormat("MM#yy").format(df); }
                
        holder.time.setText(vv);
        
        //holder.viewx.setWhatp(1);
        
        double opend = Double.parseDouble(mopenPrice.get(position))/100000;
        double closed = Double.parseDouble(mclosePrice.get(position))/100000;
        double highd = Double.parseDouble(mhighPrice.get(position))/100000;
        double lowd = Double.parseDouble(mlowPrice.get(position))/100000;
        
        opend=round(opend,5); closed=round(closed,5); highd=round(highd,5); lowd=round(lowd,5);
        

        holder.viewx.setCandle(1, opend, closed, highd, lowd, vv, oplos, ophis);
        holder.viewx.invalidate();
        
        holder.setClickListener(new CandlesFragAdapter.ViewHolder.ClickListener() {
            public void onClick(View v, int pos, boolean isLongClick) {
                if (isLongClick) {
         
                    // View v at position pos is long-clicked.
                	//String poslx = pos + "";
                	Toast.makeText(mContext, "longclick ", Toast.LENGTH_SHORT).show();
                	
                } else {
                    // View v at position pos is clicked.
                	//String possx = pos + "";
                	long dv = Long.valueOf(mTime.get(pos));
                	Date df = new java.util.Date(dv);
                	String vvx = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(df);

                	double opendx = Double.parseDouble(mopenPrice.get(pos))/100000;                	
                	opendx = round(opendx,5);
                	
                    double closedx = Double.parseDouble(mopenPrice.get(pos))/100000 + Double.parseDouble(mclosePrice.get(pos))/100000;
                    closedx = round(closedx,5);
                    double pips = (closedx - opendx)*10000;
                    pips = round(pips,0);
                    
                    String opends=opendx + ""; String closeds=closedx + "";
                	
                	Toast.makeText(mContext, vvx + "\n" + "open = " + opends +
                			" close = " + closeds + "\n" + pips + " pips", Toast.LENGTH_LONG).show();
                	//toggleSelection(pos);
                }
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return mTime.size();
    }

    public void remove(int position) {
        mTime.remove(position);
        notifyItemRemoved(position);
    }

    public void add(String text, int position) {
        mTime.add(position, text);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView openprice;
        public TextView closeprice;
        public TextView highprice;
        public TextView lowprice;
        public TextView time;
        public MyView viewx;
        private ClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            openprice = (TextView) itemView.findViewById(R.id.openprice);
            closeprice = (TextView) itemView.findViewById(R.id.closeprice);
            highprice = (TextView) itemView.findViewById(R.id.highprice);
            lowprice = (TextView) itemView.findViewById(R.id.lowprice);
            time = (TextView) itemView.findViewById(R.id.time);
            
            viewx = (MyView) itemView.findViewById(R.id.viewx);


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

        @SuppressWarnings("deprecation")
    	@Override
        public void onClick(View v) {

            // If not long clicked, pass last variable as false.
            clickListener.onClick(v, getPosition(), false);
        }

        @SuppressWarnings("deprecation")
    	@Override
        public boolean onLongClick(View v) {

            // If long clicked, passed last variable as true.
            clickListener.onClick(v, getPosition(), true);
            return true;
        }

  
    }//viewholder
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
}
