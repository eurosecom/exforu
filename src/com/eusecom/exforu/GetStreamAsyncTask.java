package com.eusecom.exforu;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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

public class GetStreamAsyncTask extends AsyncTask<Void, Void, Void> {

	String vystuptxt;
	String vystuptxt2;
	TextView vystup;
	int vystupx=0;
	
	String userpsws;
	long useridl;
	
	LinkedList<String> listget = new LinkedList<String>();
	String symbolget;
	
 interface DoSomething {
  //void doInBackground(int progress);
  void doChangeUI();
  void doChangeUI2(String string, long timestamp, double ask, double bid);
 }

 DoSomething myDoSomethingCallBack;
 int myMax;
 
 GetStreamAsyncTask(DoSomething callback, int max, String userpsw, long userid, LinkedList<String> list, String symbol){
  myDoSomethingCallBack = callback;
  myMax = max;
  userpsws=userpsw;
  useridl=userid;
  listget=list;
  symbolget=symbol;
 }

 @Override
 protected Void doInBackground(Void... params) {

	 //for (int i = 0; i <= myMax; i++) {
  // SystemClock.sleep(100);
  //myDoSomethingCallBack.doInBackground(i);
  //}


	vystuptxt=""; vystuptxt2="";
	
	Log.d("AsyncTask", "AsyncTask is running");
	
	 try {
        
        // Create new connector
		 SyncAPIConnector connector = new SyncAPIConnector(ServerEnum.DEMO);
        
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
					System.out.println("Stream trade record: " + tradeRecord);
				}

				@Override
				public void receiveTickRecord(STickRecord tickRecord) {
					vystupx=vystupx+1;
					String vystupxs=vystupx + "";
					System.out.print("My " + vystupxs + ". Stream tick record: " + tickRecord);
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
			
			//LinkedList<String> listget = new LinkedList<String>();
			//String symbolget = "EURUSD";
			//listget.add(symbolget);

			TickPricesResponse resp = APICommandFactory.executeTickPricesCommand(connector, 0L, listget, 0L);
			for (TickRecord tr : resp.getTicks()) {
				
				System.out.println("TickPrices result: "+tr.getSymbol() + " - ask: " + tr.getAsk());
			}

			connector.connectStream(sl);
			System.out.println("Stream connected.");
			
			//get stream of price
			connector.subscribePrice(symbolget);

			//get stream new just opened, modified or closed trade
			connector.subscribeTrades();

			Thread.sleep(20000);

			connector.unsubscribePrice(symbolget);
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
   


 @Override
 protected void onPostExecute(Void result) {
  super.onPostExecute(result);
  myDoSomethingCallBack.doChangeUI();
 }

}