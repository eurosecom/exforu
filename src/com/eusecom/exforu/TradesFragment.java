package com.eusecom.exforu;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eusecom.exforu.GetTradesStreamAsyncTask.DoSomething;
import com.eusecom.exforu.TradesFragAdapter.DoSomething2;

import com.eusecom.exforu.animators.BaseItemAnimator;
import com.eusecom.exforu.animators.FadeInAnimator;
import com.eusecom.exforu.animators.FadeInDownAnimator;
import com.eusecom.exforu.animators.FadeInLeftAnimator;
import com.eusecom.exforu.animators.FadeInRightAnimator;
import com.eusecom.exforu.animators.FadeInUpAnimator;



public class TradesFragment extends Fragment implements DoSomething, DoSomething2, FragmentLifecycle {

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
	private List<String> mycomentList = new ArrayList<String>();
	private List<String> mytpList = new ArrayList<String>();
	private List<String> myslList = new ArrayList<String>();

	String periodxy="D1";
	double openda;
	double closeda;
	double actpricex=0;
	double actprices=0;
	double actpriceb=0;

    private SQLiteDatabase db5=null;
    private SQLiteDatabase db6=null;
	private Cursor constantsCursor3=null;
	public MyViewAct viewact;
	
    
    TextView actprice;
    TextView actbalance;
	TextView actprofit;
	
	private SQLiteDatabase db7=null;
	String senditem, senddruh, sendvolume, sendprice, sendcomm;
	Long sendorder;
	String sendtp="0", sendsl="0";
	String profitmodel="0";

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
        }
        if( accountx.equals("2")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(getActivity()));
        	userpsws=SettingsActivity.getUserPsw(getActivity());
        }
        if( accountx.equals("1")) {
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(getActivity()));
        	userpsws=SettingsActivity.getUserPswr(getActivity());
        }
        
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
    	adapter = new TradesFragAdapter(TradesFragment.this, getActivity(), mytimeList, myopenList, myvolumeList, myorderList,
    			mysymbolList, mydruhList, actpricex, actprices, actpriceb, periodxy, mycomentList, mytpList, myslList);
    	recyclerView.setAdapter(adapter);
    	recyclerView.setItemAnimator(new FadeInRightAnimator());
    	recyclerView.getItemAnimator().setAddDuration(300);
    	recyclerView.getItemAnimator().setRemoveDuration(300);
    	registerForContextMenu(recyclerView);

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
    public void doChangeItem(final String itemx, final String idruhx, final String iorderx
    		, final String ivolumex, final String ipricex, final String icommx, final String itpx, final String islx) {

    	try{
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 
		    	 if (isOnline()) 
		            {
		    			GetTradesStreamAsyncTask.exit();
		    			GetTradesStreamAsyncTask.cancel(true);
		            }
		    	 sendValueToAct("C", 3);

		    	 senditem=itemx; senddruh=idruhx; sendorder=Long.parseLong(iorderx); 
		    	 sendvolume=ivolumex; sendprice=ipricex; sendcomm=icommx;
		    	 sendtp=itpx; sendsl=islx;
		    	 Toast.makeText(getActivity(), "sendstrx " + itemx, Toast.LENGTH_SHORT).show();

		    }
		});
    	
    	}
		catch (NullPointerException nullPointer)
		{
		System.out.println("NPE TradesFragment.java uiitem" +  nullPointer);
		}
     
    }
    
    @Override
    public void doChangeUI4(final double bidp, final double askp, final double sprd, final long timeax) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	
    	try{
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 openda=askp;
		         updateViewact();
		         
		         actpricex=askp; actprices=askp; actpriceb=bidp;
		         
		         try{
		         db6=(new DatabaseTrades(getActivity())).getWritableDatabase();
		         //insert actual price
		         db6.delete("trades", "idruh = '2' ", null);
		         ContentValues cv5=new ContentValues();		    		
		    		cv5.put("itime", timeax);
		    		cv5.put("iopen", askp);
		    		cv5.put("ivolume", "0");
		    		cv5.put("iorder", "0");
		    		cv5.put("isymbol", pair);
		    		cv5.put("idruh", "2");
		    		cv5.put("itp", "0");
		    		cv5.put("isl", "0");
		    		cv5.put("imemo", " ");
		    	 db6.insert("trades", "itime", cv5);
		    	 db6.close();
		         }
                 catch (NullPointerException nullPointer)
                 {
                 	System.out.println("NPE TradesFragment.java " +  nullPointer);
                 }
		    		
		         
		         myopenList=new ArrayList<String>(); myvolumeList=new ArrayList<String>(); 
		    	 myorderList=new ArrayList<String>(); mysymbolList=new ArrayList<String>(); 
		    	 mytimeList=new ArrayList<String>(); mydruhList=new ArrayList<String>();

		    	 readSqlTrades();
		         
		         
		         adapter = new TradesFragAdapter(TradesFragment.this, getActivity(), mytimeList, myopenList
		    	    		, myvolumeList, myorderList, mysymbolList, mydruhList, actpricex, actprices
		    	    		, actpriceb, periodxy, mycomentList, mytpList, myslList);
		    	 recyclerView.setAdapter(adapter);
		    	 recyclerView.setItemAnimator(new FadeInRightAnimator());
		    	 recyclerView.getItemAnimator().setAddDuration(300);
		    	 recyclerView.getItemAnimator().setRemoveDuration(300);
		    	 adapter.notifyDataSetChanged();

		    }
		});
    	
    	}
		catch (NullPointerException nullPointer)
		{
		System.out.println("NPE TradesFragment.java ui4 " +  nullPointer);
		}
     
    }
    
    @Override
    public void doChangeUI3(final double balance, final double equity) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	
    	try{
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 if( accountx.equals("0") || accountx.equals("1") ) {
		    		 
		    	 String sbalances = balance + "";
		    	 actbalance.setText(sbalances);
		    	 
		    	 double profitd = equity - balance;
		    	 DecimalFormat df = new DecimalFormat("0.00");             	
             	 String sprofits = df.format(profitd);
             	 sprofits = sprofits.replace(',','.');


		    	 actprofit.setText(sprofits);
		    	 
		    	 }
		    	 
		    	 if( accountx.equals("2") ) {
		    		 
			    	 actprofit.setText(profitmodel);
			    	 
		    	 }

		    }
		});
    	
    	}
		catch (NullPointerException nullPointer)
		{
		System.out.println("NPE TradesFragment.java ui3 " +  nullPointer);
		}
     
    }
    
    @Override
    public void doChangeUI2(final List<String> myopenListx, final List<String> myvolumeListx
    		,final List<String> myorderListx, final List<String> mysymbolListx, final List<String> mytimeListx) {
    	//Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	
    	try{
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 System.out.println("myopenListx: " + myopenListx.toString());
		    	 myopenList=myopenListx; myvolumeList=myvolumeListx; myorderList=myorderListx; mysymbolList=mysymbolListx;
		    	 mytimeList=mytimeListx;

		    }
		});
    	
    	}
		catch (NullPointerException nullPointer)
		{
		System.out.println("NPE TradesFragment.java ui2 " +  nullPointer);
		}
     
    }

    @Override
    public void doChangeUI() {

    	try{
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 	myopenList=new ArrayList<String>(); myvolumeList=new ArrayList<String>(); 
		    	 	myorderList=new ArrayList<String>(); mysymbolList=new ArrayList<String>(); 
		    	 	mytimeList=new ArrayList<String>(); mydruhList=new ArrayList<String>();

		    	 	readSqlTrades();

		    	 	adapter = new TradesFragAdapter(TradesFragment.this, getActivity(), mytimeList, myopenList
		    	    		, myvolumeList, myorderList, mysymbolList, mydruhList, actpricex
		    	    		, actprices, actpriceb, periodxy, mycomentList, mytpList, myslList);
		    	    recyclerView.setAdapter(adapter);
		    	    recyclerView.setItemAnimator(new FadeInRightAnimator());
		    	    recyclerView.getItemAnimator().setAddDuration(300);
		    	    recyclerView.getItemAnimator().setRemoveDuration(300);
		    	    adapter.notifyDataSetChanged();

		    }
		});
    	
    	}
		catch (NullPointerException nullPointer)
		{
		System.out.println("NPE TradesFragment.java ui " +  nullPointer);
		}
 
    }
    
    @Override
    public void doChangeUIerr(final String errs) {

    	try{
    		getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 Toast.makeText(getActivity(), errs, Toast.LENGTH_LONG).show();

		    }
    		});
    	
    	}
    	catch (NullPointerException nullPointer)
    	{
		System.out.println("NPE TradesFragment.java uierr " +  nullPointer);
    	}

    }
    
    @Override
    public void doChangeUIpost(final String errs) {

    	try{
    		getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 sendValueToAct("C", 3);
		    	 
		     }
    		});
    	}
		catch (NullPointerException nullPointer)
		{
			System.out.println("NPE TradesFragment.java uipost " +  nullPointer);
		}

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

    	try{
    	db5=(new DatabaseTrades(getActivity())).getWritableDatabase();
    	
    	myopenList=new ArrayList<String>(); myvolumeList=new ArrayList<String>(); 
	 	myorderList=new ArrayList<String>(); mysymbolList=new ArrayList<String>(); 
	 	mytimeList=new ArrayList<String>(); mydruhList=new ArrayList<String>();
	 	mycomentList=new ArrayList<String>(); mytpList=new ArrayList<String>();
	 	myslList=new ArrayList<String>();
	 	
    		
    	//sum TP za Buys	
    	String amount="0"; String tpprice="0"; String slprice="0"; String memox=" ";
    	db5.delete("trades", "idruh = '3' ", null);
    	Cursor c = db5.rawQuery("select sum(ivolume), itp, imemo from trades where idruh = '0' GROUP BY imemo,itp;", null);
    	double amountd3 = 0;
    	if(c.moveToFirst()){    		
    		while(!c.isAfterLast()) 
    		{
    			amount = c.getString(0);
    			tpprice = c.getString(1);
    			memox = c.getString(2);
    			
    			if (tpprice == null ) {tpprice="0";}
    	    	amountd3 = Double.parseDouble(tpprice);
    	    	if(amountd3 > 0 ) {
    	    	ContentValues cv51=new ContentValues(); 
    	   		cv51.put("itime", "0");
    	   		cv51.put("iopen", tpprice);
    	   		cv51.put("ivolume", amount);
    	   		cv51.put("iorder", "0");
    	   		cv51.put("isymbol", pair);
    	   		cv51.put("idruh", "3");
    	   		cv51.put("itp", "0");
    			cv51.put("isl", "0");
    			cv51.put("imemo", memox);
    	   		db5.insert("trades", "itime", cv51);
    	    	}
    	    
    	    c.moveToNext();
    		}
    	    
    	}
    	c.close();
    	 	
   		//sum TP za Sells
    	amount="0"; tpprice="0"; slprice="0"; memox=" ";
   		db5.delete("trades", "idruh = '4' ", null);
    	Cursor c2 = db5.rawQuery("select sum(ivolume), itp, imemo  from trades where idruh = '1' GROUP BY imemo,itp;", null);
    	double amountd4 = 0;
    	if(c2.moveToFirst()){    		
    		while(!c2.isAfterLast()) 
    		{
    			amount = c2.getString(0);
    			tpprice = c2.getString(1);
    			//Log.i("c2", c2.getString(1));
    			memox = c2.getString(2);
    			
    			if (tpprice == null ) {tpprice="0";}
    	    	amountd4 = Double.parseDouble(tpprice);
    	    	if(amountd4 > 0 ) {
    	    	ContentValues cv52=new ContentValues(); 
    	   		cv52.put("itime", "0");
    	   		cv52.put("iopen", tpprice);
    	   		cv52.put("ivolume", amount);
    	   		cv52.put("iorder", "0");
    	   		cv52.put("isymbol", pair);
    	   		cv52.put("idruh", "4");
    	   		cv52.put("itp", "0");
    			cv52.put("isl", "0");
    			cv52.put("imemo", memox);
    	   		db5.insert("trades", "itime", cv52);
    	    	}
    			
    	    
    	    c2.moveToNext();
    		}
    	    
    	}
    	c2.close();
    	
    	
   		
   		//sum SL za Buys
    	amount="0"; tpprice="0"; slprice="0"; memox=" ";
    	db5.delete("trades", "idruh = '5' ", null);
    	Cursor c3 = db5.rawQuery("select sum(ivolume), isl, imemo  from trades where idruh = '0' GROUP BY imemo,isl;", null);
    	double amountd5 = 0;
    	if(c3.moveToFirst()){    		
    		while(!c3.isAfterLast()) 
    		{
    			amount = c3.getString(0);
    			slprice = c3.getString(1);
    			memox = c3.getString(2);
    			
    			if (slprice == null ) {slprice="0";}
    	    	amountd5 = Double.parseDouble(slprice);
    	    	if(amountd5 > 0d ) { 
    	    	ContentValues cv53=new ContentValues(); 
    	   		cv53.put("itime", "0");
    	   		cv53.put("iopen", slprice);
    	   		cv53.put("ivolume", amount);
    	   		cv53.put("iorder", "0");
    	   		cv53.put("isymbol", pair);
    	   		cv53.put("idruh", "5");
    	   		cv53.put("itp", "0");
    			cv53.put("isl", "0");
    			cv53.put("imemo", memox);
    	   		db5.insert("trades", "itime", cv53);
    	    	}
    	    
    	    c3.moveToNext();
    		}
    	    
    	}
    	c3.close();

   		//sum SL za Sells
    	amount="0"; tpprice="0"; slprice="0"; memox=" ";
   		db5.delete("trades", "idruh = '6' ", null);
    	Cursor c4 = db5.rawQuery("select sum(ivolume), isl, imemo from trades where idruh = '1' GROUP BY imemo,isl;", null);
    	double amountd6 = 0;
    	if(c4.moveToFirst()){    		
    		while(!c4.isAfterLast()) 
    		{
    			amount = c4.getString(0);
    			slprice = c4.getString(1);
    			//Log.i("c4", c4.getString(1));
    			memox = c4.getString(2);
    			
    			if (slprice == null ) {slprice="0";}
    	    	amountd6 = Double.parseDouble(slprice);
    	    	if(amountd6 > 0d ) {
    	        ContentValues cv54=new ContentValues();		    		
    	   		cv54.put("itime", "0");
    	   		cv54.put("iopen", slprice);
    	   		cv54.put("ivolume", amount);
    	   		cv54.put("iorder", "0");
    	   		cv54.put("isymbol", pair);
    	   		cv54.put("idruh", "6");
    	   		cv54.put("itp", "0");
    			cv54.put("isl", "0");
    			cv54.put("imemo", memox);
    	   		db5.insert("trades", "itime", cv54);
    	    	}
    	    
    	    c4.moveToNext();
    		}
    	    
    	}
    	c4.close();
    	
    	
    	
    	//read items	
    	constantsCursor3=db5.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh, imemo, itp, isl " +
				"FROM  trades WHERE _id >= 0 ORDER BY iopen DESC ",
				null);
		
    	double profmod=0; double profmod2=0; double profmod3=0;
        constantsCursor3.moveToFirst();
        while(!constantsCursor3.isAfterLast()) {
        	
        	//here count Profit for Model profitmodel
        	int idruhi = Integer.parseInt(constantsCursor3.getString(constantsCursor3.getColumnIndex("idruh")));
        	double iopend = Double.parseDouble(constantsCursor3.getString(constantsCursor3.getColumnIndex("iopen")));
        	double ivolumed = Double.parseDouble(constantsCursor3.getString(constantsCursor3.getColumnIndex("ivolume")));
        	if( idruhi == 0 ){ 
        		profmod=profmod + ( 100000* ivolumed * ( actpriceb - iopend) );
        	}
        	if( idruhi == 1 ){ 
        		profmod=profmod + ( 100000* ivolumed * ( iopend - actprices) );
        	}
        	
        	//String profmods=profmod + "";
        	//Log.i("profmod ", profmods);
        	
        	myopenList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("iopen")));
        	myvolumeList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("ivolume")));
        	myorderList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("iorder")));
        	mysymbolList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("isymbol")));
        	mytimeList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("itime")));
        	mydruhList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("idruh")));
        	mytpList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("itp")));
        	myslList.add(constantsCursor3.getString(constantsCursor3.getColumnIndex("isl")));
        	
        	String imemox=constantsCursor3.getString(constantsCursor3.getColumnIndex("imemo"));

        	if (imemox != null ) {}else{ imemox="-"; }
        	if (imemox.isEmpty()) {imemox="-";}
        	//imemox="xx";
        	mycomentList.add(imemox);
        	
        	constantsCursor3.moveToNext();
        }

        if( actpricex > 0 ) {
        profmod2=profmod/actpricex;
        }
        profmod3=round(profmod2,2);
        profitmodel=profmod3 + "";
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
        	mydruhList.add("2");
        	mycomentList.add("x");
        	mytpList.add("0");
        	myslList.add("0");
        }
        
        db5.close();
        
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

   		db7=(new DatabaseTemp(getActivity())).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='0', trade='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();
   	 	
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
   		   	
   		db7=(new DatabaseTemp(getActivity())).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='0', trade='1' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();
   	 	
    	readSqlTrades();

    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetTradesStreamAsyncTask = new GetTradesStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
		GetTradesStreamAsyncTask.execute();
   	}
   	
   	//oncontextmenu
    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v,
    ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);


    	String name2="position " + sendorder;

    menu.setHeaderTitle( name2 );
    
    MenuInflater inflater = getActivity().getMenuInflater();
    inflater.inflate(R.menu.kontextrades_menu, menu);
    

    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {

    switch (item.getItemId()) {
    
    	case R.id.modify:

    		Toast.makeText(getActivity(), "edit click " + senditem, Toast.LENGTH_SHORT).show();

    		Intent ie = new Intent(getActivity(), EditTradeActivity.class);
            Bundle extrase = new Bundle();
            //can to change itp, isl
            if(senddruh.equals("0")) { extrase.putString("xtrade", "5"); }
            if(senddruh.equals("1")) { extrase.putString("xtrade", "6"); }
            extrase.putString("pairx", pair);
            extrase.putString("ivolume", sendvolume);
            extrase.putString("iprice", sendprice);
            extrase.putString("itp", sendtp);
            extrase.putString("isl", sendsl);
            extrase.putString("icomm", sendcomm);
            extrase.putLong("iorder", sendorder);
            extrase.putInt("modall", 0);
            ie.putExtras(extrase);
            startActivity(ie);
            getActivity().finish();
            
            break;
    
        
        case R.id.close:
        	
        	Toast.makeText(getActivity(), "delete click " + senditem, Toast.LENGTH_SHORT).show();
        	
        	new AlertDialog.Builder(getActivity())
        	.setTitle(getString(R.string.closetrade) + " " + sendorder)
			.setMessage(getString(R.string.wantclose))
			.setPositiveButton(R.string.textyes,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					
					
					Intent iu = new Intent(getActivity(), MakeTradeActivity.class);
		            Bundle extrasu = new Bundle();
		            //have to set ivolume, iorder is position, icomm may be empty
		            if(senddruh.equals("0")) { extrasu.putString("xtrade", "3"); }
		            if(senddruh.equals("1")) { extrasu.putString("xtrade", "4"); }
		            extrasu.putString("pairx", pair);
		            extrasu.putString("ivolume", sendvolume);
		            extrasu.putString("itp", "0");
		            extrasu.putString("isl", "0");
		            extrasu.putString("icomm", sendcomm);
		            extrasu.putString("iprice", "0");
		            //it's position 
		            extrasu.putLong("iorder", sendorder);
		            extrasu.putInt("modall", 0);
		            iu.putExtras(extrasu);
		            startActivity(iu);
		            getActivity().finish();
					
					
					
					
				}
			})
			.setNegativeButton(R.string.textno,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					// ignore, just dismiss
					
				}
			})
			.show();

            break;
            
        case R.id.modifyall:

    		Toast.makeText(getActivity(), "edit click " + senditem, Toast.LENGTH_SHORT).show();

    		Intent iea = new Intent(getActivity(), EditTradeActivity.class);
            Bundle extrasea = new Bundle();
            //can to change itp, isl
            if(senddruh.equals("0")) { extrasea.putString("xtrade", "5"); }
            if(senddruh.equals("1")) { extrasea.putString("xtrade", "6"); }
            extrasea.putString("pairx", pair);
            extrasea.putString("ivolume", sendvolume);
            extrasea.putString("iprice", sendprice);
            extrasea.putString("itp", sendtp);
            extrasea.putString("isl", sendsl);
            extrasea.putString("icomm", sendcomm);
            extrasea.putLong("iorder", sendorder);
            extrasea.putInt("modall", 1);
            iea.putExtras(extrasea);
            startActivity(iea);
            getActivity().finish();
            
            break;
            
            
        case R.id.closeall:
        	
        	Toast.makeText(getActivity(), "delete click " + senditem, Toast.LENGTH_SHORT).show();
        	
        	new AlertDialog.Builder(getActivity())
        	.setTitle(getString(R.string.closetrades) + " sys " + sendcomm)
			.setMessage(getString(R.string.wantcloses))			
			.setPositiveButton(R.string.textyes,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					
					
					Intent iu = new Intent(getActivity(), MakeTradeActivity.class);
		            Bundle extrasu = new Bundle();
		            //have to set ivolume, iorder is position, icomm may be empty
		            if(senddruh.equals("0")) { extrasu.putString("xtrade", "3"); }
		            if(senddruh.equals("1")) { extrasu.putString("xtrade", "4"); }
		            extrasu.putString("pairx", pair);
		            extrasu.putString("ivolume", sendvolume);
		            extrasu.putString("itp", "0");
		            extrasu.putString("isl", "0");
		            extrasu.putString("icomm", sendcomm);
		            extrasu.putString("iprice", "0");
		            //it's position 
		            extrasu.putLong("iorder", sendorder);
		            extrasu.putInt("modall", 1);
		            iu.putExtras(extrasu);
		            startActivity(iu);
		            getActivity().finish();

				}
			})
			.setNegativeButton(R.string.textno,
													new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
															int whichButton) {
					// ignore, just dismiss
					
				}
			})
			.show();

            break;

        }

        return super.onContextItemSelected(item);
    }
    //koniec oncontextmenu

}