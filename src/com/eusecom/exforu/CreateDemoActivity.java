/* I do not use this script now.
*/

package com.eusecom.exforu;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


 
public class CreateDemoActivity extends Activity {
 
	TextView title, geturl;
	private WebView webView;
	String webUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createdemo);
        
        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.create_demo));
        
        webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		//webView.loadUrl("http://www.google.com");
		//webView.loadUrl("https://www.xtb.com/en/demo-account#!/en/demo-account/step/1");
		//webView.loadUrl("https://form-demo.xtb.com/uk/demo/mobile");
		webView.loadUrl("https://www.xtb.com/en/demo-account#!/en/demo-account/step/1");
		geturl = (TextView) findViewById(R.id.geturl);
		
		
		webView.setWebViewClient(new WebViewClient() {
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                    view.loadUrl(url);
	                    webUrl = view.getUrl();
		        		geturl.setText(webUrl);

	                return true;
	            }

	            public void onLoadResource(WebView view, String url) {

	                	//here I get the Url, but its not accurate. Sometimes it works, sometiems it doesn't 
	                    //Toast.makeText(getApplicationContext(), browser.getUrl(),Toast.LENGTH_SHORT).show();
	            	webUrl = view.getUrl();
	        		geturl.setText(webUrl);
	        		
	            }
	            
	            @Override
	            public void onPageFinished(WebView view, String url) {
	                //System.out.println("your current url when webpage loading.. finish" + url);
	                super.onPageFinished(view, url);
	                
	                webUrl = view.getUrl();
	        		geturl.setText(webUrl);
	                
	            }


	        });
		
		
		
		
		
     
    }
    //koniec oncreate
    
   

}