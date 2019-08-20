package com.ustc.carcalculate.residemenu;

import com.ustc.carcalculate.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResideMenuItem extends RelativeLayout {

	/** menu item title */
	private TextView tv_title;
	private ImageView imageView;
	private ImageView returnIv;
	// private TextView tv_title_guanzhang;

	private RelativeLayout linner;

	public ResideMenuItem(Context context) {
		super(context);
		initViews(context);
	}

	// ///*******重点*********/////
	public ResideMenuItem(Context context, String title) {
		super(context);
		initViews(context);
		linner.setVisibility(View.VISIBLE);
		tv_title.setText(title);
	}

	public ResideMenuItem(Context context, int icon, String title) {
		super(context);
		initViews(context);
		linner.setVisibility(View.VISIBLE);
		imageView.setImageResource(icon);
		tv_title.setText(title);
	}

	private void initViews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.residemenu_item, this);
		// returnIv = (ImageView) findViewById(R.id.iv_next);
		imageView = (ImageView) findViewById(R.id.image_ceh);
		tv_title = (TextView) findViewById(R.id.tv_title);
		linner = (RelativeLayout) findViewById(R.id.relative_layout);
	}

	/**
	 * set the icon color;
	 * 
	 * @param icon
	 */
	public void setIcon(int icon) {
		imageView.setImageResource(icon);
	}

	/**
	 * set the title with resource ;
	 * 
	 * @param title
	 */
	public void setTitle(int title) {
		tv_title.setText(title);
	}

	/**
	 * set the title with string;
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
}
