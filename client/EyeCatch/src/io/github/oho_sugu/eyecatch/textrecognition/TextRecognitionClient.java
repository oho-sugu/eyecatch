package io.github.oho_sugu.eyecatch.textrecognition;

import io.github.oho_sugu.eyecatch.EyeCatchActivity;
import io.github.oho_sugu.eyecatch.textrecognition.result.RequestQueue;
import io.github.oho_sugu.eyecatch.textrecognition.util.JpegConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

import jp.ne.nttdocomo.spm.api.common.http.AuthApiKey;
import jp.ne.nttdocomo.spm.api.recognition.CharacterRecognize;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeResultData;
import net.arnx.jsonic.JSON;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class TextRecognitionClient {
	private boolean mIsRequesting = false;
	private static Semaphore mRequestSemaphore;
	CharacterRecognize mRecognize;
	private String TAG = EyeCatchActivity.TAG;

	public TextRecognitionClient() {
		// TODO Auto-generated constructor stub
		AuthApiKey.initializeAuth("ApiKey");
		mRecognize = new CharacterRecognize();

		mRequestSemaphore = new Semaphore(1);

	}

	synchronized public static boolean canRequest() {
		boolean isRequesting = false;
		int availableCount = 0;
		availableCount = mRequestSemaphore.availablePermits();
		// Log.d(TAG, "canRequest");
		if (availableCount == 0) {
			return false;
		} else {
			return true;
		}

	}

	public RecognizeResultData request(int format, byte[] imageData) {

		try {
			mRequestSemaphore.acquire();
			RecognizeResultData ret = null;
			Log.d(TAG, "Requesting.");
			byte[] jpegImage = JpegConverter.convertToJpeg(format, imageData);

			AndroidHttpClient client = null;
			try {
				client = AndroidHttpClient.newInstance("Android UserAgent");
				HttpPost request = new HttpPost(
						"https://api.apigw.smt.docomo.ne.jp/characterRecognition/v1/scene?APIKEY=74545a3739775362454c313463572f36783932334571562f6338753432775633394b6a374b4a754f7a3541");
				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				ByteArrayBody imageBody = new ByteArrayBody(jpegImage,
						"image/jpeg", "recognition.jpg");
				multipartEntity.addPart("image", imageBody);
				request.setEntity(multipartEntity);
				HttpResponse res;
				res = client.execute(request);
				// HttpResponseのEntityデータをStringへ変換
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(res.getEntity().getContent(),
								"UTF-8"));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}
				String json_str = builder.toString();

				RequestQueue request_queue = JSON.decode(json_str,
						RequestQueue.class);
				Log.d(TAG, request_queue.toString());
				Log.d(TAG, request_queue.job.id);
				Log.d(TAG, request_queue.job.queue_time);
				Log.d(TAG, request_queue.job.status);
				Log.d(TAG, request_queue.message.text);
				while (true) {

					break;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(EyeCatchActivity.TAG, "RecognitionQueueError");
				Log.e(EyeCatchActivity.TAG, e.toString());
				e.printStackTrace();
			} finally {
				Log.e(EyeCatchActivity.TAG, "Finish Recognition");
				if (client != null) {
					client.close();
				}
			}

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			mRequestSemaphore.release();
		}

		return ret;
	}

}
