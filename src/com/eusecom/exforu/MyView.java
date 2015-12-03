package com.eusecom.exforu;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;



public class MyView extends View {
	
	private int color = Color.WHITE;
	private int whatp = 0;
	private double openx = 0;
	private double closex = 0;
	private double highx = 0;
	private double lowx = 0;
	private String timex = "";
	private double maxx = 1.15000;
	private double minx = 1.09000;

	
	
    public MyView(Context cxt, AttributeSet attrs) {
        super(cxt, attrs);
        setMinimumHeight(100);
        setMinimumWidth(100);
    }
    
    public MyView(Context cxt, AttributeSet attrs, int color) {
        super(cxt, attrs);
        setMinimumHeight(100);
        setMinimumWidth(100);
        this.color = color;
    }
    


	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas cv) {
		
        cv.drawColor(color);
        Paint p = new Paint();
        
        p.setColor(Color.BLACK); 
        p.setTextSize(40);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        cv.drawText(timex, 5, 40, p);
        
        if( whatp == 0 ){
        	//p.setColor(Color.GREEN);
            //p.setStrokeWidth(5);
            //cv.drawLine(20, 0, 20, cv.getHeight(), p);
        }else{
        	
        	double x1x=((openx+lowx-minx)/(maxx-minx))*cv.getWidth();
            float x1xf = (float) (x1x);
            
            double x2x=((openx+highx-minx)/(maxx-minx))*cv.getWidth();
            float x2xf = (float) (x2x);
            
            double xr1x=((openx-minx)/(maxx-minx))*cv.getWidth();
            int xr1xf = (int) (xr1x);
            
            double xr2x=((openx+closex-minx)/(maxx-minx))*cv.getWidth();
            int xr2xf = (int) (xr2x);
            
            int plusx=1;
            if( xr1xf > xr2xf )
            {
            //System.out.println("otoc");
            plusx=0;
            int otoc=xr2xf;
            xr2xf=xr1xf;
            xr1xf=otoc;
            }
        	
            //close price
            p.setColor(Color.BLACK); 
            p.setTextSize(40);
            p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            double zavrip=openx+closex;
            zavrip=round(zavrip,5);
            String zavrips=zavrip + "";
            cv.drawText(zavrips, (cv.getWidth()-180), 40, p);
            
            
        	p.setColor(Color.GREEN);
        	if( plusx == 0 ){p.setColor(Color.RED);}
            p.setStrokeWidth(10);

            //System.out.println("time " + timex + "open " + openx + " close " + closex + " high " + highx + " low " + lowx);
            //String x1xs = x1x + ""; String wdths=cv.getWidth() + ""; String x2xs = x2x + "";
            //System.out.println("x1x " + x1xs + " width " + wdths + " x2x " + x2xs);

            //x1xf=100; x2xf=300;
            cv.drawLine(x1xf, cv.getHeight()/2, x2xf, cv.getHeight()/2, p);
 
            //String xr1xs = xr1x + ""; String wdths2=cv.getWidth() + ""; String xr2xs = xr2x + "";
            //System.out.println("time " + timex + " xr1x " + xr1xs + " width " + wdths2 + " xr2x " + xr2xs + " plus " + plusx);
            
            //xr1xf=150; xr2xf=200;            
        	Rect r = new Rect(xr1xf, 0, xr2xf, cv.getHeight());
        	p.setColor(Color.GREEN);
        	if( plusx == 0 ){p.setColor(Color.RED);}
        	p.setStyle(Paint.Style.FILL);
        	cv.drawRect(r, p);
        	p.setStyle(Paint.Style.STROKE);
        	p.setStrokeWidth(1);
            p.setColor(Color.BLACK);
            cv.drawRect(r, p);
            
             

            
        	
        }
        
    }
	

	public void setCustomColor(int color){
			this.color = color;
		   
		}
	
	public void setWhatp(int whatpx){
		   
		   	this.whatp = whatpx;
		}
	
	public void setCandle(int whatpx, double openxy, double closexy, double highxy
			, double lowxy, String timexy, String oplos, String ophis){
		   
	   	this.whatp = whatpx;
	   	this.openx = openxy;
	   	this.closex = closexy;
	   	this.highx = highxy;
	   	this.lowx = lowxy;
	   	this.timex = timexy;
	   	this.minx = (Double.parseDouble(oplos) - 400)/100000;
	   	this.maxx = (Double.parseDouble(ophis) + 400)/100000;
	   	
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
    

}