package io.github.oho_sugu.eyecatch.textrecognition;

import io.github.oho_sugu.eyecatch.EyeCatchActivity;
import io.github.oho_sugu.eyecatch.textrecognition.core.DcmApiWrapper;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionJobResult;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionJobResult.Word;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionRequestQueue;
import io.github.oho_sugu.eyecatch.textrecognition.result.common.Job;
import io.github.oho_sugu.eyecatch.textrecognition.util.JpegConverter;
import io.github.oho_sugu.eyecatch.textrecognition.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.Semaphore;

import net.arnx.jsonic.JSON;
import android.util.Log;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
//import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
//import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
//import ch.boye.httpclientandroidlib.entity.mime.content.ByteArrayBody;

public class TextRecognitionClient {
	private static Semaphore mRequestSemaphore;
	private String TAG = EyeCatchActivity.TAG;
	DcmApiWrapper mDcmApi;

	public TextRecognitionClient() {

		mRequestSemaphore = new Semaphore(1);
		try {
			mDcmApi = DcmApiWrapper
					.factory("74545a3739775362454c313463572f36783932334571562f6338753432775633394b6a374b4a754f7a3541");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	synchronized public static boolean canRequest() {
		int availableCount = 0;
		availableCount = mRequestSemaphore.availablePermits();
		// Log.d(TAG, "canRequest");
		if (availableCount == 0) {
			return false;
		} else {
			return true;
		}

	}

	public RecognitionJobResult request(int format, byte[] imageData) {
		RecognitionJobResult ret = null;

		try {
			mRequestSemaphore.acquire();
			Log.d(TAG, "Requesting.");
			byte[] jpegImage = JpegConverter.convertToJpeg(format, imageData);

			try {
				RecognitionRequestQueue queue = mDcmApi.requestQueue(jpegImage);

				if (queue != null && queue.job != null) {
					Log.d(TAG, queue.toString());
					Log.d(TAG, queue.job.id);
					Log.d(TAG, queue.job.queue_time);
					Log.d(TAG, queue.job.status);
					// Log.d(TAG, queue.message.text);
					while (true) {
						RecognitionJobResult result = mDcmApi
								.requestRecognitionResult(queue);
						if (result != null) {
							if (result.words != null && result.job != null) {
								Logger.d("" + result.job.status);
								for (Word word : result.words.word) {
									Logger.d(word.text);
									Logger.d("" + word.score);
									Logger.d("" + word.category);
								}
								if (result.job.status.equals( "success") || result.job.status.equals("deleted")) {
									Logger.d("Exit Recognition Waiting Loop");
									break;
								}
							}
						}
					}

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
			} finally {
				Logger.e("Finish Recognition");
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
