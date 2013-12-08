package io.github.oho_sugu.eyecatch;

import io.github.oho_sugu.eyecatch.textrecognition.util.Logger;
import io.github.oho_sugu.eyecatch.util.server.ListResult;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class OverlayView extends View {
	private ListResult listResult = null;
	
	public OverlayView(Context context) {
		super(context);
		setFocusable(true);
		
		this.listResult = new ListResult();
		this.listResult.addKeyword("あいうえお", 10, 2412421, 141242141);
		this.listResult.addKeyword("かきくけこ", 10, 2412421, 141242141);
		this.listResult.addKeyword("さしすせそ", 10, 2412421, 141242141);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawColor(Color.TRANSPARENT);
		
		if(this.listResult != null) {
			Paint mainPaint = new Paint();
			mainPaint.setStyle(Paint.Style.STROKE);
			mainPaint.setARGB(255, 255, 255, 255);
			Paint backPaint = new Paint();
			backPaint.setStyle(Paint.Style.STROKE);
			backPaint.setARGB(255, 0, 0, 0);
			int i = 0;
			for(ListResult.Keyword keyword : listResult.keywords){
				drawTextWithBorder(canvas, keyword.keyword+" "+keyword.count, 250, 32*i*32, mainPaint, backPaint);
				i++;
			}
		}
		
	}

	private void drawTextWithBorder(Canvas canvas, String text, int x, int y, Paint fore, Paint back){
		canvas.drawText(text, x-1, y, back);
		canvas.drawText(text, x+1, y, back);
		canvas.drawText(text, x, y-1, back);
		canvas.drawText(text, x, y+1, back);
		canvas.drawText(text, x, y, fore);
	}
	
	public void updateKeywords(ListResult listResult) {
		Logger.d("updateKeywords on OverlayView");
		this.listResult = listResult;
		this.invalidate();
	}
}
