package com.eusecom.exforu;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import pro.xstore.api.sync.ServerData.ServerEnum;

import com.eusecom.exforu.animators.BaseItemAnimator;
import com.eusecom.exforu.animators.FadeInAnimator;
import com.eusecom.exforu.animators.FadeInDownAnimator;
import com.eusecom.exforu.animators.FadeInLeftAnimator;
import com.eusecom.exforu.animators.FadeInRightAnimator;
import com.eusecom.exforu.animators.FadeInUpAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInLeftYAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInRightYAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;
//import jp.wasabeef.recyclerview.animators.LandingAnimator;
//import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInRightAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import com.eusecom.exforu.GetFavoriteStreamAsyncTask.DoSomething;


/**
 * Created by Wasabeef on 2015/01/03.
 */
@SuppressWarnings("deprecation")
public class FavoriteActivity extends ActionBarActivity implements DoSomething{

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
    
    private static String[] myfavpairs = null;
    private static String[] myaskprices = null;
    private static String[] mybidprices = null;
    private static String[] myprofits = null;
    
    private List<String> myfavList = new ArrayList<String>();
    private LinkedList<String> myfavLinkedList = new LinkedList<String>();
    
    private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;
	
	private SQLiteDatabase db7=null;
	
	String vystuptxt;
    private ProgressDialog pDialog2;
    
    String userpsws;
	long useridl;
	String accountx;
	
	GetFavoriteStreamAsyncTask GetFavoriteStreamAsyncTask;
	int myProgress;
	
	FavoriteAdapter adapter;
	RecyclerView recyclerView;
	private List<String> myAskList = new ArrayList<String>();
	private List<String> myBidList = new ArrayList<String>();
	private List<String> myProfitList = new ArrayList<String>();
	
	TextView sbalance;
	TextView sprofit;
	
	Button btnAgain;
	
	int repeat=60000;
	String repeats="60";
	String favact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        btnAgain = (Button) findViewById(R.id.btnAgain);
        btnAgain.setVisibility(View.GONE);
        
        accountx=SettingsActivity.getAccountx(this);
        
        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(this));
        	userpsws=SettingsActivity.getUserPsw(this);
        }else{
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(this));
        	userpsws=SettingsActivity.getUserPswr(this);
        }

        repeat=1000*Integer.parseInt(SettingsActivity.getStreamf(this));
        
        String repeats = getResources().getString(R.string.again) + " " + SettingsActivity.getStreamf(this) + " sec.";
        btnAgain.setText(repeats);
        
        db7=(new DatabaseTemp(this)).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='1' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);

        
        
        db2=(new DatabaseFavpairs(this)).getWritableDatabase();

        constantsCursor2=db2.rawQuery("SELECT _ID, pair2, pswd2, name2 "+
				"FROM  favpairs WHERE _id > 0 ORDER BY _id DESC ",
				null);
        
        constantsCursor2.moveToFirst();
        
        final int sizec = constantsCursor2.getCount();
        myfavpairs = new String[sizec];
        myaskprices = new String[sizec]; mybidprices = new String[sizec]; myprofits = new String[sizec];
        
        int ic=0;
        while(!constantsCursor2.isAfterLast()) {
        	
        	myfavpairs[ic] = constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2"));
        	myaskprices[ic] = ""; mybidprices[ic] = ""; myprofits[ic] = "";
        	myfavList.add(constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2")));
        	myfavLinkedList.add(constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2")));
        	ic=ic+1;
        	constantsCursor2.moveToNext();
        }
        constantsCursor2.close();
        
        Log.d("myfavList = ", myfavList.toString());
        
        Log.d("I am at onCreate ", "");
        //new XstoreConnect().execute();
        
        LinkedList<String> listget = new LinkedList<String>();
		String symbolget = "EURUSD";
		//listget.add(symbolget);
		//symbolget = "USDJPY";
		//listget.add(symbolget);
		listget=myfavLinkedList;
        
		if (isOnline()) 
        {
			GetFavoriteStreamAsyncTask = new GetFavoriteStreamAsyncTask(this, this, 20, accountx, userpsws, useridl
					, listget, symbolget, repeat);
	        GetFavoriteStreamAsyncTask.execute();
	        myAskList = new ArrayList<>(Arrays.asList(myaskprices));
	        myBidList = new ArrayList<>(Arrays.asList(mybidprices));
	        myProfitList = new ArrayList<>(Arrays.asList(myprofits));
	        
	        recyclerView = (RecyclerView) findViewById(R.id.list);
	        recyclerView.setLayoutManager(new LinearLayoutManager(this));
	        recyclerView.setItemAnimator(new FadeInAnimator());	        
	        adapter = new FavoriteAdapter(this, new ArrayList<>(Arrays.asList(myfavpairs)), myAskList, myBidList, myProfitList);
	        recyclerView.setAdapter(adapter);
	        recyclerView.setItemAnimator(new FadeInRightAnimator());
	        recyclerView.getItemAnimator().setAddDuration(300);
	        recyclerView.getItemAnimator().setRemoveDuration(300);
	        

	        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(tp);
	        
	        IntentFilter filter = new IntentFilter(ACTION_INTENT);
	        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        }
        else{
        	 

            new AlertDialog.Builder(this)
            .setTitle(getString(R.string.niejeinternet))
            .setMessage(getString(R.string.potrebujeteinternet))
            .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                  
                	finish();
                }
             })

             .show();
            

         }
        

        

    }//oncreate
    

    //broadcastreceiver for send value from fragment to activity
  	String KeyWord;
  	public static final String ACTION_INTENT = "com.eusecom.exforu.action.UI_FINISH_FAVACT";
      protected BroadcastReceiver receiver = new BroadcastReceiver() {


          @Override
          public void onReceive(Context context, Intent intent) {
        	  Log.d("FavoriteActivity", "I am at onReceive.");
              if(ACTION_INTENT.equals(intent.getAction())) {
            	  
            	  	Bundle extras = intent.getExtras();

            	  	String value = extras.getString("UI_VALUE");
            	  	int xxsp = extras.getInt("UI_XXSP");

            	  	finishFavoriteActivity(value, xxsp);
              }
          }
      };

      private void finishFavoriteActivity(String value, int xxxsp) {
          // you probably want this:
    	  String xxxsps=xxxsp + "";
    	  Log.d("finishFavoriteActivity " + value, "xxsp " + xxxsps);
    	  constantsCursor2.close();
  			db2.close();
  			

  			if (isOnline()) 
  			{
  				GetFavoriteStreamAsyncTask.exit();
  				GetFavoriteStreamAsyncTask.cancel(true);
  			}
    	  finish();


      }
      //end of broadcast
    
    public void buttonAgain(View v) {
    	
    	btnAgain.setVisibility(View.GONE);
    	
    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = "EURUSD";
		listget.add(symbolget);
		
    	GetFavoriteStreamAsyncTask = new GetFavoriteStreamAsyncTask(this, this, 20, accountx, userpsws, useridl
    			, listget, symbolget, repeat);
        GetFavoriteStreamAsyncTask.execute();
    }
    
    @Override
    public void doChangeUI2(final String symbol, final long timestamp, final double ask, final double bid) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 System.out.println("I am at doChangeUI2.");
		    	 DecimalFormat df;
		    	 
		    	 if( ask < 10 ){
	    			 df = new DecimalFormat("0.00000");
	    	 
	    		 }else{
	    			 df = new DecimalFormat("0.000"); 
	    		 }
	 
             	 String asks = df.format(ask);
             	 asks = asks.replace(',','.');
             	          	
            	 String bids = df.format(bid);
            	 bids = bids.replace(',','.');
            	 
            	 int indexmyfavlist = myfavList.indexOf(symbol);
		    	 
		    	 myaskprices[indexmyfavlist] = asks;
		    	 mybidprices[indexmyfavlist] = bids;

		    	 myAskList = new ArrayList<>(Arrays.asList(myaskprices));
		    	 myBidList = new ArrayList<>(Arrays.asList(mybidprices));
		    	 adapter = new FavoriteAdapter(getBaseContext(), new ArrayList<>(Arrays.asList(myfavpairs))
		    			 , myAskList, myBidList, myProfitList);
		         recyclerView.setAdapter(adapter);
		    	 adapter.notifyDataSetChanged();

		    }
		});
     
    }
    
    @Override
    public void doChangeUI3(final double balance, final double equity) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 sbalance = (TextView) findViewById(R.id.sbalance);
		    	 String sbalances = balance + "";
		    	 sbalance.setText(sbalances);
		    	 
		    	 double profitd = equity - balance;
		    	 DecimalFormat df = new DecimalFormat("0.00");             	
             	 String sprofits = df.format(profitd);
             	 sprofits = sprofits.replace(',','.');

		    	 sprofit = (TextView) findViewById(R.id.sprofit);
		    	 sprofit.setText(sprofits);
		    }
		});
     
    }

    @Override
    public void doChangeUI() {

    	btnAgain.setVisibility(View.VISIBLE);
    }
    
    @Override
    public void doChangeUIerr(final String errs) {

    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 Toast.makeText(FavoriteActivity.this, errs, Toast.LENGTH_LONG).show();
		    }
		});
    	

    }
    
    
    
    class XstoreConnect extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(FavoriteActivity.this);
            pDialog2.setMessage(getString(R.string.progdata));
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();

        }
 

        protected String doInBackground(String... params) {


        	vystuptxt="";
        	
        	Log.d("FavoriteActivity AsyncTask", "AsyncTask is running");
        	
        	 try {
                 
                 // Create new connector
                 SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.DEMO);
                 
                 // Create new credentials
                 // TODO: Insert your credentials
				 //Credentials credentials = new Credentials(381715L, "9a14e566");
                 Credentials credentials = new Credentials(useridl, userpsws);
                 
                 // Create and execute new login command
                 LoginResponse loginResponse = APICommandFactory.executeLoginCommand(
                         connector,         // APIConnector
                         credentials        // Credentials
                 );
                 
                 // Check if user logged in correctly
                 if(loginResponse.getStatus() == true) {
                     
                     // Print the message on console
                     System.out.println("FavActivity User logged in");
                     
                     // Create and execute all symbols command (which gets list of all symbols available for the user)
                     AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
                     
                     // Print the message on console
                     System.out.println("Available symbols:");
                     
                     // List all available symbols on console
                     for(SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                        
                    	 if (myfavList.contains(symbol.getSymbol())) {
                    	 
                    	 System.out.println("-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
                         
                         String aDataRow= "-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid();
                         vystuptxt += aDataRow + "\n";
                    	 }
                     }
                     
                 } else {
                     
                     // Print the error on console
                     System.err.println("Error: user couldn't log in!");      
                     
                 }
                 
                 // Close connection
                 connector.close();
                 System.out.println("Connection closed");
                 
             // Catch errors
             } catch (UnknownHostException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (APICommandConstructionException e) {
                 e.printStackTrace();
             } catch (APICommunicationException e) {
                 e.printStackTrace();
             } catch (APIReplyParseException e) {
                 e.printStackTrace();
             } catch (APIErrorResponse e) {
                 e.printStackTrace();
             }
    
        	
        	
 
            return null;
        }
 

        protected void onPostExecute(String file_url) {

        	pDialog2.dismiss();
        	// updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                  
                	//vystup.setText(vystuptxt);
                }
            });
        	//end of runOnUiThread
        	
        }
    }
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		constantsCursor2.close();
		db2.close();
		
		String UpdateSql7 = "UPDATE temppar SET favact='0' WHERE _id > 0 ";
	   	db7.execSQL(UpdateSql7);
		db7.close();

		if (isOnline()) 
        {
		GetFavoriteStreamAsyncTask.exit();
		GetFavoriteStreamAsyncTask.cancel(true);
        }


	}
	//ondestroy
    
    //test if internet
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    //end test if internet
    
}//activity
