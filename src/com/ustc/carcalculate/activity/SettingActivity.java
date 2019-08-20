package com.ustc.carcalculate.activity;

import com.ustc.carcalculate.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 在设置里，做程序的说退出
 * 
 * @author Janine.Z
 * 
 */
public class SettingActivity extends Activity {
	private Button quitButton;// 退出按钮
	private ImageView backImageView;// 返回按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initEvent();
	}

	private void initView() {
		backImageView = (ImageView) findViewById(R.id.iv_setting_back);
		quitButton = (Button) findViewById(R.id.btn_setting_quit);// 获取退出按钮

	}

	private void initEvent() {
		// 返回按钮的点击事件
		backImageView.setOnClickListener(new backOnClickListenter());
		// 退出按钮的点击事件
		quitButton.setOnClickListener(new quitOnClickListenter());

	}

	public class backOnClickListenter implements OnClickListener {

		@Override
		public void onClick(View view) {
			// 返回按钮：返回到主界面
			Intent backIntent = new Intent(SettingActivity.this, CarRoute.class);
			startActivity(backIntent);
			SettingActivity.this.finish();

		}
	}

	public class quitOnClickListenter implements OnClickListener {

		@Override
		public void onClick(View view) {
			showDialog();

		}
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("是否确定退出？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 实现退出
				quit();
			}

		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void quit() {
		// 将数据清零
		SharedPreferences sharedPreferences = getSharedPreferences("myPref",
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("username", "0");
		editor.putString("password", "0");
		editor.commit();// 提交修改;

		this.finish();// 结束当前界面
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(startMain);
		System.exit(0);

	}

	/**
	 * 调用系统的返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent phoneIntent = new Intent(SettingActivity.this,
					CarRoute.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
