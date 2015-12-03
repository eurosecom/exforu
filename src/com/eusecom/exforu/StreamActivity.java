/* I do not use this script now.
*/

package com.eusecom.exforu;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import java.util.LinkedList;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.STradeRecord;
import pro.xstore.api.message.records.TickRecord;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.TickPricesResponse;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;


import com.eusecom.exforu.animators.BaseItemAnimator;
import com.eusecom.exforu.animators.FadeInAnimator;
import com.eusecom.exforu.animators.FadeInDownAnimator;
import com.eusecom.exforu.animators.FadeInLeftAnimator;
import com.eusecom.exforu.animators.FadeInRightAnimator;
import com.eusecom.exforu.animators.FadeInUpAnimator;

import com.eusecom.exforu.GetStreamAsyncTask.DoSomething;

/**
 * Created by Wasabeef on 2015/01/03.
 */
@SuppressWarnings("deprecation")
public class StreamActivity extends ActionBarActivity implements DoSomething{

    enum Type {
        FadeIn(new FadeInAnimator()),
        FadeInDown(new FadeInDownAnimator()),
        FadeInUp(new FadeInUpAnimator()),
        FadeInLeft(new FadeInLeftAnimator()),
        FadeInRight(new FadeInRightAnimator());

        private BaseItemAnimator mAnimator;

        Type(BaseItemAnimator animator) {
            mAnimator = animator;
        }

        public BaseItemAnimator getAnimator() {
            return mAnimator;
        }
    }
    
    private static String[] myfavpairs = null;
    
    private List<String> myfavList = new ArrayList<String>();
    
    private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;
	
	String vystuptxt;
	TextView vystup;
	int vystupx=0;
	String vystuptxt2="";

    private ProgressDialog pDialog2;
    
    String userpsws;
	long useridl;
	
	GetStreamAsyncTask GetStreamAsyncTask;
	int myProgress;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        
        vystup = (TextView) findViewById(R.id.vystup);
        
        userpsws=SettingsActivity.getUserPsw(this);
        useridl=Long.valueOf(SettingsActivity.getUserId(this));
        
        db2=(new DatabaseFavpairs(this)).getWritableDatabase();
        
        constantsCursor2=db2.rawQuery("SELECT _ID, pair2, pswd2, name2 "+
				"FROM  favpairs WHERE _id > 0 ORDER BY _id DESC ",
				null);
		

        constantsCursor2.moveToFirst();
        
        final int sizec = constantsCursor2.getCount();
        myfavpairs = new String[sizec];
        
        int ic=0;
        while(!constantsCursor2.isAfterLast()) {
        	
        	myfavpairs[ic] = constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2"));
        	myfavList.add(constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2")));
        	ic=ic+1;
        	constantsCursor2.moveToNext();
        }
        constantsCursor2.close();
        
        Log.d("myfavList = ", myfavList.toString());
        
        LinkedList<String> listget = new LinkedList<String>();
		String symbolget = "EURUSD";
		listget.add(symbolget);
        
		GetStreamAsyncTask = new GetStreamAsyncTask(this, 20, userpsws, useridl, listget, symbolget);
		GetStreamAsyncTask.execute();
        
        //new GetStreamConnect().execute();
        

    }//oncreate
    

    @Override
    public void doChangeUI2(final String symbol, final long timestamp, final double ask, final double bid) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 String locdate = (String) DateFormat.format("dd/MM/yyyy hh:mm:ssaa", timestamp);
		    	 vystupx=vystupx+1;
		    	 String vystupxs=vystupx + "";
		    	 
		    	 String aDataRow2= vystupxs + ". -> " + symbol + " Time = " + locdate + " Ask = " + ask;
		    	 vystuptxt2 += aDataRow2 + "\n" + "\n";
		    	 
		    	 vystup.setText(vystuptxt2);

		    }
		});
     
    }

    @Override
    public void doChangeUI() {
     Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
     //vystup.setText("Finish");
    }
    
   

    class GetStreamConnect extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(StreamActivity.this);
            pDialog2.setMessage(getString(R.string.progdata));
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();

        }
 

        protected String doInBackground(String... params) {


        	vystuptxt="";
        	
        	Log.d("AsyncTask", "AsyncTask is running");
        	
        	 try {
                 
                 // Create new connector
        		 SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.DEMO);
                 
                 // Create new credentials
                 // TODO: Insert your credentials
				 //Credentials credentials = new Credentials(381715L, "9a14e566");
                 Credentials credentials = new Credentials(useridl, userpsws, "", "YOUR APP NAME");
                 
                 // Create and execute new login command
                 LoginResponse loginResponse = APICommandFactory.executeLoginCommand(
                         connector,         // APIConnector
                         credentials        // Credentials
                 );
                 
                 
                 System.out.println(loginResponse);
                 
                 
     			if (loginResponse != null && loginResponse.getStatus())
     			{

     				StreamingListener sl = new StreamingListener() {
     					@Override
     					public void receiveTradeRecord(STradeRecord tradeRecord) {
     						System.out.println("Stream trade record: " + tradeRecord);
     					}

     					@Override
     					public void receiveTickRecord(STickRecord tickRecord) {
     						vystupx=vystupx+1;
         					String vystupxs=vystupx + "";
     						System.out.print("My " + vystupxs + ". Stream tick record: " + tickRecord);
     						String aDataRow= "-> " + tickRecord;
     						vystuptxt += aDataRow + "\n" + "\n";
     					}
     				};
     				
     				LinkedList<String> list = new LinkedList<String>();
     				String symbol = "EURUSD";
     				list.add(symbol);

     				TickPricesResponse resp = APICommandFactory.executeTickPricesCommand(connector, 0L, list, 0L);
     				for (TickRecord tr : resp.getTicks()) {
     					
     					System.out.println("TickPrices result: "+tr.getSymbol() + " - ask: " + tr.getAsk());
     				}

     				connector.connectStream(sl);
     				System.out.println("Stream connected.");
     				
     				connector.subscribePrice(symbol);

     				connector.subscribeTrades();
     	
     				Thread.sleep(10000);

     				connector.unsubscribePrice(symbol);
     				connector.unsubscribeTrades();
     			
     				connector.disconnectStream();
     				System.out.println("Stream disconnected.");
     				
     				//Thread.sleep(5000);
     				
     				//connector.connectStream(sl);
     				//System.out.println("Stream connected again.");
     				//connector.disconnectStream();
     				//System.out.println("Stream disconnected again.");
     				//System.exit(0);
     			}

                 
        	 } catch (Exception ex) {
     			System.err.println(ex);
     		}
    
        	
        	
 
            return null;
        }
 

        protected void onPostExecute(String file_url) {

        	pDialog2.dismiss();
        	// updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                  
                	vystup.setText(vystuptxt);
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

	}
	//ondestroy
    
}//activity
