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
import com.ustc.carcalculate.person.ModifyPasswordActivity;
import com.ustc.carcalculate.person.ModifyTelePhoneActivity.TelephoneMyAsyncTask;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 此界面用于回答密保问题
 * 
 * @author Janine.Z
 * 
 */
public class ForgetActivity extends Activity implements OnClickListener {

	private ImageView ivBack;// 返回
	private TextView tvForgetPasswordTitle;// 设置密码问题的标题
	private EditText etPhone;// 手机号
	private Spinner spinnerQuestion;// 下拉菜单问题
	private EditText etAnswer;// 答案
	private Button btnNextStep;// 下一步

	// 下拉菜单
	private static final String[] m = { "您母亲的姓名是？", "您父亲的姓名是？", "您配偶的姓名是？",
			"您的出生地是？", "您高中班主任的名字是？", "您初中班主任的名字是？", "您小学班主任的名字是？", "您的小学校名是？",
			"您的学号（或工号）是？", "您父亲的生日是？", "您母亲的生日是？", "您配偶的生日是？" };

	private ArrayAdapter<String> adapter;

	private String telephone;// 手机号
	private String question; // 问题
	private String answer; // 答案

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget);

		initView();
		initEvent();
	}

	private void initView() {

		ivBack = (ImageView) findViewById(R.id.iv_forget_password_head_back);
		tvForgetPasswordTitle = (TextView) findViewById(R.id.tv_forget_password_title);
		tvForgetPasswordTitle.setText("忘记密码");
		btnNextStep = (Button) findViewById(R.id.btn_forget_password_next_step);
		btnNextStep.setText("下一步");

		etPhone = (EditText) findViewById(R.id.et_forget_password_telephone);
		etAnswer = (EditText) findViewById(R.id.et_forget_password_answer);

		spinnerQuestion = (Spinner) findViewById(R.id.spinner_forget_password_question);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m); // 将可选内容与ArrayAdapter连接起来
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// 设置下拉列表的风格
		spinnerQuestion.setAdapter(adapter);// 将adapter添加到spinner中
		spinnerQuestion
				.setOnItemSelectedListener(new SpinnerSelectedListener());// 添加事件Spinner事件监听
		spinnerQuestion.setVisibility(View.VISIBLE);// 设置默认值

	}

	/**
	 * 使用数组形式操作
	 * 
	 * @author Janine.Z
	 * 
	 */
	public class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			System.out.println("---Janine.Z.题目是：m[arg2]---->" + m[arg2]);
			question = m[arg2];// 获取到选中的问题

		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private void initEvent() {
		btnNextStep.setOnClickListener(this);
		ivBack.setOnClickListener(this);

	}

	private void getInfo() {

		answer = etAnswer.getText().toString();// 获取控件中的答案
		telephone = etPhone.getText().toString();// 获取控件中的手机号
		if (!TextUtils.isEmpty(telephone)) {
			if (telephoneFormat(telephone) == 1) {
				new forgetMyAsyncTask().execute(telephone, question, answer);
			}
		} else {
			Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * 手机号格式验证
	 * 
	 * @param mobile
	 * @return
	 */
	private int telephoneFormat(String mobile) {

		String line = mobile;
		String pattern = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern);

		// 现在创建 matcher 对象
		Matcher m = r.matcher(line);
		if (m.find()) {
			return 1;
		} else {
			Toast.makeText(this, "手机格式不对！", Toast.LENGTH_LONG).show();
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_forget_password_next_step:
			// 点击下一步的时候就开始查询，此处需要一个异步通信
			getInfo();
			break;
		case R.id.iv_forget_password_head_back:

			// 返回到登录界面
			Intent securityIntent = new Intent(ForgetActivity.this,
					LoginActivity.class);
			startActivity(securityIntent);
			ForgetActivity.this.finish();

			break;

		default:
			break;
		}

	}

	/**
	 * 忘记密码的异步通信任务
	 * 
	 * @author Janine.Z
	 * 
	 */
	public class forgetMyAsyncTask extends AsyncTask<String, Object, String> {
		/*
		 * 子线程
		 */
		@Override
		protected String doInBackground(String... request) {
			// 获取1.手机号 2.下拉菜单里的内容 3.答案 将这些数据传输到服务器
			String tranTelephone = request[0];
			String tranQuestion = request[1];
			String tranAnswer = request[2];

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/ForgetServlet";
			HttpPost httpPost = new HttpPost(url);
			String jsonString = "";

			JSONObject jsonObject = new JSONObject();// 将数据打包成JSON格式

			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("telephone", tranTelephone);
				jsonObject.put("question", tranQuestion);
				jsonObject.put("answer", tranAnswer);

				NameValuePair nvp = new BasicNameValuePair("forgetRequest",
						jsonObject.toString());// 打包成功后,发送数据

				Log.i("LOG", "----Janine.Z:jsonObject.toString()--->"
						+ jsonObject.toString());// 验证数据是否正常传输

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
			Log.i("LOG", "----Janine.Z:forget.jsonsString---->" + jsonString);
			return jsonString;
		}

		/*
		 * 主线程
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			int resultsCode = -1;// 代表返回的result值
			int user_id = -1;

			try {

				// JSON 数据解析 JSONObject 把任意的对象变成JSON对象
				JSONObject jsonParser = new JSONObject(result.toString());
				resultsCode = jsonParser.getInt("result");
				user_id = jsonParser.getInt("user_id");
				System.out.println("----Janine.Z:result.user_id---->"
						+ resultsCode + user_id);
			} catch (JSONException ex) {
				// 异常处理代码
				ex.printStackTrace();
			}

			if (resultsCode == 0) {
				// 回答正确，跳转到密码修改界面
				Intent intent = new Intent(ForgetActivity.this,
						ForgetPasswordActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("userId", user_id);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
				Toast.makeText(ForgetActivity.this, "回答正确", Toast.LENGTH_SHORT)
						.show();
			} else {

				Toast.makeText(ForgetActivity.this, "对不起，您回答错误!",
						Toast.LENGTH_SHORT).show();

			}
		}
	}

	/**
	 * 调用系统的返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent Intent = new Intent(ForgetActivity.this, LoginActivity.class);
			startActivity(Intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
