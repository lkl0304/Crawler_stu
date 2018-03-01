package com.mzitu;

import com.mzitu.parse.HomePage;

public class Main {
	public static void main(String[] args) {
		String url = "http://www.mzitu.com/";
		new HomePage(url).start();
	}
}
