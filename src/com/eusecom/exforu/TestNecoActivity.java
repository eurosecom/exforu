/* I do not use this script now.
*/

package com.eusecom.exforu;

/**
 * Testing own lyfecycles for PagerAdapter with Fragments
 * 
 */

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ViewGroup;

public class TestNecoActivity extends FragmentActivity {

	private MyLocalPagerAdapter pageAdapter;
	int currentPosition = 0;
	int changeorient = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_testneco);
		
		Log.i("onCreate", "onCreate");

		pageAdapter = new MyLocalPagerAdapter(getSupportFragmentManager());
		ViewPager pager = (ViewPager)findViewById(R.id.myViewPager);
		pager.setAdapter(pageAdapter);
		
		if (savedInstanceState != null){
			currentPosition = savedInstanceState.getInt("currentPosition");
			changeorient=1;
			pager.setCurrentItem(currentPosition);
		  }
		
		pager.setOnPageChangeListener(pageChangeListener);
	}
	
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  savedInstanceState.putInt("currentPosition", currentPosition);
		}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		

		@Override
		public void onPageSelected(int newPosition) {
			
			if( changeorient == 0 ) {
			Log.i("onPageSelected", "onPageSelected");
			
			FragmentLifecycle fragmentToHide = (FragmentLifecycle)pageAdapter.getItem(currentPosition);
			fragmentToHide.onPauseFragment();

			FragmentLifecycle fragmentToShow = (FragmentLifecycle)pageAdapter.getItem(newPosition);
			fragmentToShow.onResumeFragment();
			
			currentPosition = newPosition;
			}
			changeorient=0;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) { }
		
		public void onPageScrollStateChanged(int arg0) { }
	};
	
	
	public class MyLocalPagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragments;

		public MyLocalPagerAdapter(FragmentManager fm) {
			super(fm);
			fragments = new ArrayList<Fragment>();
			fragments.add(new FragmentBlue());
			fragments.add(new FragmentGreen());
			fragments.add(new FragmentPink());
		}
		
		@Override
		 public Object instantiateItem(ViewGroup container, int position) {
			
			
			switch (position) {               
            case 0:
            	Log.i("instantiateItem 0", "instantiateItem");
            	FragmentBlue fragmentb = (FragmentBlue) super.instantiateItem(container, position);
   		     	fragments.set(0, fragmentb);
   		     	return fragmentb;
            case 1:
            	Log.i("instantiateItem 1", "instantiateItem");
            	FragmentGreen fragmentg = (FragmentGreen) super.instantiateItem(container, position);
   		     	fragments.set(1, fragmentg);
   		     	return fragmentg;
            case 2:
            	Log.i("instantiateItem 2", "instantiateItem");
            	FragmentPink fragmentp = (FragmentPink) super.instantiateItem(container, position);
   		     	fragments.set(2, fragmentp);
   		     	return fragmentp;

            default:
                return null;
            }
			
		 }

		@Override
		public Fragment getItem(int position) {
			Log.i("getItem " + position, "getItem");
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}

	

}
