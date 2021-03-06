package com.eusecom.exforu;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import pro.xstore.api.sync.Credentials;
import pro.xstore.api.sync.SyncAPIConnector;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.SBalanceRecord;
import pro.xstore.api.message.records.SProfitRecord;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.STradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;

public class GetFavoriteStreamAsyncTask extends AsyncTask<Void, Void, Void> {

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
	String favact;
	private SQLiteDatabase db7=null;
	private Cursor constantsCursor7=null;
	private Activity mActivity;
	
 interface DoSomething {
  //void doInBackground(int progress);
  void doChangeUI();
  void doChangeUI2(String string, long timestamp, double ask, double bid);
  void doChangeUI3(double balance, double equity);
  void doChangeUIerr(String string);
 }

 DoSomething myDoSomethingCallBack;
 int myMax;
 
 SyncAPIConnector connector;
 
 GetFavoriteStreamAsyncTask(Activity activity, DoSomething callback, int max, String account, String userpsw, long userid, LinkedList<String> list
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
	
	Log.d("AsyncTask", "AsyncTask is running");
	
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
        
        
        System.out.println(loginResponse);
        
        
		if (loginResponse != null && loginResponse.getStatus())
		{

			StreamingListener sl = new StreamingListener() {
				
				
				@Override
				public void receiveTradeRecord(STradeRecord tradeRecord) {
					System.out.println("Favorite Stream trade record: " + tradeRecord);
				}

				@Override
				public void receiveBalanceRecord(SBalanceRecord balanceRecord) {
					System.out.println("Favorite Stream balance record: " + balanceRecord);
					
					double balance=balanceRecord.getBalance();
					double equity=balanceRecord.getEquity();
					myDoSomethingCallBack.doChangeUI3(balance, equity);
					
					db7=(new DatabaseTemp(mActivity)).getWritableDatabase();
					constantsCursor7=db7.rawQuery("SELECT _ID, favact "+
							"FROM temppar WHERE _id > 0 ORDER BY _id DESC ",
							null);
				    constantsCursor7.moveToFirst();
				    favact = constantsCursor7.getString(constantsCursor7.getColumnIndex("favact"));
				    constantsCursor7.close();
				    db7.close();
				    Log.d("Async favact 2", favact);
				    if( favact.equals("0")) {
				    	try {
							connector.unsubscribePrice(symbolget);
							connector.unsubscribeBalance();
							connector.unsubscribeProfits();
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
					System.out.println("Favorite Stream profit record: " + profitRecord);
				}
				
				@Override
				public void receiveTickRecord(STickRecord tickRecord) {
					vystupx=vystupx+1;
					String vystupxs=vystupx + "";
					System.out.print("My Favorite " + vystupxs + ". Stream tick record: " + tickRecord);
					String aDataRow= "-> " + tickRecord;
					vystuptxt += aDataRow + "\n" + "\n";

					long timestamp=tickRecord.getTimestamp();
					String symbol=tickRecord.getSymbol();
					double ask=tickRecord.getAsk();
					double bid=tickRecord.getBid();
					
					String aDataRow2= vystupxs + ". -> " + tickRecord.getSymbol() + " Ask = " + tickRecord.getAsk();
					vystuptxt2 += aDataRow2 + "\n" + "\n";
					
					    	 //myDoSomethingCallBack.doChangeUI2(vystuptxt);
					    	 myDoSomethingCallBack.doChangeUI2(symbol, timestamp, ask, bid);
	
				}
			};
			
			connector.connectStream(sl);
			System.out.println("Stream connected.");
			
			//get stream of price one symbol
			//connector.subscribePrice(symbolget);
			//get stream of price more symbols
			connector.subscribePrices(listget);
			connector.subscribeBalance();
			connector.subscribeProfits();

			//Thread.sleep(60000);
			Thread.sleep(repeati);

			connector.unsubscribePrice(symbolget);
			connector.unsubscribeBalance();
			connector.unsubscribeProfits();
		
			
			connector.disconnectStream();
			System.out.println("Stream disconnected.");
			
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
  myDoSomethingCallBack.doChangeUI();
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

}//asynctask