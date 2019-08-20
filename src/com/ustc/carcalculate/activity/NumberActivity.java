package com.ustc.carcalculate.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.ustc.carcalculate.R;
import com.ustc.carcalculate.dto.PhoneInfo;
import com.ustc.carcalculate.utils.PhoneFriend;
import com.ustc.carcalculate.utils.PinYin;
import com.ustc.carcalculate.utils.PinYinComparator;
import com.ustc.carcalculate.utils.SideBar;
import com.ustc.carcalculate.utils.SideBar.OnLetterSelectedListener;
import com.ustc.carcalculate.utils.SortAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NumberActivity extends Activity {
	public static List<PhoneInfo> list = new ArrayList<PhoneInfo>();
	private List<PhoneInfo> data;
	private List<PhoneInfo> showList; // maqing:debug add showList
	/************* 定义索引条、listview、浮动提示 **************************/
	private PinYin pinYin;
	private ListView listView;
	private SideBar sideBar;// 定义索引条
	private TextView mDialogText;// 定义浮动提示
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private SortAdapter adapter;
	/************* 定义索引条、listview、浮动提示 **************************/
	private ImageView backIv;
	private EditText searchNumberEditText;
	private String deviceid = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示自定义标题栏
		setContentView(R.layout.activity_number);
		initView();
		initEvent();
	}

	private void initView() {
		// 获取返回按钮
		backIv = (ImageView) findViewById(R.id.back_about_iv);
		// 获取搜索框
		searchNumberEditText = (EditText) findViewById(R.id.et_search_number);
		// 获取智能设备唯一编号
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceid = tm.getDeviceId();

		// 调用下面的List<PhoneInfo> getNumber()方法
		getNumber(this, deviceid);

		// 加载listview布局
		listView = (ListView) findViewById(R.id.lvphone);
		sideBar = (SideBar) findViewById(R.id.side_bar);
		mDialogText = (TextView) findViewById(R.id.dialog);
		sideBar.setmTextView(mDialogText);

		// 调用异步通信
		new MyAsyncTask().execute(deviceid);

	}

	private void initEvent() {
		sideBar.setLetterSelectedListener(letterSelectedListener);
		searchNumberEditText
				.addTextChangedListener(new searchNumberTextWatcher());
		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(NumberActivity.this, CarRoute.class);
				startActivity(intent);
				finish();// 结束当前界面
			}
		});

	}

	public class searchNumberTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (s.length() > 0) {
				List<PhoneInfo> searchNumberList = new ArrayList<PhoneInfo>();
				for (int i = 0; i < data.size(); i++) {
					System.out
							.println("----Janine.Z.data.get(i).getName()---->"
									+ data.get(i).getName());
					if (data.get(i).getName()
							.indexOf(searchNumberEditText.getText().toString()) != -1) {

						searchNumberList.add(data.get(i));
						System.out
								.println("----Janine.Z.searchNumberList.get(i).getName()---->"
										+ searchNumberList.get(i).getName());
						System.out
								.println("----Janine.Z.searchNumberList.get(i)---->"
										+ searchNumberList.get(i));
					}
				}
				showList = searchNumberList;
				System.out.println("----Janine.Z.data.size()---->"
						+ data.size());
				System.out.println("----Janine.Z.searchNumberList.size()---->"
						+ searchNumberList.size());
				adapter = new SortAdapter(NumberActivity.this, showList);
				listView.setAdapter(adapter);
			} else {
				adapter = new SortAdapter(NumberActivity.this, data);
				listView.setAdapter(adapter);
			}

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}
	}

	private OnLetterSelectedListener letterSelectedListener = new OnLetterSelectedListener() {

		@Override
		public void onLetterSelected(String s) {
			int position = adapter.getPositionBySelection(s.charAt(0));
			listView.setSelection(position);
		}
	};

	/**
	 * 将手机通讯录的内容传到数据库中
	 * 
	 * @param context
	 * @param deviceid
	 * @return
	 */
	public static List<PhoneInfo> getNumber(Context context, String deviceid) {
		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
				null, null, null, null);
		String phoneNumber = null;
		String phoneName = null;
		while (cursor.moveToNext()) {
			// 将手机通讯录里的姓名和密码添加到phoneInfo实体集中
			phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
			phoneName = cursor.getString(cursor
					.getColumnIndex(Phone.DISPLAY_NAME));
			PhoneInfo phoneInfo = new PhoneInfo(phoneName, phoneNumber);
			list.add(phoneInfo);

		}
		// 调用PhoneFriend()多线程，执行数据传输
		new PhoneFriend(list, deviceid).start();
		return list;

	}

	public List<PhoneInfo> fillData(List<Map<String, Object>> obj) {
		Map<String, Object> map;
		List<PhoneInfo> sortModels = new ArrayList<PhoneInfo>();
		for (int i = 0; i < obj.size(); i++) {
			map = obj.get(i);
			String py = pinYin.getPinYin((String) map.get("name"));
			// 将数据放到实体集里
			PhoneInfo model = new PhoneInfo();
			model.setNumber((String) map.get("number"));
			model.setName((String) map.get("name"));
			String sortLetter = py.substring(0, 1).toUpperCase(); // 获取名字拼音的首字母大写
			model.setSortLetter(sortLetter);
			sortModels.add(model);
		}
		return sortModels;

	}

	/**
	 * 异步通信任务
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... request) {
			String deviceid = request[0];

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/GFriendServlet";
			HttpPost httpPost = new HttpPost(url);
			StringBuffer jsonString = new StringBuffer();
			String result = "";
			String line = "";

			// 将数据打包成JSON格式
			JSONObject jsonObject = new JSONObject();

			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("deviceid", deviceid);

				// 打包成功后,发送数据
				NameValuePair nvp = new BasicNameValuePair("GfriendRequest",
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
				BufferedReader bReader = new BufferedReader(reader);
				line = bReader.readLine();
				// 读取字符串数据1
				// char[] buffer = new char[1024];
				// int length = 0;
				// while ((length = reader.read(buffer)) != -1) {
				// result = new String(buffer, 0, length);// 得到JSON字符串
				// jsonString.append(result);
				// inputStream.close();
				// reader.close();
				// }

				// 读取字符串数据2
				// BufferedReader myread = new BufferedReader(reader);
				// String line = "";
				//
				// while ((line = myread.readLine()) != null) {
				// result += line;
				// }
				//
				// System.out.println("-------->" + result);

			} catch (Exception e) {
				e.printStackTrace();

			}
			return line;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			/**
			 * 子线程 POST请求 返回 deviceid
			 */
			try {
				/*
				 * json 数据解析 JSONObject 把任意的对象变成json对象
				 */
				JSONObject jsonParser = new JSONObject(result.toString());

				JSONArray jsonarray; // json 数组
				jsonarray = (JSONArray) (jsonParser.get("content")); // 数据的数组赋值给JsonArray
				Log.i("a", "jsonarray.length()=" + jsonarray.length());
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject jsoncontent2 = new JSONObject(
							jsonarray.getString(i));

					// 将name和num放到map里
					Map<String, Object> map = new HashMap<String, Object>();

					String deviceid = jsoncontent2.getString("map_deviceid");// 用于以后
					String name = jsoncontent2.getString("person_name");
					String number = jsoncontent2.getString("person_phone");

					map.put("name", name);
					map.put("number", number);
					dataList.add(map);
					dataList.toString();
					// 数据
					data = fillData(dataList);
					// 按字母排序
					Collections.sort(data, new PinYinComparator());
					adapter = new SortAdapter(NumberActivity.this, data);
					listView.setAdapter(adapter);

					/**
					 * 1.初始化item 2.根据section获取position 3.实现点击item拨打电话
					 */
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {

							String phone = (String) data.get(position)
									.getNumber();
							Uri uri = Uri.parse("tel:" + phone);
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_DIAL);
							intent.setData(uri);
							startActivity(intent);

						}

					});

					// Log.i("LOG", "dateList" + dataList.toString());
				}

			} catch (JSONException ex) {
				// 异常处理代码
				Log.i("LOG", "异常");
			}

		}
	}

	/**
	 * 调用系统的返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent phoneIntent = new Intent(NumberActivity.this, CarRoute.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
