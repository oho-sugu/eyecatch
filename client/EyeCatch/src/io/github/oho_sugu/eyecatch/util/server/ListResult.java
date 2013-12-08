package io.github.oho_sugu.eyecatch.util.server;

import java.util.List;

import net.arnx.jsonic.JSONHint;

public class ListResult {
	public class Keyword {
		@JSONHint (name = "keyword")
		public String keyword;
		@JSONHint (name = "count")
		public int count;
		@JSONHint (name = "lat")
		public long lat;
		@JSONHint (name = "lon")
		public long lon;
	}
	
	public List<Keyword> keywords;
}
