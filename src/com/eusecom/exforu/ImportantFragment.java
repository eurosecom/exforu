package com.eusecom.exforu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ImportantFragment extends Fragment implements FragmentLifecycle {
    // Store instance variables
	@SuppressWarnings("unused")
	private String title;
    @SuppressWarnings("unused")
	private int page;
    @SuppressWarnings("unused")
	private String nazless;
    @SuppressWarnings("unused")
	private String numless;
	private String impless;


    // newInstance constructor for creating fragment with arguments
    public static ImportantFragment newInstance(int page, String title, String numless, String nazless, String implessx) {
    	ImportantFragment fragmentImportant = new ImportantFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putString("nazless", nazless);
        args.putString("numless", numless);
        args.putString("importantless", implessx);
        fragmentImportant.setArguments(args);
        return fragmentImportant;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        nazless = getArguments().getString("nazless");
        numless = getArguments().getString("numless");
        impless = getArguments().getString("importantless");


    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_important, container, false);

   	TextView textimportant = (TextView) view.findViewById(R.id.textimportant);
   	textimportant.setText(impless);

    
        return view;
    }
    
    @Override
   	public void onPauseFragment() {
   		Log.i("ImportantFragment", "onPauseFragment()");
   		Toast.makeText(getActivity(), "onPauseFragment():" + "ImportantFragment", Toast.LENGTH_SHORT).show(); 
   	}
   	
   	@Override
   	public void onResumeFragment() {
   		Log.i("ImportantFragment", "onResumeFragment()");
   		Toast.makeText(getActivity(), "onResumeFragment():" + "ImportantFragment", Toast.LENGTH_SHORT).show(); 
   	}
    
}