package com.ustc.carcalculate.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.ustc.carcalculate.R;
import com.ustc.carcalculate.activity.NumberActivity.searchNumberTextWatcher;
import com.ustc.carcalculate.dto.PhoneInfo;
import com.ustc.carcalculate.route.DriveRouteDetailActivity;
import com.ustc.carcalculate.route.WalkRouteDetailActivity;
import com.ustc.carcalculate.route.adapter.BusResultListAdapter;
import com.ustc.carcalculate.route.utils.AMapUtil;
import com.ustc.carcalculate.route.utils.ToastUtil;
import com.ustc.carcalculate.utils.SortAdapter;

import android.R.bool;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * LocationSource: OnMarkerClickListener: OnMapClickListener:
 * OnInfoWindowClickListener: OnPoiSearchListenerL: OnRouteSearchListener:
 * AMapNaviListener: AMapLocationListener： OnGeocodeSearchListener：
 * InfoWindowAdapter
 * 
 * @author JBaymax
 * 
 */
public class RouteActivity extends Activity implements LocationSource,
		OnMarkerClickListener, OnMapClickListener, OnInfoWindowClickListener,
		OnRouteSearchListener, AMapNaviListener, AMapLocationListener,
		OnGeocodeSearchListener, InfoWindowAdapter {
	private ImageView backIv;// 返回键

	private AMap aMap;// AMap类
	private MapView mapView;// 地图视图
	private Context mContext;
	private RouteSearch mRouteSearch;// 搜索
	private DriveRouteResult mDriveRouteResult;// 驾车
	private BusRouteResult mBusRouteResult;// 公交
	private WalkRouteResult mWalkRouteResult;// 步行

	private final int ROUTE_TYPE_BUS = 1;// 公交常量
	private final int ROUTE_TYPE_DRIVE = 2;// 驾车常量
	private final int ROUTE_TYPE_WALK = 3;// 步行常量

	private LinearLayout mBusResultLayout;// 公交车布局
	private RelativeLayout mBottonLayout;// 按钮布局
	private TextView mRotueTimeDes, mRouteDetailDes;// 路线时间,路线详情
	private ImageView mBus;// 公交图片
	private ImageView mDrive;// 驾车图片
	private ImageView mWalk;// 步行图片
	private ListView mBusResultList;

	private EditText StartPointEditText;// 起始的编辑框
	private EditText EndPointEditText;// 终点的编辑框
	private String StartPoint;
	private String EndPoint;
	// 地理编码
	private GeocodeSearch geocoderSearch;// 地理编码
	private LatLonPoint mStartPoint = null;// 起始的地理坐标
	private LatLonPoint mEndPoint = null;// 终点地理坐标
	private String mCurrentCityName;
	int resultProvince;// 出现“省”字时候的位置数字
	int resultCity;// 出现“市”字时候的位置数字
	private boolean sign = false;
	/************** 定位的成员变量 ****************/
	NaviLatLng startLatlng = null;
	NaviLatLng endLatlng = null;
	// 将起点和终点加到NaviLatLng数组
	List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
	List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
	private AMapNavi aMapNavi;
	/************** 定位的成员变量 ****************/
	private LocationManagerProxy mLocationManagerProxy;
	double lan = 0.0;
	double lon = 0.0;
	/************** 定位的成员变量 ****************/
	private String getAddress = "";// 当前位置
	private Marker mGPSMarker;
	// 第一次定位 显示当前位置
	private int one = 0;// 初始化当前位置

	private ProgressDialog progDialog = null;// 搜索时进度条

	private boolean isClickStart = false;
	private boolean isClickTarget = false;
	private Marker targetMk;
	public ArrayAdapter<String> aAdapter;

	private LinearLayout routeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route);

		mContext = this.getApplicationContext();// 上下文
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		init();// 初始化关于地图的相关信息
		initView();// 初始化控件的相关信息
		initEvent();// 注册关于控件的监听事件
		registerListener();// 注册关于地图的监听事件
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		backIv = (ImageView) findViewById(R.id.back_route_iv);// 返回按钮
		StartPointEditText = (EditText) findViewById(R.id.route_start_et);// 起始编辑框
		EndPointEditText = (EditText) findViewById(R.id.route_end_et);// 终点编辑框
		routeLayout = (LinearLayout) findViewById(R.id.RelativeLayout_roadsearch_top);

	}

	/**
	 * 初始化地图
	 */
	private void init() {

		if (aMap == null) {
			aMap = mapView.getMap();
		}
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);// 设置路线查询的监听事件
		mBottonLayout = (RelativeLayout) findViewById(R.id.botton_layout);
		mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
		mRotueTimeDes = (TextView) findViewById(R.id.firstline);
		mRouteDetailDes = (TextView) findViewById(R.id.secondline);
		mDrive = (ImageView) findViewById(R.id.route_drive);// 驾车图片
		mBus = (ImageView) findViewById(R.id.route_bus);// 公交图片
		mWalk = (ImageView) findViewById(R.id.route_walk);// 步行图片
		mBusResultList = (ListView) findViewById(R.id.bus_result_list);// 公交车查询的ListView

		// 初始化定位，只采用网络定位
		mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		mLocationManagerProxy.setGpsEnable(false);
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);

		// getLatlon("中国科技大学先进技术研究院");

	}

	/**
	 * 设置监听事件
	 */
	private void initEvent() {

		// StartPointEditText.addTextChangedListener(new
		// StartPointTextWatcher());
		// EndPointEditText.addTextChangedListener(new EndPointTextWatcher());

		StartPointEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						StartPoint = StartPointEditText.getText().toString();
						getLatlon(StartPoint);
						System.out.println("setOnEditorActionListener()--->"
								+ StartPointEditText.getText().toString());
						System.out
								.println("setOnEditorActionListener.toString--->"
										+ StartPointEditText.getText()
												.toString());
						sign = false;
						return false;
					}
				});
		EndPointEditText
				.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView arg0, int arg1,
							KeyEvent arg2) {
						EndPoint = EndPointEditText.getText().toString();
						getLatlon(EndPoint);
						System.out.println("setOnEditorActionListener()--->"
								+ EndPointEditText.getText().toString());
						System.out
								.println("setOnEditorActionListener.toString--->"
										+ EndPointEditText.getText().toString());
						sign = true;
						return false;
					}
				});

		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteActivity.this, CarRoute.class);
				startActivity(intent);
				finish();
			}
		});

	}

	/**
	 * 注册监听
	 */
	private void registerListener() {

		aMap.setOnMapClickListener(RouteActivity.this);
		aMap.setOnMarkerClickListener(RouteActivity.this);
		aMap.setOnInfoWindowClickListener(RouteActivity.this);
		aMap.setInfoWindowAdapter(RouteActivity.this);

		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种,跟随模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种,跟随模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

	}

	// 公交按钮事件
	public void onBusClick(View view) {
		searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
		mDrive.setImageResource(R.drawable.route_drive_normal);
		mBus.setImageResource(R.drawable.route_bus_select);
		mWalk.setImageResource(R.drawable.route_walk_normal);
		mapView.setVisibility(View.GONE);
		mBusResultLayout.setVisibility(View.VISIBLE);
	}

	// 驾车的按钮事件
	public void onDriveClick(View view) {
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
		mDrive.setImageResource(R.drawable.route_drive_select);
		mBus.setImageResource(R.drawable.route_bus_normal);
		mWalk.setImageResource(R.drawable.route_walk_normal);
		mapView.setVisibility(View.VISIBLE);
		mBusResultLayout.setVisibility(View.GONE);
	}

	// 步行的按钮事件
	public void onWalkClick(View view) {
		searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
		mDrive.setImageResource(R.drawable.route_drive_normal);
		mBus.setImageResource(R.drawable.route_bus_normal);
		mWalk.setImageResource(R.drawable.route_walk_select);
		mapView.setVisibility(View.VISIBLE);
		mBusResultLayout.setVisibility(View.GONE);
	}

	/**
	 * 开始搜索公交路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {
		if (mStartPoint == null) {
			ToastUtil.show(mContext, "定位中，稍后再试...");
			return;
		}
		if (mEndPoint == null) {
			ToastUtil.show(mContext, "终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);// 路径规划的起点和终点
		if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
			BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
					mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, null,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, mode);
			mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {
		dissmissProgressDialog();
		mBottonLayout.setVisibility(View.GONE);
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mBusRouteResult = result;
					BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(
							mContext, mBusRouteResult);
					mBusResultList.setAdapter(mBusResultListAdapter);
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}
			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							this, aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos());
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					mBottonLayout.setVisibility(View.VISIBLE);
					int dis = (int) drivePath.getDistance();
					int dur = (int) drivePath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur) + "("
							+ AMapUtil.getFriendlyLength(dis) + ")";
					mRotueTimeDes.setText(des);
					mRouteDetailDes.setVisibility(View.VISIBLE);
					int taxiCost = (int) mDriveRouteResult.getTaxiCost();
					mRouteDetailDes.setText("打车约" + taxiCost + "元");
					mBottonLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									DriveRouteDetailActivity.class);
							intent.putExtra("drive_path", drivePath);
							intent.putExtra("drive_result", mDriveRouteResult);
							startActivity(intent);
						}
					});
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}

	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == 1000) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mWalkRouteResult = result;
					final WalkPath walkPath = mWalkRouteResult.getPaths()
							.get(0);
					WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
							this, aMap, walkPath,
							mWalkRouteResult.getStartPos(),
							mWalkRouteResult.getTargetPos());
					walkRouteOverlay.removeFromMap();
					walkRouteOverlay.addToMap();
					walkRouteOverlay.zoomToSpan();
					mBottonLayout.setVisibility(View.VISIBLE);
					int dis = (int) walkPath.getDistance();
					int dur = (int) walkPath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur) + "("
							+ AMapUtil.getFriendlyLength(dis) + ")";
					mRotueTimeDes.setText(des);
					mRouteDetailDes.setVisibility(View.GONE);
					mBottonLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									WalkRouteDetailActivity.class);
							intent.putExtra("walk_path", walkPath);
							intent.putExtra("walk_result", mWalkRouteResult);
							startActivity(intent);
						}
					});
				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}
	}

	/**
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域 跳转到 当前位置
	 */
	private void changeCamera(CameraUpdate update) {
		// 这个方法为有动画的移动
		// aMap.animateCamera(update, 1000, callback);
		// 这个方法为瞬间移动，没有移动过程
		aMap.moveCamera(update);
	}

	/**
	 * 获取当前位置,“我的位置”
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {
			// 定位成功回调信息，设置相关消息
			lan = amapLocation.getLatitude();
			lon = amapLocation.getLongitude();
			startLatlng = new NaviLatLng(lan, lon);
		}
		// 刚打开地图的时，定位当前位置,出现蓝色箭头
		final LatLng ZHONGGUANCUN = new LatLng(lan, lon);
		getAddress = amapLocation.getAddress();
		System.out.println("getAddress--->" + getAddress);
		resultProvince = getAddress.indexOf("省");
		resultCity = getAddress.indexOf("市");

		System.out
				.println("Route.getLatlon().getAddress.resultProvince.resultCity--->"
						+ getAddress + "," + resultProvince + "," + resultCity);
		if (resultProvince == 2) {
			// 省市resultProvince==3
			mCurrentCityName = getAddress.substring(resultProvince + 1,
					resultCity);
			System.out.println("Route.getLatlon().mCurrentCityName1"
					+ mCurrentCityName);
		} else if (resultProvince != 2) {
			// 不等于2，说明是直辖市
			mCurrentCityName = getAddress.substring(0, resultCity + 1);
			System.out.println("Route.getLatlon().mCurrentCityName2"
					+ mCurrentCityName);
		}
		if (one == 0) {
			changeCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(ZHONGGUANCUN, 15, 0,
							0)));
			mGPSMarker = aMap.addMarker(new MarkerOptions()
					.title(getAddress)
					.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
							.decodeResource(getResources(),
									R.drawable.location_marker)))
					.anchor((float) 0.5, (float) 0.5).setFlat(true));
			mGPSMarker.setPosition(new LatLng(lan, lon));
			one = 1;
		}

	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(String name) {
		System.out.println("mCurrentCityName-->" + mCurrentCityName);
		GeocodeQuery query = new GeocodeQuery(name, mCurrentCityName);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 地理编码查询回调:获取的是经度纬度
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		System.out.println("onGeocodeSearched().进入--->");
		if (rCode == 1000) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				System.out.println("onGeocodeSearched().if()--->");

				if (!sign) {
					GeocodeAddress address = result.getGeocodeAddressList()
							.get(0);
					aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							AMapUtil.convertToLatLng(address.getLatLonPoint()),
							15));
					mStartPoint = address.getLatLonPoint();
					System.out.println("onGeocodeSearched().Point.1--->"
							+ mStartPoint + ";" + mEndPoint);
					sign = true;
				} else {
					GeocodeAddress address = result.getGeocodeAddressList()
							.get(0);
					aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							AMapUtil.convertToLatLng(address.getLatLonPoint()),
							15));
					mEndPoint = address.getLatLonPoint();
					System.out.println("onGeocodeSearched().Point.2--->"
							+ mStartPoint + ";" + mEndPoint);
					sign = false;
				}
				// System.out
				// .println("onGeocodeSearched().address.getLatLonPoint()--->"
				// + address.getLatLonPoint());
				// String addressName = "经纬度值:" + address.getLatLonPoint()
				// + "\n位置描述:" + address.getFormatAddress();
				// ToastUtil.show(RouteActivity.this, addressName);

			} else {
				ToastUtil.show(RouteActivity.this, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this, rCode);
		}
	}

	// 填出infowindow
	@Override
	public void onInfoWindowClick(Marker marker) {
		isClickStart = false;
		isClickTarget = false;
		if (marker.equals(targetMk)) {
			// endEditText.setText("地图上的终点");
			// endPoint = AMapUtil.convertToLatLonPoint(targetMk.getPosition());
			targetMk.hideInfoWindow();
			targetMk.remove();
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		} else {
			marker.showInfoWindow();
		}
		return false;
	}

	/**
	 * 只获取终点的Marker
	 */
	@Override
	public void onMapClick(LatLng latng) {
		if (isClickTarget) {
			targetMk = aMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.point)).position(latng)
					.title("点击选择为目的地"));
			targetMk.showInfoWindow();
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 
	 * @param startLatlng
	 * @param endLatlng
	 */
	public void calculateRoute() {
		startList.add(startLatlng);// 经纬度
		endList.add(endLatlng);// 经纬度
		aMapNavi.calculateDriveRoute(startList, endList, null,
				PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES);
	}

	private boolean calculateSuccess;

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
	@Deprecated
	public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {

	}

	@Override
	public void hideCross() {

	}

	@Override
	public void hideLaneInfo() {

	}

	@Override
	public void onArriveDestination() {

	}

	@Override
	public void onArrivedWayPoint(int arg0) {

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {

	}

	@Override
	public void onCalculateRouteSuccess() {

	}

	@Override
	public void onEndEmulatorNavi() {

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {

	}

	@Override
	public void onInitNaviFailure() {

	}

	@Override
	public void onInitNaviSuccess() {

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {

	}

	@Override
	@Deprecated
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {

	}

	@Override
	public void onReCalculateRouteForYaw() {

	}

	@Override
	public void onStartNavi(int arg0) {

	}

	@Override
	public void onTrafficStatusUpdate() {

	}

	@Override
	public void showCross(AMapNaviCross arg0) {

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {

	}

	@Override
	public void activate(OnLocationChangedListener arg0) {

	}

	@Override
	public void deactivate() {

	}

	/**
	 * 调用系统的返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent phoneIntent = new Intent(RouteActivity.this, CarRoute.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void notifyParallelRoad(int i) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		// 移除定位请求
		mLocationManagerProxy.removeUpdates(this);
		// 销毁定位
		mLocationManagerProxy.destroy();
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
	}

	@Override
	public void onCalculateMultipleRoutesSuccess(int[] arg0) {
		// TODO Auto-generated method stub

	}

}
