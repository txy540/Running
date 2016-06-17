package com.example.activity;

import com.example.db.RUNOpenHelper;
import com.example.model.RunInfo;
import com.example.running.R;
import com.example.util.Utils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class ShareActivity extends Activity {
	private RUNOpenHelper dbHelper;
	RunInfo run;
	float speed;
	double distance;
	float calorie;
	String time;
	protected void onCreate(Bundle saveInstanceState) {
		 Bundle savedInstanceState = null;
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show);		
		dbHelper = new RUNOpenHelper(this, "run_data.db", null, 1);
		TextView caldata=(TextView)findViewById(R.id.caldata);
		TextView disdata=(TextView)findViewById(R.id.disdata);
		TextView timdata=(TextView)findViewById(R.id.timdata);
		TextView spedata=(TextView)findViewById(R.id.spedata);
		Button share=(Button)findViewById(R.id.share);
		Button save=(Button)findViewById(R.id.save);
		Intent intent=getIntent();
		distance=intent.getDoubleExtra("distance",0);
		 speed=intent.getFloatExtra("speed",0);
		 calorie=intent.getFloatExtra("calorie",0);
		 time=intent.getStringExtra("time");				 
		caldata.setText(Utils.getValueWith2Suffix(calorie)+"");
		disdata.setText(Utils.getValueWith2Suffix(distance)+"");
		timdata.setText(time);
		spedata.setText(Utils.getValueWith2Suffix(speed)+"");
		save.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.execSQL(
						"insert into rundata(distance,speed,time,calorie)values(?,?,?,?)",
						new String[] { distance + "", speed + "", time + "",
								calorie + "" });
				Toast.makeText(ShareActivity.this, "数据保存成功",
						Toast.LENGTH_SHORT).show();

			}
		});

		share.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				StartShareApp(ShareActivity.this, "分享到",
						"距离:"+distance+"时间:"+time+"配速"+speed+"消耗卡路里"+calorie);
				
						}
		
		});
	}
	protected void StartShareApp(Context context, final String shareTitle, final String shareMsg) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setType("text/plain");
		intent.setAction(Intent.ACTION_SEND);
		//intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
		context.startActivity(Intent.createChooser(intent, shareTitle));
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
