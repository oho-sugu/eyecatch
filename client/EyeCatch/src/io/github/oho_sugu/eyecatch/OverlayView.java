package io.github.oho_sugu.eyecatch;

import io.github.oho_sugu.eyecatch.util.server.ListResult;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class OverlayView extends View {
	private ListResult listResult = null;
	
	public OverlayView(Context context) {
		super(context);
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		
	}
	
	public void setKeywords(ListResult listResult) {
		this.listResult = listResult;
	}
}
