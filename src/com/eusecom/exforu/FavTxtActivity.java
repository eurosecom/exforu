/* I do not use this script now.
*/

package com.eusecom.exforu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SymbolRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.AllSymbolsResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.ServerData.ServerEnum;
import pro.xstore.api.sync.SyncAPIConnector;
 
public class FavTxtActivity extends Activity {
 
    TextView vystup;
    String vystuptxt;
    private ProgressDialog pDialog2;
    
    private static String[] myfavpairs = null;
    
    private List<String> myfavList = new ArrayList<String>();
    
    private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;
	
	String userpsws;
	long useridl;
	int opakovanie=1;
	Timer timer;
	int repeat=60000;
	String repeats="60";
	String errors="";
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favpairs_txt);
        
        vystup = (TextView) findViewById(R.id.vystup);
        userpsws=SettingsActivity.getUserPsw(this);
        useridl=Long.valueOf(SettingsActivity.getUserId(this));
        repeat=1000*Integer.parseInt(SettingsActivity.getRepeat(this));
        repeats=SettingsActivity.getRepeat(this);
        
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
        

        Log.d("I am at onCreate ", "");
        //new XstoreConnect().execute();
        callAsynchronousTask();

        
    }
    //koniec oncreate
    
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {       
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {       
                        try {
                        	XstoreConnect XstoreConnect = new XstoreConnect();
                            // XstoreConnect this class is the class that extends AsynchTask 
                        	XstoreConnect.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, repeat); //execute in every repeat ms
    }
    

    class XstoreConnect extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(FavTxtActivity.this);
            pDialog2.setMessage(getString(R.string.progdata));
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            if( opakovanie == 1 ) { pDialog2.show(); }

        }
 

        protected String doInBackground(String... params) {


        	vystuptxt="";
        	errors ="";
        	
        	Log.d("AsyncTask XstoreConnect is running ", "AsyncTask XstoreConnect is running");
        	
        	 try {
                 
                 // Create new connector
                 SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.DEMO);
                 
                 //useridl=381715L;
                 //String userpsws="9a14e566";

                 
                 // Create new credentials
                 // TODO: Insert your credentials
                 @SuppressWarnings("deprecation")
				Credentials credentials = new Credentials(useridl, userpsws);
                 
                 // Create and execute new login command
                 LoginResponse loginResponse = APICommandFactory.executeLoginCommand(
                         connector,         // APIConnector
                         credentials        // Credentials
                 );
                 
                 // Check if user logged in correctly
                 if(loginResponse.getStatus() == true) {
                     
                     // Print the message on console
                     System.out.println("User logged in");
                     
                     // Create and execute all symbols command (which gets list of all symbols available for the user)
                     AllSymbolsResponse availableSymbols = APICommandFactory.executeAllSymbolsCommand(connector);
                     
                     // Print the message on console
                     System.out.println("Available symbols:");
                     
                     // List all available symbols on console
                     for(SymbolRecord symbol : availableSymbols.getSymbolRecords()) {
                    	 
                    	 if (myfavList.contains(symbol.getSymbol())) {
                         System.out.println("-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
                         
                         String aDataRow= "-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + "\n" + " Bid: " + symbol.getBid();
                         vystuptxt += aDataRow + "\n" + "\n";
                    	 }
                     }
                     
                 } else {
                     
                     // Print the error on console
                	 errors = errors + " User couldn't log in! \n";
                     System.err.println("Error: user couldn't log in!");      
                     
                 }
                 
                 // Close connection
                 connector.close();
                 System.out.println("Connection closed");
                 
             // Catch errors
             } catch (UnknownHostException e) {
            	 errors = errors + " Unknown Host Exception! \n";
                 e.printStackTrace();
             } catch (IOException e) {
            	 errors = errors + " IO Exception! \n";
                 e.printStackTrace();
             } catch (APICommandConstructionException e) {
            	 errors = errors + " API Command Construction Exception! \n";
                 e.printStackTrace();
             } catch (APICommunicationException e) {
            	 errors = errors + " API Communication Exception! \n";
                 e.printStackTrace();
             } catch (APIReplyParseException e) {
            	 errors = errors + " API Reply ParseException! \n";
                 e.printStackTrace();
             } catch (APIErrorResponse e) {
            	 errors = errors + " API Error Response! \n";
                 e.printStackTrace();
             }
    
        	
        	
 
            return null;
        }
 

        protected void onPostExecute(String file_url) {

        	if( opakovanie == 1 ) { pDialog2.dismiss(); }
        	// updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                  
                	String opakovaniex=opakovanie + "";
                	vystuptxt=opakovaniex + "x go" + " repeat after " + repeats + " sec \n" + errors + vystuptxt;
                	opakovanie=opakovanie+1;
                	vystup.setText(vystuptxt);
                }
            });
        	//end of runOnUiThread
        	
        }
    }
    //assynctask
    
    protected void onDestroy() {

        super.onDestroy();

        timer.cancel();
    }
    
    

}