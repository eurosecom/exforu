package com.eusecom.exforu;

/* The Projekt began 1.6.2015 */

import java.util.ArrayList;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.FragmentManager;

import com.eusecom.exforu.adapter.NavDrawerListAdapter;
import com.eusecom.exforu.model.NavDrawerItem;


@SuppressWarnings("deprecation")
public class EXforUActivity extends ActionBarActivity {

	
	/**
	 * The Drawer
	 */
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
	
	Button lessons_button;
	Menu menu;
	
	private SQLiteDatabase db2=null;
	private Cursor constantsCursor2=null;
	int favx=0;
	int positionx=0;
	private SQLiteDatabase db7=null;
	String myfavpair, accountx, accountname;
	TextView title;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_xfor);

		Toolbar mToolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(mToolbar);
		
		accountx=SettingsActivity.getAccountx(this);

		accountname="DEMO";        
        if( accountx.equals("2")) {
        	accountname="MODEL";
        }
        if( accountx.equals("1")) {
        	accountname="REAL";
        }
		
		title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.app_name) + " " + accountname);
		
		db7=(new DatabaseTemp(this)).getWritableDatabase();        
        String UpdateSql7 = "UPDATE temppar SET favact='0', candl='0', buse='0', trade='0', hist='0' WHERE _id > 0 ";
   	 	db7.execSQL(UpdateSql7);
   	 	db7.close();

		
		db2=(new DatabaseFavpairs(this)).getWritableDatabase();       
        constantsCursor2=db2.rawQuery("SELECT _ID, pair2, pswd2, name2 "+
				"FROM  favpairs WHERE _id > 0 ORDER BY _id DESC ",
				null);
		

        favx = constantsCursor2.getCount();
        constantsCursor2.moveToFirst();
        myfavpair=constantsCursor2.getString(constantsCursor2.getColumnIndex("pair2"));
        Log.i("myfavpair ", myfavpair);
        constantsCursor2.close();

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

            spinnerAdapter.add("EUR");
            spinnerAdapter.add("USD");

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //recyclerView.setItemAnimator(Type.values()[position].getAnimator());
                //recyclerView.getItemAnimator().setAddDuration(300);
                //recyclerView.getItemAnimator().setRemoveDuration(300);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

		 
        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
 
        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        
        String nav1 = String.format(navMenuTitles[1], myfavpair);
        navDrawerItems.add(new NavDrawerItem(nav1, navMenuIcons.getResourceId(1, -1)));
        
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(3, -1)));
 
        String favxs=favx + "";
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(2, -1), true, favxs));
        
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
        
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));
         
 
        // Recycle the typed array
        navMenuIcons.recycle();
 
        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name1, // nav drawer open - description for accessibility ??
                R.string.app_name2 // nav drawer close - description for accessibility ??
        ) {
            public void onDrawerClosed(View view) {
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
 
        if (savedInstanceState == null) {
            // on first time display view for first nav item


        	HomeFragment fragment = new HomeFragment();
        	FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        	
        }

	
	}
	//end of oncreate

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        
        switch (item.getItemId()) {
		case R.id.xfavorite:

			positionx=4;
			//startActivity(new Intent(this, AnimatorSampleActivity.class));
			startActivity(new Intent(this, SetFavActivity.class));
				
			return(true);

			
		case R.id.preferences:
			
			if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
				startActivity(new Intent(this, SettingsActivity.class));
			}
			else {
				startActivity(new Intent(this, EditPreferencesNew.class));
			}



			return(true);
			
			
		case R.id.xdemo:
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
         	Editor editor = prefs.edit();
         	editor.putString("accountx", "0").apply();
         	editor.commit();
         	
         	accountx="0";
    		accountname="DEMO";
    		title.setText(getResources().getString(R.string.app_name) + " " + accountname);

			return(true);
			
			
		case R.id.xreal:
			
			SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
         	Editor editor1 = prefs1.edit();
         	editor1.putString("accountx", "0").apply();
         	editor1.commit();
         	
         	accountx="1";
    		accountname="REAL";
    		title.setText(getResources().getString(R.string.app_name) + " " + accountname);

			return(true);
			
			
		case R.id.xmodel:
			
			SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
         	Editor editor2 = prefs2.edit();
         	editor2.putString("accountx", "2").apply();
         	editor2.commit();
         	
         	accountx="2";
    		accountname="MODEL";
    		title.setText(getResources().getString(R.string.app_name) + " " + accountname);

			return(true);
			
		

	
		default:
			return super.onOptionsItemSelected(item);
		}

    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



		
		/**
	     * Diplaying fragment view for selected nav drawer list item
	     * */
	    private void displayView(int position) {

	    	positionx=position;
	    	
	        switch (position) {
	        case 0:
	        	
	        		if (android.os.Build.VERSION.SDK_INT>=16) {
            		
                    Intent slideactivity = new Intent(EXforUActivity.this, FavoriteActivity.class);
              	   
      				Bundle bndlanimation =
      						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
      				startActivity(slideactivity, bndlanimation);
                	}else{
                	
                        Intent i = new Intent(getApplicationContext(), FavoriteActivity.class);
                        startActivity(i);
                	}
	        	

	            break;
	                
	        case 1:
	        		/*
	        		if (android.os.Build.VERSION.SDK_INT>=16) {
            		
                    Intent slideactivity = new Intent(EXforUActivity.this, XstoreConnectActivity.class);
              	   
      				Bundle bndlanimation =
      						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
      				startActivity(slideactivity, bndlanimation);
                	}else{
                	
                        Intent i = new Intent(getApplicationContext(), XstoreConnectActivity.class);
                        startActivity(i);
                	}


	        		if (android.os.Build.VERSION.SDK_INT>=16) {
            		
                    Intent slideactivity = new Intent(EXforUActivity.this, FavTxtActivity.class);
              	   
      				Bundle bndlanimation =
      						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
      				startActivity(slideactivity, bndlanimation);
                	}else{
                	
                        Intent i = new Intent(getApplicationContext(), FavTxtActivity.class);
                        startActivity(i);
                	}


	        		if (android.os.Build.VERSION.SDK_INT>=16) {
            		
                    Intent slideactivity = new Intent(EXforUActivity.this, StreamActivity.class);
              	   
      				Bundle bndlanimation =
      						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
      				startActivity(slideactivity, bndlanimation);
                	}else{
                	
                        Intent i = new Intent(getApplicationContext(), StreamActivity.class);
                        startActivity(i);
                	}
                	*/
                	

	        	Intent i = new Intent(this, LearningActivity.class);
	        	Bundle extras = new Bundle();
                extras.putString("pairx", myfavpair);
                extras.putInt("whatspage", 0);
                i.putExtras(extras);                
                startActivity(i);
	        	
	            break;
	            
	        case 2:

	        		if (android.os.Build.VERSION.SDK_INT>=16) {
            		
                    Intent slideactivity = new Intent(EXforUActivity.this, HistoryActivity.class);
              	   
      				Bundle bndlanimation =
      						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
      				startActivity(slideactivity, bndlanimation);
                	}else{
                	
                        Intent ih = new Intent(getApplicationContext(), HistoryActivity.class);
                        startActivity(ih);
                	}
	        	
	            break;
	            
	        case 3:
	        	//Toast.makeText(EXforUActivity.this, "4", Toast.LENGTH_SHORT).show();
	        	startActivity(new Intent(this, SetFavActivity.class));
	        	
	            break;
	        case 4:
	            //fragment = new HomeFragment();
	        	//Toast.makeText(EXforUActivity.this, "5", Toast.LENGTH_SHORT).show();
	        	
	        	if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
					startActivity(new Intent(this, SettingsActivity.class));
				}
				else {
					startActivity(new Intent(this, EditPreferencesNew.class));
				}

	            break;
	            
	            
	        case 5:

        		if (android.os.Build.VERSION.SDK_INT>=16) {
        		
                Intent slideactivity = new Intent(EXforUActivity.this, CreateDemoActivity.class);
          	   
  				Bundle bndlanimation =
  						ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animation_toleft,R.anim.animation_toleft2).toBundle();
  				startActivity(slideactivity, bndlanimation);
            	}else{
            	
                    Intent ih = new Intent(getApplicationContext(), CreateDemoActivity.class);
                    startActivity(ih);
            	}
        	
        		break;
	            

	            /*
	        	//Toast.makeText(EXforUActivity.this, "4", Toast.LENGTH_SHORT).show();
	        	startActivity(new Intent(this, TestNecoActivity.class));
	        	*/

	 
	        default:
	            break;
	        }
	        
	 
	    }
	 

	    
	    /**
	     * Slide menu item click listener
	     * */
	    private class SlideMenuClickListener implements
	            ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position,
	                long id) {

	            // display view for selected nav drawer item
	            displayView(position);
	        }
	    }
	    
	    @Override
	    public void onResume(){
	        super.onResume();
	        
	        if( positionx == 5 ) { setDrawer(); }

	    }
	    //onresume
	    
	    public void setDrawer(){

	        db2=(new DatabaseFavpairs(this)).getWritableDatabase();       
	        constantsCursor2=db2.rawQuery("SELECT _ID, pair2, pswd2, name2 "+
					"FROM  favpairs WHERE _id > 0 ORDER BY _id DESC ",
					null);
			

	        favx = constantsCursor2.getCount();
	        constantsCursor2.close();
	        
	        // load slide menu items
	        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
	        
	        // nav drawer icons from resources
	        navMenuIcons = getResources()
	                .obtainTypedArray(R.array.nav_drawer_icons);
	        
	        
	        navDrawerItems = new ArrayList<NavDrawerItem>();
	        
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
	        
	        String nav1 = String.format(navMenuTitles[1], myfavpair);
	        navDrawerItems.add(new NavDrawerItem(nav1, navMenuIcons.getResourceId(1, -1)));
	        
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(3, -1)));
	 
	        String favxs=favx + "";
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(2, -1), true, favxs));
	        
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
	        
	        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(4, -1)));
	        
	        // Recycle the typed array
	        navMenuIcons.recycle();

	        // setting the nav drawer list adapter
	        adapter = new NavDrawerListAdapter(getApplicationContext(),
	                navDrawerItems);
	        mDrawerList.setAdapter(adapter);

	    }//setDrawer


	      
	
//end of activity	
}
