package com.example.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.example.distancecompute.DistanceCompute;
import com.example.model.RunInfo;
import com.example.running.R;
import com.example.util.Utils;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private LocationManager locationManager;
	private String provider;
	private ImageButton StartRun;
	private TextView TvDistance;
	private Button StopRun;
	private Button EndRun;
	private TextView Tvcalorie;
	private TextView TvSpeed;
	float speed, speed1, speed2;
    private long exitTime = 0;
	int speed3;
	float weight = 65;
	double distance;
	float calorie;
	double time;
	//DistanceCompute mgetDistance;
	LocationListener locationListener;
	Chronometer ch;
	boolean isPause = false;// 用于判断是否为暂停状态
	public double lat1, lat2;// 纬度
	public double lng1, lng2;// 经度
	private boolean isStart = true;// 是否开始计算移动距离，是否开始跑步
	public static final int UPDATE_TIME = 1;
	public static final int REFRESH_UI = 2;
	// private RunInfoDao mRunInfoDao;// 数据库
	private volatile boolean isRefreshUI = true;// 是否暂停刷新UI的标识
	private static final int REFRESH_TIME = 4000; // 4秒刷新一次
	// private DistanceCompute mgetDistance;
	Location startLocation = null;
	RunInfo run;
	@SuppressLint("HandlerLeak")
	private Handler refreshHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_UI:
				if (isRefreshUI) {
//					Log.i("111", "dingwei");
					TvDistance.setText("距离: "
							+ Utils.getValueWith2Suffix(distance) + "公里");
					// TvDistance.append(String.valueOf(distance));
					TvSpeed.setText(
							"速度: " + Utils.getValueWith2Suffix(speed) + "分/公里");
					// TvDistance.append(String.valueOf(speed));
					Tvcalorie.setText("卡路里: "
							+ Utils.getValueWith2Suffix(calorie) + "大卡");
					// TvDistance.append(String.valueOf(calorie));
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	// 定时器，每4秒刷新一次UI
	private Timer refreshTimer = new Timer(true);
	private TimerTask refreshTask = new TimerTask() {
		// Timer和TimerTask可以做为实现线程的第三种方式，前两种方式分别是继承自Thread类和实现Runnable接口。Timer是一种线程设施
		// 用于安排以后再后台线程中执行的任务。可安排任务执行一次或定期执行，看做定时器，TSK是一个抽象类，实现Runnable接口，具备多线程能力。
		public void run() {// 定时任务的run方法
			if (isRefreshUI) {
				Message msg = refreshHandler.obtainMessage();
				msg.what = REFRESH_UI;
				refreshHandler.sendMessage(msg);
			}
		}
	};

        
    
	protected void onCreate(Bundle saveInstanceState) {
		Bundle savedInstanceState = null;
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.running);
		  //mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		run = new RunInfo();
		StartRun = (ImageButton) findViewById(R.id.start);
		refreshTimer.schedule(refreshTask, 0, REFRESH_TIME);// 0秒后开始，4秒为刷新周期
		TvDistance = (TextView) findViewById(R.id.km);
		StopRun = (Button) findViewById(R.id.stop);
		EndRun = (Button) findViewById(R.id.end);
		TvSpeed = (TextView) findViewById(R.id.speed);
		Tvcalorie = (TextView) findViewById(R.id.calorie);
		ch = (Chronometer) findViewById(R.id.testtime);
		ch.setFormat("%s");  		
		StartRun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				StartRun.setImageDrawable(getResources().getDrawable(R.drawable.today_run));
				// 开始跑步
				if (isStart) {// 设置开始计时时间				
					time = SystemClock.elapsedRealtime();
					ch.setBase(SystemClock.elapsedRealtime());
					// 启动计时器
					ch.start();
					isStart = false;
				}
				
				locationManager = (LocationManager) getSystemService(
						Context.LOCATION_SERVICE);
				// 获取所有可用的位置提供器
				List<String> providerList = locationManager.getProviders(true);
				if (!providerList.contains(LocationManager.GPS_PROVIDER)) {
					provider = LocationManager.GPS_PROVIDER;
					// 当没有可用的位置提供器时，弹出Toast提示用户
					Toast.makeText(MainActivity.this, "GPS定位未打开，请开启",
							Toast.LENGTH_SHORT).show();
				} 
				//else {
					provider = LocationManager.GPS_PROVIDER;
//					if (locationManager.isProviderEnabled(provider)) {
//						Log.e("provider可用", "provider可用");
//						startLocation = locationManager
//								.getLastKnownLocation(provider);
//					
//						lat1 = startLocation.getLatitude();
//						lng1 = startLocation.getLongitude();
//					}

					// LocationListener LocationListener = null;
					LocationListener locationListener = new LocationListener() {
						@Override
						public void onLocationChanged(Location location) {
							if (location != null) {
								// 得到设备的位置信息,计算距离
								if (startLocation == null) {
									startLocation = location;
								}

								speed1 = location.getSpeed();
								speed = 1000 / speed1 / 60;

								// float[] results = new float[1];
								// Location.distanceBetween(lat1, lng1, lat2,
								// lng2,
								// results);
								// distance = results[0] / 1000;
								// 拿到整个跑步过程的时间
								//time = location.getTime() - time;
								distance = startLocation.distanceTo(location)
										/ 1000;
								calorie = (float) (weight * distance * 1.036
										/ 1000);
								if (isStart) {
									refreshTask.run();
								}
							}
						}

						@Override
						public void onStatusChanged(String provider, int status,
								Bundle extras) {

						}

						@Override
						public void onProviderEnabled(String provider) {
							isStart = true;
						}

						@Override
						public void onProviderDisabled(String provider) {
							Log.i("provider", "provider不可用");
							isStart = false;
							refreshTask.cancel();
						}
					};
					locationManager.requestLocationUpdates(provider, 0, 3,
							locationListener);
				}

				// }

		//	}
		});
		StopRun.setOnClickListener(new OnClickListener() {
			// 暂停
			@Override
			public void onClick(View v) {
				if (!isPause) {
					isStart = false;
					refreshTask.cancel();
					ch.stop();				
					isPause = true;
					StopRun.setText("继续跑步");
				} else {
					Double temp = Double.parseDouble(
					ch.getText().toString().split(":")[1]) * 1000;// 获得计时的秒数
					ch.setBase((long) (SystemClock.elapsedRealtime() - temp));// 设置计时基点					
					ch.start();
					isStart = true;
					refreshTask.run();						
					stopText();
				}
			}
		});

		EndRun.setOnClickListener(new OnClickListener() {
			// 停止
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						MainActivity.this);
				dialog.setTitle("提示");
				dialog.setMessage("确认停止跑步？");
				dialog.setPositiveButton("是",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {// 响应事件
						ch.stop();
						// 结束跑步，停止刷新UI
						isStart = true;
						refreshTask.cancel();
						String endtime=(String)ch.getText();
						Intent intent = new Intent(MainActivity.this,
								ShareActivity.class);
						intent.putExtra("distance", distance);
						intent.putExtra("speed", speed);
						intent.putExtra("calorie", calorie);
						intent.putExtra("time", endtime);
						startActivity(intent);
					}
				});
				dialog.setNegativeButton("否", null);
				dialog.show();			
			}			
		});		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {//用来捕捉手机键盘被按下的事件，该方法的返回值为一个boolean类型的变量，当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理，而当返回false时，表示并没有完全处理完该事件，更希望其他回调方法继续对其进行处理，例如Activity中的回调方法
    	//参数keyCode，该参数为被按下的键值即键盘码，参数event，该参数为按键事件的对象，其中包含了触发事件的详细信息，例如事件的状态、事件的类型、事件发生的时间等。
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {//if判断中要加一个event.getAction() == KeyEvent.ACTION_DOWN判断，因为按键有两个事件ACTION_DOWN和ACTION_UP，也就是按下和松开，如果不加这个判断，代码会执行两遍。
            if ((System.currentTimeMillis() - exitTime) > 2000) {// 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", 0).show();
                exitTime = System.currentTimeMillis();//更新时间，System.currentTimeMillis()返回当前的计算机时间,产生一个毫秒数
            } else {
                finish();
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);}
	// 设置处于暂停状态时，pause按钮的文字显示
	public void stopText() {
		if (isPause) {
			StopRun.setText("暂停跑步");
			isPause = false;
		}
	}

	public void onResume() {
		super.onResume();
		ch.setBase(SystemClock.elapsedRealtime());
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	protected void onDestroy() {
		super.onDestroy();
		if (locationManager != null) {
		// 关闭程序时将监听器移除
		locationManager.removeUpdates(locationListener);
		}
		}
}
/**
 * 定位服务监听器
 */
 class MyLocationListener implements BDLocationListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isFirstLoc; //是否首次定位

    public MyLocationListener(MapView mapView, BaiduMap baiduMap, boolean isFirstLoc) {
        mMapView = mapView;
        mBaiduMap = baiduMap;
        this.isFirstLoc = isFirstLoc;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        // map view 销毁后不在处理新接收的位置
        if (bdLocation == null || mMapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            Log.i("location", ll.toString());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
    }
}

/**
 * 定位服务
 */
 class LocationService {
    private LocationClient client = null;
    private LocationClientOption mOption,DIYoption;
    private final Object  objLock = new Object();

    public LocationService(Context locationContext){
        synchronized (objLock){
            if (client == null){
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
            }
        }
    }

    public boolean registerListener(BDLocationListener listener){
        boolean isSuccess = false;
        if(listener != null){
            client.registerLocationListener(listener);
            isSuccess = true;
        }
        return  isSuccess;
    }

    public void unregisterListener(BDLocationListener listener){
        if(listener != null){
            client.unRegisterLocationListener(listener);
        }
    }

    public boolean setLocationOption(LocationClientOption option){
        if(option != null){
            if(client.isStarted())
                client.stop();
            DIYoption = option;
            client.setLocOption(option);
        }
        return false;
    }

    public LocationClientOption getOption(){
        return DIYoption;
    }

    public LocationClientOption getDefaultLocationClientOption() {
        if(mOption == null){
            mOption = new LocationClientOption();
            mOption.setOpenGps(true);
            mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        }
        return mOption;
    }

    /**
     * 开启定位服务
     */
    public void start(){
        synchronized (objLock) {
            if(client != null && !client.isStarted()){
                client.start();
            }
        }
    }

    /**
     * 终止定位服务
     */
    public void stop(){
        synchronized (objLock) {
            if(client != null && client.isStarted()){
                client.stop();
            }
        }
    }
}
