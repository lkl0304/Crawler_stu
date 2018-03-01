# -*- coding: utf-8 -*-
# @Time    : 2018/2/28 12:50
# @Author  : Soft
# @File    : mzitu_.py
# @Desc    : 妹子图图片爬取
import requests
import logging

import time
from pyquery import PyQuery as pq
import os
import re
import threading

logging.basicConfig(level=logging.INFO)
log = logging.getLogger(__name__)


# 下载一个妹子的图片
class load_img(threading.Thread):
    def __init__(self, pg_info):
        super().__init__()
        self.headers = dict(Referer='http://www.mzitu.com')
        self.folder = 'G:/PyCharmPro/Crawler_stu/Project_/download04'
        self.p_info = pg_info
        self._url = None
        self.title = None
        self.pages = None
        self.timeout = 12

    def get_html(self, _url):
        try:
            res = requests.get(_url, headers=self.headers, timeout=self.timeout)
            if res.ok:
                return res.text
        except BaseException as e:
            log.error("请求失败--> %s" % e)
            return False

    def get_all_page(self, html):
        try:
            doc = pq(html)
            num = int(pq(doc.find('div.pagenavi > a').not_(':last-child')[-1]).text())
            self.pages = [self._url + "/" + str(x) for x in range(2, num)]
        except BaseException as e:
            log.error("Load_img 获取所有页码失败--> %s" % e)

    def get_img_url_to_save(self, html):
        try:
            doc = pq(html)
            img_url = doc.find('div.main-image img').attr('src')
            filename = img_url[img_url.rfind('/') + 1:]
            load_path = os.path.join(self.title, filename)
            log.debug("将图片 %s 保存到 %s 中..." % (filename, load_path))
            # threading.Thread(target=self.save_img, args=(img_url, load_path)).start()
            self.save_img(img_url, load_path)
        except BaseException as e:
            log.error("获取本页图片失败--> %s" % e)

    def create_path(self, path):
        if path:
            try:
                folder, filename = os.path.split(os.path.join(self.folder, path))
                if not os.path.exists(folder):
                    os.makedirs(folder)
            except BaseException as e:
                log.error("创建文件夹失败--> %s" % e)
        else:
            raise FileExistsError("文件夹不存在")

    def save_img(self, img_url, local_path):
        try:
            res = requests.get(img_url, headers=self.headers)
            if res.ok:
                self.create_path(local_path)
                with open(os.path.join(self.folder, local_path), 'wb') as fd:
                    fd.write(res.content)
        except BaseException as e:
            log.error("保存图片失败--> %s" % e)

    def run(self):
        self.title = self.p_info.get('title')
        self._url = self.p_info.get('url')
        rgx = re.compile(r'(:|>|<|\?|\\|\*)')
        if rgx.search(self.title):
            self.title = rgx.sub('_', self.title)
        log.info("  标题：" + self.title + " 地址: " + self._url)
        try:
            html = self.get_html(self._url)
            self.get_all_page(html)
            self.get_img_url_to_save(html)
            for p_url in self.pages:
                log.info("  标题：" + self.title + " --> 地址: " + p_url)
                _html = self.get_html(p_url)
                self.get_img_url_to_save(_html)
                time.sleep(2)
        except BaseException as e:
            log.error("运行出错--> %s" % e)


class Home:
    def __init__(self, _url):
        self.url = _url
        # 所有页面
        self.pages = None
        # 每一页中的所有列表
        self.lis = None
        self.headers = dict(Referer='http://www.mzitu.com')

    def get_html(self, page_url):
        try:
            res = requests.get(page_url, headers=self.headers, timeout=10)
            if res.ok:
                return res.text
        except BaseException as e:
            log.error("请求失败--> %s" % e)
            return False

    def get_pages(self, html):
        # doc = pq(html).find('nav.navigation')
        try:
            # num = int(pq(doc('a.page-numbers').not_('.next')[-1]).text())
            self.pages = [self.url + '/page/' + str(x) + '/' for x in range(31, 40)]
        except BaseException as e:
            log.debug("Home 获取所有页码失败--> %s" % e)

    def get_lis(self, html):
        self.lis = list()
        try:
            doc = pq(html).find('ul#pins')
            for li in doc.children('li').items():
                _a = li.children('a')
                li_url = _a.attr('href')
                li_title = _a.children('img').attr('alt')
                self.lis.append(dict(url=li_url, title=li_title))
            return self.lis
        except BaseException as e:
            log.debug("获取本页数据失败--> %s " % e)

    def start(self):
        html = self.get_html(self.url)
        if html:
            log.info("开始运行，爬取首页网址：%s" % self.url)
            self.get_pages(html)
            if self.pages:
                i = 1
                for page in self.pages:
                    log.info("第 " + str(i) + " 页的url--> %s" % page)
                    i += 1
                    _html = self.get_html(page_url=page)
                    if _html:
                        _li_list = self.get_lis(_html)
                        task = []
                        for li in _li_list:
                            lg = load_img(li)
                            lg.start()
                            task.append(lg)
                        for t in task:
                            t.join()
                    else:
                        log.error("页面打开失败！")
            else:
                log.error("页码不存在")
        else:
            log.error('获取页面失败')


if __name__ == '__main__':
    url = 'http://www.mzitu.com'
    h = Home(_url=url)
    h.start()
