package com.eusecom.exforu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by Wasabeef on 2015/01/03.
 * Used by EuroSecom
 * iDruh 0=buy lightblue,1=sell redlight,2=actual price green
 */
@SuppressLint("SimpleDateFormat")
public class TradesFragAdapter extends RecyclerView.Adapter<TradesFragAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mTime;
    private List<String> miopenPrice;
    private List<String> miVolume;
    private List<String> miOrder;
    private List<String> miSymbol;
	private List<String> miDruh;
	private List<String> miComent;
	private List<String> miTp;
	private List<String> miSl;
    
	private double actprice;
	private double actprics;
	private double actpricb;
    @SuppressWarnings("unused")
	private String periodxy="D1";
    
    interface DoSomething2 {

        void doChangeItem(String itemx, String druhx, String orderx, String ivolx, String ipricex,
        		String icommx, String itpx, String islx );
        }

        DoSomething2 myDoSomething2CallBack;
    

    public TradesFragAdapter(DoSomething2 callback, Context context, List<String> timex, List<String> iopenPrice, 
    		List<String> iVolume, List<String> iOrder, List<String> iSymbol
    		, List<String> iDruh, double actpricex, double actpricesx, double actpricebx
    		, String period, List<String> iComent, List<String> iTp, List<String> iSl) {
        mContext = context;
        mTime = timex;
        miopenPrice = iopenPrice;
        miVolume = iVolume;
        miOrder = iOrder;
        miSymbol = iSymbol;
        miDruh = iDruh;
        actprice=actpricex;
        actprics=actpricesx;
        actpricb=actpricebx;
        periodxy=period;        
        miComent = iComent;
        miTp = iTp;
        miSl = iSl;
        myDoSomething2CallBack = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_list_trades, parent, false);
        return new ViewHolder(v);
    }


	@SuppressWarnings("deprecation")
	@Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.iopenprice.setText(miopenPrice.get(position));
        try{
        holder.ivolume.setText(miVolume.get(position));
        }
        catch (NullPointerException nullPointer)
        {
        	System.out.println("NPE " +  nullPointer);
        }
        holder.iorder.setText(miOrder.get(position));
        String comentx="";
        try{
        	comentx=miComent.get(position);       	
            }
            catch (NullPointerException nullPointer)
            {
            	System.out.println("NPE " +  nullPointer);
            }
        if(comentx != null && !comentx.isEmpty()){}else{comentx="-";}
        if(comentx.length() <= 0 ) { comentx="-"; }
        comentx = comentx + " " + miSymbol.get(position);
        holder.isymbol.setText(comentx);
        
        
        long dv = Long.valueOf(mTime.get(position));
    	Date df = new java.util.Date(dv);
    	//String vv = new SimpleDateFormat("dd.MM.yyyy hh:mma").format(df);
    	String vv = new SimpleDateFormat("dd.MM HH:mm").format(df);
                
        holder.itime.setText(vv);
        
        double volumed=Double.parseDouble(miVolume.get(position));
        
        double vopenpd=0d;
        try{
        vopenpd=Double.parseDouble(miopenPrice.get(position));
        }
        catch (NullPointerException nullPointer)
        {
        	System.out.println("NPE " +  nullPointer);
        }
        double pricedifd=0d;

        int iidruh = Integer.parseInt(miDruh.get(position));
        
        switch(iidruh) {
        case 0:
        	
        	holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_bluelight) );
        	holder.iorder.setText(miOrder.get(position)+ " Buy");
        	pricedifd=actpricb-vopenpd;
        	String actpricbs=actpricb + "";
        	holder.iactprice.setText(actpricbs);
        	holder.isltp.setText(miSl.get(position) + " / " + miTp.get(position));
        	
        break;
        case 1:

        	holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_redlight) );
        	holder.iorder.setText(miOrder.get(position)+ " Sell");
        	pricedifd=vopenpd-actprics;
        	String actpricss=actprics + "";
        	holder.iactprice.setText(actpricss);
        	holder.isltp.setText(miSl.get(position) + " / " + miTp.get(position));
        	
        break;
        
        case 2:
        		//holder.iorder.setVisibility(View.GONE);
        		holder.iprofit.setVisibility(View.GONE);
        		holder.ivolume.setVisibility(View.GONE);
        		holder.txtvolume.setVisibility(View.INVISIBLE);
        		
        		holder.txtactprice.setVisibility(View.GONE);
            	holder.iactprice.setVisibility(View.GONE);
            	holder.txtsltp.setVisibility(View.GONE);
            	holder.isltp.setVisibility(View.GONE);
        		
        		holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_lightgreen) );
            	holder.iorder.setText("Actual Price");
            	pricedifd=0;
        break;
        
        case 3:
        	//holder.iorder.setVisibility(View.GONE);
        	holder.iprofit.setVisibility(View.GONE);
        	holder.itime.setVisibility(View.INVISIBLE);
        	
        	holder.txtactprice.setVisibility(View.GONE);
        	holder.iactprice.setVisibility(View.GONE);
        	holder.txtsltp.setVisibility(View.GONE);
        	holder.isltp.setVisibility(View.GONE);
        	
    		holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_yellow) );
        	holder.iorder.setText("TP Buy");
        	pricedifd=0;
        break;
        
        case 4:
        	//holder.iorder.setVisibility(View.GONE);
        	holder.iprofit.setVisibility(View.GONE);
        	holder.itime.setVisibility(View.INVISIBLE);
        	
        	holder.txtactprice.setVisibility(View.GONE);
        	holder.iactprice.setVisibility(View.GONE);
        	holder.txtsltp.setVisibility(View.GONE);
        	holder.isltp.setVisibility(View.GONE);
        	
    		holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_yellow) );
        	holder.iorder.setText("TP Sell");
        	pricedifd=0;
        break;
        
        case 5:
        	//holder.iorder.setVisibility(View.GONE);
        	holder.iprofit.setVisibility(View.GONE);
        	holder.itime.setVisibility(View.INVISIBLE);
        	
        	holder.txtactprice.setVisibility(View.GONE);
        	holder.iactprice.setVisibility(View.GONE);
        	holder.txtsltp.setVisibility(View.GONE);
        	holder.isltp.setVisibility(View.GONE);
        	
    		holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_lightbrown) );
        	holder.iorder.setText("SL Buy");
        	pricedifd=0;
        break;
        
        case 6:
        	//holder.iorder.setVisibility(View.GONE);
        	holder.iprofit.setVisibility(View.GONE);
        	holder.itime.setVisibility(View.INVISIBLE);
        	
        	holder.txtactprice.setVisibility(View.GONE);
        	holder.iactprice.setVisibility(View.GONE);
        	holder.txtsltp.setVisibility(View.GONE);
        	holder.isltp.setVisibility(View.GONE);
        	
    		holder.itemView.setBackgroundDrawable( mContext.getResources().getDrawable(R.drawable.border_listtrades_lightbrown) );
        	holder.iorder.setText("SL Sell");
        	pricedifd=0;
        break;
		}
 
        if( actprice > 0 ) {
        //System.out.println("actprice " + actprice);
        double profitd=100000*pricedifd*volumed;
        double profitd2=profitd/actprice;
        profitd2=round(profitd2,2);
        String profits=profitd2 + "";
        holder.iprofit.setText(profits);
        
        }//end of priceb > 0
        
        setAnimation(holder.relLayout, position);

        
        holder.setClickListener(new TradesFragAdapter.ViewHolder.ClickListener() {
            public void onClick(View v, int pos, boolean isLongClick) {
            	
            	String idruhx = "9"; String iorderx = "0"; String ivolumex = "0"; String ipricex = "0"; String icommx = "";
            	String itpx = ""; String islx = "";
            	try{
            	idruhx = miDruh.get(pos);
            	iorderx = miOrder.get(pos);
            	ivolumex = miVolume.get(pos);
            	ipricex = miopenPrice.get(pos);
            	icommx = miComent.get(pos);
            	itpx = miTp.get(pos);
            	islx = miSl.get(pos);
            	}
            	catch (Exception e)
                {
                	System.out.println("Exception " +  e);
                }
            	
                if (isLongClick) {
         
                    // View v at position pos is long-clicked.
                	String poslx = pos + "";
                	Toast.makeText(mContext, "longclick " + poslx, Toast.LENGTH_SHORT).show();
                	if(idruhx.equals("0") || idruhx.equals("1") ) {
                	myDoSomething2CallBack.doChangeItem(poslx, idruhx, iorderx, ivolumex, ipricex, icommx, itpx, islx);
                	System.out.println("...context is called");
                		try{
                		v.showContextMenu();
                        }
                        catch (NullPointerException nullPointer)
                        {
                        	System.out.println("NPE showContextMenu" +  nullPointer);
                        }
                		
                	}
                	
                	
                } else {
                    // View v at position pos is clicked.
                	String poslx = pos + "";
                	Toast.makeText(mContext, "shortclick ", Toast.LENGTH_SHORT).show();
                	if(idruhx.equals("0") || idruhx.equals("1") ) {
                	myDoSomething2CallBack.doChangeItem(poslx, idruhx, iorderx, ivolumex, ipricex, icommx, itpx, islx);
                	System.out.println("...context is called");
                		try{
                		v.showContextMenu();
                        }
                        catch (NullPointerException nullPointer)
                        {
                        	System.out.println("NPE showContextMenu" +  nullPointer);
                        }
                		
                	}
                }
            }
        });
        
    }
	
	//@SuppressWarnings("unused")
	private void setAnimation(View viewToAnimate, int position)
	{

	        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
	        viewToAnimate.startAnimation(animation);

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

        public TextView iopenprice;
        public TextView ivolume;
        public TextView isymbol;
        public TextView iorder;
        public TextView itime;
        public TextView iprofit;
        public RelativeLayout relLayout;
        public TextView txtvolume;
        //public MyView viewx;
        
        public TextView txtactprice;
        public TextView iactprice;
        public TextView txtsltp;
        public TextView isltp;
        
        
        private ClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);

            itime = (TextView) itemView.findViewById(R.id.itime);
            iorder = (TextView) itemView.findViewById(R.id.iorder);
            iopenprice = (TextView) itemView.findViewById(R.id.iopenprice);
            ivolume = (TextView) itemView.findViewById(R.id.ivolume);
            isymbol = (TextView) itemView.findViewById(R.id.isymbol);
            iprofit = (TextView) itemView.findViewById(R.id.iprofit);
            
            relLayout = (RelativeLayout) itemView.findViewById(R.id.relLayout);
            
            txtvolume = (TextView) itemView.findViewById(R.id.txtvolume);
            
            txtactprice = (TextView) itemView.findViewById(R.id.txtactprice);
            iactprice = (TextView) itemView.findViewById(R.id.iactprice);
            txtsltp = (TextView) itemView.findViewById(R.id.txtsltp);
            isltp = (TextView) itemView.findViewById(R.id.isltp);
            
            
            //viewx = (MyView) itemView.findViewById(R.id.viewx);


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
