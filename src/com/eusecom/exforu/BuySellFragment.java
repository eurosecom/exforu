package com.eusecom.exforu;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eusecom.exforu.GetBuySellStreamAsyncTask.DoSomething;


public class BuySellFragment extends Fragment implements DoSomething, FragmentLifecycle {

	@SuppressWarnings("unused")
	private int page;
	@SuppressWarnings("unused")
	private String impless;
	private String pair;
	
	String userpsws;
	long useridl;
	String accountx;
	int repeat=60000;
	
	GetBuySellStreamAsyncTask GetBuySellStreamAsyncTask;
	

	String periodxy="D1";
	double actpricex=1.09;
	double actprices=1.092;
	double actpriceb=1.088;

    TextView actbalance;
	TextView actprofit;
	EditText actprics;
	EditText actpricb;
	
	EditText inputMnoz, inputTp, inputSl;
	EditText inputComment;
	Button btnPlus, btnPlusTp, btnPlusSl;
    Button btnMinus, btnMinusTp, btnMinusSl;
    Button btnSell;
    Button btnBuy;
    Button btnSellok;
    Button btnBuyok;
    
    String startlot, steplot;
    
    private SQLiteDatabase db7=null;


    // newInstance constructor for creating fragment with arguments
    public static BuySellFragment newInstance(int page, String pairx, String implessx) {
    	BuySellFragment fragmentBuySell = new BuySellFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("pairx", pairx);
        args.putString("importantless", implessx);
        fragmentBuySell.setArguments(args);
        return fragmentBuySell;
    }
    
    //broadcastreceiver for send value from activity to fragment ui
  	String KeyWord;
  	public static final String ACTION_INTENT = "com.eusecom.exforu.action.SAVE_TRADE";
      protected BroadcastReceiver receiver = new BroadcastReceiver() {


          @Override
          public void onReceive(Context context, Intent intent) {
        	  Log.d("change ui", "I am at onReceive BuySellFragment.");
              if(ACTION_INTENT.equals(intent.getAction())) {
            	  
            	  	Bundle extras = intent.getExtras();

            	  	String value = extras.getString("UI_VALUE");
            	  	int xxsp = extras.getInt("UI_XXSP");

            	  	//problems with restarting GetBuySellStreamAsyncTask.java
            	  	updateUIOnReceiverValue(value, xxsp);
              }
          }
      };

      private void updateUIOnReceiverValue(String value, int xxxsp) {
          // you probably want this:
    	  String xxxsps=xxxsp + "";
    	  Log.d("value " + value, "xxsp " + xxxsps);
    	  
    	  if (isOnline()) 
          {
  			GetBuySellStreamAsyncTask.exit();
  			GetBuySellStreamAsyncTask.cancel(true);
          }

      }
      //end of broadcast

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
        pair = getArguments().getString("pairx");
        impless = getArguments().getString("importantless");
        
        accountx=SettingsActivity.getAccountx(getActivity());
        periodxy =SettingsActivity.getPeriodx(getActivity());
        startlot =SettingsActivity.getStartlot(getActivity());
        steplot =SettingsActivity.getSteplot(getActivity());

        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(getActivity()));
        	userpsws=SettingsActivity.getUserPsw(getActivity());
        }
        if( accountx.equals("2")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(getActivity()));
        	userpsws=SettingsActivity.getUserPsw(getActivity());
        }
        if( accountx.equals("1")) {
        	useridl=Long.valueOf(SettingsActivity.getUserIdr(getActivity()));
        	userpsws=SettingsActivity.getUserPswr(getActivity());
        }

        IntentFilter filter = new IntentFilter(ACTION_INTENT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);

    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetBuySellStreamAsyncTask = new GetBuySellStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
        
    }
    //oncreate

    
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_buysell, container, false);


        actbalance = (TextView) view.findViewById(R.id.actbalance);
        actprofit = (TextView) view.findViewById(R.id.actprofit);
        actprics = (EditText) view.findViewById(R.id.actprics);
        actpricb = (EditText) view.findViewById(R.id.actpricb);

        btnSellok = (Button) view.findViewById(R.id.btnSellok);
        btnBuyok = (Button) view.findViewById(R.id.btnBuyok);
    
        // Sell button click event
        btnSell = (Button) view.findViewById(R.id.btnSell);
        btnSell.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	btnBuyok.setVisibility(View.GONE);
            	btnSellok.setVisibility(View.VISIBLE);
            	if( accountx.equals("2")) {
                    actprics.setEnabled(true);
                    actprics.setFocusableInTouchMode(true);
                    }
                
            }
        });
        
        // Buy button click event
        btnBuy = (Button) view.findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	btnSellok.setVisibility(View.GONE);
            	btnBuyok.setVisibility(View.VISIBLE);
            	if( accountx.equals("2")) {
                    actpricb.setEnabled(true);
                    actpricb.setFocusableInTouchMode(true);
                    }
                
            }
        });
        
        inputMnoz = (EditText) view.findViewById(R.id.inputMnoz);
        inputMnoz.setText(startlot);
        inputMnoz.requestFocus();
        
        // Plus button click event
        btnPlus = (Button) view.findViewById(R.id.btnPlus);

        btnPlus.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputMnoz.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs); 
                double stepi = Double.parseDouble(steplot);
                mnozi = mnozi + stepi;
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputMnoz.setText(mnozs);
                inputMnoz.requestFocus();
                
            }
        });

        // Minus button click event
        btnMinus = (Button) view.findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputMnoz.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs);
                double stepi = Double.parseDouble(steplot);
                if( mnozi > stepi ) { mnozi = mnozi - stepi; }
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputMnoz.setText(mnozs);
                inputMnoz.requestFocus();
                
            }
        });
        
        
        Spinner ispinner = (Spinner) view.findViewById(R.id.ispinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

            spinnerAdapter.add(SettingsActivity.getComment1(getActivity()));
            spinnerAdapter.add(SettingsActivity.getComment2(getActivity()));
            spinnerAdapter.add(SettingsActivity.getComment3(getActivity()));

        ispinner.setAdapter(spinnerAdapter);
        ispinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        
        // Sellok button click event
        btnSellok = (Button) view.findViewById(R.id.btnSellok);
        btnSellok.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	btnSellok.setVisibility(View.GONE);
    	    	
    	    	EditText inputMnoz = (EditText) view.findViewById(R.id.inputMnoz);
    	    	EditText inputTp = (EditText) view.findViewById(R.id.inputTp);
    	    	EditText inputSl = (EditText) view.findViewById(R.id.inputSl);
    	    	EditText actprics = (EditText) view.findViewById(R.id.actprics);
    	    	String ivolume = inputMnoz.getText().toString();
    	    	String itp = inputTp.getText().toString();
    	    	String isl = inputSl.getText().toString();
    	    	String actprict = actprics.getText().toString();
    	    	
    	    	Spinner ispinner=(Spinner) view.findViewById(R.id.ispinner);
    	    	String icomm = ispinner.getSelectedItem().toString();

    	    	Intent iu = new Intent(getActivity(), MakeTradeActivity.class);
                Bundle extrasu = new Bundle();
                extrasu.putString("xtrade", "2");
                extrasu.putString("pairx", pair);
                extrasu.putString("ivolume", ivolume);
                extrasu.putString("itp", itp);
                extrasu.putString("isl", isl);
                extrasu.putString("icomm", icomm);
                extrasu.putLong("iorder", 0);
                extrasu.putString("iprice", actprict);
                iu.putExtras(extrasu);
                startActivity(iu);
                getActivity().finish();
                
            }
        });
        
        // Buyok button click event
        btnBuyok = (Button) view.findViewById(R.id.btnBuyok);
        btnBuyok.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	btnBuyok.setVisibility(View.GONE);
    	    	
    	    	EditText inputMnoz = (EditText) view.findViewById(R.id.inputMnoz);
    	    	EditText inputTp = (EditText) view.findViewById(R.id.inputTp);
    	    	EditText inputSl = (EditText) view.findViewById(R.id.inputSl);
    	    	EditText actpricb = (EditText) view.findViewById(R.id.actpricb);
    	    	String ivolume = inputMnoz.getText().toString();
    	    	String itp = inputTp.getText().toString();
    	    	String isl = inputSl.getText().toString();
    	    	String actprict = actpricb.getText().toString();
    	    	
    	    	Spinner ispinner=(Spinner) view.findViewById(R.id.ispinner);
    	    	String icomm = ispinner.getSelectedItem().toString();

    	    	Intent iu = new Intent(getActivity(), MakeTradeActivity.class);
                Bundle extrasu = new Bundle();
                extrasu.putString("xtrade", "1");
                extrasu.putString("pairx", pair);
                extrasu.putString("ivolume", ivolume);
                extrasu.putString("itp", itp);
                extrasu.putString("isl", isl);
                extrasu.putString("icomm", icomm);
                extrasu.putLong("iorder", 0);
                extrasu.putString("iprice", actprict);
                iu.putExtras(extrasu);
                startActivity(iu);
                getActivity().finish();
    	    	
                
            }
        });
        
        inputTp = (EditText) view.findViewById(R.id.inputTp);
        inputTp.setText("0");
        //inputTp.requestFocus();
        
        // Plus button click event
        btnPlusTp = (Button) view.findViewById(R.id.btnPlusTp);
        btnPlusTp.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputTp.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs); 
                double stepi = Double.parseDouble(steplot);
                mnozi = mnozi + stepi;
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputTp.setText(mnozs);
                inputTp.requestFocus();
                
            }
        });

        // Minus button click event
        btnMinusTp = (Button) view.findViewById(R.id.btnMinusTp);
        btnMinusTp.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputTp.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs);
                double stepi = Double.parseDouble(steplot);
                if( mnozi > stepi ) { mnozi = mnozi - stepi; }
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputTp.setText(mnozs);
                inputTp.requestFocus();
                
            }
        });
        
        inputSl = (EditText) view.findViewById(R.id.inputSl);
        inputSl.setText("0");
        //inputSl.requestFocus();
        
        // Plus button click event
        btnPlusSl = (Button) view.findViewById(R.id.btnPlusSl);
        btnPlusSl.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputSl.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs); 
                double stepi = Double.parseDouble(steplot);
                mnozi = mnozi + stepi;
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputSl.setText(mnozs);
                inputSl.requestFocus();
                
            }
        });

        // Minus button click event
        btnMinusSl = (Button) view.findViewById(R.id.btnMinusSl);
        btnMinusSl.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

                //String mnoz = inputMnoz.getText().toString();
                String mnozs = inputSl.getText().toString();
                //int mnozi = Integer.parseInt(mnozs);
                double mnozi = Double.parseDouble(mnozs);
                double stepi = Double.parseDouble(steplot);
                if( mnozi > stepi ) { mnozi = mnozi - stepi; }
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputSl.setText(mnozs);
                inputSl.requestFocus();
                
            }
        });
        
        
        
        return view;
        
    }//oncreateview
    
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {

        	try{
            // launch your AsyncTask here, if the task has not been executed yet
            if(GetBuySellStreamAsyncTask.getStatus().equals(AsyncTask.Status.PENDING)) {
            	
            	GetBuySellStreamAsyncTask.execute();
            }
        	} catch (NullPointerException ex) {

        	}
        }
    }
    
    @Override
    public void doChangeUI4(final double bidp, final double askp, final double sprd, final long timeax) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 String askps=askp + "";
		    	 String bidps=bidp + "";
		    	 actpricb.setText(askps);
		    	 actprics.setText(bidps);

		    }
		});
     
    }
    
    @Override
    public void doChangeUI3(final double balance, final double equity) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 String sbalances = balance + "";
		    	 actbalance.setText(sbalances);
		    	 
		    	 double profitd = equity - balance;
		    	 DecimalFormat df = new DecimalFormat("0.00");             	
             	 String sprofits = df.format(profitd);
             	 sprofits = sprofits.replace(',','.');
             	 
             	 actprofit.setText(sprofits);

		    }
		});
     
    }
    
    @Override
    public void doChangeUI2(final List<String> myopenListx, final List<String> myvolumeListx
    		,final List<String> myorderListx, final List<String> mysymbolListx, final List<String> mytimeListx) {
    	//Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {



		    }
		});
     
    }

    @Override
    public void doChangeUI(final List<String> myopenListx, final List<String> myvolumeListx
    		,final List<String> myorderListx, final List<String> mysymbolListx, final List<String> mytimeListx) {

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {



		    }
		});
 
    }
    
    @Override
    public void doChangeUIerr(final String errs) {

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {
		    	 Toast.makeText(getActivity(), errs, Toast.LENGTH_LONG).show();

		    }
		});

    }
    
    @Override
    public void doChangeUIpost(final String errs) {

    	getActivity().runOnUiThread(new Runnable() {
		     @Override
		     public void run() {

		    	 sendValueToAct("B", 2);
		    }
		});

    }
    
    //sending values from fragment to activity
  	protected void sendValueToAct(String value, int xxsp) {
          // it has to be the same name as in the fragment
          Intent intent = new Intent("com.eusecom.exforu.action.UI_UPDATE_AGAIN");
          Bundle dataBundle = new Bundle();
          dataBundle.putInt("UI_XXSP", xxsp);
          dataBundle.putString("UI_VALUE", value);
          intent.putExtras(dataBundle);
          
          Log.d("change again", "I am at sendValueToAct.");
          LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
          
        
      }
    
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		

		if (isOnline()) 
        {
			GetBuySellStreamAsyncTask.exit();
			GetBuySellStreamAsyncTask.cancel(true);
        }


	}
	//ondestroy
    
    
    //test if internet
    public boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    //end test if internet
    
   
    
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    @Override
   	public void onPauseFragment() {
   		Log.i("BuySellFragment", "onPauseFragment()");
   		Toast.makeText(getActivity(), "onPauseFragment():" + "BuySellFragment", Toast.LENGTH_SHORT).show();
   		
   		db7=(new DatabaseTemp(getActivity())).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='0', trade='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();

		if (isOnline()) 
        {
			GetBuySellStreamAsyncTask.exit();
			GetBuySellStreamAsyncTask.cancel(true);
        }
   	}
   	
   	@Override
   	public void onResumeFragment() {
   		Log.i("BuySellFragment", "onResumeFragment()");
   		Toast.makeText(getActivity(), "onResumeFragment():" + "BuySellFragment", Toast.LENGTH_SHORT).show();
   		
   		db7=(new DatabaseTemp(getActivity())).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='1', trade='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();

    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetBuySellStreamAsyncTask = new GetBuySellStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
		GetBuySellStreamAsyncTask.execute();
   	}
   	

}//end of fragment