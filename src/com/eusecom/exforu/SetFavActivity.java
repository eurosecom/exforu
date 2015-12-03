package com.eusecom.exforu;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.Arrays;
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


/**
 * Created by Wasabeef on 2015/01/03.
 */
@SuppressWarnings("deprecation")
public class SetFavActivity extends ActionBarActivity {

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
    
    private static String[] offerpairs = new String[]{
        "EURUSD", "USDJPY", "USDCAD", "AUDUSD", "GBPUSD", "EURGBP"
    };
    
    private List<String> myfavList = new ArrayList<String>();
    
    private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setfav);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        
        db2=(new DatabaseFavpairs(this)).getWritableDatabase();
        
        constantsCursor2=db2.rawQuery("SELECT _ID, pair2, pswd2, name2 "+
				"FROM  favpairs WHERE _id > 0 ORDER BY _id DESC ",
				null);
		

        constantsCursor2.moveToFirst();
        
        final int sizec = constantsCursor2.getCount();
        myfavpairs = new String[sizec];
        
        int ic=0;
        while(!constantsCursor2.isAfterLast()) {

        	myfavpairs[ic] = constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2"));
        	myfavList.add(constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2")));
        	ic=ic+1;
        	constantsCursor2.moveToNext();
        }
        constantsCursor2.close();
        
        Log.d("myfavList = ", myfavList.toString()); 
        

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new FadeInAnimator());
        final SetFavAdapter adapter = new SetFavAdapter(this, new ArrayList<>(Arrays.asList(myfavpairs)));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new FadeInRightAnimator());
        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
                
                int size = offerpairs.length;
                        for (int i = 0; i < size; i++) {
                            System.out.println("Index[" + i + "] = " + offerpairs[i]);
                            spinnerAdapter.add(offerpairs[i]);
                        }
        spinner.setAdapter(spinnerAdapter);



        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
        	
            @Override
            public void onClick(View v) {
            	
            	Spinner spinner = (Spinner) findViewById(R.id.spinner);
            	String spinvalue = spinner.getSelectedItem().toString();          	
            	int indexwhere = myfavList.indexOf(spinvalue);
            	
            	if( indexwhere >= 0 ) { 
            		adapter.remove(indexwhere);
            		myfavList.remove(spinvalue);
            		Log.d("myfavList = ", myfavList.toString());
            		
            		String[] argsx={spinvalue};
            		db2.delete("favpairs", "pair2=?", argsx);
	
            	}
                adapter.add(spinvalue, 0);
                myfavList.add(0, spinvalue);
                Log.d("myfavList = ", myfavList.toString());
                
                ContentValues values=new ContentValues(2);                
        		values.put("pair2", spinvalue);
        		values.put("nick2", "xxx");
        		db2.insert("favpairs", "pairs", values);
            }
        });

        findViewById(R.id.del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	
            	Spinner spinner = (Spinner) findViewById(R.id.spinner);
            	String spinvalue = spinner.getSelectedItem().toString();           	
            	int indexwhere = myfavList.indexOf(spinvalue);
            	
            	
            	if( indexwhere >= 0 ) { 
            		adapter.remove(indexwhere);
            		//myfavList.clear();
            		//myfavList.remove(indexwhere);
            		myfavList.remove(spinvalue);
            		Log.d("myfavList = ", myfavList.toString());
            		
            		String[] argsx={spinvalue};
            		db2.delete("favpairs", "pair2=?", argsx);
            		
            	}
            	
            }
        });
        
        
        
        
    }//oncreate
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		constantsCursor2.close();
		db2.close();

	}
	//ondestroy
}
