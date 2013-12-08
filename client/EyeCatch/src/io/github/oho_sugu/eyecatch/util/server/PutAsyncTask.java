package io.github.oho_sugu.eyecatch.util.server;

import io.github.oho_sugu.eyecatch.OverlayView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import net.arnx.jsonic.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public class PutAsyncTask extends
		AsyncTask<ServerParameter, Integer, ListResult> {
	private static final String DOMAIN = "http://eyecatcher12345.appspot.com/";
	private OverlayView oView;
	public void setOverlayView(OverlayView view){
		this.oView = view;
	}
	
	@Override
	protected ListResult doInBackground(ServerParameter... arg0) {
		if (arg0.length != 1)
			return null;

		ListResult listResult = null;
		try {
			AndroidHttpClient client = AndroidHttpClient
					.newInstance("Vuzix Android UserAgent");
			HttpResponse res = client.execute(new HttpGet(DOMAIN + "put?key="
					+ arg0[0].keyword + "&lat=" + arg0[0].lat + "&lon="
					+ arg0[0].lon));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					res.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			String json_str = builder.toString();

			listResult = JSON.decode(json_str, ListResult.class);

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
		return listResult;
	}

	@Override
	protected void onPostExecute(ListResult result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if(oView != null){
			oView.updateKeywords(result);
		}
	}
}
