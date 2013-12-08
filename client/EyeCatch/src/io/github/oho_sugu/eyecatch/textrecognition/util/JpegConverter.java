package io.github.oho_sugu.eyecatch.textrecognition.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

public class JpegConverter {

	public static byte[] convertToJpeg(Camera camera, byte[] yuvImage) {
		byte[] jpegImage = null;
	    Camera.Parameters params = camera.getParameters();
        Camera.Size size = params.getPreviewSize();
		YuvImage image = new YuvImage(yuvImage,params.getPreviewFormat(), size.width,size.height, null);

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
