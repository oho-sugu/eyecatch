package io.github.oho_sugu.eyecatch.textrecognition.result;

import net.arnx.jsonic.JSONHint;
import io.github.oho_sugu.eyecatch.textrecognition.result.common.Job;
import io.github.oho_sugu.eyecatch.textrecognition.result.common.Message;

public class RecognitionRequestQueue {
	@JSONHint(name="job")
	public Job job;
	@JSONHint(name="message")
	public Message message;

}
