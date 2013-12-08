package io.github.oho_sugu.eyecatch.textrecognition.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Rect;
import android.graphics.YuvImage;

public class JpegConverter {

	public static byte[] convertToJpeg(int format, byte[] yuvImage) {
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
