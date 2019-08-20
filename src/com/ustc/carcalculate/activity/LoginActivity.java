package com.ustc.carcalculate.activity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText accountEditText;
	private EditText passwordEditText;

	private Button loginButton;
	private Button closeButton;

	private TextView registTextView;
	private TextView forgetTextView;

	String account;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		intiView();
		initEvent();
	}

	private void intiView() {

		accountEditText = (EditText) findViewById(R.id.et_login_account);
		passwordEditText = (EditText) findViewById(R.id.et_login_password);

		loginButton = (Button) findViewById(R.id.btn_login);
		closeButton = (Button) findViewById(R.id.btn_login_close);

		registTextView = (TextView) findViewById(R.id.tv_regist);
		registTextView.setText(Html.fromHtml("<u>" + "立即注册" + "</u>"));
		forgetTextView = (TextView) findViewById(R.id.tv_login_forget);

	}

	private void initEvent() {
		loginButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);

		registTextView.setOnClickListener(this);
		forgetTextView.setOnClickListener(this);

	}

	/**
	 * 登录的异步通信任务
	 */
	public class loginMyAsyncTask extends AsyncTask<String, Void, String> {
		/**
		 * 子线程
		 */
		@Override
		protected String doInBackground(String... request) {

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/LoginServlet";
			HttpPost httpPost = new HttpPost(url);
			String jsonString = "";

			JSONObject jsonObject = new JSONObject();// 将数据打包成JSON格式

			account = accountEditText.getText().toString();// 获得用户名称
			password = passwordEditText.getText().toString();// 获得密码

			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("account", account);
				jsonObject.put("password", password);
				// jsonObject.put("deviceid", deviceid);

				NameValuePair nvp = new BasicNameValuePair("loginRequest",
						jsonObject.toString());// 打包成功后,发送数据
				Log.i("LOG",
						"jsonObject.toString()--->" + jsonObject.toString());
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
					jsonString = new String(buffer, 0, length);// 得到JSON字符串
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
			Log.i("LOG", "login**" + jsonString);
			return jsonString;

		}

		/**
		 * 主线程
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			int code = -1;// 代表返回的result值
			int user_id = -1;
			String username = null;
			String phonenum = null;
			String headimg = null;

			// 接受子线程中的返回值 并判断是否需要跳转的下一页的
			try {

				// JSON 数据解析 JSONObject 把任意的对象变成JSON对象
				JSONObject jsonParser = new JSONObject(result.toString());
				code = jsonParser.getInt("result");
				user_id = jsonParser.getInt("user_id");
				username = jsonParser.getString("username");
				phonenum = jsonParser.getString("phone");
				headimg = jsonParser.getString("image");
				/******************* 将登录时的用户名和密码保存到共享参数中 *******************/
				SharedPreferences pref = getSharedPreferences("myPref",
						MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("username", username);
				editor.putString("phonenum", phonenum);
				editor.putString("password", password);
				editor.putString("userId", String.valueOf(user_id));// 存的是用户ID
				editor.putString("headimg", headimg);// 存的是用户ID
				editor.commit(); // 提交
				/******************* 将登录时的用户名和密码保存到共享参数中 *******************/
			} catch (JSONException ex) {
				// 异常处理代码
				ex.printStackTrace();
			}

			if (validate()) {
				if (code == 0) {
					Intent intent = new Intent(LoginActivity.this,
							CarRoute.class);
					startActivity(intent);
					finish();
					Toast.makeText(LoginActivity.this, "登录成功!",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this, "登录失败!",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 验证用户名和密码是否填写
	 * 
	 * @return
	 */
	private boolean validate() {
		account = accountEditText.getText().toString();
		if (account.equals("")) {
			showDialog("昵称/手机号是必填项！");
			return false;
		}
		password = passwordEditText.getText().toString();
		if (password.equals("")) {
			showDialog("用户密码是必填项！");
			return false;
		}
		return true;
	}

	/**
	 * 若没有填写用户名/密码，则弹出提示框
	 * 
	 * @param msg
	 */
	private void showDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			// 异步通信任务
			new loginMyAsyncTask().execute();

			break;
		case R.id.btn_login_close:
			Intent mainintent = new Intent(LoginActivity.this, CarRoute.class);
			startActivity(mainintent);// 关闭
			finish();
			break;
		case R.id.tv_regist:
			Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
			startActivity(intent);// 立即注册
			finish();
			break;
		case R.id.tv_login_forget:
			Intent forgetintent = new Intent(LoginActivity.this,
					ForgetActivity.class);
			startActivity(forgetintent);// 忘记密码
			finish();
			break;
		default:
			break;
		}

	}
}
