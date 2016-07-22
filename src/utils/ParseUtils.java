/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.google.gson.Gson;
import com.kyt.framework.config.LogUtil;
import front.render.Detailmpl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author ysa
 */
public class ParseUtils {

    private static final org.apache.log4j.Logger _logger = LogUtil.getLogger(Detailmpl.class);
    
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static String readJsonFromUrl(String url) throws IOException {
        _logger.info("ParseUtils().readJsonFromUrl " + url);
        InputStream is = new URL(url).openStream();
        String json = "";
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            json = readAll(rd);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return json;
    }
}
