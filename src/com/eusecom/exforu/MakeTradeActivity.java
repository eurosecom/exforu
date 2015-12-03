package com.eusecom.exforu;
 

import java.io.IOException;
import java.net.UnknownHostException;

import pro.xstore.api.message.codes.TRADE_OPERATION_CODE;
import pro.xstore.api.message.codes.TRADE_TRANSACTION_TYPE;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.TradeTransInfoRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.SymbolResponse;
import pro.xstore.api.message.response.TradeTransactionResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import pro.xstore.api.sync.ServerData.ServerEnum;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

 
public class MakeTradeActivity extends Activity {
 
    String xtrade;
    String userpsws;
	long useridl;
	String accountx;
	SyncAPIConnector connector;
	String errors="";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.all_ucty);
        
        // getting product details from intent
        Intent i = getIntent();
        
        Bundle extras = i.getExtras();
        xtrade = extras.getString("xtrade");
        
        accountx=SettingsActivity.getAccountx(this);
        
        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(this));
        	userpsws=SettingsActivity.getUserPsw(this);
        }else{
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(this));
        	userpsws=SettingsActivity.getUserPswr(this);
        }

        //Save trade
        new SaveTrade().execute();
        
        //save trade to sqlite
        
        Intent ir = getIntent();
        if( xtrade.equals("1")) { setResult(101, ir); }
        if( xtrade.equals("2")) { setResult(102, ir); }
        finish();
 
    }
    //koniec oncreate
 

    /**
     * Background Async Task to Save Trade
     * */
    class SaveTrade extends AsyncTask<String, String, String> {
    	

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
 

        protected String doInBackground(String... args) {
        	try {
                
                // Create new connector
        		 if( accountx.equals("0")) {
        			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.DEMO);");
        			 connector = new SyncAPIConnector(ServerEnum.DEMO);
        		 }else{
        			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.REAL);");
        			 connector = new SyncAPIConnector(ServerEnum.REAL); 
        		 }
                
                // Create new credentials
                // TODO: Insert your credentials
        		@SuppressWarnings("deprecation")
        		//Credentials credentials = new Credentials(381715L, "9a14e566", "", "YOUR APP NAME");
        		Credentials credentials = new Credentials(useridl, userpsws, "", "YOUR APP NAME");
                
                // Create and execute new login command
                LoginResponse loginResponse = APICommandFactory.executeLoginCommand(
                        connector,         // APIConnector
                        credentials        // Credentials
                );
                
                
                System.out.println("SaveTrade " + loginResponse);
                
                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, "EURUSD");
                System.out.println("symbolResponse " + symbolResponse);
                
                double price = symbolResponse.getSymbol().getAsk();
                //System.out.println("price " + price);
                double sl = 0.0;
                double tp = 0.0;
                String symbol = symbolResponse.getSymbol().getSymbol();
                double volume = 0.1;
                long order = 0;
                String customComment = "my comment";
                long expiration = 0;
                
                TradeTransInfoRecord ttOpenInfoRecord = null;
                
                if( xtrade.equals("1")) {
                	ttOpenInfoRecord = new TradeTransInfoRecord(
                			TRADE_OPERATION_CODE.SELL, 
                			TRADE_TRANSACTION_TYPE.OPEN, 
                			price, sl, tp, symbol, volume, order, customComment, expiration);
                }else{
                	ttOpenInfoRecord = new TradeTransInfoRecord(
                        	TRADE_OPERATION_CODE.BUY, 
                        	TRADE_TRANSACTION_TYPE.OPEN, 
                        	price, sl, tp, symbol, volume, order, customComment, expiration);	
                }

                TradeTransactionResponse tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                System.out.println("tradeTransactionResponse " + tradeTransactionResponse);

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
        	
        	

        }
 
    }
    //koniec savetrade
}