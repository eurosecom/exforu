/* 
   Extension of FragmentStatePagerAdapter which intelligently caches 
   all active fragments and manages the fragment lifecycles. 
   Usage involves extending from SmartFragmentStatePagerAdapter as you would any other PagerAdapter.
*/

package com.eusecom.exforu;


import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class LearningActivity extends FragmentActivity {

	private SmartFragmentStatePagerAdapter adapterViewPager;
    String strana="0", accountname, accountx;
    static String str1, str2, str3, str4;
    static Drawable drw1, drw2, drw3;
    static ViewPager vpPager;
    static String numless;
	static String nazless;
	static String importantless="";
	static String pairx;
	static int whatspage;
    
    TextView title, idpage;
    Spinner spinner;
    Button btnAgain;
    int repeat=60000;
    
    int currentPosition = 0;
	int changeorient = 0;
	
	private SQLiteDatabase db7=null;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learninglay);
        
        db7=(new DatabaseTemp(this)).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='1', buse='0', trade='0', hist='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();
   	 	
        if (isOnline()) 
        {
        Intent i = getIntent();        
        Bundle extras = i.getExtras();       
        pairx = extras.getString("pairx");
        whatspage = extras.getInt("whatspage");
        
        accountx=SettingsActivity.getAccountx(this);
        
        accountname="DEMO";        
        if( accountx.equals("2")) {
        	accountname="MODEL";
        }
        if( accountx.equals("1")) {
        	accountname="REAL";
        }
        
        title = (TextView) findViewById(R.id.title);
        title.setText(pairx + " " + accountname);      
        
        idpage = (TextView) findViewById(R.id.idpage);
        
        btnAgain = (Button) findViewById(R.id.btnAgain);
        btnAgain.setVisibility(View.GONE);
        
        repeat=1000*Integer.parseInt(SettingsActivity.getStreamf(this));
        
        String repeats = getResources().getString(R.string.again) + " " + SettingsActivity.getStreamf(this) + " sec.";
        btnAgain.setText(repeats);

        numless = "numless";
        nazless = "nazless";
        importantless = "impless";
        
        
        String[] sliderTitles = getResources().getStringArray(R.array.slider_tab_text);
        str1 = sliderTitles[0];
        str2 = sliderTitles[1];
        str3 = sliderTitles[2];
        str4 = sliderTitles[3];
        
        drw1 = getResources().getDrawable( R.drawable.ic_action_merge );
        drw2 = getResources().getDrawable( R.drawable.ic_action_directions );
        drw3 = getResources().getDrawable( R.drawable.ic_action_view_as_list );
        

    	vpPager = (ViewPager) findViewById(R.id.vpPager);
    	adapterViewPager = new MySmartPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        
        vpPager.setOffscreenPageLimit(4);
        
        if (savedInstanceState != null){
			currentPosition = savedInstanceState.getInt("currentPosition");
			changeorient=1;
			vpPager.setCurrentItem(currentPosition);
		  }
  
        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	
        	
            @Override
            public void onPageSelected(int position) {
            	int stranai = position + 1;
            	strana=stranai + "";

            	if( stranai == 1 ) {
            		//Toast.makeText(LearningActivity.this, " " + strana + ". " + str1, Toast.LENGTH_SHORT).show();
            			}
            	if( stranai == 2 ) {
                    //Toast.makeText(LearningActivity.this, " " + strana + ". " + str2, Toast.LENGTH_SHORT).show();
                			}
            	if( stranai == 3 ) {
                    //Toast.makeText(LearningActivity.this, " " + strana + ". " + str3, Toast.LENGTH_SHORT).show();
                			}
            	if( stranai == 4 ) {
                    //Toast.makeText(LearningActivity.this, " " + strana + ". " + str4, Toast.LENGTH_SHORT).show();
                			}
            	
            	if( changeorient == 0 ) {
            		
            	FragmentLifecycle fragmentToHide = (FragmentLifecycle)adapterViewPager.getItem(currentPosition);
    			fragmentToHide.onPauseFragment();
 
    			FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapterViewPager.getItem(position);
    			fragmentToShow.onResumeFragment();

    			
    			currentPosition = position;           		
            	}
    			changeorient=0;
    			whatspage=0;
            	
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
                 spinnerAdapter.add("M1");
                 spinnerAdapter.add("M5");
                 spinnerAdapter.add("M15");
                 spinnerAdapter.add("M30");
                 spinnerAdapter.add("H1");
                 spinnerAdapter.add("H4");
                 spinnerAdapter.add("D1");
                 spinnerAdapter.add("W1");
                 spinnerAdapter.add("MN1");

        spinner.setAdapter(spinnerAdapter);
        
        String periodxy =SettingsActivity.getPeriodx(this);
        spinner.setSelection(spinnerAdapter.getPosition(periodxy),true);
        
        IntentFilter filter = new IntentFilter(ACTION_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        
        }
        else{
        	 

            new AlertDialog.Builder(this)
            .setTitle(getString(R.string.niejeinternet))
            .setMessage(getString(R.string.potrebujeteinternet))
            .setPositiveButton(getString(R.string.textok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                  
                	finish();
                }
             })

             .show();
            

         }
        
        
    }
    //end of oncreate
	
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  savedInstanceState.putInt("currentPosition", currentPosition);
		}
	
	//broadcastreceiver for send value from fragment to activity
  	String KeyWord;
  	public static final String ACTION_INTENT = "com.eusecom.exforu.action.UI_UPDATE_AGAIN";
      protected BroadcastReceiver receiver = new BroadcastReceiver() {


          @Override
          public void onReceive(Context context, Intent intent) {
        	  //Log.d("change again", "I am at onReceive.");
              if(ACTION_INTENT.equals(intent.getAction())) {
            	  
            	  	Bundle extras = intent.getExtras();

            	  	String value = extras.getString("UI_VALUE");
            	  	int xxsp = extras.getInt("UI_XXSP");

            	  	updateAgain(value, xxsp);
              }
          }
      };

      private void updateAgain(String value, int xxxsp) {
          // you probably want this:
    	  String xxxsps=xxxsp + "";
    	  //Log.d("updateAgain " + value, "xxsp " + xxxsps);
    	  btnAgain.setVisibility(View.VISIBLE);
    	  idpage.setText(xxxsps);

      }
      //end of broadcast
	
	public void onResume() {
	    super.onResume();
	    
	    if (isOnline()) 
        {
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                System.out.println("click " + selectedItem);
                //save to preferences and send to CandlesFragment.java
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
             	Editor editor = prefs.edit();
                editor.putString("periodx", selectedItem).apply();
                editor.commit();
                
                System.out.println("strana " + strana);

                	//problems with restarting GetLearningStreamAsyncTask.java
                	//sendValueToFragments(selectedItem, 1);
                	callAgain(0);
   
            }

            public void onNothingSelected(AdapterView<?> parent) 
            {

            }           
        });
        }

	}
	//onresume
	
		//call again learnactivity
		protected void callAgain(int whatspagex) {
	        
	        Intent i = new Intent(this, LearningActivity.class);
	    	Bundle extras = new Bundle();
	        extras.putString("pairx", pairx);
	        extras.putInt("whatspage", whatspagex);
	        i.putExtras(extras);                
	        startActivity(i);
	        finish();
	        
	    }
	
	//sending values from activity to fragments
	protected void sendValueToFragments(String value, int xxsp) {
        // it has to be the same name as in the fragment
        Intent intent = new Intent("com.eusecom.exforu.action.SAVE_TRADE");
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("UI_XXSP", xxsp);
        dataBundle.putString("UI_VALUE", value);
        intent.putExtras(dataBundle);
        
        //Log.d("change ui", "I am at sendValueToFragments.");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        
    }


    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public static class MySmartPagerAdapter extends SmartFragmentStatePagerAdapter {
    private static int NUM_ITEMS = 4;
    private List<Fragment> fragments;

        public MySmartPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            
            this.fragments = new ArrayList<Fragment>();
    		fragments.add(CandlesFragment.newInstance(0, pairx, importantless, whatspage));
    		fragments.add(BuySellFragment.newInstance(2, pairx, importantless));
    		fragments.add(TradesFragment.newInstance(2, pairx, importantless));
    		fragments.add(ImportantFragment.newInstance(1, str4, numless, nazless, importantless));
        }

            // Returns total number of pages
            @Override
            public int getCount() {
                return NUM_ITEMS;
            }

            // Returns the fragment to display for that page
            @Override
            public Fragment getItem(int position) {
            	
            	return fragments.get(position);
            	
            	//better for own lyfecycles is new ArrayList<Fragment>()
                //switch (position) {               
                //case 0: 
                //	return CandlesFragment.newInstance(0, pairx, importantless);
                //case 1: 
                //	return ImportantFragment.newInstance(1, str4, numless, nazless, importantless);
                //case 2:
                //	return TradesFragment.newInstance(2, pairx, importantless);
                //case 3:
                //	Log.d("importantless at getitem", importantless);
                //    return ImportantFragment.newInstance(3, str4, numless, nazless, importantless);
                //default:
                //    return null;
                //}
            }
            
            @Override
   		 	public Object instantiateItem(ViewGroup container, int position) {
   			

   			switch (position) {               
               case 0:
               	//Log.i("instantiateItem 0", "instantiateItem");
               	CandlesFragment fragment0 = (CandlesFragment) super.instantiateItem(container, position);
      		     	fragments.set(0, fragment0);
      		     	return fragment0;
               case 1:
               	//Log.i("instantiateItem 1", "instantiateItem");
               	BuySellFragment fragment1 = (BuySellFragment) super.instantiateItem(container, position);
      		     	fragments.set(1, fragment1);
      		     	return fragment1;
               case 2:
               	//Log.i("instantiateItem 2", "instantiateItem");
               	TradesFragment fragment2 = (TradesFragment) super.instantiateItem(container, position);
      		     	fragments.set(2, fragment2);
      		     	return fragment2;  	
               case 3:
                  	//Log.i("instantiateItem 3", "instantiateItem");
                  	ImportantFragment fragment3 = (ImportantFragment) super.instantiateItem(container, position);
         		     	fragments.set(3, fragment3);
         		     	return fragment3;

               default:
                   return null;
               }
            }

            @Override
            public CharSequence getPageTitle(int position) {
            	
            	switch (position) {
                case 0:
                	SpannableStringBuilder sb1 = new SpannableStringBuilder(" " + str1 + " " ); // space added before text for convenience
                	
                	sb1= coloredSpannable(sb1, position);
                	
                    drw1.setBounds(0, 0, drw1.getIntrinsicWidth(), drw1.getIntrinsicHeight());
                    ImageSpan span1 = new ImageSpan(drw1, ImageSpan.ALIGN_BASELINE);
                    sb1.setSpan(span1, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    
                    return sb1;
                case 1: 

                	SpannableStringBuilder sb2 = new SpannableStringBuilder(" " + str2 + " " ); // space added before text for convenience

                	//sb2.setSpan(new ForegroundColorSpan(Color.BLACK), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                	sb2= coloredSpannable(sb2, position);
                	
                    drw2.setBounds(0, 0, drw2.getIntrinsicWidth(), drw2.getIntrinsicHeight());
                    ImageSpan span2 = new ImageSpan(drw2, ImageSpan.ALIGN_BASELINE);
                    sb2.setSpan(span2, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    
                    return sb2;
                case 2: 
                	SpannableStringBuilder sb3 = new SpannableStringBuilder(" " + str3 + " " ); // space added before text for convenience

                	//sb3.setSpan(new ForegroundColorSpan(Color.BLACK), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                	sb3= coloredSpannable(sb3, position);
                	
                    drw3.setBounds(0, 0, drw3.getIntrinsicWidth(), drw3.getIntrinsicHeight());
                    ImageSpan span3 = new ImageSpan(drw3, ImageSpan.ALIGN_BASELINE);
                    sb3.setSpan(span3, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    
                    return sb3;
                    
                case 3: 

                	SpannableStringBuilder sb4 = new SpannableStringBuilder(" " + str4 + " " ); // space added before text for convenience

                	//sb2.setSpan(new ForegroundColorSpan(Color.BLACK), 4, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                	sb4= coloredSpannable(sb4, position);
                	
                    drw2.setBounds(0, 0, drw2.getIntrinsicWidth(), drw2.getIntrinsicHeight());
                    ImageSpan span4 = new ImageSpan(drw2, ImageSpan.ALIGN_BASELINE);
                    sb4.setSpan(span4, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    
                    return sb4;

                default:
                    return null;
                }

            }
            //end of getPageTitle
            
            private SpannableStringBuilder coloredSpannable(SpannableStringBuilder source, int pos) {
                final SpannableStringBuilder ss = source;
                int dlzkass=ss.length();
                int polkass=dlzkass/2;

                if (vpPager.getCurrentItem() > pos) {
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), polkass, dlzkass, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else {

                }
                if (vpPager.getCurrentItem() < pos) {
                    ss.setSpan(new ForegroundColorSpan(Color.BLACK), 1, polkass, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else {

                }
                return ss;
            }
            

        }
    	//end of SmartFragmentStatePagerAdapter
   
    	public void buttonAgain(View v) {
    	
    	btnAgain.setVisibility(View.GONE);
    	Intent i = new Intent(this, LearningActivity.class);
    	Bundle extras = new Bundle();
        extras.putString("pairx", pairx);
        int whatspagex = Integer.parseInt(idpage.getText().toString());
        extras.putInt("whatspage", whatspagex);
        
        
        i.putExtras(extras);                
        startActivity(i);
        finish();


    	}

	    
	    @Override
	    public void onDestroy() {
	    	


	        super.onDestroy();
	    }
	    //end of destroy
	    
	    //test if internet
	    public boolean isOnline() {
	        ConnectivityManager cm =
	            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getActiveNetworkInfo();
	        if (netInfo != null && netInfo.isConnected()) {
	            return true;
	        }
	        return false;
	    }
	    //end test if internet
	    
	    
	    
	    // Response from MakeTradeActivity.class DROPED
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);

	        if (resultCode == 101) {
	        	//Toast.makeText(LearningActivity.this, "Trade Sell has made", Toast.LENGTH_LONG).show();
	        	//may be will be better start learningactivity again and set page 2
	        	//vpPager.setCurrentItem(2);
	        	sendValueToFragments("1", 1);
	        	callAgain(2);

	        }
	        if (resultCode == 102) {
	        	//Toast.makeText(LearningActivity.this, "Trade Buy has made", Toast.LENGTH_LONG).show();
	        	//vpPager.setCurrentItem(2);
	        	sendValueToFragments("2", 2);
	        	callAgain(2);

	        }

	 
	    }//end of onactivityresult
	    
	    void switchFragment(int target){
	    	vpPager.setCurrentItem(target);
	       }
	    


}//end of activity