package io.github.oho_sugu.eyecatch.textrecognition.result.common;

import net.arnx.jsonic.JSONHint;

public class Message {

	@JSONHint(name = "@text")
	public String text;
};