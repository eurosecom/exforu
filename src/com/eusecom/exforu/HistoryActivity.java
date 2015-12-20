package com.eusecom.exforu;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.eusecom.exforu.animators.BaseItemAnimator;
import com.eusecom.exforu.animators.FadeInAnimator;
import com.eusecom.exforu.animators.FadeInDownAnimator;
import com.eusecom.exforu.animators.FadeInLeftAnimator;
import com.eusecom.exforu.animators.FadeInRightAnimator;
import com.eusecom.exforu.animators.FadeInUpAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInLeftYAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInRightYAnimator;
//import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;
//import jp.wasabeef.recyclerview.animators.LandingAnimator;
//import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInRightAnimator;
//import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
//import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import com.eusecom.exforu.GetFavoriteStreamAsyncTask.DoSomething;


/**
 * Created by Wasabeef on 2015/01/03.
 */
@SuppressWarnings("deprecation")
public class HistoryActivity extends ActionBarActivity implements DoSomething{

    enum Type {
        FadeIn(new FadeInAnimator()),
        FadeInDown(new FadeInDownAnimator()),
        FadeInUp(new FadeInUpAnimator()),
        FadeInLeft(new FadeInLeftAnimator()),
        FadeInRight(new FadeInRightAnimator()),
        //Landing(new LandingAnimator()),
        //ScaleIn(new ScaleInAnimator()),
        //ScaleInTop(new ScaleInTopAnimator()),
        //ScaleInBottom(new ScaleInBottomAnimator()),
        //ScaleInLeft(new ScaleInLeftAnimator()),
        //ScaleInRight(new ScaleInRightAnimator()),
        //FlipInTopX(new FlipInTopXAnimator()),
        //FlipInBottomX(new FlipInBottomXAnimator()),
        //FlipInLeftY(new FlipInLeftYAnimator()),
        //FlipInRightY(new FlipInRightYAnimator()),
        //SlideInLeft(new SlideInLeftAnimator()),
        //SlideInRight(new SlideInRightAnimator()),
        //SlideInDown(new SlideInDownAnimator()),
        //SlideInUp(new SlideInUpAnimator()),
        //OvershootInRight(new OvershootInRightAnimator()),
        //OvershootInLeft(new OvershootInLeftAnimator())
        ;

        private BaseItemAnimator mAnimator;

        Type(BaseItemAnimator animator) {
            mAnimator = animator;
        }

        public BaseItemAnimator getAnimator() {
            return mAnimator;
        }
    }
    
    private static String[] myfavpairs = null;
    private static String[] myaskprices = null;
    private static String[] mybidprices = null;
    private static String[] myprofits = null;
    private static String[] myidevents = null;
    
    private List<String> myfavList = new ArrayList<String>();
    private LinkedList<String> myfavLinkedList = new LinkedList<String>();
    
    private SQLiteDatabase db21=null;
	private Cursor constantsCursor21=null;
	
	String vystuptxt;
    
    String userpsws;
	long useridl;
	String accountx;
	int myProgress;
	
	HistoryAdapter adapter;
	RecyclerView recyclerView;
	private List<String> myAskList = new ArrayList<String>();
	private List<String> myBidList = new ArrayList<String>();
	private List<String> myProfitList = new ArrayList<String>();
	
	String favact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db21=(new DatabaseHistoryEvents(this)).getWritableDatabase();

        constantsCursor21=db21.rawQuery("SELECT _ID, pair2, pswd2, name2 "+
				"FROM  historyevents WHERE _id > 0 ORDER BY _id DESC ",
				null);
        
        constantsCursor21.moveToFirst();
        
        final int sizec = constantsCursor21.getCount();
        myfavpairs = new String[sizec]; myidevents = new String[sizec];
        myaskprices = new String[sizec]; mybidprices = new String[sizec]; myprofits = new String[sizec];
        
        int ic=0;
        while(!constantsCursor21.isAfterLast()) {
        	
        	myfavpairs[ic] = constantsCursor21.getString(constantsCursor21.getColumnIndex("pair2"));
        	myidevents[ic] = constantsCursor21.getString(constantsCursor21.getColumnIndex("_id"));
        	myaskprices[ic] = ""; mybidprices[ic] = ""; myprofits[ic] = "";
        	myfavList.add(constantsCursor21.getString(constantsCursor21.getColumnIndex("pair2")));
        	myfavLinkedList.add(constantsCursor21.getString(constantsCursor21.getColumnIndex("pair2")));
        	ic=ic+1;
        	constantsCursor21.moveToNext();
        }
        constantsCursor21.close();
        
        //Log.d("myfavList = ", myfavList.toString());
        //Log.d("myidevents = ", myidevents[1]);

		if (isOnline()) 
        {

	        myAskList = new ArrayList<>(Arrays.asList(myaskprices));
	        myBidList = new ArrayList<>(Arrays.asList(mybidprices));
	        myProfitList = new ArrayList<>(Arrays.asList(myprofits));
	        
	        recyclerView = (RecyclerView) findViewById(R.id.list);
	        recyclerView.setLayoutManager(new LinearLayoutManager(this));
	        recyclerView.setItemAnimator(new FadeInAnimator());	        
	        adapter = new HistoryAdapter(this, new ArrayList<>(Arrays.asList(myfavpairs)),
	        		new ArrayList<>(Arrays.asList(myidevents)), 
	        		myAskList, myBidList, myProfitList);
	        recyclerView.setAdapter(adapter);
	        recyclerView.setItemAnimator(new FadeInRightAnimator());
	        recyclerView.getItemAnimator().setAddDuration(300);
	        recyclerView.getItemAnimator().setRemoveDuration(300);
	        

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
        

        

    }//oncreate
    


    
    @Override
    public void doChangeUI2(final String symbol, final long timestamp, final double ask, final double bid) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {


		    }
		});
     
    }
    
    @Override
    public void doChangeUI3(final double balance, final double equity) {
     //Toast.makeText(StreamActivity.this, "Finish", Toast.LENGTH_LONG).show();
    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {


		    }
		});
     
    }

    @Override
    public void doChangeUI() {


    }
    
    @Override
    public void doChangeUIerr(final String errs) {

    	runOnUiThread(new Runnable() {
		     @Override
		     public void run() {


		    }
		});
    	

    }
    
    
  
    
    @Override
	public void onDestroy() {
		super.onDestroy();
		
		constantsCursor21.close();
		db21.close();



	}
	//ondestroy
    
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
    
}//activity
