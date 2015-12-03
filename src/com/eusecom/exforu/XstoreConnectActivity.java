/* I do not use this script now.
*/

package com.eusecom.exforu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.UnknownHostException;
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
 
public class XstoreConnectActivity extends Activity {
 
    TextView vystup;
    String vystuptxt;
    private ProgressDialog pDialog2;
    
    String userpsws;
	long useridl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairs_txt);
        
        vystup = (TextView) findViewById(R.id.vystup);
        userpsws=SettingsActivity.getUserPsw(this);
        useridl=Long.valueOf(SettingsActivity.getUserId(this));

        Log.d("I am at onCreate ", "");
        new XstoreConnect().execute();

        
    }
    //koniec oncreate
    

    class XstoreConnect extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(XstoreConnectActivity.this);
            pDialog2.setMessage(getString(R.string.progdata));
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();

        }
 

        protected String doInBackground(String... params) {


        	vystuptxt="";
        	
        	Log.d("AsyncTask XstoreConnect is running ", "AsyncTask XstoreConnect is running");
        	
        	 try {
                 
                 // Create new connector
                 SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.DEMO);
                 
                 // Create new credentials
                 // TODO: Insert your credentials
                 @SuppressWarnings("deprecation")
                 Credentials credentials = new Credentials(useridl, userpsws);
                 //Credentials credentials = new Credentials(381715L, "9a14e566");
                 
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
                         System.out.println("-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid());
                         
                         String aDataRow= "-> " + symbol.getSymbol() + " Ask: " + symbol.getAsk() + " Bid: " + symbol.getBid();
                         vystuptxt += aDataRow + "\n";
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
                  
                	vystup.setText(vystuptxt);
                }
            });
        	//end of runOnUiThread
        	
        }
    }
    
    

}