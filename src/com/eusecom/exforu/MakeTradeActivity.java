package com.eusecom.exforu;
 

import java.io.IOException;
import java.net.UnknownHostException;
import pro.xstore.api.message.codes.REQUEST_STATUS;
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
import pro.xstore.api.message.response.TradeTransactionStatusResponse;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import pro.xstore.api.sync.ServerData.ServerEnum;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

 
public class MakeTradeActivity extends Activity {
 
	static String pair;
    String xtrade;
    String ivolume;
    String itp;
    String isl;
    String icomm;
    String iprice;
    long iorder=0;
    String userpsws;
	long useridl;
	String accountx;
	SyncAPIConnector connector;
	String errors="";
	private ProgressDialog pDialog;
	
	long getorder=0;
    String errcode="";
    REQUEST_STATUS tradeok;
    long tradeokl=0;
    private SQLiteDatabase db7=null;
    private SQLiteDatabase db61=null;
    int modall;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.all_ucty);
        
        // getting product details from intent
        Intent i = getIntent();
        
        Bundle extras = i.getExtras();
        xtrade = extras.getString("xtrade");
        pair = extras.getString("pairx");
        ivolume = extras.getString("ivolume");
        itp = extras.getString("itp");
        isl = extras.getString("isl");
        icomm = extras.getString("icomm");
        iorder = extras.getLong("iorder");
        iprice = extras.getString("iprice");
        modall = extras.getInt("modall");
        
        accountx=SettingsActivity.getAccountx(this);
        
        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(this));
        	userpsws=SettingsActivity.getUserPsw(this);
        	if( xtrade.equals("1")) { new SaveTrade().execute(); }
        	if( xtrade.equals("2")) { new SaveTrade().execute(); }
        	if( xtrade.equals("3")) { new DeleteTrade().execute(); }
        	if( xtrade.equals("4")) { new DeleteTrade().execute(); }
        }
        if( accountx.equals("2")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(this));
        	userpsws=SettingsActivity.getUserPsw(this);
        	new SaveTradeModel().execute();
        }
        if( accountx.equals("1")) {
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(this));
        	userpsws=SettingsActivity.getUserPswr(this);
        	if( xtrade.equals("1")) { new SaveTrade().execute(); }
        	if( xtrade.equals("2")) { new SaveTrade().execute(); }
        	if( xtrade.equals("3")) { new DeleteTrade().execute(); }
        	if( xtrade.equals("4")) { new DeleteTrade().execute(); }
        }


        db7=(new DatabaseTemp(this)).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='0', trade='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();
   	 	
 
    }
    //koniec oncreate
    
 
    class SaveTradeModel extends AsyncTask<String, String, String> {
    	

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MakeTradeActivity.this);
            if( xtrade.equals("1")) { pDialog.setMessage(getString(R.string.progtrade)); }
            if( xtrade.equals("2")) { pDialog.setMessage(getString(R.string.progtrade)); }
            if( xtrade.equals("3")) { pDialog.setMessage(getString(R.string.progdeltrade)); }
            if( xtrade.equals("4")) { pDialog.setMessage(getString(R.string.progdeltrade)); }
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
 

        protected String doInBackground(String... args) {
        	try {
                
                // Create new connector
        		if( accountx.equals("0")) {
       			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.DEMO);");
       			 connector = new SyncAPIConnector(ServerEnum.DEMO);
       		 	}
       		 	if( accountx.equals("2")) {
       			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.DEMO);");
       			 connector = new SyncAPIConnector(ServerEnum.DEMO);
       		 	}
       		 	if( accountx.equals("1")) {
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
                
                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, pair);
                System.out.println("symbolResponse " + symbolResponse);
                
                if (loginResponse != null && loginResponse.getStatus())
        		{
                
                //double price = symbolResponse.getSymbol().getAsk();
                	double price = 0;
                try {
                	price = Double.parseDouble(iprice);
                } catch (NumberFormatException e) {
                    // EditText EtPotential does not contain a valid double
                }
                System.out.println("price " + price);
                if (isl == null ) {isl="0";}
                if( isl.equals("")) {isl="0";}

                if (itp == null ) {itp="0";}
                if( itp.equals("")) {itp="0";}

                String idruh="0";
                if( xtrade.equals("1")) { idruh="0"; }
                if( xtrade.equals("2")) { idruh="1"; }

                String iordermaxs = "0"; int iordermax=0;
                if( xtrade.equals("1") || xtrade.equals("2") )
                {
         			db61=(new DatabaseModels(MakeTradeActivity.this)).getWritableDatabase();
         			Cursor c61 = db61.rawQuery("select iorder from models where iorder >= 0 ORDER BY iorder DESC LIMIT 1 ;", null);
         	    	
         	    	if(c61.moveToFirst()){    		
         	    		while(!c61.isAfterLast()) 
         	    		{
         	    			iordermaxs = c61.getString(0);
         	    	    	iordermax = Integer.parseInt(iordermaxs) + 1;
         	    	    	getorder=iordermax;         	    	    	
         	    	    	iordermaxs=iordermax + "";

         	    	    
         	    	    c61.moveToNext();
         	    		}
         	    	    
         	    	}


	    	    	ContentValues cv61=new ContentValues();
	    	    	long unixTime = System.currentTimeMillis();
	    	        String unixtimes=unixTime + "";
	    	    	
	    	        if(getorder == 0 ){
	    	        	getorder=100001;
	    	        	iordermax=100001;
     	    	    	iordermaxs=iordermax + "";
	    	        }
	    	        
	    	    	cv61.put("itime", unixtimes);
	    	    	cv61.put("iopen", price);
	    	    	cv61.put("ivolume", ivolume);
	    	    	cv61.put("iorder", iordermaxs);
	    	    	cv61.put("isymbol", pair);
	    	    	cv61.put("idruh", idruh);
	    	    	cv61.put("imemo", icomm);
	    	    	cv61.put("itp", itp);
	    	    	cv61.put("isl", isl);

	    	   		db61.insert("models", "itime", cv61);
	    	   		db61.close();

                	if( getorder > 0 ){
                	tradeokl = 1;
                	//System.out.println("tradeokl " + tradeokl);
                	}
                }
                	
                	
                if( xtrade.equals("3") || xtrade.equals("4") )
                {
             		db61=(new DatabaseModels(MakeTradeActivity.this)).getWritableDatabase();        		

             		if( modall == 0 ){
             			String UpdateSql1 = "DELETE FROM models WHERE iorder='" + iorder + "' ";
             			db61.execSQL(UpdateSql1);
             			}
             		if( modall == 1 ){
                 		String UpdateSql1 = "DELETE FROM models WHERE imemo='" + icomm + "' ";
                 	 	db61.execSQL(UpdateSql1);
                 		}
    	    	   	db61.close();
    	    	   	getorder=iorder;

                    if( getorder > 0 ){
                    tradeokl = 3;
                    //System.out.println("tradeokl " + tradeokl);
                    	
                    }
                	
                }
                
                
        		}

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
        	
        	pDialog.dismiss();
        	
        	SQLiteDatabase db6=(new DatabaseTrades(MakeTradeActivity.this)).getWritableDatabase();
            db6.delete("trades", "_ID > 0", null);
            db6.close();
        	
        	String titlex="";
        	//this.setTitle(String.format(getResources().getString(R.string.title_activity_mojkosik), SettingsActivity.getDoklad(this)));
        	if( xtrade.equals("1")) { titlex=String.format(getResources().getString(R.string.tradeorderednum), getorder); }
        	if( xtrade.equals("2")) { titlex=String.format(getResources().getString(R.string.tradeorderednum), getorder); }
        	if( xtrade.equals("3")) { titlex=String.format(getResources().getString(R.string.tradeclosednum), getorder); }
        	if( xtrade.equals("4")) { titlex=String.format(getResources().getString(R.string.tradeclosednum), getorder); }
        	if( modall == 1 ) { titlex=getString(R.string.alltradeclosed); }

        	if( tradeokl == 3 || tradeokl == 1 ){
        		
        	new AlertDialog.Builder(MakeTradeActivity.this)        	
        	.setMessage(titlex) 
            .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 

                	Intent i = new Intent(MakeTradeActivity.this, LearningActivity.class);
    	        	Bundle extras = new Bundle();
                    extras.putString("pairx", pair);
                    extras.putInt("whatspage", 1);
                    i.putExtras(extras);                
                    startActivity(i);
                	finish();
                }
             })

             .show();
        	
        	}else{
        		new AlertDialog.Builder(MakeTradeActivity.this)
        		.setMessage(getString(R.string.tradenotmodifyed))
        		.setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) {                   
                   	 	
        				Intent i = new Intent(MakeTradeActivity.this, LearningActivity.class);
        	        	Bundle extras = new Bundle();
                        extras.putString("pairx", pair);
                        extras.putInt("whatspage", 1);
                        i.putExtras(extras);                
                        startActivity(i);
                    	finish();
        			}
        		})

        		.show();
        	}


        }
 
    }
    //koniec savetrade model

    class SaveTrade extends AsyncTask<String, String, String> {
    	

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MakeTradeActivity.this);
            if( xtrade.equals("1")) { pDialog.setMessage(getString(R.string.progtrade)); }
            if( xtrade.equals("2")) { pDialog.setMessage(getString(R.string.progtrade)); }
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
 

        protected String doInBackground(String... args) {
        	try {
                
                // Create new connector
        		if( accountx.equals("0")) {
       			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.DEMO);");
       			 connector = new SyncAPIConnector(ServerEnum.DEMO);
       		 	}
       		 	if( accountx.equals("1")) {
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
                
                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, pair);
                System.out.println("symbolResponse " + symbolResponse);
                
                if (loginResponse != null && loginResponse.getStatus())
        		{
                
                double price = symbolResponse.getSymbol().getAsk();
                //System.out.println("price " + price);
                if (isl == null ) {isl="0";}
                if( isl.equals("")) {isl="0";}
                double sl = Double.parseDouble(isl);
                if (itp == null ) {itp="0";}
                if( itp.equals("")) {itp="0";}
                double tp =  Double.parseDouble(itp);
                String symbol = symbolResponse.getSymbol().getSymbol();
                double volume = Double.parseDouble(ivolume);
                long order = iorder;
                String customComment = icomm;
                long expiration = 0;
                
                TradeTransInfoRecord ttOpenInfoRecord = null;
                
                //open
                if( xtrade.equals("1")) {
                	price = symbolResponse.getSymbol().getAsk();
                	ttOpenInfoRecord = new TradeTransInfoRecord(
                        	TRADE_OPERATION_CODE.BUY, 
                        	TRADE_TRANSACTION_TYPE.OPEN, 
                        	price, sl, tp, symbol, volume, order, customComment, expiration);	
                }
                if( xtrade.equals("2")) {
                	price = symbolResponse.getSymbol().getBid();
                	ttOpenInfoRecord = new TradeTransInfoRecord(
                			TRADE_OPERATION_CODE.SELL, 
                			TRADE_TRANSACTION_TYPE.OPEN, 
                			price, sl, tp, symbol, volume, order, customComment, expiration);
                }

                TradeTransactionResponse tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                System.out.println("tradeTransactionResponse " + tradeTransactionResponse);
                getorder = tradeTransactionResponse.getOrder();
                errcode = tradeTransactionResponse.getErrCode().toString();
                if( errcode.equals("ERR_CODE")) { getorder=0; }
               
                	if( getorder > 0 ){
                	
                	TradeTransactionStatusResponse tradeTransactionStatusResponse 
                	= APICommandFactory.executeTradeTransactionStatusCommand(connector, getorder);

                	System.out.println("tradeTransactionStatusResponse " + tradeTransactionStatusResponse);
                	tradeok= tradeTransactionStatusResponse.getRequestStatus();
                	String tradeoks = tradeok.toString();
                	tradeokl = tradeok.getCode();
                	//System.out.println("tradeok " + tradeok);
                	System.out.println("tradeoks " + tradeoks);
                	//System.out.println("tradeokl " + tradeokl);
                	
                	}
                
        		}

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
        	
        	pDialog.dismiss();
        	
        	SQLiteDatabase db6=(new DatabaseTrades(MakeTradeActivity.this)).getWritableDatabase();
            db6.delete("trades", "_ID > 0", null);
            db6.close();
        	
        	String titlex="";
        	if( xtrade.equals("1")) { titlex=String.format(getResources().getString(R.string.tradeorderednum), getorder); }
        	if( xtrade.equals("2")) { titlex=String.format(getResources().getString(R.string.tradeorderednum), getorder); }

        	if( tradeokl == 3 || tradeokl == 1 ){

        	new AlertDialog.Builder(MakeTradeActivity.this)        	
        	.setMessage(titlex) 
            .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                  
                	Intent i = new Intent(MakeTradeActivity.this, LearningActivity.class);
    	        	Bundle extras = new Bundle();
                    extras.putString("pairx", pair);
                    extras.putInt("whatspage", 1);
                    i.putExtras(extras);                
                    startActivity(i);
                	finish();
                }
             })

             .show();
        	
        	}else{
        		new AlertDialog.Builder(MakeTradeActivity.this)
        		.setMessage(getString(R.string.tradenotmodifyed))
        		.setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) { 
                  
        				Intent i = new Intent(MakeTradeActivity.this, LearningActivity.class);
        	        	Bundle extras = new Bundle();
                        extras.putString("pairx", pair);
                        extras.putInt("whatspage", 1);
                        i.putExtras(extras);                
                        startActivity(i);
                    	finish();
        			}
        		})

        		.show();
        	}


        }
 
    }
    //koniec savetrade
    
    class DeleteTrade extends AsyncTask<String, String, String> {
    	

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MakeTradeActivity.this);
            if( xtrade.equals("3")) { pDialog.setMessage(getString(R.string.progdeltrade)); }
            if( xtrade.equals("4")) { pDialog.setMessage(getString(R.string.progdeltrade)); }
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
 

        protected String doInBackground(String... args) {
        	try {
                
                // Create new connector
        		if( accountx.equals("0")) {
       			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.DEMO);");
       			 connector = new SyncAPIConnector(ServerEnum.DEMO);
       		 	}

       		 	if( accountx.equals("1")) {
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
                
                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, pair);
                System.out.println("symbolResponse " + symbolResponse);
                
                if (loginResponse != null && loginResponse.getStatus())
        		{
                
                double price = symbolResponse.getSymbol().getAsk();
                //System.out.println("price " + price);
                if (isl == null ) {isl="0";}
                if( isl.equals("")) {isl="0";}
                double sl = Double.parseDouble(isl);
                if (itp == null ) {itp="0";}
                if( itp.equals("")) {itp="0";}
                double tp =  Double.parseDouble(itp);
                String symbol = symbolResponse.getSymbol().getSymbol();
                double volume = Double.parseDouble(ivolume);
                long order = iorder;
                String customComment = icomm;
                long expiration = 0;
                
                
                SQLiteDatabase db9=(new DatabaseTrades(MakeTradeActivity.this)).getWritableDatabase();    	    	

                Cursor constantsCursor9=null;
    	    	if( modall == 0 ){
                constantsCursor9=db9.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh, imemo " +
    				"FROM trades WHERE iorder = '" + iorder + "' ORDER BY iorder ",
    				null);
    	    		}
    	    	if( modall == 1 ){
                    constantsCursor9=db9.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh, imemo " +
        				"FROM trades WHERE imemo = '" + icomm + "' ORDER BY iorder ",
        				null);
        	    	}
        	
    	    	constantsCursor9.moveToFirst();
    	    	while(!constantsCursor9.isAfterLast()) {
            	
    	    		//tropenList.add(constantsCursor8.getString(constantsCursor9.getColumnIndex("iopen")));
    	    		order = Long.valueOf(constantsCursor9.getString(constantsCursor9.getColumnIndex("iorder")));
    	    		TradeTransInfoRecord ttOpenInfoRecord = null;

                    //close
                    if( xtrade.equals("3")) {
                    	price = symbolResponse.getSymbol().getBid();
                    	ttOpenInfoRecord = new TradeTransInfoRecord(
                            	TRADE_OPERATION_CODE.BUY, 
                            	TRADE_TRANSACTION_TYPE.CLOSE, 
                            	price, sl, tp, symbol, volume, order, customComment, expiration);	
                    }
                    if( xtrade.equals("4")) {
                    	price = symbolResponse.getSymbol().getAsk();
                    	ttOpenInfoRecord = new TradeTransInfoRecord(
                    			TRADE_OPERATION_CODE.SELL, 
                    			TRADE_TRANSACTION_TYPE.CLOSE, 
                    			price, sl, tp, symbol, volume, order, customComment, expiration);
                    }


                    TradeTransactionResponse tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                    System.out.println("tradeTransactionResponse " + tradeTransactionResponse);
                    getorder = tradeTransactionResponse.getOrder();
                    errcode = tradeTransactionResponse.getErrCode().toString();

    	    		constantsCursor9.moveToNext();
    	    	}
    	    	db9.close();
    	    	

                if( errcode.equals("ERR_CODE")) { getorder=0; }
               
                	if( getorder > 0 ){
                	
                	TradeTransactionStatusResponse tradeTransactionStatusResponse 
                	= APICommandFactory.executeTradeTransactionStatusCommand(connector, getorder);

                	System.out.println("tradeTransactionStatusResponse " + tradeTransactionStatusResponse);
                	tradeok= tradeTransactionStatusResponse.getRequestStatus();
                	String tradeoks = tradeok.toString();
                	tradeokl = tradeok.getCode();
                	//System.out.println("tradeok " + tradeok);
                	System.out.println("tradeoks " + tradeoks);
                	//System.out.println("tradeokl " + tradeokl);
                	
                	}
                
        		}

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
        	
        	pDialog.dismiss();
        	
        	SQLiteDatabase db6=(new DatabaseTrades(MakeTradeActivity.this)).getWritableDatabase();
            db6.delete("trades", "_ID > 0", null);
            db6.close();
        	
        	String titlex="";
        	if( modall == 0 ){
        	if( xtrade.equals("3")) { titlex=String.format(getResources().getString(R.string.tradeclosednum), iorder); }
        	if( xtrade.equals("4")) { titlex=String.format(getResources().getString(R.string.tradeclosednum), iorder); }
        			}
        	if( modall == 1 ){
            	if( xtrade.equals("3")) { titlex=getString(R.string.alltradeclosed); }
            	if( xtrade.equals("4")) { titlex=getString(R.string.alltradeclosed); }
            		}
        	if( tradeokl == 3 || tradeokl == 1 ){

        	new AlertDialog.Builder(MakeTradeActivity.this)        	
        	.setMessage(titlex) 
            .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                  
                	Intent i = new Intent(MakeTradeActivity.this, LearningActivity.class);
    	        	Bundle extras = new Bundle();
                    extras.putString("pairx", pair);
                    extras.putInt("whatspage", 1);
                    i.putExtras(extras);                
                    startActivity(i);
                	finish();
                }
             })

             .show();
        	
        	}else{
        		new AlertDialog.Builder(MakeTradeActivity.this)
        		.setMessage(getString(R.string.tradenotmodifyed))
        		.setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) { 
                  
        				Intent i = new Intent(MakeTradeActivity.this, LearningActivity.class);
        	        	Bundle extras = new Bundle();
                        extras.putString("pairx", pair);
                        extras.putInt("whatspage", 1);
                        i.putExtras(extras);                
                        startActivity(i);
                    	finish();
        			}
        		})

        		.show();
        	}


        }
 
    }
    //koniec deletetrade
}