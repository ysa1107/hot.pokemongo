/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.servlets;

import front.render.IndexImpl;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Y Sa
 */

public class IndexServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //this.initAnnotation(req, IndexServlet.class);


        String content = IndexImpl.getInstance().renderData(req, resp);
        this.out(content, req, resp);
    }
}
