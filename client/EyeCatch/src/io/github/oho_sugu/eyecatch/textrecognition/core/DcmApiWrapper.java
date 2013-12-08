package io.github.oho_sugu.eyecatch.textrecognition.core;

import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionJobResult;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionRequestQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import net.arnx.jsonic.JSON;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.utils.URIBuilder;
import ch.boye.httpclientandroidlib.client.utils.URIUtils;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

public class DcmApiWrapper {
	private static URI mCommonUrl;
	private static String mCommonScheme = "https";
	private static String mCommonHost = "api.apigw.smt.docomo.ne.jp";
	private static int mCommonPort = 443;

	private static String mRecognitionRequest = "/characterRecognition/v1/scene";

	private DcmApiWrapper(String apiKey) throws URISyntaxException {
		URIBuilder builder = new URIBuilder();
		builder.setScheme(mCommonScheme);
		builder.setHost(mCommonHost);
		builder.setPort(mCommonPort);

		builder.addParameter("APIKEY", apiKey);

		mCommonUrl = builder.build();
	}

	public static DcmApiWrapper factory(String apiKey) throws URISyntaxException {
		return new DcmApiWrapper(apiKey);
	}

	public RecognitionRequestQueue requestQueue(byte[] jpegImage)
			throws UnsupportedEncodingException, IllegalStateException,
			IOException {
		URI requestQueueUrl;
		RecognitionRequestQueue requestQueue = null;
		DefaultHttpClient client;
		try {
			client = new DefaultHttpClient();

			requestQueueUrl = URIUtils.rewriteURI(mCommonUrl);
			requestQueueUrl.resolve(mRecognitionRequest);

			HttpPost httpPost = new HttpPost(requestQueueUrl);
			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			ByteArrayBody imageBody = new ByteArrayBody(jpegImage,
					"image/jpeg", "recognition.jpg");
			multipartEntity.addPart("image", imageBody);
			httpPost.setEntity(multipartEntity);

			HttpResponse res;
			res = client.execute(httpPost);
			// HttpResponseのEntityデータをStringへ変換
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					res.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			String json_str = builder.toString();

			requestQueue = JSON.decode(json_str, RecognitionRequestQueue.class);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestQueue;
	}

	public RecognitionJobResult requestRecognitionResult(
			RecognitionRequestQueue queue) throws UnsupportedEncodingException, IllegalStateException, IOException {
		URI requestUrl;
		RecognitionJobResult jobResult = null;
		DefaultHttpClient client;
		try {
			client = new DefaultHttpClient();
			requestUrl = URIUtils.rewriteURI(mCommonUrl);
			requestUrl.resolve("/characterRecognition/v1/scene/");
			requestUrl.resolve(queue.job.id);


			HttpGet httpGet = new HttpGet(requestUrl);

			HttpResponse res;
			res = client.execute(httpGet);
			// HttpResponseのEntityデータをStringへ変換
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					res.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			String json_str = builder.toString();

			jobResult = JSON.decode(json_str, RecognitionJobResult.class);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jobResult;
	}

}
