package com.example.interpreter.translation;

import cn.bbzzzs.common.util.JsonUtil;
import com.example.interpreter.entity.TranslationBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CodeTranslationService {

    /**
     * 有道翻译 API
     */
    private static final String API = "http://fanyi.youdao.com/openapi.do";

    /**
     * 翻译接口, 返回翻译后的实体
     *
     * @param message   翻译信息
     * @return
     */
    public static TranslationBean translation(String message) {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            URL url = new URL(API);

            // 连接对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("encoding", "UTF-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

            // 翻译文本
            String text = "你好呀, HelloServlet";
            bw.write(String.format("keyfrom=fadabvaa&key=522071532&type=data&doctype=json&version=1.1&q=%s", message));
            bw.flush();

            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return JsonUtil.stringToBean(sb.toString(), TranslationBean.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bw.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
