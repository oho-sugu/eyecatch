package io.github.oho_sugu.eyecatch.textrecognition;

import io.github.oho_sugu.eyecatch.EyeCatchActivity;

import java.util.concurrent.Semaphore;

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
import android.util.Log;

public class TextRecognitionClient {
	private boolean mIsRequesting = false;
	private Semaphore mRequestSemaphore;
	CharacterRecognize mRecognize;
	private String TAG = EyeCatchActivity.TAG;

	public TextRecognitionClient() {
		// TODO Auto-generated constructor stub
		AuthApiKey.initializeAuth("ApiKey");
		mRecognize = new CharacterRecognize();

		mRequestSemaphore = new Semaphore(1);

	}

	synchronized public boolean canRequest() {
		boolean isRequesting = false;
		int availableCount = 0;
		availableCount = mRequestSemaphore.availablePermits();
		Log.d(TAG, "canRequest");
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

			CharacterRecognizeRequestParam requestParam = new CharacterRecognizeRequestParam();
			requestParam.setLang(Lang.CHARACTERS_JP);
			// requestParam.setImageData(“image.jpgのフルパス”);
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

	private byte[] convertToJpeg(int format ,byte[] yuvImage){
		byte[] jpegImage =null;
		YuvImage image = new YuvImage(yuvImage, format, 428, 240, null);

		Rect rect = new Rect(0, 0, 428, 240);

		return jpegImage;
		
		
	}
}
