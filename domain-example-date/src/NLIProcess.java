import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class NLIProcess {
	private static final String url = "https://cn.olami.ai/cloudservice/api";
	private static final String Appkey = "01cb5938a0d44417808195695da4d297";
	private static final String Appsecret = "16050d2f362a423793d8a2686e3f126d";
	private static final String api = "nli";
	
	public static JSONObject process (String input) {
		JSONObject NLIresult = new JSONObject();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appkey", Appkey));
		params.add(new BasicNameValuePair("api", api));
		
		long timestamp = Calendar.getInstance().getTimeInMillis();
		params.add(new BasicNameValuePair("timestamp", String.valueOf(timestamp)));
		params.add(new BasicNameValuePair("sign", generateSign(timestamp)));
		
		JSONObject request = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("input_type", 0);
			data.put("text", input);
			
			request.put("data_type", "stt");
			request.put("data", data);
		} catch (JSONException e1) {
			e1.printStackTrace();
			return NLIresult;
		}
		params.add(new BasicNameValuePair("rq", request.toString()));
		params.add(new BasicNameValuePair("cusid", "asdfghj"));

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String contnt = EntityUtils.toString(entity);
					System.out.println("Response content: " + contnt);
					NLIresult = new JSONObject(contnt);
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return NLIresult;
		} finally {
			try {
				httpclient.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
		return NLIresult;
	}

	private static String generateSign(long timestamp) {
		String sign = Appsecret + "api=" + api + "appkey=" + Appkey + "timestamp=" + timestamp + Appsecret;
		return MD5.MD5String(sign);
	}
}
