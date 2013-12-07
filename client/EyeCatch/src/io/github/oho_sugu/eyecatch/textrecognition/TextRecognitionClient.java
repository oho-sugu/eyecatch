package io.github.oho_sugu.eyecatch.textrecognition;

import io.github.oho_sugu.eyecatch.EyeCatchActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;

import jp.ne.nttdocomo.spm.api.common.exception.SdkException;
import jp.ne.nttdocomo.spm.api.common.exception.ServerException;
import jp.ne.nttdocomo.spm.api.common.http.AuthApiKey;
import jp.ne.nttdocomo.spm.api.recognition.CharacterRecognize;
import jp.ne.nttdocomo.spm.api.recognition.CharacterRecognizeResult;
import jp.ne.nttdocomo.spm.api.recognition.constants.ImageContentType;
import jp.ne.nttdocomo.spm.api.recognition.constants.Lang;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeResultData;
import jp.ne.nttdocomo.spm.api.recognition.data.RecognizeStatusData;
import jp.ne.nttdocomo.spm.api.recognition.param.CharacterRecognizeJobInfoRequestParam;
import jp.ne.nttdocomo.spm.api.recognition.param.CharacterRecognizeRequestParam;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.util.Log;

public class TextRecognitionClient {
	private  boolean mIsRequesting = false;
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
//		Log.d(TAG, "canRequest");
		if (availableCount == 0) {
			return false;
		} else {
			return true;
		}

	}

	public RecognizeResultData request(int format, byte[] imageData) {
		RecognizeResultData ret = null;
		Log.d(TAG, "request");

		try {
			mRequestSemaphore.acquire();

			Log.d(TAG, "Requesting.");
			byte[] jpegImage = convertToJpeg(format, imageData);

			try {
				AndroidHttpClient client = AndroidHttpClient
						.newInstance("Android UserAgent");
				HttpPost request = new HttpPost(
						"https://api.apigw.smt.docomo.ne.jp/characterRecognition/v1/scene?APIKEY=74545a3739775362454c313463572f36783932334571562f6338753432775633394b6a374b4a754f7a3541");
				MultipartEntity multipartEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);
				ByteArrayBody imageBody = new ByteArrayBody(jpegImage, "image/jpeg", "recognition.jpg");
				multipartEntity.addPart("image", imageBody);
				request.setEntity(multipartEntity);
				HttpResponse res;
				res = client.execute(request);
				// HttpResponseのEntityデータをStringへ変換
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(res.getEntity().getContent(),
								"UTF-8"));
				String jsonstr = reader.toString();
				Log.d(EyeCatchActivity.TAG, jsonstr);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e(EyeCatchActivity.TAG, e.toString());
			}

			CharacterRecognizeRequestParam requestParam = new CharacterRecognizeRequestParam();
			requestParam.setLang(Lang.CHARACTERS_JP);
			requestParam.setImageData(jpegImage);
			requestParam.setImageContentType(ImageContentType.IMAGE_JPEG);
			// 認識処理クラスにリクエストデータを渡し、レスポンスデータを取得する
			try {
				RecognizeStatusData response = mRecognize.request(requestParam);

				CharacterRecognizeResult recognizer = new CharacterRecognizeResult();
				// 認識結果取得リクエストデータクラスを作成してジョブ ID をセットする
				CharacterRecognizeJobInfoRequestParam resultRequestParam = new CharacterRecognizeJobInfoRequestParam();

				resultRequestParam.setJobId(response.getJob().getId());
				// 認識処理クラス 状態取得メソッドにリクエストデータを渡し、レスポンスデータを取得する
				ret = recognizer.request(resultRequestParam);
			} catch (SdkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			mRequestSemaphore.release();
		}

		return ret;
	}

	private byte[] convertToJpeg(int format, byte[] yuvImage) {
		byte[] jpegImage = null;
		YuvImage image = new YuvImage(yuvImage, format, 428, 240, null);

		Rect rect = new Rect(0, 0, 428, 240);
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			image.compressToJpeg(rect, 100, output);
			jpegImage = output.toByteArray();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jpegImage;

	}
}
