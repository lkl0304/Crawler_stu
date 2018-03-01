package com.mzitu.parse;

import java.io.BufferedInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mzitu.utils.Const;
import com.mzitu.utils.FileUtil;

public class SaveMzImg implements Runnable {
	private String title = null;
	private List<String> pages = new CopyOnWriteArrayList<>();
	
	public SaveMzImg(Map<String, String> MzInfo) {
		this.title = MzInfo.get("title");
		pages.add(MzInfo.get("url"));
		this.getDocAndPages();
	}
	
	private Document getDocAndPages(String... urls) {
		String url = this.pages.get(0);
		if (urls.length > 0) {
			url = urls[0];
		}
		try {
			Document document = Jsoup.connect(url)
								.headers(Const.headers)
								.userAgent(Const.userAgent)
								.get();
			// 不存在页面信息则获取
			if (this.pages.size() <= 1) {
				String num = document.select("div.pagenavi > a")
								.last().previousElementSibling().text();
				int pageNum = Integer.parseInt(num);
				for (int i = 2; i <= pageNum; i++) {
					pages.add(pages.get(0) + "/" + i);
				}
			}
			return document;
		} catch (Exception e) {
			System.err.println("ERROR: 打开妹子图片页面连接失败--> " + url);
//			e.printStackTrace();
		}
		return null;
	}
	
	private void getImgToDown(Document document) {
		if (document == null) return;
		String src = null;
		try {
			src = document.select("div.main-image img").attr("src");
			BufferedInputStream input = Jsoup.connect(src)
										.headers(Const.headers).ignoreContentType(true)
										.execute().bodyStream();
			String filename = src.substring(src.lastIndexOf("/"));
			FileUtil.save(this.title + filename, input);
		} catch (Exception e) {
			System.err.println("ERROR: 图片保存失败 --> " + src);
//			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		pages.forEach((p_url)->{
			System.out.println("     正在下载-> {title: " + this.title + "; url: " + p_url + "}");
			this.getImgToDown(this.getDocAndPages(p_url));
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {}
		});
	}
}
