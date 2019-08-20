package com.ustc.carcalculate.utils;

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
import org.json.JSONObject;

public class CarLocation extends Thread {
	double longitude;// 经度
	double latitude;// 纬度
	float precision;
	String address;
	String method;
	String deviceid;

	/**
	 * 传输数据 经度、纬度
	 */

	public CarLocation(double latitude, double longitude, float precision,
			String address, String method, String deviceid) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.precision = precision;
		this.address = address;
		this.method = method;
		this.deviceid = deviceid;
	}

	private void doPost() {
		HttpClient httpClient = new DefaultHttpClient();
		String url = "http://114.214.170.137:8383/CarWebServer/CarServlet";
		HttpPost httpPost = new HttpPost(url);
		// 将数据打包成JSON格式
		JSONObject jsonObject = new JSONObject();
		String jsonString = "";
		System.out.println("4/28.address-->" + address);
		try {
			jsonObject.put("longitude", longitude);// 经度
			jsonObject.put("latitude", latitude);// 纬度
			jsonObject.put("precision", precision);
			jsonObject.put("address", address);
			jsonObject.put("method", method);
			jsonObject.put("deviceid", deviceid);

			// 打包成功后,发送数据
			NameValuePair nvp = new BasicNameValuePair("carRequest",
					jsonObject.toString());

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

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		doPost();
		// doGet();

	}
}