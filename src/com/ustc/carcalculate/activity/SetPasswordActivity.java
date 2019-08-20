package com.ustc.carcalculate.activity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.ustc.carcalculate.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SetPasswordActivity extends Activity implements OnClickListener {

	private ImageView ivBack;// 返回

	private EditText nameEditText;// 昵称
	private EditText passwordEditText;// 密码

	private Button submitButton;// 完成提交

	private String telephone;// 电话
	private String question; // 问题
	private String answer; // 答案
	private String name;// 昵称
	private String password;// 密码
	private String headimg;
	String deviceid;// 设备Id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.activity_set_password);

		// 获取上界面传来的参数
		Bundle bundle = this.getIntent().getExtras();
		telephone = bundle.getString("telephone");
		question = bundle.getString("question");
		answer = bundle.getString("answer");
		initView();
		initEvent();
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_set_password_head_back);
		submitButton = (Button) findViewById(R.id.btn_set_password_submit);
		nameEditText = (EditText) findViewById(R.id.et_setpassword_name);
		passwordEditText = (EditText) findViewById(R.id.et_set_password);

	}

	private void initEvent() {
		ivBack.setOnClickListener(this);
		submitButton.setOnClickListener(this);

	}

	/**
	 * 验证码校验
	 * 
	 * @param name
	 * @return
	 */
	private int nickNameFormat(String name) {
		String line = name;
		String pattern = "^.{2,6}$";

		Pattern r = Pattern.compile(pattern);// 创建 Pattern 对象
		Matcher m = r.matcher(line);// 现在创建 matcher 对象

		if (m.find()) {
			Log.i("LOG", "验证 Yes");
			return 1;
		} else {
			Log.i("LOG", "验证  No");
			return 0;
		}
	}

	/**
	 * 密码校验
	 * 
	 * @param name
	 * @return
	 */
	private int passwordFormat(String name) {
		String line = name;
		String pattern = "^[0-9_a-zA-Z]{6,12}$";

		Pattern r = Pattern.compile(pattern);// 创建 Pattern 对象
		Matcher m = r.matcher(line);// 现在创建 matcher 对象

		if (m.find()) {
			Log.i("LOG", "验证 Yes");
			return 1;
		} else {
			Log.i("LOG", "验证  No");
			return 0;
		}
	}

	/**
	 * 异步任务 POST请求发送 name, telephone, password,question, answer, deviceid 等信息
	 * 
	 * @author zhaonan
	 * 
	 */
	public class RegistMyAsyncTask extends AsyncTask<String, Void, String> {
		/**
		 * 子线程 POST请求 返回
		 */
		@Override
		protected String doInBackground(String... request) {

			String name_name = request[0];
			String telephone_telephone = request[1];
			String password_password = request[2];
			String question_question = request[3];
			String answer_answer = request[4];
			String deviceid_deviceid = request[5];
			String jsonString = "";

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/RegistServlet";
			HttpPost httpPost = new HttpPost(url);
			// 将数据打包成JSON格式
			JSONObject jsonObject = new JSONObject();

			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("name", name_name);
				jsonObject.put("telephone", telephone_telephone);
				jsonObject.put("question", question_question);
				jsonObject.put("answer", answer_answer);
				jsonObject.put("password", password_password);
				jsonObject.put("deviceid", deviceid_deviceid);
				jsonObject.put("level", "普通会员");

				// 打包成功后,发送数据
				NameValuePair nvp = new BasicNameValuePair("RegistRequest",
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
				char[] buffer = new char[1024];
				int length = 0;
				while ((length = reader.read(buffer)) != 0) {

					jsonString = new String(buffer, 0, length); // 得到JSON字符串
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			Log.i("LOG", "**" + jsonString);
			return jsonString;
		}

		/**
		 * 主线程 操作UI
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// 接受子线程中的返回值 并判断是否需要跳转的下一页的
			int code = 1;
			int user_id = -1;
			String headimg = null;
			try {
				// JSON 数据解析 JSONObject 把任意的对象变成JSON对象
				JSONObject jsonParser = new JSONObject(result.toString());
				code = jsonParser.getInt("result");
				user_id = jsonParser.getInt("user_id");
				headimg = jsonParser.getString("image");
				Log.i("LOG", "code" + String.valueOf(code));

			} catch (JSONException ex) {

				Log.i("LOG", "异常");
			}
			// 若code=0说明，数据库已存在该用户的手机号,不能重复注册
			if (code == 0) {
				Toast.makeText(SetPasswordActivity.this, "手机号已被注册!",
						Toast.LENGTH_SHORT).show();

			} else {
				Toast.makeText(SetPasswordActivity.this, "注册成功!",
						Toast.LENGTH_SHORT).show();
				// 这里是数据提交成功 跳转到相应的页面
				Intent intent = new Intent(SetPasswordActivity.this,
						CarRoute.class);
				startActivity(intent);

				/******************* 将登录时的用户名和密码保存到共享参数中 *******************/
				SharedPreferences pref = getSharedPreferences("myPref",
						MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("username", name);
				editor.putString("phonenum", telephone);
				editor.putString("password", password);
				editor.putString("userId", String.valueOf(user_id));// 存的是用户ID
				editor.putString("headimg", headimg);
				editor.commit(); // 提交
				/******************* 将登录时的用户名和密码保存到共享参数中 *******************/

				finish();
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_set_password_head_back:
			// 返回按钮：返回到密保界面
			Intent registIntent = new Intent(SetPasswordActivity.this,
					LoginActivity.class);
			startActivity(registIntent);
			SetPasswordActivity.this.finish();
			break;
		case R.id.btn_set_password_submit:
			name = nameEditText.getText().toString();
			password = passwordEditText.getText().toString();
			if (nickNameFormat(name) == 1) {
				if (passwordFormat(password) == 1) {
					// 向服务器提交用户信息
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					deviceid = tm.getDeviceId();// 获取智能设备唯一编号
					new RegistMyAsyncTask().execute(name, telephone, password,
							question, answer, deviceid);
				} else {
					Toast.makeText(SetPasswordActivity.this,
							"密码只能是数字、下划线、字母，长度6-12位", Toast.LENGTH_SHORT)
							.show();
				}

			} else {
				Toast.makeText(SetPasswordActivity.this, "昵称长度为2-6位",
						Toast.LENGTH_SHORT).show();
			}
		default:
			break;
		}
	}

	/**
	 * 调用系统的返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 返回到密保界面
			Intent Intent = new Intent(SetPasswordActivity.this,
					SecurityQuestionActivity.class);
			startActivity(Intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
