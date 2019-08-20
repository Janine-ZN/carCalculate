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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPasswordActivity extends Activity implements OnClickListener {
	private ImageView back;// 返回
	private TextView title;// 头部信息
	private EditText content;// 修改内容
	private Button submit;// 完成

	int userId;// user表中的主键
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_password);// 使用了密码修改的布局

		// 获取上界面传来的talephone的值
		Bundle bundle = this.getIntent().getExtras();
		userId = bundle.getInt("userId");
		initView();
		initEvent();
	}

	private void initView() {

		back = (ImageView) findViewById(R.id.iv_password_back);
		title = (TextView) findViewById(R.id.tv_password_title);
		content = (EditText) findViewById(R.id.et_password_content);
		submit = (Button) findViewById(R.id.btn_password_submit);
		title.setText("更改密码");
	}

	private void initEvent() {

		back.setOnClickListener(this);
		submit.setOnClickListener(this);

	}

	public void getUserId() {
		password = content.getText().toString();// 获取edittext中的值
		if (!TextUtils.isEmpty(password)) {
			if (passwordFormat(password) == 1) {
				new ForgetPasswordMyAsyncTask().execute(String.valueOf(userId),
						password);
			}
		} else {
			Toast.makeText(this, "密码不能设置为空！", Toast.LENGTH_LONG).show();
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
			Toast.makeText(this, "密码只能是数字、下划线、字母，长度6-12位", Toast.LENGTH_LONG)
					.show();
			return 0;
		}
	}

	/**
	 * 改变密码的异步通信任务
	 */
	public class ForgetPasswordMyAsyncTask extends
			AsyncTask<String, Void, String> {
		// 子线程 POST请求 返回
		@Override
		protected String doInBackground(String... request) {
			// TODO Auto-generated method stub
			String user_id = request[0];
			String password = request[1];
			String jsonString = "";

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/ModifyPasswordServlet";
			HttpPost httpPost = new HttpPost(url);
			// 将数据打包成JSON格式
			JSONObject jsonObject = new JSONObject();
			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("user_id", user_id);
				jsonObject.put("password", password);

				// 打包成功后,发送数据
				NameValuePair nvp = new BasicNameValuePair(
						"ModifyPasswordRequest", jsonObject.toString());

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
			Log.i("LOG", "----Janine.Z:forgetpassword.jsonString--->"
					+ jsonString);
			return jsonString;
		}

		/**
		 * 主线程 操作UI
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// 接受子线程中的返回值 并判断是否需要跳转的下一页的
			int resultsCode = -1;
			try {
				// JSON 数据解析 JSONObject 把任意的对象变成JSON对象
				JSONObject jsonParser = new JSONObject(result.toString());
				resultsCode = jsonParser.getInt("result");

				Log.i("LOG", String.valueOf(resultsCode));

			} catch (JSONException ex) {
				// 异常处理代码
				Toast.makeText(ForgetPasswordActivity.this, "异常",
						Toast.LENGTH_SHORT).show();
				Log.i("LOG", "异常");
			}

			if (resultsCode == 0) {
				// 假如修改成功，则回到登录界面
				Toast.makeText(ForgetPasswordActivity.this, "修改成功！",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ForgetPasswordActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();

			} else {
				// 假如修改失败了，则提示修改失败
				Toast.makeText(ForgetPasswordActivity.this, "修改失败！",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_password_back:
			// 返回到上一界面
			Intent passwordIntent = new Intent(ForgetPasswordActivity.this,
					ForgetActivity.class);
			startActivity(passwordIntent);
			ForgetPasswordActivity.this.finish();
			break;
		case R.id.btn_password_submit:
			// 用来实现异步通信任务
			getUserId();
			break;
		default:
			break;
		}
	}
}
