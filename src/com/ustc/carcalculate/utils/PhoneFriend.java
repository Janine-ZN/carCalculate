package com.ustc.carcalculate.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ustc.carcalculate.dto.PhoneInfo;

/**
 * 将好友的姓名和电话号码，发送到服务器，进行存储
 * 
 * @author zxl
 * 
 */
public class PhoneFriend extends Thread {
	List<PhoneInfo> list;
	String deviceid;

	public PhoneFriend(List<PhoneInfo> list, String deviceid) {
		this.list = list;
		this.deviceid = deviceid;
	}

	private void doPost() {
		HttpClient httpClient = new DefaultHttpClient();
		String url = "http://114.214.170.137:8383/CarWebServer/FrientServlet";
		HttpPost httpPost = new HttpPost(url);
		// 将数据打包成JSON格式
		JSONObject jsonObject = new JSONObject();
		JSONArray array = new JSONArray();

		// 假如不为空，即为真
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				// 将查询出来的结果,打包成Json格式传到客户端
				PhoneInfo phone = list.get(i);
				System.out.println("list.get(i)" + list.get(i));
				JSONObject jObject = new JSONObject();
				try {
					jObject.put("name", phone.getName());
					jObject.put("number", phone.getNumber());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				array.put(jObject.toString());// 加载JObject对象
			}
		}

		try {
			jsonObject.put("content", array);// 经度
			jsonObject.put("deviceid", deviceid);// 纬度

			// 打包成功后,发送数据
			NameValuePair nvp = new BasicNameValuePair("friendRequest",
					jsonObject.toString());

			List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
			nvpList.add(nvp);
			// 得到JSON数据,并设置编码格式
			HttpEntity entity = new UrlEncodedFormEntity(nvpList, "UTF-8");
			// 将打包好的JSON数据,放到entity对象里,而entity对象里的数据是以List的形式存在的
			httpPost.setEntity(entity);
			// 执行请求
			httpClient.execute(httpPost);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@Override
	public void run() {
		doPost();
		// doGet();

	}
}
