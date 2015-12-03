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
import android.widget.Button;
import android.widget.EditText;
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
	TextView actprics;
	TextView actpricb;
	
	EditText inputMnoz;
	EditText inputComment;
	Button btnPlus;
    Button btnMinus;
    Button btnSell;
    Button btnBuy;
    Button btnSellok;
    Button btnBuyok;


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

        if( accountx.equals("0")) {
        	useridl=Long.valueOf(SettingsActivity.getUserId(getActivity()));
        	userpsws=SettingsActivity.getUserPsw(getActivity());
        }else{
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
        View view = inflater.inflate(R.layout.fragment_buysell, container, false);


        actbalance = (TextView) view.findViewById(R.id.actbalance);
        actprofit = (TextView) view.findViewById(R.id.actprofit);
        actprics = (TextView) view.findViewById(R.id.actprics);
        actpricb = (TextView) view.findViewById(R.id.actpricb);
  
        btnSellok = (Button) view.findViewById(R.id.btnSellok);
        btnBuyok = (Button) view.findViewById(R.id.btnBuyok);
    
        // Sell button click event
        btnSell = (Button) view.findViewById(R.id.btnSell);
        btnSell.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	btnBuyok.setVisibility(View.GONE);
            	btnSellok.setVisibility(View.VISIBLE);
                
            }
        });
        
        // Buy button click event
        btnBuy = (Button) view.findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {

            	btnSellok.setVisibility(View.GONE);
            	btnBuyok.setVisibility(View.VISIBLE);
                
            }
        });
        
        inputMnoz = (EditText) view.findViewById(R.id.inputMnoz);
        inputMnoz.setText("0.1");
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
                mnozi = mnozi + 0.1;
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
                if( mnozi > 0.1 ) { mnozi = mnozi - 0.1; }
                mnozi=round(mnozi,2);
                mnozs = mnozi + "";
                inputMnoz.setText(mnozs);
                inputMnoz.requestFocus();
                
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


		    }
		});

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
   		


    	LinkedList<String> listget = new LinkedList<String>();
		String symbolget = pair;
		listget.add(symbolget);

		GetBuySellStreamAsyncTask = new GetBuySellStreamAsyncTask(getActivity(), this, 20, accountx, userpsws, useridl, listget, symbolget, repeat);
		GetBuySellStreamAsyncTask.execute();
   	}

}