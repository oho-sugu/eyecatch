package io.github.oho_sugu.eyecatch.textrecognition;

import io.github.oho_sugu.eyecatch.EyeCatchActivity;
import io.github.oho_sugu.eyecatch.textrecognition.core.DcmApiWrapper;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionJobResult;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionJobResult.Word;
import io.github.oho_sugu.eyecatch.textrecognition.result.RecognitionRequestQueue;
import io.github.oho_sugu.eyecatch.textrecognition.util.JpegConverter;
import io.github.oho_sugu.eyecatch.textrecognition.util.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
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

	public RecognitionJobResult request(Camera camera, byte[] imageData) {
		RecognitionJobResult ret = null;

		try {
			mRequestSemaphore.acquire();
			Log.d(TAG, "Requesting.");
			byte[] jpegImage = JpegConverter.convertToJpeg(camera, imageData);
			
			String path=Environment.getDataDirectory()+"/data/io.github.oho_sugu.eyecatch/test.jpg";
			FileOutputStream fos;
			try {
				 fos = new FileOutputStream(path);
				fos.write(jpegImage);
				fos.close();

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
									ret =result;
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
