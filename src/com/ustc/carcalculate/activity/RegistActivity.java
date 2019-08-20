package com.ustc.carcalculate.activity;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.ustc.carcalculate.R;
import com.ustc.carcalculate.utils.MyCountTimer;

import static cn.smssdk.framework.utils.R.getStringRes;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistActivity extends Activity implements OnClickListener {

	private Button btnGetVerifyCode;// 获取验证码
	private Button btnNextStep;// 下一步
	private ImageView ivBack;// 返回

	private EditText etTelephone;// 手机号
	private EditText etVerifyCode;// 返回

	String APPKEY = "da318680ba16";// 获取短信验证的Key
	String APPSECRET = "f840f41ed5d512dbe03cc053802c4c30";// 获取短信验证的secret

	String telephone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示系统标题
		setContentView(R.layout.activity_regist);

		intiView();
		initEvent();

		// 初始化验证码
		SMSSDK.initSDK(this, APPKEY, APPSECRET);
		EventHandler eh = new EventHandler() {

			@Override
			public void afterEvent(int event, int result, Object data) {

				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}

		};
		SMSSDK.registerEventHandler(eh);

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.i("event", "event=" + event);
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 短信注册成功后，返回MainActivity,然后提示新好友
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					Toast.makeText(getApplicationContext(), "提交验证码成功",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(RegistActivity.this,
							SecurityQuestionActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("telephone", telephone);
					intent.putExtras(bundle);
					startActivity(intent);
					RegistActivity.this.finish();
					// textView2.setText("提交验证码成功");//验证验证码通过。
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					Toast.makeText(getApplicationContext(), "验证码已经发送",
							Toast.LENGTH_SHORT).show();
					// textView2.setText("验证码已经发送");//发送验证码到手机。
				} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表
					Toast.makeText(getApplicationContext(), "获取国家列表成功",
							Toast.LENGTH_SHORT).show();
					// countryTextView.setText(data.toString());
				}
			} else {
				// 验证码错误
				((Throwable) data).printStackTrace();
				int resId = getStringRes(RegistActivity.this,
						"smssdk_network_error");
				Toast.makeText(RegistActivity.this, "验证码错误", Toast.LENGTH_SHORT)
						.show();
				if (resId > 0) {
					Toast.makeText(RegistActivity.this, resId,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
	}

	/**
	 * 获取各个控件
	 */
	private void intiView() {

		btnGetVerifyCode = (Button) findViewById(R.id.btn_regist_get_verify_code);
		btnNextStep = (Button) findViewById(R.id.btn_regist_next_step);

		ivBack = (ImageView) findViewById(R.id.iv_regist_head_back);

		etTelephone = (EditText) findViewById(R.id.et_regist_telephone);
		etVerifyCode = (EditText) findViewById(R.id.et_regist_verify_code);

	}

	/**
	 * 设置控件的监听事件
	 */
	private void initEvent() {

		btnGetVerifyCode.setOnClickListener(this);
		btnNextStep.setOnClickListener(this);
		ivBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_regist_get_verify_code:
			if (!TextUtils.isEmpty(etTelephone.getText().toString())) {
				MyCountTimer myCountTimer = new MyCountTimer(btnGetVerifyCode);// 计时器
				myCountTimer.start();
				SMSSDK.getVerificationCode("86", etTelephone.getText()
						.toString());// 获取验证码
				telephone = etTelephone.getText().toString();
			} else {
				Toast.makeText(this, "电话不能为空", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.btn_regist_next_step:
			if (!TextUtils.isEmpty(etVerifyCode.getText().toString())) {
				SMSSDK.submitVerificationCode("86", telephone, etVerifyCode
						.getText().toString());// 验证验证码
			} else {
				Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.iv_regist_head_back:
			// 返回按钮：返回到登录界面
			Intent registIntent = new Intent(RegistActivity.this,
					LoginActivity.class);
			startActivity(registIntent);
			RegistActivity.this.finish();
			break;
		}

	}

	/**
	 * 调用系统的返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 返回到登录界面
			Intent Intent = new Intent(RegistActivity.this, LoginActivity.class);
			startActivity(Intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
