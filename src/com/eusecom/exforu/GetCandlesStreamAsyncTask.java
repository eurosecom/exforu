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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pro.xstore.api.message.command.APICommandFactory;
import pro.xstore.api.message.error.APICommandConstructionException;
import pro.xstore.api.message.error.APICommunicationException;
import pro.xstore.api.message.error.APIReplyParseException;
import pro.xstore.api.message.records.RateInfoRecord;
import pro.xstore.api.message.records.SBalanceRecord;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.message.records.SProfitRecord;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.STradeRecord;
import pro.xstore.api.message.response.APIErrorResponse;
import pro.xstore.api.message.response.ChartResponse;
import pro.xstore.api.message.response.LoginResponse;
import pro.xstore.api.streaming.StreamingListener;
import pro.xstore.api.sync.ServerData.ServerEnum;
import pro.xstore.api.message.codes.PERIOD_CODE;

@SuppressLint("SimpleDateFormat")
public class GetCandlesStreamAsyncTask extends AsyncTask<Void, Void, Void> {

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

	private List<String> myopenList = new ArrayList<String>();
	private List<String> mycloseList = new ArrayList<String>();
	private List<String> myhighList = new ArrayList<String>();
	private List<String> mylowList = new ArrayList<String>();
	private List<String> mytimeList = new ArrayList<String>();
	
 interface DoSomething {
  //void doInBackground(int progress);
  void doChangeUI(List<String> myopenList, List<String> mycloseList, List<String> mychighList
		  , List<String> mylowList, List<String> mytimeList);
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
 
 private SQLiteDatabase db3=null;
 private Activity mActivity;
 
 	String candl, buse, trade;
	private SQLiteDatabase db71=null;
	private Cursor constantsCursor71=null;
 
 GetCandlesStreamAsyncTask(Activity activity, DoSomething callback, int max, String account, String userpsw, long userid
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
        
        
        System.out.println("AsyncCandles " + loginResponse);
        
        
		if (loginResponse != null && loginResponse.getStatus())
		{

			StreamingListener sl = new StreamingListener() {
				
				
				@Override
				public void receiveTradeRecord(STradeRecord tradeRecord) {
					System.out.println("Candles Stream trade record: " + tradeRecord);
				}

				@Override
				public void receiveBalanceRecord(SBalanceRecord balanceRecord) {
					//System.out.println("Candle Stream balance record: " + balanceRecord);
					
					double balance=balanceRecord.getBalance();
					double equity=balanceRecord.getEquity();
					myDoSomethingCallBack.doChangeUI3(balance, equity);
					
					db71=(new DatabaseTemp(mActivity)).getWritableDatabase();
					constantsCursor71=db71.rawQuery("SELECT _ID, candl "+
							"FROM temppar WHERE _id > 0 ORDER BY _id DESC ",
							null);
				    constantsCursor71.moveToFirst();
				    candl = constantsCursor71.getString(constantsCursor71.getColumnIndex("candl"));
				    constantsCursor71.close();
				    db71.close();
				    //Log.d("Async candl 2", candl);
				    if( candl.equals("0")) {
				    	try {
				    		connector.unsubscribePrice(symbolget);
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
					System.out.println("Candles Stream profit record: " + profitRecord);
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
					System.out.println("Candles Stream Candle record: " + candleRecord);
				}
				
				
			};
        
            db3=(new DatabaseCandles(mActivity)).getWritableDatabase();
            db3.delete("candles", "_ID > 0", null);
            
            long unixTime = System.currentTimeMillis();
            //System.out.println("1434723162180 unixTime=" + unixTime);
            		
            String periodxy =SettingsActivity.getPeriodx(mActivity);
            PERIOD_CODE PERIOD_CODEXy=PERIOD_CODE.PERIOD_D1; long unixTimeod = unixTime - 8640000000L;
            if( periodxy.equals("M1")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_M1; unixTimeod = unixTime - 6000000; }
            if( periodxy.equals("M5")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_M5; unixTimeod = unixTime - 30000000; }
            if( periodxy.equals("M15")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_M15; unixTimeod = unixTime - 90000000; }
            if( periodxy.equals("M30")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_M30; unixTimeod = unixTime - 180000000; }
            if( periodxy.equals("H1")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_H1; unixTimeod = unixTime - 360000000; }
            if( periodxy.equals("H4")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_H4; unixTimeod = unixTime - 1440000000; }
            //200 dni
            if( periodxy.equals("D1")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_D1; unixTimeod = unixTime - 17280000000L; }
            if( periodxy.equals("W1")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_W1; unixTimeod = unixTime - 60480000000L; }
            if( periodxy.equals("MN1")) { PERIOD_CODEXy=PERIOD_CODE.PERIOD_MN1; unixTimeod = unixTime - 241920000000L; }

            String opens=""; String closes=""; String highs=""; String lows=""; String times="";
			ChartResponse chartresponse = APICommandFactory.executeChartLastCommand(connector, symbolget, PERIOD_CODEXy, unixTimeod);
            for(RateInfoRecord chartx : chartresponse.getRateInfos()) {

            	//long dv = Long.valueOf(chartx.getCtm());
            	//Date df = new java.util.Date(dv);
            	//String vv = new SimpleDateFormat("dd.MM.yyyy hh:mma").format(df);
            	
            	//System.out.println("-> date =" + vv + " open =" + chartx.getOpen() + " high =" + chartx.getHigh()
            	//		 + " low =" + chartx.getLow() + " close =" + chartx.getClose());
            	opens=chartx.getOpen() + ""; closes=chartx.getClose() + ""; 
            	highs=chartx.getHigh() + ""; lows=chartx.getLow() + ""; times=chartx.getCtm() + "";
            	
            	myopenList.add(opens); mycloseList.add(closes); myhighList.add(highs); mylowList.add(lows);
            	mytimeList.add(times);
            	
            	ContentValues cv3=new ContentValues();
        		
        		cv3.put("time", times);
        		cv3.put("open", opens);
        		cv3.put("close", closes);
        		cv3.put("high", highs);
        		cv3.put("low", lows);
        		cv3.put("druh", "1");
        		

        		db3.insert("candles", "time", cv3);

               }
            db3.close();
            //System.out.println("chart -> " + chartresponse.toString());

            myDoSomethingCallBack.doChangeUI(myopenList, mycloseList, myhighList, mylowList, mytimeList);

			connector.connectStream(sl);
			System.out.println("AsyncCandles " + "Stream connected.");
			
			//get stream of price one symbol
			connector.subscribePrice(symbolget);			
			//get stream of price more symbols
			//connector.subscribePrices(listget);
			connector.subscribeBalance();
			//connector.subscribeProfits();
			//connector.subscribeCandle(symbolget);

			//Thread.sleep(60000);
			Thread.sleep(repeati);

			connector.unsubscribePrice(symbolget);
			connector.unsubscribeBalance();
			//connector.unsubscribeProfits();
			//connector.unsubscribeCandle(symbolget);
		
			
			connector.disconnectStream();
			System.out.println("AsyncCandles " + "Stream disconnected.");
			
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

}//asynctask