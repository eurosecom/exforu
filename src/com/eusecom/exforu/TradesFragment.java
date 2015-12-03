package com.eusecom.exforu;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
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

import com.eusecom.exforu.GetTradesStreamAsyncTask.DoSomething;

import com.eusecom.exforu.animators.BaseItemAnimator;
import com.eusecom.exforu.animators.FadeInAnimator;
import com.eusecom.exforu.animators.FadeInDownAnimator;
import com.eusecom.exforu.animators.FadeInLeftAnimator;
import com.eusecom.exforu.animators.FadeInRightAnimator;
import com.eusecom.exforu.animators.FadeInUpAnimator;

public class TradesFragment extends Fragment implements DoSomething, FragmentLifecycle {

	@SuppressWarnings("unused")
	private int page;
	@SuppressWarnings("unused")
	private String impless;
	private String pair;
	
	String userpsws;
	long useridl;
	String accountx;
	int repeat=60000;
	
	GetTradesStreamAsyncTask GetTradesStreamAsyncTask;
	
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

	protected TradesFragAdapter adapter;
	protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    
	private List<String> myopenList = new ArrayList<String>();
	private List<String> myvolumeList = new ArrayList<String>();
	private List<String> myorderList = new ArrayList<String>();
	private List<String> mysymbolList = new ArrayList<String>();
	private List<String> mytimeList = new ArrayList<String>();
	private List<String> mydruhList = new ArrayList<String>();

	String periodxy="D1";
	double openda;
	double closeda;
	double actpricex=1.09;
	double actprices=1.092;
	double actpriceb=1.088;

    private SQLiteDatabase db5=null;
	private Cursor constantsCursor3=null;
	public MyViewAct viewact;
	
    
    TextView actprice;
    TextView actbalance;
	TextView actprofit;


    // newInstance constructor for creating fragment with arguments
    public static TradesFragment newInstance(int page, String pairx, String implessx) {
    	TradesFragment fragmentTrades = new TradesFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("pairx", pairx);
        args.putString("importantless", implessx);
        fragmentTrades.setArguments(args);
        return fragmentTrades;
    }
    
    //broadcastreceiver for send value from activity to fragment ui
  	String KeyWord;
  	public static final String ACTION_INTENT = "com.eusecom.exforu.action.UI_UPDATE_PERIOD_TRADES";
      protected BroadcastReceiver receiver = new BroadcastReceiver() {


          @Override
          public void onReceive(Context context, Intent intent) {
        	  Log.d("change ui", "I am at onReceive TradesFragment.");
              if(ACTION_INTENT.equals(intent.getAction())) {
            	  
            	  	Bundle extras = intent.getExtras();

            	  	String value = extras.getString("UI_VALUE");
            	  	int xxsp = extras.getInt("UI_XXSP");

            	  	//problems with restarting GetTradesStreamAsyncTask.java
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

        db5=(new DatabaseTrades(getActivity())).getWritableDatabase();
    	readSqlTrades();
    	
    	//System.out.println("openlist: " + myopenList.toString());

        IntentFilter filter = new IntentFilter(ACTION_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
        
    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetTradesStreamAsyncTask = new GetTradesStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
        
    }
    //oncreate

    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trades, container, false);

        actprice = (TextView) view.findViewById(R.id.actprice);
        actbalance = (TextView) view.findViewById(R.id.actbalance);
        actprofit = (TextView) view.findViewById(R.id.actprofit);
        

        recyclerView = (RecyclerView) view.findViewById(R.id.tradesrv);
        mLayoutManager = new LinearLayoutManager(getActivity());

    	((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayout.VERTICAL);

    	recyclerView.setLayoutManager(mLayoutManager);
    	//recyclerView.setItemAnimator(new FadeInAnimator());
    	adapter = new TradesFragAdapter(getActivity(), mytimeList, myopenList, myvolumeList, myorderList,
    			mysymbolList, mydruhList, actpricex, actprices, actpriceb, periodxy);
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

        	doChangeUI();
        	
        	try{
            // launch your AsyncTask here, if the task has not been executed yet
            if(GetTradesStreamAsyncTask.getStatus().equals(AsyncTask.Status.PENDING)) {
            	
            	GetTradesStreamAsyncTask.execute();
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

		    	 openda=askp;
		         updateViewact();
		         
		         actpricex=askp; actprices=askp; actpriceb=bidp;
		         
		         //insert actual price
		         db5.delete("trades", "idruh = '2' ", null);
		         ContentValues cv5=new ContentValues();		    		
		    		cv5.put("itime", timeax);
		    		cv5.put("iopen", askp);
		    		cv5.put("ivolume", "0");
		    		cv5.put("iorder", "0");
		    		cv5.put("isymbol", pair);
		    		cv5.put("idruh", "2");
		    	 db5.insert("trades", "time", cv5);
		    		
		         
		         myopenList=new ArrayList<String>(); myvolumeList=new ArrayList<String>(); 
		    	 myorderList=new ArrayList<String>(); mysymbolList=new ArrayList<String>(); 
		    	 mytimeList=new ArrayList<String>(); mydruhList=new ArrayList<String>();

		    	 readSqlTrades();
		         
		         
		         adapter = new TradesFragAdapter(getActivity(), mytimeList, myopenList
		    	    		, myvolumeList, myorderList, mysymbolList, mydruhList, actpricex, actprices, actpriceb, periodxy);
		    	 recyclerView.setAdapter(adapter);
		    	 recyclerView.setItemAnimator(new FadeInRightAnimator());
		    	 recyclerView.getItemAnimator().setAddDuration(300);
		    	 recyclerView.getItemAnimator().setRemoveDuration(300);
		    	 adapter.notifyDataSetChanged();

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
    public void doChangeUI2(final List<String> myopenListx, final List<String> myvolumeListx
    		,final List<String> myorderListx, final List<String> mysymbolListx, final List<String> mytimeListx) {
    	//Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 System.out.println("myopenListx: " + myopenListx.toString());
		    	 myopenList=myopenListx; myvolumeList=myvolumeListx; myorderList=myorderListx; mysymbolList=mysymbolListx;
		    	 mytimeList=mytimeListx;

		    }
		});
     
    }

    @Override
    public void doChangeUI() {

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 	myopenList=new ArrayList<String>(); myvolumeList=new ArrayList<String>(); 
		    	 	myorderList=new ArrayList<String>(); mysymbolList=new ArrayList<String>(); 
		    	 	mytimeList=new ArrayList<String>(); mydruhList=new ArrayList<String>();

		    	 	readSqlTrades();

		    	 	adapter = new TradesFragAdapter(getActivity(), mytimeList, myopenList
		    	    		, myvolumeList, myorderList, mysymbolList, mydruhList, actpricex, actprices, actpriceb, periodxy);
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

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {


		    }
		});

    }
    
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		constantsCursor3.close();
		db5.close();

		if (isOnline()) 
        {
			GetTradesStreamAsyncTask.exit();
			GetTradesStreamAsyncTask.cancel(true);
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
	public boolean updateViewact() {


        openda=round(openda,5); closeda=round(closeda,5); 
        
        actpricex=round((openda+closeda),5);
        String actprices=actpricex+"";
        actprice.setText(actprices);
        
        return false;
    }
    //update viewact
    
    //update readSqlTrades
    public boolean readSqlTrades() {

    	myopenList=new ArrayList<String>(); myvolumeList=new ArrayList<String>(); 
	 	myorderList=new ArrayList<String>(); mysymbolList=new ArrayList<String>(); 
	 	mytimeList=new ArrayList<String>(); mydruhList=new ArrayList<String>();
	 	
    	try {
    		
    	//sum TP and SL	
    	String amount;
    	db5.delete("trades", "idruh = '3' ", null);
    	Cursor c = db5.rawQuery("select sum(ivolume) from trades where idruh = '0' GROUP BY idruh;", null);
    	if(c.moveToFirst())
    	    amount = c.getString(0);
    	else
    	    amount = "0";
    	c.close();
    	
    	ContentValues cv51=new ContentValues();		    		
   		cv51.put("itime", "0");
   		cv51.put("iopen", "1.12");
   		cv51.put("ivolume", amount);
   		cv51.put("iorder", "0");
   		cv51.put("isymbol", pair);
   		cv51.put("idruh", "3");
   		db5.insert("trades", "time", cv51);
    	
    	Cursor c2 = db5.rawQuery("select sum(ivolume) from trades where idruh = '1' GROUP BY idruh;", null);
    	if(c2.moveToFirst())
    	    amount = c2.getString(0);
    	else
    	    amount = "0";
    	c2.close();
    	
        ContentValues cv52=new ContentValues();		    		
   		cv52.put("itime", "0");
   		cv52.put("iopen", "1.12");
   		cv52.put("ivolume", amount);
   		cv52.put("iorder", "0");
   		cv52.put("isymbol", pair);
   		cv52.put("idruh", "3");
   		db5.insert("trades", "time", cv52);
    	
    	//read items	
    	constantsCursor3=db5.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh " +
				"FROM  trades WHERE _id >= 0 ORDER BY iopen DESC ",
				null);
		
        constantsCursor3.moveToFirst();
        while(!constantsCursor3.isAfterLast()) {
        	
        	myopenList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("iopen")));
        	myvolumeList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("ivolume")));
        	myorderList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("iorder")));
        	mysymbolList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("isymbol")));
        	mytimeList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("itime")));
        	mydruhList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("idruh")));

        	constantsCursor3.moveToNext();
        }

        constantsCursor3.close();
    	} catch (IllegalStateException ignored) {
    	    // There's no way to avoid getting this if saveInstanceState has already been called.
    	}
    	
    	long unixTime = System.currentTimeMillis();
        String unixtimes=unixTime + "";
        if( mydruhList.size() == 0 ) {
        	myopenList.add("1.0");
        	myvolumeList.add("0.0");
        	myorderList.add("0.0");
        	mysymbolList.add("0.0");
        	mytimeList.add(unixtimes);
        	mydruhList.add("0");
        }
        
        return false;
    }
    //update readSqlTrades
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
   	public void onPauseFragment() {
   		Log.i("TradesFragment", "onPauseFragment()");
   		Toast.makeText(getActivity(), "onPauseFragment():" + "TradesFragment", Toast.LENGTH_SHORT).show();
   		constantsCursor3.close();
		db5.close();

		if (isOnline()) 
        {
			GetTradesStreamAsyncTask.exit();
			GetTradesStreamAsyncTask.cancel(true);
        }
   	}
   	
   	@Override
   	public void onResumeFragment() {
   		Log.i("TradesFragment", "onResumeFragment()");
   		Toast.makeText(getActivity(), "onResumeFragment():" + "TradesFragment", Toast.LENGTH_SHORT).show();
   		
   		db5=(new DatabaseTrades(getActivity())).getWritableDatabase();
    	readSqlTrades();

    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetTradesStreamAsyncTask = new GetTradesStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
		GetTradesStreamAsyncTask.execute();
   	}

}