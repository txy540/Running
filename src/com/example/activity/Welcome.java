package com.example.activity;

import com.example.running.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
public class Welcome extends Activity{
	private Handler handler;
	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		TextView tv=(TextView)findViewById(R.id.version);
         tv.setText("  "+"将跑步进行到底！");
		handler = new Handler()
		{
			public void handleMessage(Message paramAnonymousMessage)
			{
				super.handleMessage(paramAnonymousMessage);
				Welcome.this.toLogin();
				Welcome.this.finish();
			}
		};

		new Thread(){
			public void run(){
				try{
					Thread.sleep(5000);
					Welcome.this.handler.sendEmptyMessage(0);
				}
				catch (InterruptedException localInterruptedException){
					localInterruptedException.printStackTrace();
				}
			}
		}.start();
	}


	/**
	 * 登录
	 */
	private void toLogin(){
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
	}


	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
