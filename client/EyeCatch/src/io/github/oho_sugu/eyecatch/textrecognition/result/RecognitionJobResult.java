package io.github.oho_sugu.eyecatch.textrecognition.result;

import io.github.oho_sugu.eyecatch.textrecognition.result.common.Job;
import io.github.oho_sugu.eyecatch.textrecognition.result.common.Message;

import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSONHint;

public class RecognitionJobResult {

	public class Point {

		@JSONHint(name = "@x")
		public int x;
		@JSONHint(name = "@y")
		public int y;
	}

	public class Shape {

		@JSONHint(name = "@count")
		public int count;
		@JSONHint(name = "@count")
		List<Point> point;
	}

	public class Word {
		@JSONHint(name = "@text")
		public String text;
		@JSONHint(name = "@score")
		public float score;
		@JSONHint(name = "@category")
		public String category;
		@JSONHint(name = "shape")
		public Shape shape;

	}

	public class Words {
		@JSONHint(name = "@count")
		public int count;

		public List<Word> word;

	}

	public Job job;
	public Words words;
	public Message message;

}
