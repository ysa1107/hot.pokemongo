/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.servlets;


import cache.LocalCached;
import com.kyt.framework.config.LogUtil;
import configuration.Configuration;
import entity.RewriteEntity;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.RewriteUtils;

/**
 *
 * @author Y Sa
 */
public class DispatchServlet extends HttpServlet {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(DispatchServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getPathInfo();
            if (path.contains("///")) {
                String[] url = path.split("///");
                response.sendRedirect("http://" + url[1]);
            }
            if (path.contains("mozekcdn-a.akamaihd.net") || path.contains("proximic.com")) {
                response.sendRedirect("/");
            }
            try {
                RewriteUtils.forwardRequest(request, response, getServletContext(), (List<RewriteEntity>) LocalCached.get(Configuration.REWRITE_ENTITY_MEM_KEY, null));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
            response.sendRedirect(Configuration.ROOT_URL);
        }
    }
}
