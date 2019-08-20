package com.ustc.carcalculate.residemenu;

import java.io.File;

import com.ustc.carcalculate.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 头部数据
 * 
 * @author znzn
 * 
 */
public class ResideMenuInfo extends LinearLayout {

	/** menu item icon */
	private ImageView iv_icon;
	/** menu item title */
	private TextView tv_username;
	private TextView tv_userid;

	public ResideMenuInfo(Context context) {
		super(context);
		initViews(context);
	}

	/****************** 加载用户信息 ***************/
	public ResideMenuInfo(Context context, int icon, String title, String id) {
		super(context);
		initViews(context);
		iv_icon.setImageResource(icon);
		tv_username.setText(title);
		tv_userid.setText(id);
	}

	public ResideMenuInfo(Context context, String headPath, String title,
			String id) {
		super(context);
		initViews(context);
		File file = new File(headPath);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(headPath);
			iv_icon.setImageBitmap(bitmap);
		}
		tv_username.setText(title);
		tv_userid.setText(id);
	}

	/****************** 设置用户信息 ***************/
	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu_info, this);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		tv_username = (TextView) findViewById(R.id.tv_username);// 用户昵称
		tv_userid = (TextView) findViewById(R.id.tv_userid);// 用户ID
	}

	/**
	 * set the icon color;
	 * 
	 * @param icon
	 */
	public void setIcon(int icon) {
		iv_icon.setImageResource(icon);
	}

	/**
	 * set the title with string;
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_username.setText(title);
	}

	/**
	 * set the title with string;
	 * 
	 * @param dengji
	 */
	public void setDengJi(String userid) {
		tv_userid.setText(userid);
	}
}