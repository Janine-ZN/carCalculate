package com.ustc.carcalculate.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrowOptions;

import com.ustc.carcalculate.R;
import com.ustc.carcalculate.carroute.utils.Taxijifei;
import com.ustc.carcalculate.person.AboutActivity;
import com.ustc.carcalculate.residemenu.ResideMenu;
import com.ustc.carcalculate.residemenu.ResideMenuInfo;
import com.ustc.carcalculate.residemenu.ResideMenuItem;
import com.ustc.carcalculate.utils.CarLocation;

/**
 * 混合定位示例
 * 
 * @param <InfoWindow>
 * 
 *  添加GPS跟随
 * */
/****************** 继承的Activity和点击响应应注意 ******************/
public class CarRoute extends Activity implements LocationSource,
		AMapLocationListener, OnInfoWindowClickListener, InfoWindowAdapter,
		OnMarkerClickListener, View.OnClickListener, SensorEventListener {
	// 每秒的距离，现假设是100km/h
	private static final int INTERVAL_DISTANCE = 28;
	private static final double DELAY_DISTANCE = 3.4;

	private AMap aMap;
	private MapView mapView;

	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;

	// 记录每个定位点信息的链表
	private List<LatLng> list = new ArrayList<LatLng>();
	private List<Double> timeList = new ArrayList<Double>();
	private List<Double> speedList = new ArrayList<Double>();
	private List<LatLng> beforeList = new ArrayList<LatLng>();

	// 按钮
	private Button startEndBtn;// 定义上下车的按钮
	private TextView position;// 定义定位的按钮
	private TextView jifei;// 定义显示的计算效果textview控件
	private TextView route;// 路线

	// 是否第一次定位
	private boolean isFirstLocate = true;
	LatLng lastLatLng;

	/* 是否上车 */
	private boolean isGetOn = false;
	/* 是否第一次用 */
	private boolean isNext = true;

	private boolean is_closed = false;
	/* 点击后退键的时间 */
	private long exitTime = 0;
	/* 判断是否上车 还是下车 */
	private int count = 0;

	// 地图中UI控件
	private UiSettings mUiSettings;

	// GPS跟随
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private long lastTime = 0;
	private final int TIME_SENSOR = 100;
	private float mAngle;
	private Marker mGPSMarker;
	private Marker mGPSMarker1;

	// 当前经纬度
	private double lan;
	private double lon;

	// 第一次定位 显示当前位置
	private int one = 0;// 初始化当前位置

	/****************************** 定义侧滑菜单相关变量 ******************************/
	public ResideMenu resideMenu;// 侧滑菜单

	private ResideMenuItem itemMylevel;// 我的积分
	private ResideMenuItem itemMyFriend;// 我的好友
	private ResideMenuItem itemAbout;// 设置

	private ResideMenuInfo info;// 头部个人信息设置
	/****************************** 定义侧滑菜单相关变量 ******************************/

	// 显示绿色的折线
	private NavigateArrowOptions Naviaget = new NavigateArrowOptions();
	private NavigateArrowOptions add2; // 定义一个数组

	private String getAddress = "";

	private String deviceid = "111";

	private int driver = 1;// 初始化一个司机数
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 显示自定义标题栏
		setContentView(R.layout.activity_map);

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		// 初始化传感器
		mSensorManager = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		init();// 获取各个控件

		initEvent();
		setUpMenu();// 设置侧滑菜单
		setListener();// 设置侧滑菜单的监听事件

		// 得到前一个页面传递的参数以后 进行初始化
		Intent intent = this.getIntent();
		lan = intent.getDoubleExtra("lan", 0.0);
		lon = intent.getDoubleExtra("lon", 0.0);
		if (lan != 0.0 && lon != 0.0) {
			final LatLng ZHONGGUANCUN = new LatLng(lan, lon);
			changeCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(ZHONGGUANCUN, 15, 0,
							0)));
		}

	}

	/**
	 * 点击上车实现计费
	 * 
	 * @author zhaonan
	 * 
	 */
	public class CalculateOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (count == 0) {
				init();
				// 两秒定位一次
				if (!isNext)
					mAMapLocationManager.requestLocationData(
							LocationProviderProxy.AMapNetwork, 2 * 1000, 2,
							CarRoute.this);

				isGetOn = true;

				// 再次 上车的时候删除折线
				if (add2 != null) {
					/*
					 * 清空所添加的点坐标 add2 （无效） 清空 Naviaget 试试 重新赋值给它 删除地图上所有的坐标 重新添加
					 * GPS跟随
					 */
					add2 = null;
					Naviaget = null;
					Naviaget = new NavigateArrowOptions();
					aMap.clear();
					mGPSMarker = aMap.addMarker(new MarkerOptions()
							// .snippet("00")
							.title(getAddress)
							.icon(BitmapDescriptorFactory
									.fromBitmap(BitmapFactory.decodeResource(
											getResources(),
											R.drawable.location_marker)))
							.anchor((float) 0.5, (float) 0.5).setFlat(true));
					mGPSMarker.setPosition(new LatLng(lan, lon));
				}

				jifei.setText("开始计算价格中...");
				startEndBtn.setText("下车");
				count = 1;
			} else {
				/********************** 点击“下车”计算出结果 **********************/
				// 总路程
				if (timeList.size() != 0 && list.size() != 0
						&& speedList.size() != 0) {
					double total = 0;
					double total_jifei = 0;
					double total_delay_time_jifei = 0;
					int delay_time_count = 0;
					double total_delay = 0;
					// 打印结果
					String all_distances = "before:\r\n";
					String right_distance = "after:\r\n";
					String unGetOn = "没上车:\r\n";
					// 前一次正确的信息
					double first = timeList.get(0);
					LatLng firstPosition = list.get(0);
					double first_speed = speedList.get(0);
					// 前一次是否加速度是否有浮动
					boolean isRight = true;
					// 前一次判断是否是“正确”的结果
					boolean isResult = true;

					int count = 0;
					// 未上车的信息
					for (int i = 1; i < beforeList.size(); i++) {
						double distance = AMapUtils.calculateLineDistance(
								beforeList.get(i), beforeList.get(i - 1));
						unGetOn += distance + "\r\n";
					}
					// 计算距离
					for (int i = 1; i < list.size(); i++) {
						double distance = AMapUtils.calculateLineDistance(
								list.get(i), firstPosition);
						double interval = timeList.get(i) - first;

						double distance_log = AMapUtils.calculateLineDistance(
								list.get(i), list.get(i - 1));
						double interval_log = timeList.get(i)
								- timeList.get(i - 1);
						all_distances += distance_log + "     "
								+ speedList.get(i) + "     " + interval_log
								+ "\r\n";

						// 两次判断，半径&加速度判断
						if (isProperDistance(distance, interval / 1000.0)) {

							if (isRight && count < 3) {
								double vMax = getMax(speedList.get(i),
										first_speed);
								// 20/3.6=5.6
								if (vMax / 3.6 + 5.6 >= distance * 1000.0
										/ interval) {
									total += distance;
									right_distance += distance_log + "  ";
									isRight = true;
									isResult = true;
								} else {
									isResult = false;
									isRight = false;
								}

							} else {
								total += distance;
								right_distance += distance_log + "  ";
								isRight = true;
								isResult = true;
							}
							first = timeList.get(i);
							firstPosition = list.get(i);
							first_speed = speedList.get(i);
							count = 0;
						} else {
							isRight = true;
							isResult = false;
							count++;
						}
						if (isResult) {
							if (isDelay(distance, interval / 1000.0)) {
								delay_time_count++;
								total_delay += interval / 1000.0;
							}
							// 计费部分调用
							Taxijifei taxi = new Taxijifei(CarRoute.this,
									"HeFei.txt", total, distance);
							double jifei_temp = taxi.jifei();
							total_jifei += jifei_temp;
							Taxijifei dtj = new Taxijifei(CarRoute.this,
									"HeFei.txt", interval / 1000.0, total_delay);
							total_delay_time_jifei += dtj.jifei();
							right_distance += jifei_temp + "  " + total_delay
									+ "  " + total_delay_time_jifei + "\r\n";
						}
					}
					right_distance += total + "\r\n";
					Date now = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd.HH:mm:ss");
					String now_ = dateFormat.format(now);

					writeFileToSD(now_ + ".txt", unGetOn + all_distances
							+ right_distance);

					Taxijifei inital_jifei = new Taxijifei(CarRoute.this,
							"HeFei.txt");
					// 费用去除小数，显示米 (int)Math.floor(total)
					jifei.setText("总距离:"
							+ (int) Math.floor(total)
							+ "米\n费用是"
							+ String.valueOf(inital_jifei.Getfujiafei()
									+ inital_jifei.Getqibujia() + total_jifei
									+ total_delay_time_jifei));
					Log.i("JIFEI",
							total_jifei + " " + inital_jifei.Getqibujia());

				} else {
					jifei.setText("没有费用");
				}
				list.clear();
				timeList.clear();

				// 下车的时候显示蓝色折线
				aMap.addNavigateArrow(add2);

				isFirstLocate = true;
				isGetOn = false;
				beforeList.clear();
				deactivate();
				isNext = false;

				count = 0;
				startEndBtn.setText("上车");
				// 下车
			}

		}

	}

	/**
	 * 设置点击事件
	 */
	private void initEvent() {
		startEndBtn.setOnClickListener(new CalculateOnClickListener());// 点击上车按钮，实现计费功能
		position.setOnClickListener(new positionOnClickListener());// 获取当前位置
		route.setOnClickListener(new routeOnClickListener());// 多路线规划
	}

	/**
	 * 点击“路线”TextView当前位置
	 * 
	 * @author Janine.Z
	 * 
	 */
	public class routeOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(CarRoute.this, RouteActivity.class);
			startActivity(intent);
			finish();

		}
	}

	/**
	 * 点击“定位”TextView当前位置
	 * 
	 * @author Janine.Z
	 * 
	 */
	public class positionOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			final LatLng ZHONGGUANCUN = new LatLng(lan, lon);
			changeCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(ZHONGGUANCUN, 15, 0,
							0)));
		}
	}

	/**
	 * 设置侧滑菜单
	 */
	private void setUpMenu() {

		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.reside_menu);// 菜单的背景图片设置

		/**** 侧滑菜单的背景图 *****/
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		resideMenu.setScaleValue(0.6f);

		// 禁止使用右侧菜单
		resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		// 创建菜单条目
		itemMylevel = new ResideMenuItem(this, R.drawable.level, "我的等级");
		itemMyFriend = new ResideMenuItem(this, R.drawable.friends, "好友司机");
		itemAbout = new ResideMenuItem(this, R.drawable.setting, "设置");

		resideMenu.addMenuItem(itemMylevel, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemMyFriend, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemAbout, ResideMenu.DIRECTION_LEFT);

		// 读取用户ID到myPref文件中(2)
		SharedPreferences pref = getSharedPreferences("myPref",
				Activity.MODE_PRIVATE);
		String username = pref.getString("username", "恋半世红尘");
		String userId = pref.getString("userId", "2920336708");
		String headPath = pref.getString("headimg", "0");
		if (headPath.equals(0)) {
			info = new ResideMenuInfo(this, R.drawable.title_head_button,
					username, userId);
		} else {
			info = new ResideMenuInfo(this, headPath, username, userId);
		}

	}

	/**
	 * 点击"我的"textview显示左边侧滑栏
	 * 
	 * @param view
	 */
	public void Menu(View view) {

		// 读取myPref文件里信息
		SharedPreferences preferences = getSharedPreferences("myPref",
				Activity.MODE_PRIVATE);
		String username = preferences.getString("username", "0");
		String password = preferences.getString("password", "0");
		String phonenum = preferences.getString("phonenum", "0");

		// 如果不是第一次登录，显示侧滑菜单
		if ((!username.equals("0")) && (!password.equals("0"))
				|| (!phonenum.equals("0")) && (!password.equals("0"))) {

			resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			// 否则，显示登录/注册界面
		} else {

			Intent intent = new Intent(CarRoute.this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}

	/**
	 * 菜单items的点击事件
	 */
	private void setListener() {
		resideMenu.addMenuInfo(info);

		itemMyFriend.setOnClickListener(this);
		itemAbout.setOnClickListener(this);

		info.setOnClickListener(this);

	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			// 地图控件 UI 缩放控件 地图 logo 指南针 比例尺
			mUiSettings = aMap.getUiSettings();
			setUpMap();
			Log.i("init", "init");
		}
		startEndBtn = (Button) findViewById(R.id.start_end_btn);// 获取上下车的按钮
		jifei = (TextView) findViewById(R.id.jifei);// 获取合肥的出租车费用
		route = (TextView) findViewById(R.id.route_tv);// 路线导航的textview
		position = (TextView) findViewById(R.id.position_tv);// 获取定位的按钮

	}

	/**
	 * 设置一些amap的属性 GPS跟随
	 */
	private void setUpMap() {

		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种,跟随模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		/* 设置缩放级别 */
		aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnInfoWindowClickListener(OnInfoWindowClickListener);// 设置InfoWindow点击隐藏

		// 显示指南针
		mUiSettings.setCompassEnabled(true);
		// 在此处先设置一下蓝色箭头的弹框，重点：勿删
		mGPSMarker = aMap.addMarker(new MarkerOptions()
				.snippet("00")
				.title(getAddress)
				.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.location_marker)))
				.anchor((float) 0.5, (float) 0.5).setFlat(true));

	}

	/**
	 * 添加GPS跟随
	 */
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		registerSensorListener();
	}

	/**
	 * 方法必须重写
	 */

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		deactivate();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数 设置GPS跟随
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation.getAMapException().getErrorCode() == 0) {

				// 将定位经纬度改为全局变量,方便以后调用
				lan = amapLocation.getLatitude();
				lon = amapLocation.getLongitude();
				String address = amapLocation.getAddress();
				int resultCity = address.indexOf("市");
				getAddress = address.substring(resultCity + 1);// address.substring(resultCity):市蜀山区望江西路靠近中国科技大学先进技术研究院
				// getAddress = amapLocation.getAddress();// 第一次对getAddress进行赋值

				// 刚打开地图的时，定位当前位置
				final LatLng ZHONGGUANCUN = new LatLng(lan, lon);
				if (one == 0) {
					changeCamera(CameraUpdateFactory
							.newCameraPosition(new CameraPosition(ZHONGGUANCUN,
									15, 0, 0)));
					one = 1;
				}

				// 获取手机唯一编号 吐丝一下 数据同步 发送数据给服务器 参数 经纬度 精度 地址 方式
				String deviceid = telephone();// 返回值是deviceid
				new CarLocation(lan, lon, amapLocation.getAccuracy(),
						amapLocation.getAddress(), amapLocation.getProvider(),
						deviceid).start();// 将用户当前的位置和信息上传到服务器端

				LatLng p1 = new LatLng(lan, lon);
				// 显示司机位置
				if (driver == 1) {
					// 将Double类型转成String类型
					new MyAsyncTask().execute(deviceid, Double.toString(lan),
							Double.toString(lon));
					driver = 2;
				}

				// GPS跟随 //先获得纬度，再获得经度
				mGPSMarker.setPosition(new LatLng(amapLocation.getLatitude(),
						amapLocation.getLongitude()));

				Calendar calendar = Calendar.getInstance();
				double lastTime = calendar.getTimeInMillis();
				// 如果定位准确
				if (amapLocation.hasAccuracy()) {
					if (isGetOn) {

						if (isFirstLocate) {
							isFirstLocate = false;
						} else {
							// 占时不添加折线
							// polyline = aMap.addPolyline((new
							// PolylineOptions()
							// .add(lastLatLng, p1).color(Color.RED)
							// .width(10)));

							// 定位成功的时候 添加绿色的折线 添加点
							add2 = Naviaget.add(lastLatLng).width(20);

						}
						lastLatLng = p1;
						list.add(p1);
						timeList.add(lastTime);
						speedList.add(amapLocation.getSpeed() * 3.6);
					} else {
						beforeList.add(p1);
					}
				}

				Log.i("position", lan + ";" + lon);

			} else {
				Log.e("AmapErr", "Location ERR:"
						+ amapLocation.getAMapException().getErrorCode());
				Toast.makeText(CarRoute.this, "网络出问题咯", 1000).show();
			}
		}

		Log.i("change", "change");
	}

	/* 判断间隔是否正确 */
	public boolean isProperDistance(double x, double interval) {
		if (x <= INTERVAL_DISTANCE * interval)
			return true;
		else
			return false;
	}

	/* 判断是否堵车 */

	public boolean isDelay(double x, double interval) {
		if (x <= DELAY_DISTANCE * interval)
			return true;
		else
			return false;
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除

			/* 确保首次定位准确，连续定位5次 */
			// 上车就定位
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 2 * 1000, 2, this);

		}
		Log.i("activate", "activate");
	}

	/**
	 * 停止定位 添加GPS定位 mAMapLocationManager 要放出来？
	 */
	@Override
	public void deactivate() {
		/* mListener = null; */
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			/* mAMapLocationManager.destroy(); */
		}
		/* mAMapLocationManager = null; */
		unRegisterSensorListener();
	}

	/**
	 * 写入文件
	 * 
	 */
	private void writeFileToSD(String filename, String content) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Log.d("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		try {
			String pathName = "/sdcard/test/";
			File file = new File(pathName + filename);

			if (!file.exists()) {
				Log.d("TestFile", "Create the file:" + filename);
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file);
			byte[] buf = content.getBytes();
			stream.write(buf);
			stream.close();

		} catch (Exception e) {
			Log.e("TestFile", "Error on writeFilToSD.");
			e.printStackTrace();
		}
	}

	// 获得较大的瞬时速度
	private double getMax(double v1, double v2) {
		if (v1 < v2)
			return v2;
		else
			return v1;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// System.out.println("JBaymax:进入onMarkerClick--->");
		// if (marker.isInfoWindowShown()) {
		// marker.hideInfoWindow();
		// }
		return false;
	}

	/*
	 * GPS跟随
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (System.currentTimeMillis() - lastTime < TIME_SENSOR) {
			return;
		}
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ORIENTATION: {
			float x = event.values[0];

			x += getScreenRotationOnPhone(this);
			x %= 360.0F;
			if (x > 180.0F)
				x -= 360.0F;
			else if (x < -180.0F)
				x += 360.0F;
			if (Math.abs(mAngle - x) < 5.0f) {
				break;
			}
			mAngle = x;

			if (mGPSMarker != null) {
				mGPSMarker.setRotateAngle(-mAngle);

			}
			lastTime = System.currentTimeMillis();
		}
		}

	}

	public void registerSensorListener() {
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void unRegisterSensorListener() {
		mSensorManager.unregisterListener(this, mSensor);
	}

	/**
	 * 获取当前屏幕旋转角度
	 * 
	 * @param activity
	 * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
	 */
	public static int getScreenRotationOnPhone(Context context) {
		final Display display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			return 0;

		case Surface.ROTATION_90:
			return 90;

		case Surface.ROTATION_180:
			return 180;

		case Surface.ROTATION_270:
			return -90;
		}
		return 0;
	}

	/*
	 * GPS跟随函数
	 */

	/**
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域 跳转到 当前位置
	 */
	private void changeCamera(CameraUpdate update) {
		// 这个方法为有动画的移动
		// aMap.animateCamera(update, 1000, callback);
		// 这个方法为瞬间移动，没有移动过程
		aMap.moveCamera(update);
	}

	/********************** menu的item点击事件 ******************/
	@Override
	public void onClick(View view) {
		if (view == itemMyFriend) {
			Intent intent = new Intent(CarRoute.this, NumberActivity.class);
			startActivity(intent);
			finish();
		} else if (view == itemMylevel) {
			Intent intent = new Intent();
			intent.putExtra("flog", "sw");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		} else if (view == itemAbout) {
			Intent intent = new Intent();
			intent.putExtra("flog", "待开发......");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
			finish();
		} else if (view == info) {
			// 转到个人信息界面
			Intent aboutIntent = new Intent(CarRoute.this, AboutActivity.class);
			startActivity(aboutIntent);
			finish();
		}
	}

	/**
	 * 相对于左菜单的开关
	 */
	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			is_closed = false;
			// leftMenu.setVisibility(View.GONE);
			/******* 不显示左边菜单 *******/
		}

		@Override
		public void closeMenu() {
			is_closed = true;
			// leftMenu.setVisibility(View.VISIBLE);
			/******* 显示左边菜单 *******/
		}
	};

	// What good method is to access resideMenu？
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	/**
	 * 自定义InfoWindow
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = null;
		infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
				null);// 加载自定义InfoWindow窗口
		render(marker, infoWindow);
		return infoWindow;
	}

	/**
	 * 自定义InfoWindow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			imageView = (ImageView) view.findViewById(R.id.badge);
			imageView.setImageResource(R.drawable.taxi);
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(15);
			// String address = getAddress.substring(15);
			titleUi.setText(getAddress);
			System.out.println("JBaymax:getAddress--->"
					+ titleUi.getText().toString());

		} else {
			titleUi.setText("");
		}

	}

	/**
	 * 当marker的信息窗口被点击时，回调此方法。
	 * 
	 * @param marke
	 */
	public void onInfoWindowClick(Marker marke) {
		// 自定义操作
		// if (marke.isInfoWindowShown()) {
		// marke.showInfoWindow();
		// }
		// marke.hideInfoWindow();
	}

	private OnInfoWindowClickListener OnInfoWindowClickListener = new OnInfoWindowClickListener() {

		@Override
		public void onInfoWindowClick(Marker marker) {
			if (marker.isInfoWindowShown()) {
				marker.hideInfoWindow();// 这个是隐藏infowindow窗口的方法
			}
		}
	};

	/**
	 * 异步任务 POST请求 HTTP请求获取出租车和好友的位置
	 * 
	 * @author JBaymax
	 * 
	 */

	public class MyAsyncTask extends AsyncTask<String, Void, String> {

		/**
		 * 子线程 POST请求 返回 deviceid
		 */

		@Override
		protected String doInBackground(String... request) {
			String deviceid = request[0];
			String lan = request[1];
			String lon = request[2];

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/TaxiServlet";
			HttpPost httpPost = new HttpPost(url);
			String jsonString = "";
			// 将数据打包成JSON格式
			JSONObject jsonObject = new JSONObject();

			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("deviceid", deviceid);
				jsonObject.put("lan", lan);
				jsonObject.put("lon", lon);

				// 打包成功后,发送数据
				NameValuePair nvp = new BasicNameValuePair("taxiRequest",
						jsonObject.toString());

				List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
				nvpList.add(nvp);
				// 得到JSON数据,并设置编码格式
				HttpEntity entity = new UrlEncodedFormEntity(nvpList, "UTF-8");
				// 将打包好的JSON数据,放到entity对象里,而entity对象里的数据是以List的形式存在的
				httpPost.setEntity(entity);
				// 执行请求
				HttpResponse httpResponse = httpClient.execute(httpPost);

				// 下面是读取数据
				HttpEntity en = httpResponse.getEntity();
				InputStream inputStream = en.getContent();
				InputStreamReader reader = new InputStreamReader(inputStream,
						"UTF-8");

				// 读取字符串数据2
				BufferedReader myread = new BufferedReader(reader);
				String line = "";

				while ((line = myread.readLine()) != null) {
					jsonString += line;
				}

				System.out.println("---taxiRequest返回的jsonString----->"
						+ jsonString);

			} catch (Exception e) {
				e.printStackTrace();

			}
			Log.i("LOG", "服务器传来的jsonObject:" + jsonString);
			return jsonString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			/*
			 * 接受子线程中的返回值 并判断是否需要跳转的下一页的
			 */
			int code = -1;
			int count = 0; // 一共显示多少个司机
			LatLng latlng = null; // 最后的经纬度
			try {
				/*
				 * json 数据解析 JSONObject 把任意的对象变成json对象
				 */
				JSONObject jsonParser = new JSONObject(result.toString());
				code = jsonParser.getInt("result");
				count = jsonParser.getInt("count");
				Log.i("LOG", jsonParser.getString("result") + "result");

				JSONArray jsonarray; // json 数组
				jsonarray = (JSONArray) (jsonParser.get("content")); // 数据的数组赋值给JsonArray
				Log.i("LOG", jsonarray + "jsonarray");
				if (count != 0 && code == 0) {
					// 清空一次
					aMap.clear();
				}

				for (int i = 0; i < count; i++) {
					// 添加司机
					JSONObject jsoncontent2 = new JSONObject(
							jsonarray.getString(i));
					// 每次解析以后就添加一个 Double.parseDouble 强制类型转换
					latlng = new LatLng(Double.parseDouble(jsoncontent2
							.getString("map_latitude")),
							Double.parseDouble(jsoncontent2
									.getString("map_longitude")));
					if (jsoncontent2.getInt("fTaxi") == 1) {
						mGPSMarker1 = aMap
								.addMarker(new MarkerOptions()
										.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
												.decodeResource(getResources(),
														R.drawable.taxi_2)))
										.anchor((float) 0.5, (float) 0.5)
										.setFlat(true));
					} else {
						mGPSMarker1 = aMap
								.addMarker(new MarkerOptions()
										.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
												.decodeResource(getResources(),
														R.drawable.taxi_1)))
										.anchor((float) 0.5, (float) 0.5)
										.setFlat(true));
					}

					mGPSMarker1.setPosition(latlng);

					Log.i("LOG",
							Double.parseDouble(jsoncontent2
									.getString("map_longitude"))
									+ "count"
									+ Double.parseDouble(jsoncontent2
											.getString("map_latitude")));
				}

			} catch (JSONException ex) {
				// 异常处理代码
				Log.i("LOG", "异常");
			}

			if (code == 0) {
				// 添加自己当前位置 不需要添加的 清除的时候，没有清除
				mGPSMarker = aMap.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
								.decodeResource(getResources(),
										R.drawable.location_marker)))
						.anchor((float) 0.5, (float) 0.5).setFlat(true)
						.snippet("00").title(getAddress));
				mGPSMarker.setPosition(new LatLng(lan, lon));

			} else {
				Toast.makeText(CarRoute.this, "网络出问题了", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * 获取智能设备唯一编号
	 * 
	 * @return
	 */
	public String telephone() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String deviceid = tm.getDeviceId();// 获取智能设备唯一编号
		Log.i("LOG", deviceid);
		return deviceid;
	}

	/**
	 * 退出功能
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				this.exitApp();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 退出程序
	 */
	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(CarRoute.this, "再按一次退出程序", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
}
