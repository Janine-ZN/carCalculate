package com.ustc.carcalculate.person.other;

import com.ustc.carcalculate.R;
import com.ustc.carcalculate.person.AboutActivity;
import com.ustc.carcalculate.person.ModifyTelePhoneActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 修改手机号提示界面
 * 
 * @author zhaonan
 * 
 */
public class TelephoneActivity extends Activity implements OnClickListener {

	private ImageView ivBack;// 返回
	private Button changButton;// 更改按钮
	private TextView titleTextView;// 标题
	private TextView telephoneTextView;// 手机号

	String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示系统标题
		setContentView(R.layout.telephone);
		// 获取上界面传来的phone的值
		Bundle bundle = this.getIntent().getExtras();
		phone = bundle.getString("phone");
		System.out.println("debug--phone-->" + phone);
		initView();
		initEvent();
	}

	/**
	 * 获取各个控件
	 */
	private void initView() {

		ivBack = (ImageView) findViewById(R.id.iv_telephone_back);
		changButton = (Button) findViewById(R.id.btn_telephone);
		titleTextView = (TextView) findViewById(R.id.tv_telephone_title);
		titleTextView.setText("更改手机号");
		telephoneTextView = (TextView) findViewById(R.id.tv_show_telephone_info);
		telephoneTextView.setText(phone);

	}

	private void initEvent() {
		ivBack.setOnClickListener(this);
		changButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_telephone_back:
			// 返回到个人信息界面
			Intent backIntent = new Intent(TelephoneActivity.this,
					AboutActivity.class);
			startActivity(backIntent);
			TelephoneActivity.this.finish();
			break;
		case R.id.btn_telephone:
			// 点击进入手机修改
			Intent telephoneIntent = new Intent(TelephoneActivity.this,
					ModifyTelePhoneActivity.class);
			startActivity(telephoneIntent);
			TelephoneActivity.this.finish();
			break;

		default:
			break;
		}

	}
}
