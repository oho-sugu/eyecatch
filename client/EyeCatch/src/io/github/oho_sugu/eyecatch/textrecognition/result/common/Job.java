package io.github.oho_sugu.eyecatch.textrecognition.result.common;

import net.arnx.jsonic.JSONHint;

public class Job {

	@JSONHint(name = "@id")
	public String id;

	@JSONHint(name = "@status")
	public String status;
	@JSONHint(name = "@queue-time")
	public String queue_time;
};