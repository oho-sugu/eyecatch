package io.github.oho_sugu.eyecatch.util.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public class ListAsyncTask extends AsyncTask<ServerParameter, Integer, String> {
	private static final String DOMAIN = "http://eyecatcher12345.appspot.com/";
	@Override
	protected String doInBackground(ServerParameter... arg0) {
		if (arg0.length != 1) return "NG";
		
		try {
			AndroidHttpClient client = AndroidHttpClient.newInstance("Vuzix Android UserAgent");
			HttpResponse res = client.execute(
					new HttpGet(DOMAIN+"list?lat="+arg0[0].lat+"&lon="+arg0[0].lon)
					);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "UTF-8"));
			if(reader.readLine().equals("OK")){
				return "OK";
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "NG";
	}
}
