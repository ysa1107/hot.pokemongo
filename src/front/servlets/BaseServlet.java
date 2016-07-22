/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.servlets;


import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import configuration.Configuration;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;


/**
 *
 * @author Y Sa
 */
public class BaseServlet extends HttpServlet {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(BaseServlet.class);

    public BaseServlet() {
    }

//    protected void initAnnotation(HttpServletRequest req, Class<? extends HttpServlet> type) {
//
//        WebServlet info = type.getAnnotation(WebServlet.class);
//        req.setAttribute("servletName", ConvertUtils.toString(info.name()));
//
//        if (info.initParams().length > 0) {
//            for (WebInitParam initParam : info.initParams()) {
//                req.setAttribute(ConvertUtils.toString(initParam.name()), ConvertUtils.toString(initParam.value()));
//            }
//        }
//    }

//    protected void initProfilerLog(HttpServletRequest req, HttpServletResponse resp) {
//        boolean dbg = ("true".equalsIgnoreCase(req.getParameter("debug")));
//        ProfilerLog profiler = new ProfilerLog(dbg);
//
//        if (dbg) {
//            profiler.doStartLog("RENDER_PAGE");
//        }
//
//        req.setAttribute("profiler", profiler);
//        req.setAttribute("profilerDebug", dbg);
//        req.setAttribute("startNanoTime", System.nanoTime());
//    }

//    protected void initPlugin(HttpServletRequest req, HttpServletResponse resp) throws UnsupportedEncodingException {
//        //check User LoggedIn at here 
//        ProfileNCTStruct _userInfo = Auth.checkUserLogin(req, resp);
//
//        String encryptId = req.getParameter("tv");
//
//        if (StringUtils.isEmpty(encryptId)) {
//            encryptId = _userInfo.getUserEncryptId();
//        }
//
//        long ownerId = 0;
//        if (!StringUtils.isEmpty(encryptId)) {
//            ownerId = NumberUtils.IDDecrypt(encryptId);
//        }
//
//        // Init ownerInfo, ViewerInfo and set to req
//        req.setAttribute("viewerInfo", _userInfo);
//        req.setAttribute("ownerId", ownerId);
//    }

    protected void out(String content, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (resp.getStatus() != HttpServletResponse.SC_MOVED_TEMPORARILY && resp.getStatus() != HttpServletResponse.SC_MOVED_PERMANENTLY) {
            setDefaultHeader(resp);

            try (PrintWriter out = resp.getWriter()) {
                out.println(content);
                //out.println(content.replaceAll("\n", "").replaceAll("( )+", " "));
                //out.println(content.replaceAll("\n( )+\n", "\n").replaceAll("\n\n", "\n"));
                this.outProfilerLog(req, resp);

                out.flush();
                out.close();

                //log timing
                String timingNamespace = (req.getAttribute("servletName") == null) ? "Unknow Servlet" : req.getAttribute("servletName").toString();
                long startTime = ConvertUtils.toLong(req.getAttribute("startNanoTime"));
            } catch (Exception ex) {
                _log.error(LogUtil.stackTrace(ex));
            }
        }
    }

    private void outProfilerLog(HttpServletRequest req, HttpServletResponse resp) {
        try (PrintWriter out = resp.getWriter()) {
            Boolean checkUsername = false;
            
            if (ConvertUtils.toBoolean(req.getAttribute("profilerDebug")) && checkUsername) {
            }
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
        }
    }

    protected void setDefaultHeader(HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
        resp.setHeader("Vary", "Accept-Encoding");
        resp.setHeader("P3P", "CP='NOI ADM DEV PSAi COM NAV OUR OTRo STP IND DEM'");
    }
}
