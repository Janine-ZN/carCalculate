package com.ustc.carcalculate.person.other;

import com.ustc.carcalculate.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 进行修改密保问题之前,先做个提示;在该界面进行数据库查询返回
 * 
 * @author zhaonan
 * 
 */
public class QuestionActivity extends Activity {

	private ImageView ivBack;// 返回
	private Button changButton;// 更改按钮
	private TextView titleTextView;// 标题
	private TextView questionTextView;// 之前的密保问题

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示系统标题
		setContentView(R.layout.question);
		initView();
		initEvent();
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_question_back);
		titleTextView = (TextView) findViewById(R.id.tv_question_title);
		titleTextView.setText("修改密保问题");
		questionTextView = (TextView) findViewById(R.id.tv_show_question_info);
		

	}

	private void initEvent() {
		// TODO Auto-generated method stub

	}
}
