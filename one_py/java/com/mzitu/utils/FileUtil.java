package com.mzitu.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
	public static void save(String path, InputStream in) {
		String realPath = FileUtil.getRealPath(path);
		if (FileUtil.mkdirs(realPath)) {
			File file = new File(realPath);
			try(OutputStream out = new FileOutputStream(file)) {
				byte[] bs = new byte[8192];
				int len = 0;
				while ((len = in.read(bs)) != -1) {
					out.write(bs, 0, len);
				}
			} catch (FileNotFoundException e) {
				System.err.println("文件不存在！");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("文件打开失败！");
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					System.err.println("网络输入流关闭失败！");
					e.printStackTrace();
				}
			}
		}
	}
	
	private static boolean mkdirs(String path) {
		File file = new File(path);
		File parentFile = file.getParentFile();
		try {
			parentFile.mkdirs();
			file.createNewFile();
			return true;
		} catch (Exception e) {
			System.err.println("--> 创建文件异常：" + file.getAbsolutePath());
			return false;
		}
	}
	
	private static String getRealPath(String path) {
		String folder = ClassLoader.getSystemResource(".").getFile();
		return folder + "download/" + path;
	}
}
