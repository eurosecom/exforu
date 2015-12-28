package com.eusecom.exforu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.TextView;
import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SBalanceRecord;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.message.records.SProfitRecord;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.STradeRecord;
import pro.xstore.api.message.records.TradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.message.response.TradesResponse;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;

@SuppressLint("SimpleDateFormat")
public class GetTradesStreamAsyncTask extends AsyncTask<Void, Void, Void> {

	String vystuptxt;
	String vystuptxt2;
	TextView vystup;
	int vystupx=0;
	
	String userpsws;
	long useridl;
	String accountx;
	
	LinkedList<String> listget = new LinkedList<String>();
	String symbolget;
	int repeati;
	String errors="";

	
 interface DoSomething {
  //void doInBackground(int progress);
  void doChangeUI();
  void doChangeUI2(List<String> myopenList, List<String> mycloseList, List<String> mychighList
		  , List<String> mylowList, List<String> mytimeList);
  void doChangeUI3(double balance, double equity);
  void doChangeUI4(double bidp, double askp, double sprd, long timeax);
  void doChangeUIerr(String string);
  void doChangeUIpost(String string);
 }

 DoSomething myDoSomethingCallBack;
 int myMax;
 
 SyncAPIConnector connector;
 
 private SQLiteDatabase db6=null;
 private Activity mActivity;
 private SQLiteDatabase db61=null;
 
 String candl, buse, trade;
 private SQLiteDatabase db72=null;
 private Cursor constantsCursor72=null;
 
 GetTradesStreamAsyncTask(Activity activity, DoSomething callback, int max, String account, String userpsw, long userid
		 , LinkedList<String> list
		 , String symbol, int repeat){
  myDoSomethingCallBack = callback;
  myMax = max;
  accountx=account;
  userpsws=userpsw;
  useridl=userid;
  listget=list;
  symbolget=symbol;
  repeati=repeat;
  mActivity = activity;
 }

 @Override
 protected Void doInBackground(Void... params) {
	    
	vystuptxt=""; vystuptxt2="";
	
	//Log.d("AsyncTask", "AsyncTask is running");
	
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
        
        
        System.out.println("AsyncTrades " + loginResponse);
        
        
		if (loginResponse != null && loginResponse.getStatus())
		{

			StreamingListener sl = new StreamingListener() {
				
				
				@Override
				public void receiveTradeRecord(STradeRecord tradeRecord) {
					//System.out.println("Stream trade record: " + tradeRecord);
					
					getTradesAgain();
					myDoSomethingCallBack.doChangeUI();
					
				}

				@Override
				public void receiveBalanceRecord(SBalanceRecord balanceRecord) {
					//System.out.println("Stream balance record: " + balanceRecord);
					
					double balance=balanceRecord.getBalance();
					double equity=balanceRecord.getEquity();
					myDoSomethingCallBack.doChangeUI3(balance, equity);
					
					db72=(new DatabaseTemp(mActivity)).getWritableDatabase();
					constantsCursor72=db72.rawQuery("SELECT _ID, trade "+
							"FROM temppar WHERE _id > 0 ORDER BY _id DESC ",
							null);
				    constantsCursor72.moveToFirst();
				    trade = constantsCursor72.getString(constantsCursor72.getColumnIndex("trade"));
				    constantsCursor72.close();
				    db72.close();
				    //Log.d("Async trade 2", trade);
				    if( trade.equals("0")) {
				    	try {
				    		connector.unsubscribePrice(symbolget);
							connector.unsubscribeTrades();
							connector.unsubscribeBalance();
						} catch (APICommunicationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						

						connector.disconnectStream();
						System.out.println("Stream disconnected.");
				    }
				}
				
				@Override
				public void receiveProfitRecord(SProfitRecord profitRecord) {
					//System.out.println("Stream profit record: " + profitRecord);
				}
				
				@Override
				public void receiveTickRecord(STickRecord tickRecord) {

					//System.out.println("Stream tick record: " + tickRecord);
					double bidp=tickRecord.getBid();
					double askp=tickRecord.getAsk();
					double sprd=tickRecord.getSpreadTable();
					long timeax=tickRecord.getTimestamp();
					myDoSomethingCallBack.doChangeUI4(bidp, askp, sprd, timeax);
				}
				
				@Override
				public void receiveCandleRecord(SCandleRecord candleRecord) {
					//System.out.println("Stream Candle record: " + candleRecord);
				}
				
				
			};
        
                      
			getTradesAgain();
            
			connector.connectStream(sl);
			System.out.println("AsyncTrades " + "Stream connected.");
			
			//get stream of price one symbol
			connector.subscribePrice(symbolget);
			connector.subscribeTrades();
			//get stream of price more symbols
			//connector.subscribePrices(listget);
			connector.subscribeBalance();
			//connector.subscribeProfits();
			//connector.subscribeCandle(symbolget);

			//Thread.sleep(60000);
			Thread.sleep(repeati);

			connector.unsubscribePrice(symbolget);
			connector.unsubscribeTrades();
			connector.unsubscribeBalance();
			//connector.unsubscribeProfits();
			//connector.unsubscribeCandle(symbolget);
		
			
			connector.disconnectStream();
			System.out.println("AsyncTrades " + "Stream disconnected.");
			
		}

		// Catch errors
     } catch (UnknownHostException e) {
    	 errors = errors + " Unknown Host Exception! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
         e.printStackTrace();
     } catch (IOException e) {
    	 errors = errors + " IO Exception! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
         e.printStackTrace();
     } catch (APICommandConstructionException e) {
    	 errors = errors + " API Command Construction Exception! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
         e.printStackTrace();
     } catch (APICommunicationException e) {
    	 errors = errors + " API Communication Exception! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
         e.printStackTrace();
     } catch (APIReplyParseException e) {
    	 errors = errors + " API Reply ParseException! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
         e.printStackTrace();
     } catch (APIErrorResponse e) {
    	 errors = errors + " API Error Response! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
         e.printStackTrace();
     } catch (InterruptedException e) {
		// TODO Auto-generated catch block
    	 errors = errors + " Interrupted Exception! \n";
    	 myDoSomethingCallBack.doChangeUIerr(errors);
		e.printStackTrace();
	}
        
	 
   return null;
}
   


 @Override
 protected void onPostExecute(Void result) {
  super.onPostExecute(result);
  myDoSomethingCallBack.doChangeUIpost("1");
 
 }
 
 	protected void exit(){

	 try {
		Thread.sleep(10);
		
	 	//connector.unsubscribePrice(symbolget);
		//connector.unsubscribeBalance();
		//connector.unsubscribeProfits();
		if(connector.isConnected()){
		connector.disconnectStream();
		}
	 	} catch (Exception ex) {
			System.err.println(ex);
		}
 	}//exit
 
 
 	protected void getTradesAgain(){

 		System.out.println("getTradesAgain " + accountx);
 		String opent=""; String volumet=""; String ordert=""; String symbolt=""; String timet=""; String cmdt="";
 		String ccoment=""; String tpx=""; String slx="";
 		db6=(new DatabaseTrades(mActivity)).getWritableDatabase();
        db6.delete("trades", "_ID > 0", null);
        
 		if( accountx.equals("0") || accountx.equals("1") ) {
 			 		
 		try {
 			
 		
        TradesResponse tradesresponse = APICommandFactory.executeTradesCommand(connector, true);
        //System.out.println("tradesresponse " + tradesresponse.toString());
        System.out.println("getTradesAgain " + "demo");
        
        for(TradeRecord tradesx : tradesresponse.getTradeRecords()) {

        	
        	//System.out.println(" opentime =" + tradesx.getOpen_time() + " order =" + tradesx.getOrder() 
        	//		 + " openprice =" + tradesx.getOpen_price() + " volume =" + tradesx.getVolume());

        	opent=tradesx.getOpen_price() + ""; 
        	volumet=tradesx.getVolume() + ""; 
        	ordert=tradesx.getOrder() + ""; 
        	symbolt=tradesx.getSymbol() + ""; 
        	timet=tradesx.getOpen_time() + "";
        	cmdt=tradesx.getCmd() + "";
        	ccoment=tradesx.getCustomComment() + "";
        	tpx=tradesx.getTp() + "";
        	slx=tradesx.getSl() + "";
        	
            ContentValues cv6=new ContentValues();
    		
    		cv6.put("itime", timet);
    		cv6.put("iopen", opent);
    		cv6.put("ivolume", volumet);
    		cv6.put("iorder", ordert);
    		cv6.put("isymbol", symbolt);
    		cv6.put("idruh", cmdt);
    		cv6.put("imemo", ccoment);
    		cv6.put("itp", tpx);
    		cv6.put("isl", slx);
    		

    		db6.insert("trades", "time", cv6);

           }            
                      
 		}
           // Catch errors
 	     catch (APICommandConstructionException e) {
 	    	 errors = errors + " API Command Construction Exception! \n";
 	    	 myDoSomethingCallBack.doChangeUIerr(errors);
 	         e.printStackTrace();
 	     } catch (APICommunicationException e) {
 	    	 errors = errors + " API Communication Exception! \n";
 	    	 myDoSomethingCallBack.doChangeUIerr(errors);
 	         e.printStackTrace();
 	     } catch (APIReplyParseException e) {
 	    	 errors = errors + " API Reply ParseException! \n";
 	    	 myDoSomethingCallBack.doChangeUIerr(errors);
 	         e.printStackTrace();
 	     } catch (APIErrorResponse e) {
 	    	 errors = errors + " API Error Response! \n";
 	    	 myDoSomethingCallBack.doChangeUIerr(errors);
 	         e.printStackTrace();
 	     } 
 		
 									}
 		if( accountx.equals("2") )  {
 			
 			System.out.println("getTradesAgain " + "model");
 			
 			try{
 		    		db61=(new DatabaseModels(mActivity)).getWritableDatabase();
 		    		Cursor c = db61.rawQuery("select iopen, ivolume, iorder, isymbol, itime, idruh, imemo, itp, isl from models where iorder >= 0 ORDER BY iorder ;", null);
 		    		if(c.moveToFirst()){    		
 		    			while(!c.isAfterLast()) 
 		    			{

 		    			opent=c.getString(0); 
 		    			volumet=c.getString(1); 
 		    			ordert=c.getString(2); 
 		    			symbolt=c.getString(3); 
 		    			timet=c.getString(4);
 		    			cmdt=c.getString(5);
 		    			ccoment=c.getString(6);
 		    			tpx=c.getString(7);
 		    			slx=c.getString(8);

 		    	    	ContentValues cv61=new ContentValues();
 		    	    	System.out.println("ordert " +  ordert);
 		    	    	
 		    	    	cv61.put("itime", timet);
 		    	    	cv61.put("iopen", opent);
 		    	    	cv61.put("ivolume", volumet);
 		    	    	cv61.put("iorder", ordert);
 		    	    	cv61.put("isymbol", symbolt);
 		    	    	cv61.put("idruh", cmdt);
 		    	    	cv61.put("imemo", ccoment);
 		    	    	cv61.put("itp", tpx);
 		    	    	cv61.put("isl", slx);

 		    	   		db6.insert("trades", "itime", cv61);
 
 		    	   		c.moveToNext();
 		    			}
 		    	}
 		    	c.close();
 		    	db61.close();
 		    	
 			 	}
 				catch (NullPointerException nullPointer)
 				{
            	System.out.println("NPE GetTradesStreamAsyncTsk.java" +  nullPointer);
 				}
 			
 									}
 		
 		db6.close();
 			

 	}//gettradesagain

}//asynctask