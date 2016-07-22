/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.render;

import business.da.FeedDA;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import com.kyt.framework.util.DateTimeUtils;
import com.kyt.framework.util.JSONUtil;
import com.kyt.framework.util.StringUtils;
import configuration.Configuration;
import configuration.Const;
import entity.FeedEnt;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.JUtils;
import utils.OpenGraph;
import utils.ParseUtils;

/**
 *
 * @author Y Sa
 */
public class Detailmpl extends RenderBaseImpl {
    private static final org.apache.log4j.Logger _logger = LogUtil.getLogger(Detailmpl.class);
    private static final String VIEW_SCRIPT_DIR = "front/views/script/detail/";
  

    private Detailmpl() {
    }

    public static Detailmpl getInstance() {
        return RenderIndexImplHolder.INSTANCE;
    }

    private static class RenderIndexImplHolder {

        private static final Detailmpl INSTANCE = new Detailmpl();
    }

    public static void main(String[] args) {
        System.out.println(JUtils.Strings.IDEncrypt(18));
    }
    
    public String renderData(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String key = ConvertUtils.toString(req.getParameter("p"),"");
            int id = (int)JUtils.Strings.IDDecrypt(key);
            //int id = ConvertUtils.toInt(key);
            String content;
            TemplateDataDictionary dic = TemplateDictionary.create();

            FeedEnt feed = FeedDA.getInstance(Configuration.DB_NAME).get(id);
            
            String json = JSONUtil.Serialize(feed);
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject  obj = jelement.getAsJsonObject();
            if(obj != null){
                int media_type = ConvertUtils.toInt(obj.get("media_type").getAsString());
                switch (media_type) {
                    case Const.MediaType.GIF:
                        dic.addSection("IS_PIC");
                        dic.setVariable("media_url", obj.get("media_url").getAsString());
                        break;
                    case Const.MediaType.IMAGE:
                        dic.addSection("IS_PIC");
                        dic.setVariable("media_url", obj.get("media_url").getAsString());
                        break;
                    case Const.MediaType.MP4:
                        dic.addSection("IS_VIDEO");
                        dic.setVariable("media_url", obj.get("media_url").getAsString());
                        break;
                    case Const.MediaType.YOUTUBE:
                        String v_key = obj.get("media_url").getAsString();
                        dic.setVariable("ytkey", v_key);
                        dic.addSection("IS_YOUTUBE");
                        break;
                    default:
                        break;
                }
                
                
                if(!StringUtils.isEmpty(obj.get("description").getAsString())){
                    dic.addSection("DESCRIPTION");
                    dic.setVariable("description",obj.get("description").getAsString());
                }
                dic.setVariable("id", ConvertUtils.toString(id));
                dic.setVariable("key", key);
                dic.setVariable("title", obj.get("title").getAsString());
                dic.setVariable("created", DateTimeUtils.toString(new Date(ConvertUtils.toLong(obj.get("created").toString())), "dd/MM/yyyy hh:mm:ss"));
                dic.setVariable("tags", obj.get("tags").getAsString());
                dic.setVariable("thumbnail", obj.get("thumbnail").getAsString());
                dic.setVariable("viewed", obj.get("viewed").getAsString());
                dic.setVariable("liked", obj.get("liked").getAsString());
                dic.setVariable("commented", obj.get("commented").getAsString());
            }
            
            dic.setVariable("STATIC_URL", Configuration.STATIC_URL);
            dic.setVariable("STATIC_URL_PROMOTE", Configuration.STATIC_URL_PROMOTE);
            dic.setVariable("ROOT_URL", Configuration.ROOT_URL);
            
            OpenGraph og = new OpenGraph();
            req.setAttribute("openGraph", og.setSEODetail(req, feed));
            
            Template template = this.getTemplate(VIEW_SCRIPT_DIR, "detail");
            content = template.renderToString(dic);

            return renderHtml(content, "", req, resp);
        } catch (Exception ex) {
            _logger.error(LogUtil.stackTrace(ex));
            //ErrorUtils.logErrorLatency(_logger, ex);
            return "";
        }
    }

    
}
