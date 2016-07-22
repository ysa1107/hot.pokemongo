/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import cache.LocalCached;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import configuration.Configuration;
import entity.RewriteEntity;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author Y Sa
 */
public class RewriteUtils {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(RewriteUtils.class);

    public static void warmListRewriteEntity() {
        try {
            List<RewriteEntity> listRewriteEntity = parseConfigRewrite(Configuration.REWRITE_PATH);
            LocalCached.put(Configuration.REWRITE_ENTITY_MEM_KEY, listRewriteEntity, 0L, null);
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
        }
    }

    public static List<RewriteEntity> parseConfigMapping(String pathXml)
            throws Exception {
        try {
            if ((pathXml == null) || (pathXml.isEmpty())) {
                return null;
            }
            List<RewriteEntity> listReturn = new ArrayList();

            Document xmlDoc = Jsoup.parse(readFile(pathXml), "", Parser.xmlParser());

            Elements eles = xmlDoc.select("mapping > item");
            for (Element ele : eles) {
                RewriteEntity entity = new RewriteEntity();

                entity.setUrlMappingServlet(ele.getElementsByTag("servlet").get(0).text());
                entity.setUrlMapping(ele.getElementsByTag("url").get(0).text());
                entity.setStatus(ConvertUtils.toBoolean(ele.getElementsByTag("status").get(0).text()));

                listReturn.add(entity);
            }
            return listReturn;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static List<RewriteEntity> parseConfigRewrite(String pathXml)
            throws Exception {
        try {
            if ((pathXml == null) || (pathXml.isEmpty())) {
                return null;
            }
            List<RewriteEntity> listReturn = new ArrayList();

            Document xmlDoc = Jsoup.parse(readFile(pathXml), "", Parser.xmlParser());

            Elements eles = xmlDoc.select("rewrite > item");
            for (Element ele : eles) {
                RewriteEntity entity = new RewriteEntity();

                entity.setUrlPrefix(ele.getElementsByTag("urlPrefix").get(0).text());
                entity.setUrlRegex(ele.getElementsByTag("urlRegex").get(0).text());
                entity.setUrlRegexReplace(ele.getElementsByTag("urlRegexReplace").get(0).text());
                entity.setTypeMove(ConvertUtils.toInt(ele.getElementsByTag("typeMove").get(0).text()));
                entity.setStatus(ConvertUtils.toBoolean(ele.getElementsByTag("status").get(0).text()));

                listReturn.add(entity);
            }
            return listReturn;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static Map<String, List<RewriteEntity>> parseAllConfig(String pathXml)
            throws Exception {
        try {
            if ((pathXml == null) || (pathXml.isEmpty())) {
                return null;
            }
            Map<String, List<RewriteEntity>> mapReturn = new HashMap();

            List<RewriteEntity> listReturn = new ArrayList();

            Document xmlDoc = Jsoup.parse(readFile(pathXml), "", Parser.xmlParser());

            Elements eles = xmlDoc.select("mapping > item");
            for (Element ele : eles) {
                RewriteEntity entity = new RewriteEntity();

                entity.setUrlMappingServlet(ele.getElementsByTag("servlet").get(0).text());
                entity.setUrlMapping(ele.getElementsByTag("url").get(0).text());

                listReturn.add(entity);
            }
            mapReturn.put("mapping", listReturn);

            eles = xmlDoc.select("rewrite > item");
            listReturn = new ArrayList();
            for (Element ele : eles) {
                RewriteEntity entity = new RewriteEntity();

                entity.setUrlPrefix(ele.getElementsByTag("urlPrefix").get(0).text());
                entity.setUrlRegex(ele.getElementsByTag("urlRegex").get(0).text());
                entity.setUrlRegexReplace(ele.getElementsByTag("urlRegexReplace").get(0).text());
                entity.setTypeMove(ConvertUtils.toInt(ele.getElementsByTag("typeMove").get(0).text()));
                entity.setStatus(ConvertUtils.toBoolean(ele.getElementsByTag("status").get(0).text()));

                listReturn.add(entity);
            }
            mapReturn.put("rewrite", listReturn);

            return mapReturn;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public static String readFile(String path)
            throws IOException {
        BufferedReader br = null;
        StringBuilder returnStr = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(path));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                returnStr.append(sCurrentLine);
            }
            return returnStr.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                throw ex;
            }
        }

    }

    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = "http";
        if ((request.getScheme() != null) && (!request.getScheme().isEmpty())) {
            scheme = request.getScheme();
        }
        if (request.getServerPort() == 443) {
            scheme = "https";
        }
        String baseUrl = scheme + "://" + request.getServerName();
        if ((request.getServerPort() > 0) && (request.getServerPort() != 80) && (request.getServerPort() != 443)) {
            baseUrl = baseUrl + ":" + request.getServerPort();
        }
        return baseUrl;
    }

    public static void forwardRequest(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, List<RewriteEntity> listEntity)
            throws Exception {
        try {
            String requestURI;
            requestURI = request.getPathInfo();
            if ((requestURI != null) && (!requestURI.isEmpty())) {
                request.setAttribute("rawUrl", getBaseUrl(request) + requestURI);
            }
            if ((request.getQueryString() != null) && (!request.getQueryString().isEmpty())) {
                request.setAttribute("rawUrlQueryString", request.getQueryString());
            }
            if ((listEntity != null) && (listEntity.size() > 0)) {
                for (RewriteEntity rewriteEntity : listEntity) {
                    if ((rewriteEntity.getStatus())
                            && (requestURI.startsWith(rewriteEntity.getUrlPrefix()))) {
                        Pattern p = Pattern.compile(rewriteEntity.getUrlRegex());
                        Matcher m = p.matcher(requestURI);
                        StringBuffer sb = new StringBuffer();
                        while (m.find()) {
                            m.appendReplacement(sb, rewriteEntity.getUrlRegexReplace());
                        }
                        m.appendTail(sb);

                        String location = sb.toString();
                        if ((!location.isEmpty()) && (!location.equals(requestURI))) {
                            if (rewriteEntity.getTypeMove() == 3) {
                                RequestDispatcher rd = servletContext.getRequestDispatcher(location);
                                rd.forward(request, response);
                                return;
                            }
                            if (rewriteEntity.getTypeMove() == 1) {
                                response.sendRedirect(getBaseUrl(request) + location);
                                return;
                            }
                            if (rewriteEntity.getTypeMove() != 2) {
                                break;
                            }
                            response.setStatus(301);
                            response.setHeader("Location", getBaseUrl(request) + location);
                            response.setHeader("Connection", "close");
                            return;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            _log.error("Rewrite error : " + LogUtil.stackTrace(ex));
        }
        if ((response.getStatus() != 302) && (response.getStatus() != 301)) {
            _log.info("Rewrite URL : " + getBaseUrl(request) + request.getRequestURI() + "?" + request.getQueryString());

            logRequest(request);
            response.sendRedirect(getBaseUrl(request) + "/not_found?utm_medium=NoUrl&u_source=GagBuzz&utm_campaign=All");
        }
    }

    private static void logRequest(HttpServletRequest request) {
        if (request != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Request URI : " + request.getRequestURI() + "\n");
            sb.append("Request PathInfo : " + request.getPathInfo() + "\n");
            sb.append("Request QueryString : " + request.getQueryString());
            _log.info(sb.toString());
        }
    }
}
