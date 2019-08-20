package com.ustc.carcalculate.activity;

import com.ustc.carcalculate.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SecurityQuestionActivity extends Activity implements
		OnClickListener {

	private Button btnNextStep;// 下一步
	private ImageView ivBack;// 返回
	private TextView tvSetQuestionTitle;// 设置密码问题的标题
	private EditText etanswer;// 答案

	private Spinner spinnerQuestion;// 下拉菜单问题

	// 下拉菜单
	private static final String[] m = { "您母亲的姓名是？", "您父亲的姓名是？", "您配偶的姓名是？",
			"您的出生地是？", "您高中班主任的名字是？", "您初中班主任的名字是？", "您小学班主任的名字是？", "您的小学校名是？",
			"您的学号（或工号）是？", "您父亲的生日是？", "您母亲的生日是？", "您配偶的生日是？" };

	private ArrayAdapter<String> adapter;

	private String question; // 问题
	private String answer; // 答案
	private String telephone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_security_question);

		// 获取上界面传来的talephone的值
		Bundle bundle = this.getIntent().getExtras();
		telephone = bundle.getString("telephone");
		initView();
		initEvent();
	}

	private void initView() {
		tvSetQuestionTitle = (TextView) findViewById(R.id.tv_set_security_question_title);
		tvSetQuestionTitle.setText("设置密保问题");
		btnNextStep = (Button) findViewById(R.id.btn_security_next_step);
		btnNextStep.setText("下一步");
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
		btnNextStep.setOnClickListener(this);
		ivBack.setOnClickListener(this);

	}

	/**
	 * 使用数组形式操作
	 * 
	 * @author zhaonan
	 * 
	 */
	public class SpinnerSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			Toast.makeText(SecurityQuestionActivity.this, "题目是：" + m[arg2],
					Toast.LENGTH_SHORT).show();
			question = m[arg2];
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_security_next_step:
			answer = etanswer.getText().toString();
			// 此处不进行一步通信
			if (!TextUtils.isEmpty(answer)) {
				Intent passwordIntent = new Intent(
						SecurityQuestionActivity.this,
						SetPasswordActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("telephone", telephone);
				bundle.putString("question", question);
				bundle.putString("answer", answer);
				passwordIntent.putExtras(bundle);// 将必要的参数，传到密码设置界面
				startActivity(passwordIntent);
				SecurityQuestionActivity.this.finish();
				System.out.println("zhaonan:debug--telephone,question,answer->"
						+ telephone + "," + question + "," + answer);

			} else {
				Toast.makeText(SecurityQuestionActivity.this, "不能设置空答案",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.iv_security_head_back:
			Intent securityIntent = new Intent(SecurityQuestionActivity.this,
					RegistActivity.class);
			startActivity(securityIntent);
			SecurityQuestionActivity.this.finish();
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
			Intent phoneIntent = new Intent(SecurityQuestionActivity.this,
					SetPasswordActivity.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
