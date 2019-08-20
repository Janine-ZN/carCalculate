package com.ustc.carcalculate.person;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ModifyQuestionActivity extends Activity implements OnClickListener {
	private Button btnSubmit;// 完成
	private ImageView ivBack;// 返回
	private EditText etanswer;// 答案

	private Spinner spinnerQuestion;// 下拉菜单问题

	// 下拉菜单
	private static final String[] m = { "您母亲的姓名是？", "您父亲的姓名是？", "您配偶的姓名是？",
			"您的出生地是？", "您高中班主任的名字是？", "您初中班主任的名字是？", "您小学班主任的名字是？", "您的小学校名是？",
			"您的学号（或工号）是？", "您父亲的生日是？", "您母亲的生日是？", "您配偶的生日是？" };

	private ArrayAdapter<String> adapter;

	String userId;// user表中的主键
	private String question; // 问题
	private String answer; // 答案

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_security_question);

		initView();
		initEvent();
	}

	private void initView() {

		btnSubmit = (Button) findViewById(R.id.btn_security_next_step);
		btnSubmit.setText("完成");
		ivBack = (ImageView) findViewById(R.id.iv_security_head_back);
		etanswer = (EditText) findViewById(R.id.et_answer);

		spinnerQuestion = (Spinner) findViewById(R.id.spinner_question);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m); // 将可选内容与ArrayAdapter连接起来
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// 设置下拉列表的风格
		spinnerQuestion.setAdapter(adapter);// 将adapter添加到spinner中
		spinnerQuestion
				.setOnItemSelectedListener(new SpinnerSelectedListener());// 添加事件Spinner事件监听
		spinnerQuestion.setVisibility(View.VISIBLE);// 设置默认值

	}

	private void initEvent() {

		btnSubmit.setOnClickListener(this);
		ivBack.setOnClickListener(this);

	}

	/**
	 * 使用数组形式操作
	 * 
	 * @author JBaymax
	 * 
	 */
	public class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Toast.makeText(ModifyQuestionActivity.this, "题目是：" + m[arg2],
					Toast.LENGTH_SHORT).show();
			question = m[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	/**
	 * 获取问题和答案
	 */
	public void getInfo() {
		// 读取共享参数中的uesrId值,,,,该值是user表的主键
		SharedPreferences pref = getSharedPreferences("myPref", MODE_PRIVATE);
		Editor editor = pref.edit();
		userId = pref.getString("userId", "2920336708");
		editor.commit(); // 提交
		answer = etanswer.getText().toString();
		if (!TextUtils.isEmpty(answer)) {
			new ModifyQuestionMyAsyncTask().execute(userId, question, answer);
		} else {
			Toast.makeText(this, "答案不能为空！", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 修改密保的异步通信任务
	 * 
	 * @author JBaymax
	 * 
	 */
	public class ModifyQuestionMyAsyncTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... request) {
			String user_id = request[0];
			String question = request[1];
			String answer = request[2];
			String jsonString = "";

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/ModifyQuestionServlet";
			HttpPost httpPost = new HttpPost(url);
			// 将数据打包成JSON格式
			JSONObject jsonObject = new JSONObject();
			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("user_id", user_id);
				jsonObject.put("question", question);
				jsonObject.put("answer", answer);

				// 打包成功后,发送数据
				NameValuePair nvp = new BasicNameValuePair(
						"ModifyQuestionRequest", jsonObject.toString());

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
			Log.i("LOG", "JBaymax:debug--jsonString--->" + jsonString);
			return jsonString;
		}

		/**
		 * 主线程 操作UI
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// 接受子线程中的返回值 并判断是否需要跳转的下一页的
			int code = -1;
			try {
				// JSON 数据解析 JSONObject 把任意的对象变成JSON对象
				JSONObject jsonParser = new JSONObject(result.toString());
				code = jsonParser.getInt("result");
				Log.i("LOG", String.valueOf(code));

			} catch (JSONException ex) {
				// 异常处理代码
				Toast.makeText(ModifyQuestionActivity.this, "异常",
						Toast.LENGTH_SHORT).show();
				Log.i("LOG", "异常");
			}

			if (code == 0) {
				// 进入个人信息界面
				Toast.makeText(ModifyQuestionActivity.this, "修改成功！",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ModifyQuestionActivity.this,
						AboutActivity.class);
				startActivity(intent);
				finish();

			} else {
				// 数据提交失败了
				Toast.makeText(ModifyQuestionActivity.this, "数据提交失败了！",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_security_next_step:
			// 异步通信任务
			getInfo();
			break;
		case R.id.iv_security_head_back:
			// 返回到密保提示界面
			Intent phoneIntent = new Intent(ModifyQuestionActivity.this,
					AboutActivity.class);
			startActivity(phoneIntent);
			finish();
			break;

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
			Intent phoneIntent = new Intent(ModifyQuestionActivity.this,
					AboutActivity.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
