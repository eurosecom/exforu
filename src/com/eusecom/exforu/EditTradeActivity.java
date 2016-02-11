package com.eusecom.exforu;
 

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

 
public class EditTradeActivity extends Activity {
 
	static String pair;
    String xtrade;
    String ivolume;
    String iprice;
    String itp;
    String isl;
    String icomm;
    long iorder=0;
    String userpsws;
	long useridl;
	String accountx;
	SyncAPIConnector connector;
	String errors="";
	private ProgressDialog pDialog;
	
	TextView title;
	TextView actbalance;
	TextView actprofit;
	TextView balancetxt;
	TextView profittxt;
	TextView actprics;
	TextView actpricb;
	
	EditText inputMnoz, inputTp, inputSl;
	EditText inputComment;
	Button btnPlus, btnPlusTp, btnPlusSl;
    Button btnMinus, btnMinusTp, btnMinusSl;
    Button btnSell;
    Button btnBuy;
    Button btnSellok;
    Button btnBuyok;
    
    String startlot, steplot;
    long getorder=0;
    String errcode="";
    REQUEST_STATUS tradeok;
    long tradeokl=0;
    private SQLiteDatabase db7=null;
    private SQLiteDatabase db62=null;
    int modall;

 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     
        setContentView(R.layout.edittrade);
        
        // getting product details from intent
        Intent i = getIntent();
        
        Bundle extras = i.getExtras();
        xtrade = extras.getString("xtrade");
        pair = extras.getString("pairx");
        ivolume = extras.getString("ivolume");
        iprice = extras.getString("iprice");
        itp = extras.getString("itp");
        isl = extras.getString("isl");
        icomm = extras.getString("icomm");
        iorder = extras.getLong("iorder");
        modall = extras.getInt("modall");
        
        String trx="Buy";
        if( xtrade.equals("6")) { trx="Sell"; }
        
        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.edit) + " " + trx + " " + pair 
        		 + " " + getResources().getString(R.string.positiontxt)  + iorder );
        if( modall == 1 ){
        	title.setText(getResources().getString(R.string.edit) + " " + trx + " " + pair 
           		 + " " + getResources().getString(R.string.positionalltxt) + " " + icomm  );	
        }
        
        steplot =SettingsActivity.getSteplot(this);
        accountx=SettingsActivity.getAccountx(this);
        
        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(this));
        	userpsws=SettingsActivity.getUserPsw(this);
        }
        if( accountx.equals("2")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(this));
        	userpsws=SettingsActivity.getUserPsw(this);
        }
        if( accountx.equals("1")) {
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(this));
        	userpsws=SettingsActivity.getUserPswr(this);
        }
        
        db7=(new DatabaseTemp(this)).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='0', trade='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();
        
        actbalance = (TextView) findViewById(R.id.actbalance);
        actprofit = (TextView) findViewById(R.id.actprofit);
        actprics = (TextView) findViewById(R.id.actprics);
        actpricb = (TextView) findViewById(R.id.actpricb);
        balancetxt = (TextView) findViewById(R.id.balancetxt);
        profittxt = (TextView) findViewById(R.id.profittxt);
  
        btnSellok = (Button) findViewById(R.id.btnSellok);
        btnBuyok = (Button) findViewById(R.id.btnBuyok);
        btnSell = (Button) findViewById(R.id.btnSell);
        btnBuy = (Button) findViewById(R.id.btnBuy);
        
        actbalance.setVisibility(View.GONE);
        actprofit.setVisibility(View.GONE);
        balancetxt.setVisibility(View.GONE);
        profittxt.setVisibility(View.GONE);
        
        if( xtrade.equals("5")) { 
        	actpricb.setText(iprice);
        	btnBuyok.setVisibility(View.VISIBLE);
        	btnSell.setVisibility(View.INVISIBLE);
        	}
        if( xtrade.equals("6")) { 
        	actprics.setText(iprice);
        	btnSellok.setVisibility(View.VISIBLE);
        	btnBuy.setVisibility(View.INVISIBLE);
        	}
        
        inputMnoz = (EditText) findViewById(R.id.inputMnoz);
        inputMnoz.setText(ivolume);
        
        inputTp = (EditText) findViewById(R.id.inputTp);
        inputTp.setText(itp);
        
        inputSl = (EditText) findViewById(R.id.inputSl);
        inputSl.setText(isl);
        
        btnMinus = (Button) findViewById(R.id.btnMinus);
        btnMinus.setVisibility(View.GONE);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        btnPlus.setVisibility(View.GONE);
        
        Spinner ispinner = (Spinner) findViewById(R.id.ispinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            spinnerAdapter.add(icomm);
        ispinner.setAdapter(spinnerAdapter);

        
        // Sellok button click event
        btnSellok = (Button) findViewById(R.id.btnSellok);
        btnSellok.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	isl = inputSl.getText().toString();
            	itp = inputTp.getText().toString();
            	
            	//Save trade
            	if( accountx.equals("0")) {
                	new SaveTrade().execute();
                	}
            	if( accountx.equals("1")) {
            	new SaveTrade().execute();
            		}
            	if( accountx.equals("2")) {
                	new SaveTradeModel().execute();
                	}
                
            }
        });
        
        // Buyok button click event
        btnBuyok = (Button) findViewById(R.id.btnBuyok);
        btnBuyok.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	isl = inputSl.getText().toString();
            	itp = inputTp.getText().toString();
            	
            	//Save trade
            	if( accountx.equals("0")) {
                	new SaveTrade().execute();
                	}
            	if( accountx.equals("1")) {
            	new SaveTrade().execute();
            		}
            	if( accountx.equals("2")) {
                	new SaveTradeModel().execute();
                	}
                
            }
        });
        
        
        // Plus button click event
        btnPlusTp = (Button) findViewById(R.id.btnPlusTp);
        btnPlusTp.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputTp.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs); 
                double stepi = Double.parseDouble(steplot);
                mnozi = mnozi + stepi;
                mnozi=round(mnozi,5);
                mnozs = mnozi + "";
                inputTp.setText(mnozs);
                inputTp.requestFocus();
                
            }
        });

        // Minus button click event
        btnMinusTp = (Button) findViewById(R.id.btnMinusTp);
        btnMinusTp.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputTp.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs);
                double stepi = Double.parseDouble(steplot);
                if( mnozi > stepi ) { mnozi = mnozi - stepi; }
                mnozi=round(mnozi,5);
                mnozs = mnozi + "";
                inputTp.setText(mnozs);
                inputTp.requestFocus();
                
            }
        });
        
        // Plus button click event
        btnPlusSl = (Button) findViewById(R.id.btnPlusSl);
        btnPlusSl.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputSl.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs); 
                double stepi = Double.parseDouble(steplot);
                mnozi = mnozi + stepi;
                mnozi=round(mnozi,5);
                mnozs = mnozi + "";
                inputSl.setText(mnozs);
                inputSl.requestFocus();
                
            }
        });

        // Minus button click event
        btnMinusSl = (Button) findViewById(R.id.btnMinusSl);
        btnMinusSl.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputSl.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs);
                double stepi = Double.parseDouble(steplot);
                if( mnozi > stepi ) { mnozi = mnozi - stepi; }
                mnozi=round(mnozi,5);
                mnozs = mnozi + "";
                inputSl.setText(mnozs);
                inputSl.requestFocus();
                
            }
        });
 
    }
    //koniec oncreate
    
    
    class SaveTradeModel extends AsyncTask<String, String, String> {
    	

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditTradeActivity.this);
            pDialog.setMessage(getString(R.string.progmodtrade));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }
 

        protected String doInBackground(String... args) {
        	try {
                
                // Create new connector
       		 	if( accountx.equals("2")) {
       			 //System.out.println("connector = new SyncAPIConnector(ServerEnum.DEMO);");
       			 connector = new SyncAPIConnector(ServerEnum.DEMO);
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
                
                if (loginResponse != null && loginResponse.getStatus())
        		{
                	
                	if (isl == null ) {isl="0";}
                    if( isl.equals("")) {isl="0";}
                    if (itp == null ) {itp="0";}
                    if( itp.equals("")) {itp="0";}
                    
                    if( modall == 0 ){
                    	db62=(new DatabaseModels(EditTradeActivity.this)).getWritableDatabase();
                    	String UpdateSql62 = "UPDATE models SET itp='" + itp + "', isl='" + isl + "' " 
                    		+ " WHERE iorder='" + iorder + "' ";
                    	db62.execSQL(UpdateSql62);
                    	}
                    if( modall == 1 ){
                        db62=(new DatabaseModels(EditTradeActivity.this)).getWritableDatabase();
                        String UpdateSql62 = "UPDATE models SET itp='" + itp + "', isl='" + isl + "' " 
                        		+ " WHERE imemo='" + icomm + "' ";
                 	 	db62.execSQL(UpdateSql62);
                        }


                	getorder=iorder;

                	if( getorder > 0 ){

                    	tradeokl = 1;
                    	
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
        	
        	SQLiteDatabase db6=(new DatabaseTrades(EditTradeActivity.this)).getWritableDatabase();
            db6.delete("trades", "_ID > 0", null);
            db6.close();
        	
        	String titlex="";
        	if( xtrade.equals("5")) { titlex=String.format(getResources().getString(R.string.trademodifyednum), iorder); }
        	if( xtrade.equals("6")) { titlex=String.format(getResources().getString(R.string.trademodifyednum), iorder); }
        	if( modall == 1 ) { titlex=getString(R.string.alltrademodifyed); }

        	
        	if( tradeokl == 3 || tradeokl == 1 ){
        		
        		new AlertDialog.Builder(EditTradeActivity.this)
                .setMessage(titlex)
                .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { 
                      
                    	Intent i = new Intent(EditTradeActivity.this, LearningActivity.class);
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
        		new AlertDialog.Builder(EditTradeActivity.this)
        		.setMessage(getString(R.string.tradenotmodifyed))
        		.setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) { 
                  
        				Intent i = new Intent(EditTradeActivity.this, LearningActivity.class);
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
    //koniec savetrademodel
    
   
 


    class SaveTrade extends AsyncTask<String, String, String> {
    	

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditTradeActivity.this);
            pDialog.setMessage(getString(R.string.progmodtrade));
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
                
                if (loginResponse != null && loginResponse.getStatus())
        		{
                
                SymbolResponse symbolResponse = APICommandFactory.executeSymbolCommand(connector, pair);
                System.out.println("symbolResponse " + symbolResponse);
                
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
                
                SQLiteDatabase db9=(new DatabaseTrades(EditTradeActivity.this)).getWritableDatabase();    	    	

                Cursor constantsCursor9=null;
    	    	if( modall == 0 ){
                constantsCursor9=db9.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh, imemo, iopen " +
    				"FROM trades WHERE iorder = '" + iorder + "' ORDER BY iorder ",
    				null);
    	    		}
    	    	if( modall == 1 ){
                    constantsCursor9=db9.rawQuery("SELECT _ID, itime, iopen, ivolume, iorder, isymbol, idruh, imemo, iopen " +
        				"FROM trades WHERE imemo = '" + icomm + "' ORDER BY iorder ",
        				null);
        	    	}
        	
    	    	constantsCursor9.moveToFirst();
    	    	while(!constantsCursor9.isAfterLast()) {
            	
    	    		//tropenList.add(constantsCursor8.getString(constantsCursor9.getColumnIndex("iopen")));
    	    		order = Long.valueOf(constantsCursor9.getString(constantsCursor9.getColumnIndex("iorder")));
    	    		volume = Double.parseDouble(constantsCursor9.getString(constantsCursor9.getColumnIndex("ivolume")));
    	    		price = Double.parseDouble(constantsCursor9.getString(constantsCursor9.getColumnIndex("iopen")));
    	    		TradeTransInfoRecord ttOpenInfoRecord = null;
           
                
                //modify
                if( xtrade.equals("5")) {
                	ttOpenInfoRecord = new TradeTransInfoRecord(
                        	TRADE_OPERATION_CODE.BUY, 
                        	TRADE_TRANSACTION_TYPE.MODIFY, 
                        	price, sl, tp, symbol, volume, order, customComment, expiration);	
                }
                if( xtrade.equals("6")) {
                	ttOpenInfoRecord = new TradeTransInfoRecord(
                			TRADE_OPERATION_CODE.SELL, 
                			TRADE_TRANSACTION_TYPE.MODIFY, 
                			price, sl, tp, symbol, volume, order, customComment, expiration);
                }
                
                System.out.println("ttOpenInfoRecord " + ttOpenInfoRecord);

                TradeTransactionResponse tradeTransactionResponse = APICommandFactory.executeTradeTransactionCommand(connector, ttOpenInfoRecord);

                System.out.println("tradeTransactionResponse " + tradeTransactionResponse);
                getorder = tradeTransactionResponse.getOrder();
                errcode = tradeTransactionResponse.getErrCode().toString();
                if( errcode.equals("ERR_CODE")) { getorder=0; }
                
                constantsCursor9.moveToNext();
    	    	}
    	    	db9.close();
               
                	if( getorder > 0 ){
                	
                	TradeTransactionStatusResponse tradeTransactionStatusResponse 
                	= APICommandFactory.executeTradeTransactionStatusCommand(connector, getorder);

                	System.out.println("tradeTransactionStatusResponse " + tradeTransactionStatusResponse);
                	tradeok= tradeTransactionStatusResponse.getRequestStatus();
                	System.out.println("tradeok " + tradeok);
                	tradeokl = tradeok.getCode();
                	
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
        	
        	SQLiteDatabase db6=(new DatabaseTrades(EditTradeActivity.this)).getWritableDatabase();
            db6.delete("trades", "_ID > 0", null);
            db6.close();
        	
        	String titlex="";
        	if( xtrade.equals("5")) { titlex=String.format(getResources().getString(R.string.trademodifyednum), iorder); }
        	if( xtrade.equals("6")) { titlex=String.format(getResources().getString(R.string.trademodifyednum), iorder); }
        	if( modall == 1 ) { titlex=getString(R.string.alltrademodifyed); }

        	
        	if( tradeokl == 3 || tradeokl == 1 ){
        		
        		new AlertDialog.Builder(EditTradeActivity.this)
                .setMessage(titlex)
                .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { 
                      
                    	Intent i = new Intent(EditTradeActivity.this, LearningActivity.class);
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
        		new AlertDialog.Builder(EditTradeActivity.this)
        		.setMessage(getString(R.string.tradenotmodifyed))
        		.setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int which) { 
                  
        				Intent i = new Intent(EditTradeActivity.this, LearningActivity.class);
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
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}