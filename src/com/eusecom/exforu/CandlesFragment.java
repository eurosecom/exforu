package com.eusecom.exforu;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eusecom.exforu.GetCandlesStreamAsyncTask.DoSomething;

import com.eusecom.exforu.animators.BaseItemAnimator;
import com.eusecom.exforu.animators.FadeInAnimator;
import com.eusecom.exforu.animators.FadeInDownAnimator;
import com.eusecom.exforu.animators.FadeInLeftAnimator;
import com.eusecom.exforu.animators.FadeInRightAnimator;
import com.eusecom.exforu.animators.FadeInUpAnimator;

public class CandlesFragment extends Fragment implements DoSomething, FragmentLifecycle {

	@SuppressWarnings("unused")
	private int page;
	@SuppressWarnings("unused")
	private String impless;
	private String pair;
	
	String userpsws;
	long useridl;
	String accountx;
	int repeat=60000;
	
	GetCandlesStreamAsyncTask GetCandlesStreamAsyncTask;
	
	enum Type {
        FadeIn(new FadeInAnimator()),
        FadeInDown(new FadeInDownAnimator()),
        FadeInUp(new FadeInUpAnimator()),
        FadeInLeft(new FadeInLeftAnimator()),
        FadeInRight(new FadeInRightAnimator()),
        //Landing(new LandingAnimator()),
        //ScaleIn(new ScaleInAnimator()),
        //ScaleInTop(new ScaleInTopAnimator()),
        //ScaleInBottom(new ScaleInBottomAnimator()),
        //ScaleInLeft(new ScaleInLeftAnimator()),
        //ScaleInRight(new ScaleInRightAnimator()),
        //FlipInTopX(new FlipInTopXAnimator()),
        //FlipInBottomX(new FlipInBottomXAnimator()),
        //FlipInLeftY(new FlipInLeftYAnimator()),
        //FlipInRightY(new FlipInRightYAnimator()),
        //SlideInLeft(new SlideInLeftAnimator()),
        //SlideInRight(new SlideInRightAnimator()),
        //SlideInDown(new SlideInDownAnimator()),
        //SlideInUp(new SlideInUpAnimator()),
        //OvershootInRight(new OvershootInRightAnimator()),
        //OvershootInLeft(new OvershootInLeftAnimator())
        ;

        private BaseItemAnimator mAnimator;

        Type(BaseItemAnimator animator) {
            mAnimator = animator;
        }

        public BaseItemAnimator getAnimator() {
            return mAnimator;
        }
    }

	protected CandlesFragAdapter adapter;
	protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    
	private List<String> myopenList = new ArrayList<String>();
	private List<String> mycloseList = new ArrayList<String>();
	private List<String> myhighList = new ArrayList<String>();
	private List<String> mylowList = new ArrayList<String>();
	private List<String> mytimeList = new ArrayList<String>();
	
	private List<String> tropenList = new ArrayList<String>();
	private List<String> trdruhList = new ArrayList<String>();

	String oplos="";
	String ophis="";
	String periodxy="D1";

    private SQLiteDatabase db4=null;
	private Cursor constantsCursor3=null;
	public MyViewAct viewact;
	
	double openda = 0;
    double closeda = 0;
    double highda = 0;
    double lowda = 0;
    long timea=0;
    
    TextView actprice;
    TextView actbalance;
	TextView actprofit;
	
	private SQLiteDatabase db8=null;
	private Cursor constantsCursor8=null;
	
	
    // newInstance constructor for creating fragment with arguments
    public static CandlesFragment newInstance(int page, String pairx, String implessx) {
    	CandlesFragment fragmentCandles = new CandlesFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("pairx", pairx);
        args.putString("importantless", implessx);
        fragmentCandles.setArguments(args);
        return fragmentCandles;
    }
    
    //broadcastreceiver for send value from activity to fragment ui
  	String KeyWord;
  	public static final String ACTION_INTENT = "com.eusecom.exforu.action.UI_UPDATE_PERIOD_CANDLES";
      protected BroadcastReceiver receiver = new BroadcastReceiver() {


          @Override
          public void onReceive(Context context, Intent intent) {
        	  Log.d("change ui", "I am at onReceive CandlesFragment.");
              if(ACTION_INTENT.equals(intent.getAction())) {
            	  
            	  	Bundle extras = intent.getExtras();

            	  	String value = extras.getString("UI_VALUE");
            	  	int xxsp = extras.getInt("UI_XXSP");

            	  	//problems with restarting GetCandlesStreamAsyncTask.java
            	  	updateUIOnReceiverValue(value, xxsp);
              }
          }
      };

      private void updateUIOnReceiverValue(String value, int xxxsp) {
          // you probably want this:
    	  String xxxsps=xxxsp + "";
    	  Log.d("value " + value, "xxsp " + xxxsps);

      }
      //end of broadcast

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
        pair = getArguments().getString("pairx");
        impless = getArguments().getString("importantless");
        
        accountx=SettingsActivity.getAccountx(getActivity());
        periodxy =SettingsActivity.getPeriodx(getActivity());
        
        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(getActivity()));
        	userpsws=SettingsActivity.getUserPsw(getActivity());
        }else{
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(getActivity()));
        	userpsws=SettingsActivity.getUserPswr(getActivity());
        }

        db4=(new DatabaseCandles(getActivity())).getWritableDatabase();
    	readSqlCandles();
    	
    	db8=(new DatabaseTrades(getActivity())).getWritableDatabase();
    	
    	//System.out.println("openlist: " + myopenList.toString());

        IntentFilter filter = new IntentFilter(ACTION_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        
        LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetCandlesStreamAsyncTask = new GetCandlesStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
        
    }
    //oncreate

    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candles, container, false);

        actprice = (TextView) view.findViewById(R.id.actprice);
        actbalance = (TextView) view.findViewById(R.id.actbalance);
        actprofit = (TextView) view.findViewById(R.id.actprofit);

        viewact = (MyViewAct) view.findViewById(R.id.viewact);
        
        openda = Double.parseDouble(myopenList.get(0))/100000 + Double.parseDouble(mycloseList.get(0))/100000;
        closeda = Double.parseDouble(mycloseList.get(0))/100000;
        highda = Double.parseDouble(myhighList.get(0))/100000;
        lowda = Double.parseDouble(mylowList.get(0))/100000;
        
        closeda = 0;
        highda = 0;
        lowda = 0;
        
        timea = Long.valueOf(mytimeList.get(0));
        updateViewact();
        
        //mytimeList.remove(0); myopenList.remove(0); mycloseList.remove(0);
	 	//myhighList.remove(0); mylowList.remove(0);
        
        recyclerView = (RecyclerView) view.findViewById(R.id.candlesrv);
        mLayoutManager = new LinearLayoutManager(getActivity());

    	((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayout.VERTICAL);

    	recyclerView.setLayoutManager(mLayoutManager);
    	//recyclerView.setItemAnimator(new FadeInAnimator());
    	adapter = new CandlesFragAdapter(getActivity(), mytimeList, myopenList, mycloseList, myhighList, mylowList, oplos, ophis, periodxy);
    	recyclerView.setAdapter(adapter);
    	recyclerView.setItemAnimator(new FadeInRightAnimator());
    	recyclerView.getItemAnimator().setAddDuration(300);
    	recyclerView.getItemAnimator().setRemoveDuration(300);
    
    	

        return view;
        
    }//oncreateview
    
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

        	try{
                // launch your AsyncTask here, if the task has not been executed yet
                if(GetCandlesStreamAsyncTask.getStatus().equals(AsyncTask.Status.PENDING)) {
                	
                	GetCandlesStreamAsyncTask.execute();
                }
            	} catch (NullPointerException ex) {

            	}
        }
    }
    

    @Override
    public void doChangeUI4(final double bidp, final double askp, final double sprd, final long timeax) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 
		    	 //System.out.println("Ask: " + askp + "Bid: " + bidp);

		         //closeda = bidp-openda+(sprd/10000);
		         closeda = bidp-openda;
		         if( closeda > highda ) { highda=closeda; }
		         if( closeda < lowda  ) { lowda=closeda;  }
		         timea=timeax;
		         

		         updateViewact();

		    }
		});
     
    }
    
    @Override
    public void doChangeUI3(final double balance, final double equity) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 String sbalances = balance + "";
		    	 actbalance.setText(sbalances);
		    	 
		    	 double profitd = equity - balance;
		    	 DecimalFormat df = new DecimalFormat("0.00");             	
             	 String sprofits = df.format(profitd);
             	 sprofits = sprofits.replace(',','.');

		    	 actprofit.setText(sprofits);

		    }
		});
     
    }
    
    @Override
    public void doChangeUI2(final List<String> myopenListx, final List<String> mycloseListx
    		,final List<String> myhighListx, final List<String> mylowListx, final List<String> mytimeListx) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 System.out.println("myopenListx: " + myopenListx.toString());
		    	 myopenList=myopenListx; mycloseList=mycloseListx; myhighList=myhighListx; mylowList=mylowListx;
		    	 mytimeList=mytimeListx;

		    }
		});
     
    }

    @Override
    public void doChangeUI(final List<String> myopenListx, final List<String> mycloseListx
    		,final List<String> myhighListx, final List<String> mylowListx, final List<String> mytimeListx) {

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 	myopenList=new ArrayList<String>(); mycloseList=new ArrayList<String>(); 
		    	 	myhighList=new ArrayList<String>(); mylowList=new ArrayList<String>(); 
		    	 	mytimeList=new ArrayList<String>();

		    	 	readSqlCandles();
		    	 	
		    	 	//openda = Double.parseDouble(myopenList.get(0))/100000;
		            //closeda = Double.parseDouble(mycloseList.get(0))/100000;
		            //highda = Double.parseDouble(myhighList.get(0))/100000;
		            //lowda = Double.parseDouble(mylowList.get(0))/100000;
		    	 	
		    	 	openda = Double.parseDouble(myopenList.get(0))/100000 + Double.parseDouble(mycloseList.get(0))/100000;
		            //closeda = Double.parseDouble(mycloseList.get(0))/100000;
		            //highda = Double.parseDouble(myhighList.get(0))/100000;
		            //lowda = Double.parseDouble(mylowList.get(0))/100000;
		            
		            closeda = 0;
		            highda = 0;
		            lowda = 0;
		            
		            timea = Long.valueOf(mytimeList.get(0));
		    	 	updateViewact();
		    	 
		    	 	//mytimeList.remove(0); myopenList.remove(0); mycloseList.remove(0);
		    	 	//myhighList.remove(0); mylowList.remove(0);
		    	 
		    	 	adapter = new CandlesFragAdapter(getActivity(), mytimeList, myopenList
		    	    		, mycloseList, myhighList, mylowList, oplos, ophis, periodxy);
		    	    recyclerView.setAdapter(adapter);
		    	    recyclerView.setItemAnimator(new FadeInRightAnimator());
		    	    recyclerView.getItemAnimator().setAddDuration(300);
		    	    recyclerView.getItemAnimator().setRemoveDuration(300);
		    	    adapter.notifyDataSetChanged();

		    }
		});
 
    }
    
    @Override
    public void doChangeUIerr(final String errs) {

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 Toast.makeText(getActivity(), errs, Toast.LENGTH_LONG).show();
		    }
		});

    }
    
    @Override
    public void doChangeUIpost(final String errs) {

    	sendValueToAct("A", 1);

    }
    
    //sending values from fragment to activity
  	protected void sendValueToAct(String value, int xxsp) {
          // it has to be the same name as in the fragment
          Intent intent = new Intent("com.eusecom.exforu.action.UI_UPDATE_AGAIN");
          Bundle dataBundle = new Bundle();
          dataBundle.putInt("UI_XXSP", xxsp);
          dataBundle.putString("UI_VALUE", value);
          intent.putExtras(dataBundle);
          
          Log.d("change again", "I am at sendValueToAct.");
          LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
          
        
      }
    
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		constantsCursor3.close();
		db4.close();
		constantsCursor8.close();
		db8.close();

		if (isOnline()) 
        {
		GetCandlesStreamAsyncTask.exit();
		GetCandlesStreamAsyncTask.cancel(true);
        }


	}
	//ondestroy
    
    
    //test if internet
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    //end test if internet
    
    //update viewact
    @SuppressLint("SimpleDateFormat")
	public boolean readtrades() {
    	
    	
    	tropenList=new ArrayList<String>(); trdruhList=new ArrayList<String>();

    	try {
    		
    		//read items except act price idruh=2
        	constantsCursor8=db8.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh " +
    				"FROM  trades WHERE idruh != '2' ORDER BY iopen DESC ",
    				null);
    		
            constantsCursor8.moveToFirst();
            while(!constantsCursor8.isAfterLast()) {
            	
            	tropenList.add(constantsCursor8.getString(constantsCursor8.getColumnIndex("iopen")));
            	trdruhList.add(constantsCursor8.getString(constantsCursor8.getColumnIndex("idruh")));

            	constantsCursor8.moveToNext();
            }

            

    		} catch (IllegalStateException ignored) {
    	    // There's no way to avoid getting this if saveInstanceState has already been called.
    		}
    	
    		//constantsCursor8.close();
    		//db8.close();
    	
    		if( trdruhList.size() == 0 ) {
    			tropenList.add("1.0");
    			trdruhList.add("0");
    		}
    	
        return false;
    }
    //read trades
    
    //update viewact
    @SuppressLint("SimpleDateFormat")
	public boolean updateViewact() {


        openda=round(openda,5); closeda=round(closeda,5); highda=round(highda,5); lowda=round(lowda,5);
        
        double actpricex=round((openda+closeda),5);
        String actprices=actpricex+"";
        actprice.setText(actprices);

        long dv = timea;
    	Date df = new java.util.Date(dv);
    	String vva = new SimpleDateFormat("dd.MM").format(df);
    	if( periodxy.equals("M1")) { vva = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("M5")) { vva = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("M15")) { vva = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("M30")) { vva = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("H1")) { vva = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("H4")) { vva = new SimpleDateFormat("HH:mm").format(df); }
        if( periodxy.equals("D1")) { vva = new SimpleDateFormat("dd.MM").format(df); }
        if( periodxy.equals("W1")) { vva = new SimpleDateFormat("dd.MM.yy").format(df); }
        if( periodxy.equals("MN1")) { vva = new SimpleDateFormat("MM#yy").format(df); }
        
        
        if(oplos.equals("")){ oplos="80000"; }
        if(ophis.equals("")){ ophis="140000"; }
        //System.out.println("oplos " + oplos);
        
        readtrades();

        viewact.setCandle(1, openda, closeda, highda, lowda, vva, oplos, ophis, tropenList, trdruhList);
        //System.out.println("openda " + openda);
        viewact.invalidate();
        
        return false;
    }
    //update viewact
    
    //update readSqlCandles
    public boolean readSqlCandles() {

    	try {
    	constantsCursor3=db4.rawQuery("SELECT _ID, time, open, close, high, low "+
				"FROM  candles WHERE _id > 0 ORDER BY time DESC ",
				null);
		
        constantsCursor3.moveToFirst();
        while(!constantsCursor3.isAfterLast()) {
        	
        	myopenList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("open")));
        	mycloseList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("close")));
        	myhighList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("high")));
        	mylowList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("low")));
        	mytimeList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("time")));

        	constantsCursor3.moveToNext();
        }
        

        constantsCursor3=db4.rawQuery("SELECT (open+low) AS oplo "+
 				"FROM  candles WHERE _id > 0 ORDER BY oplo ",
 				null);
     
        if (constantsCursor3.moveToFirst()) {
    	    oplos = constantsCursor3.getString(constantsCursor3.getColumnIndex("oplo"));
    	}
        //System.out.println("oplos " + oplos);
     
        constantsCursor3=db4.rawQuery("SELECT (open+high) AS ophi "+
 				"FROM  candles WHERE _id > 0 ORDER BY ophi DESC ",
 				null);

        if (constantsCursor3.moveToFirst()) {
    	    ophis = constantsCursor3.getString(constantsCursor3.getColumnIndex("ophi"));
    	}
        //System.out.println("ophis " + ophis);
        
        constantsCursor3.close();
    	} catch (IllegalStateException ignored) {
    	    // There's no way to avoid getting this if saveInstanceState has already been called.
    	}
    	
    	long unixTime = System.currentTimeMillis();
        String unixtimes=unixTime + "";
        if( myopenList.size() == 0 ) {
        	myopenList.add("1.0");
        	mycloseList.add("0.0");
        	myhighList.add("0.0");
        	mylowList.add("0.0");
        	mytimeList.add(unixtimes);
        }
        
        return false;
    }
    //update readSqlCandles
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
	public void onPauseFragment() {
				
		    	 Log.i("CandlesFragment", "onPauseFragment()");
		    	 Toast.makeText(getActivity(), "onPauseFragment():" + "CandlesFragment", Toast.LENGTH_SHORT).show();
		    	 constantsCursor3.close();
		 		 db4.close();
		 		 constantsCursor8.close();
		 		 db8.close();
		 		 

		 		if (isOnline()) 
		         {
		 		GetCandlesStreamAsyncTask.exit();
		 		GetCandlesStreamAsyncTask.cancel(true);
		         }

	}
	
	@Override
	public void onResumeFragment() {
		Log.i("CandlesFragment", "onResumeFragment()");
		Toast.makeText(getActivity(), "onResumeFragment():" + "CandlesFragment", Toast.LENGTH_SHORT).show();

			db4=(new DatabaseCandles(getActivity())).getWritableDatabase();
			readSqlCandles();
			db8=(new DatabaseTrades(getActivity())).getWritableDatabase();
    	
			LinkedList<String> listget = new LinkedList<String>();
			String symbolget = pair;
			listget.add(symbolget);

			GetCandlesStreamAsyncTask = new GetCandlesStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);	        
            GetCandlesStreamAsyncTask.execute();

	}
	
    
}