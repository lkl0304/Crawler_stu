package com.mzitu.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.mzitu.utils.Const;

public class HomePage {
	private List<String> pages;
	
	public List<String> getPages() {
		return pages;
	}
	
	public HomePage(String url) {
		pages = new LinkedList<>();
		pages.add(url);
		this.getPageDoc(url);  // 预先加载所有页面
	}
	
	private Document getPageDoc(String url) {
		try {
			Document document = Jsoup.connect(url)
								.headers(Const.headers).get();
			// 不存在页面信息则获取
			if (this.pages.size() <= 1) {
				String num = document.select("nav.navigation a.page-numbers")
								.last().previousElementSibling().text();
				int pageNum = Integer.parseInt(num);
				for (int i = 2; i <= pageNum; i++) {
					pages.add(pages.get(0) + "page/" + i + "/");
				}
			}
			return document;
		} catch (Exception e) {
			System.err.println("ERROR: 打开连接失败--> " + url);
//			e.printStackTrace();
		}
		return null;
	}
	
	private List<Map<String, String>> getPageMzList(Document doc) {
		List<Map<String, String>> list = new LinkedList<>();
		if (doc != null) {
			try {
				Elements lis = doc.select("ul#pins > li");
				lis.forEach((elem)->{
					String href = elem.getElementsByTag("a").get(0).attr("href");
					String title = elem.getElementsByTag("span").get(0)
								.text().replaceAll("(:|<|>|\\\\|\\?|\\*|,)", "_");
					Map<String, String> map = new HashMap<>();
					map.put("title", title);
					map.put("url", href);
					list.add(map);
				});
			} catch (Exception e) {
				System.err.println("ERROR: 解析页面出错！");
//				e.printStackTrace();
			}
		}
		return list;
	}

	public void start() {
		for(String page : pages) {
			System.out.println("妹子列表页面地址--> " + page);
			List<Map<String, String>> maps = this.getPageMzList(this.getPageDoc(page));
			List<Thread> ts = new ArrayList<>();
			maps.forEach((map) -> {
				Thread thread = new Thread(new SaveMzImg(map));
				ts.add(thread);
				thread.start();
			});
			// 保证每次只启用24个线程
			ts.forEach((thread)->{
				try {
					thread.join();
				} catch (InterruptedException e) {
					System.err.println("ERROR: 等待线程异常!");
					e.printStackTrace();
				}
			});
		}
	}
}
