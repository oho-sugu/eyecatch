package io.github.oho_sugu.eyecatch.util.server;

import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSONHint;

public class ListResult {
	public class Keyword {
		public Keyword(){
		}
		@JSONHint (name = "keyword")
		public String keyword;
		@JSONHint (name = "count")
		public int count;
		@JSONHint (name = "lat")
		public long lat;
		@JSONHint (name = "lon")
		public long lon;
	}
	
	public List<Keyword> keywords = new ArrayList<Keyword>();
	
	public void addKeyword(String keyword,int count,long lat,long lon){
		Keyword key = new Keyword();
		key.keyword = keyword;
		key.count = count;
		key.lat = lat;
		key.lon = lon;
		this.keywords.add(key);
	}
}
