package com.ustc.carcalculate.activity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.ustc.carcalculate.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * 欢迎界面，继承点击事件
 */
public class SplashActivity extends Activity implements AMapLocationListener,
		OnClickListener {

	private LocationManagerProxy mLocationManagerProxy;
	// 初始化延迟事件为1000毫秒，即1秒
	private static final int sleepTime = 1000;
	// 初始化经纬度
	Double lan = 0.0;
	Double lon = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		init();
	}

	/**
	 * 初始化定位
	 */
	private void init() {
		// 初始化定位，只采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(false);
		// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
		// 在定位结束后，在合适的生命周期调用destroy()方法
		// 其中如果间隔时间为-1，则定位只定一次,
		// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		// 移除定位请求
		mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		mLocationManagerProxy.destroy();
	}

	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {

	}

	/**
	 * 获取当前位置，将经纬度传到下一个界面
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 定位成功回调信息，设置相关消息
			lan = amapLocation.getLatitude();
			lon = amapLocation.getLongitude();

			new Thread(new Runnable() {
				public void run() {

					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					/************************* 得到结果以后 页面跳转 ******************/
					Intent intent = new Intent(SplashActivity.this,
							CarRoute.class);
					intent.putExtra("lan", lan);
					intent.putExtra("lon", lon);
					startActivity(intent);
					finish();
				}
			}).start();
		}
	}
}
