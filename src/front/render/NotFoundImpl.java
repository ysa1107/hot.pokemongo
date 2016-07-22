/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.render;

import com.kyt.framework.config.LogUtil;
import configuration.Configuration;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 *
 * @author Y Sa
 */
public class NotFoundImpl extends RenderBaseImpl {

    private static final org.apache.log4j.Logger _logger = LogUtil.getLogger(NotFoundImpl.class);
    private static final String VIEW_SCRIPT_DIR = "front/views/script/common/";
  

    private NotFoundImpl() {
    }

    public static NotFoundImpl getInstance() {
        return RenderIndexImplHolder.INSTANCE;
    }

    private static class RenderIndexImplHolder {

        private static final NotFoundImpl INSTANCE = new NotFoundImpl();
    }

    public String renderData(HttpServletRequest req, HttpServletResponse resp) {
        try {

            String content;
            TemplateDataDictionary dic = TemplateDictionary.create();

            dic.setVariable("STATIC_URL", Configuration.STATIC_URL);
            dic.setVariable("STATIC_URL_PROMOTE", Configuration.STATIC_URL_PROMOTE);
            Template template = this.getTemplate(VIEW_SCRIPT_DIR, "notfound");
            content = template.renderToString(dic);

            return renderHtml(content, "", req, resp);
        } catch (Exception ex) {
            _logger.error(LogUtil.stackTrace(ex));
            //ErrorUtils.logErrorLatency(_logger, ex);
            return "";
        }
    }

    
}
