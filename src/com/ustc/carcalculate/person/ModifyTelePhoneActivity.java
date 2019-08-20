package com.ustc.carcalculate.person;

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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 此类是修改手机号; 在此界面之前，还应该有提示界面，，请参考滴滴打车
 * 
 * @author Administrator
 * 
 */
public class ModifyTelePhoneActivity extends Activity implements
		OnClickListener {

	private ImageView back;// 返回
	private TextView title;// 头部信息
	private EditText content;// 修改内容
	private Button submit;// 完成

	String telephone;// 手机号
	String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示系统标题
		setContentView(R.layout.about_modify);// 修改昵称、手机号、密码使用了同一个布局
		initView();
		initEvent();
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.iv_modify_back);
		title = (TextView) findViewById(R.id.tv_modify_title);
		content = (EditText) findViewById(R.id.et_modify_content);
		content.setInputType(InputType.TYPE_CLASS_NUMBER);// 调数字键盘
		submit = (Button) findViewById(R.id.btn_modify_submit);
		title.setText("更改手机号");

	}

	private void initEvent() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	public void getTelephone() {
		// 读取共享参数中的uesrId值,,,,该值是user表的主键
		SharedPreferences pref = getSharedPreferences("myPref", MODE_PRIVATE);
		Editor editor = pref.edit();
		userId = pref.getString("userId", "2920336708");
		editor.commit(); // 提交
		telephone = content.getText().toString();// 获取edittext中的值
		if (!TextUtils.isEmpty(telephone)) {
			if (telephoneFormat(telephone) == 1) {
				new TelephoneMyAsyncTask().execute(userId, telephone);
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

	/**
	 * 改变手机号的异步通信任务
	 */
	public class TelephoneMyAsyncTask extends AsyncTask<String, Void, String> {
		// 子线程 POST请求 返回
		@Override
		protected String doInBackground(String... request) {
			// TODO Auto-generated method stub
			String user_id = request[0];
			String telephone = request[1];
			String jsonString = "";

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/ModifyTelephoneServlet";
			HttpPost httpPost = new HttpPost(url);
			// 将数据打包成JSON格式
			JSONObject jsonObject = new JSONObject();
			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("user_id", user_id);
				jsonObject.put("telephone", telephone);

				// 打包成功后,发送数据
				NameValuePair nvp = new BasicNameValuePair(
						"ModifyTelephoneRequest", jsonObject.toString());

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
			Log.i("LOG", "nan:debug--telephone.jsonString--->" + jsonString);
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
				Toast.makeText(ModifyTelePhoneActivity.this, "异常",
						Toast.LENGTH_SHORT).show();
				Log.i("LOG", "异常");
			}

			if (code == 0) {
				// 修改成功后，进入个人信息界面
				Toast.makeText(ModifyTelePhoneActivity.this, "修改成功！",
						Toast.LENGTH_SHORT).show();

				/******************* 将登录时的用户名和密码保存到共享参数中 *******************/
				SharedPreferences pref = getSharedPreferences("myPref",
						MODE_PRIVATE);
				Editor editor = pref.edit();
				editor.putString("phonenum", telephone);
				editor.commit(); // 提交
				/******************* 将登录时的用户名和密码保存到共享参数中 *******************/

				Intent intent = new Intent(ModifyTelePhoneActivity.this,
						AboutActivity.class);
				startActivity(intent);
				finish();

			} else {
				// 数据提交失败了
				Toast.makeText(ModifyTelePhoneActivity.this, "数据提交失败了！",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_modify_back:
			// 返回个人信息界面
			Intent telephoneIntent = new Intent(ModifyTelePhoneActivity.this,
					AboutActivity.class);
			startActivity(telephoneIntent);
			ModifyTelePhoneActivity.this.finish();
			break;
		case R.id.btn_modify_submit:
			// 用来实现异步通信任务
			getTelephone();
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
			Intent phoneIntent = new Intent(ModifyTelePhoneActivity.this,
					AboutActivity.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
