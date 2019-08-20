package com.ustc.carcalculate.person;

import java.io.File;
import java.io.IOException;
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
import com.ustc.carcalculate.activity.CarRoute;
import com.ustc.carcalculate.person.other.TelephoneActivity;
import com.ustc.carcalculate.utils.ImageUtils;
import com.ustc.carcalculate.utils.SDCardUtils;
import com.ustc.carcalculate.utils.T;
import com.ustc.carcalculate.view.CircleImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 1.设置头像的功能，等基本功能实现后再研究; 2.设置昵称、手机号、行业、职业、密码、密保等，需要使用共享参数
 * 
 * @author Janine.Z
 * 
 */
public class AboutActivity extends Activity implements OnClickListener {
	// 昵称、手机号、行业、职业、密码、密保
	private RelativeLayout namelayout, telephonelayout, industrylayout,
			occupationlayout, passwordlayout, questionlayout;

	private ImageView ivBack;// 返回
	private CircleImageView faceImage;// 头像
	// private ImageView faceImage;
	private TextView nameTextView;// 昵称
	private TextView telephoneTextView;// 手机
	private TextView industryTextView;// 行业
	private TextView occupationTextView;// 职业

	// 以下变量需要从共享参数中得到
	String telephone;
	String phone;
	String A;
	String B;
	String name;

	String userid;
	String headPath;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示系统标题
		setContentView(R.layout.activity_about);
		initView();
		initEvent();
	}

	/**
	 * 获取各个控件
	 */
	private void initView() {

		ivBack = (ImageView) findViewById(R.id.iv_about_head_back);
		// 需要获取TextView
		faceImage = (CircleImageView) findViewById(R.id.userIcon);

		nameTextView = (TextView) findViewById(R.id.tv_about_name);
		telephoneTextView = (TextView) findViewById(R.id.tv_about_telephone);
		industryTextView = (TextView) findViewById(R.id.tv_about_industry);
		occupationTextView = (TextView) findViewById(R.id.tv_about_occupation);

		// 获取Relative布局
		namelayout = (RelativeLayout) findViewById(R.id.relative_about_name);
		telephonelayout = (RelativeLayout) findViewById(R.id.relative_about_telephone);
		industrylayout = (RelativeLayout) findViewById(R.id.relative_about_industry);
		occupationlayout = (RelativeLayout) findViewById(R.id.relative_about_occupation);
		passwordlayout = (RelativeLayout) findViewById(R.id.relative_safety_password);
		questionlayout = (RelativeLayout) findViewById(R.id.relative_safety_question);

		getUserInfo();
	}

	/**
	 * 设置点击事件监听
	 */
	private void initEvent() {

		ivBack.setOnClickListener(this);
		faceImage.setOnClickListener(this);
		namelayout.setOnClickListener(this);
		telephonelayout.setOnClickListener(this);
		industrylayout.setOnClickListener(this);
		occupationlayout.setOnClickListener(this);
		passwordlayout.setOnClickListener(this);
		questionlayout.setOnClickListener(this);

	}

	/**
	 * 从共享参数中得到手机号
	 */
	public void getUserInfo() {
		SharedPreferences preferences = getSharedPreferences("myPref",
				Activity.MODE_PRIVATE);
		name = preferences.getString("username", "0");// 读取到昵称
		telephone = preferences.getString("phonenum", "0");// 读取到的手机号
		userid = preferences.getString("userId", "1445456");
		headPath = preferences.getString("headimg", "0");
		A = telephone.substring(0, 3);// 前三位数
		B = telephone.substring(telephone.length() - 4, telephone.length());// 后四位数
		phone = A + "****" + B;
		telephoneTextView.setText(phone);
		nameTextView.setText(name);
		System.out.println("headPath--->" + headPath);
		if (!headPath.equals(0)) {
			File file = new File(headPath);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(headPath);
				faceImage.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (SDCardUtils.isSDCardEnable()) {
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	// 数据传输
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (SDCardUtils.isSDCardEnable()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					T.showShort(AboutActivity.this, "未找到存储卡，无法存储照片！");
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {

			Bitmap photo = extras.getParcelable("data");
			faceImage.setImageBitmap(photo);
			updateHeadphoto(photo);
			// ByteArrayOutputStream stream = new ByteArrayOutputStream();//
			// 转换成Byte数组类型
			// photo.compress(Bitmap.CompressFormat.PNG, 60, stream);
			// byte[] bytes = stream.toByteArray();
			// System.out.println("AboutActivity.bytes--->" + bytes + "," +
			// "楠");
			// // Base64编码并发送给服务器
			// String img = new String(
			// Base64.encodeToString(bytes, Base64.DEFAULT));
			//
			// new imageMyAsyncTask().execute(userid, img);

		}
	}

	private void updateHeadphoto(Bitmap photo) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCardDir = Environment.getExternalStorageDirectory();

			String path = null;
			try {
				path = sdCardDir.getCanonicalPath() + ImageUtils.HEAD_PHOTO_DIR; // 获取头像存储目录
			} catch (IOException e) {
				e.printStackTrace();
			}

			ImageUtils.writeImage(path, photo); // 将头像图片写入SD卡。
			// 将图片的路径存在共享参数中
			SharedPreferences userInfo = getSharedPreferences("myPref",
					MODE_PRIVATE);
			Editor editor = userInfo.edit();
			editor.putString("headimg", path + "/" + ImageUtils.HEAD_PHOTO_NAME);
			editor.commit();
			new imageMyAsyncTask().execute(userid, headPath);

		} else {
			T.showShort(this, "未检测到SD卡！");
		}

	}

	/**
	 * 头像上传的异步通信任务
	 */
	public class imageMyAsyncTask extends AsyncTask<String, Void, String> {
		/**
		 * 子线程
		 */
		@Override
		protected String doInBackground(String... request) {
			String id = request[0];
			String image = request[1];

			HttpClient httpClient = new DefaultHttpClient();
			String url = "http://114.214.170.137:8383/CarWebServer/ImageServlet";
			HttpPost httpPost = new HttpPost(url);
			String jsonString = "";

			JSONObject jsonObject = new JSONObject();// 将数据打包成JSON格式

			try {
				// 将获取到的数据按JSON格式传到数据库
				jsonObject.put("id", id);
				jsonObject.put("image", image);

				NameValuePair nvp = new BasicNameValuePair("imageRequest",
						jsonObject.toString());// 打包成功后,发送数据

				Log.i("LOG", "AboutActivity.jsonObject.toString()--->"
						+ jsonObject.toString());
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
			Log.i("LOG", "image**" + jsonString);
			return jsonString;

		}

		/**
		 * 主线程
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			int code = -1;// 代表返回的result值

			// 接受子线程中的返回值 并判断是否需要跳转的下一页的
			try {

				// JSON 数据解析 JSONObject 把任意的对象变成JSON对象
				JSONObject jsonParser = new JSONObject(result.toString());
				code = jsonParser.getInt("result");
			} catch (JSONException ex) {
				// 异常处理代码
				ex.printStackTrace();
			}

			if (code == 0) {
				Intent intent = new Intent(AboutActivity.this, CarRoute.class);
				startActivity(intent);
				finish();
				Toast.makeText(AboutActivity.this, "修改成功!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(AboutActivity.this, "修改失败!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	/**
	 * 实现点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userIcon:
			// 点击头像
			showDialog();
			break;
		case R.id.relative_about_name:
			// 点击进入昵称修改
			Intent nameIntent = new Intent(AboutActivity.this,
					ModifyNameActivity.class);
			startActivity(nameIntent);
			AboutActivity.this.finish();
			break;
		case R.id.relative_about_telephone:
			// 点击进入修改手机号提示界面
			Intent telephoneIntent = new Intent(AboutActivity.this,
					TelephoneActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("phone", phone);
			telephoneIntent.putExtras(bundle);
			startActivity(telephoneIntent);
			AboutActivity.this.finish();
			break;
		case R.id.relative_about_industry:

			break;
		case R.id.relative_about_occupation:

			break;
		case R.id.relative_safety_password:
			// 点击进入密码修改
			Intent passwordIntent = new Intent(AboutActivity.this,
					ModifyPasswordActivity.class);
			startActivity(passwordIntent);
			AboutActivity.this.finish();
			break;
		case R.id.relative_safety_question:
			// 点击进入密保问题提示界面
			Intent questionIntent = new Intent(AboutActivity.this,
					ModifyQuestionActivity.class);
			startActivity(questionIntent);
			AboutActivity.this.finish();
			break;
		case R.id.iv_about_head_back:
			// 返回主界面
			Intent backIntent = new Intent(AboutActivity.this, CarRoute.class);
			startActivity(backIntent);
			AboutActivity.this.finish();

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
			Intent phoneIntent = new Intent(AboutActivity.this, CarRoute.class);
			startActivity(phoneIntent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
