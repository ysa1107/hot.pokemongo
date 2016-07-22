/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.render;

import business.da.FeedDA;
import com.kyt.framework.config.Config;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import configuration.Configuration;
import entity.FeedEnt;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.MapUtils;
import utils.JUtils;
import utils.OpenGraph;

/**
 *
 * @author Y Sa
 */
public class RenderBaseImpl {

    private static final org.apache.log4j.Logger _logger = LogUtil.getLogger(RenderBaseImpl.class);
    
    private static final String DEFAULT_LAYOUT = "layout_default";

    public RenderBaseImpl() {
    }

    public static RenderBaseImpl getInstance() {
        return RenderBaseImplHolder.INSTANCE;
    }

    private static class RenderBaseImplHolder {

        private static final RenderBaseImpl INSTANCE = new RenderBaseImpl();
    }

    public String renderAjax(String actionContent, HttpServletRequest req, HttpServletResponse resp) {
        String content = actionContent;
        if (!StringUtils.isEmpty(req.getParameter("callback"))) {
            content = req.getParameter("callback").toString() + "(" + content + ")";
        }
        return content;
    }

    public String renderHtml(String actionContent, String layout, HttpServletRequest req, HttpServletResponse resp) {
        long startTime = System.nanoTime();

        //if layout is null or empty, get default layout
        if (StringUtils.isEmpty(layout)) {
            layout = DEFAULT_LAYOUT;
        }

        String content = "";
        try {
            TemplateDataDictionary dic = TemplateDictionary.create();
            
            //get slideshow
            List<FeedEnt> feed = FeedDA.getInstance(Configuration.DB_NAME).getPaging(1, 1, 20, "byhot");
            StringBuilder sb = new StringBuilder();
            for(FeedEnt f:feed){
                if(f.media_type != 1){
                    continue;
                }
                String url = Configuration.ROOT_URL + "detail/" + JUtils.Strings.IDEncrypt(f.id);
                sb.append("<div class=\"random-item\">");
                sb.append("<a class=\"random-img\" href=\"" + url + "\" style=\"background-image:url(" + f.media_url + "); display:block; min-height:100px; background-size:cover; background-position-y:center\"></a>");
                sb.append("</div>");
            }
            dic.setVariable("slideshow", sb.toString());
            dic.setVariable("main_content", actionContent);

            renderStaticVersion(dic);
            renderCommonInfo(req, dic);
            //renderAdvertiseInfo(req, resp, dic);
            //renderUserHeader(req, dic);
            renderSEOInfo(req, dic);
            //renderRightBar(req, resp, dic);
            //renderGACustom(req, dic);
            

            //Render Se_Balloon..
//            String uri = req.getRequestURI().replace(Configuration.ROOT_URL, "");
//            if (uri.contains("/search")) {
//                dic.addSection("IS_SEARCH_PAGE");
//            }
            //renderKeyWordHot(req, dic);

            //get menu            
            //dic.setVariable("MENU_HTML", LocalCached.get("menu_html", null).toString());

            Template template = this.getTemplate("front/views/layouts/", layout);
            content = template.renderToString(dic);
        } catch (Exception ex) {
            _logger.error(LogUtil.stackTrace(ex));
            //ErrorUtils.logErrorLatency(_logger, ex);
        }

        return content;
    }

    protected Template getTemplate(String tplName) throws Exception {
        TemplateLoader temploader = TemplateImpl.getTemplateLoader("front/views/");
        return temploader.getTemplate(tplName);
    }

    public Template getTemplate(String directory, String tplName) throws TemplateException {
        TemplateLoader temploader = TemplateImpl.getTemplateLoader(directory);
        return temploader.getTemplate(tplName);
    }

//    protected void renderRightBar(HttpServletRequest req, HttpServletResponse resp, TemplateDataDictionary dic) {
//        try {
//            //on/off adv
//            boolean isRightBar = true;
//            String servletName = ConvertUtils.toString(req.getAttribute("servletName"));
//            if (!StringUtils.isEmpty(servletName)) {
//                for (String NOT_ALLOW_RELOAD_PAGE_ITEM : NOT_ALLOW_RIGHT_BAR) {
//                    if (servletName.contains(NOT_ALLOW_RELOAD_PAGE_ITEM)) {
//                        isRightBar = false;
//                        break;
//                    }
//                }
//            }
//
//            if (isRightBar) {
//                dic.addSection("IS_RIGHT_BAR");
//
//                ProfileNCTStruct _profileNCT = (ProfileNCTStruct) req.getAttribute("viewerInfo");
//
//                String onOffAdvFunction;
//                String onOffAdvTitle;
//                if (_profileNCT.getUserId() > 0) {
//                    if (_profileNCT.getPowerUser() == false) {
//                        onOffAdvFunction = "NCTAdv.annouceOffAdv();";
//                        onOffAdvTitle = "Tắt Quảng Cáo";
//
//                        utils.Utils.removeCookie(req, resp, Configuration.ONOFF_ADV_COOKIE);
//                    } else {
//                        String onOffCookie = utils.Utils.getCookie(req, Configuration.ONOFF_ADV_COOKIE);
//                        if (onOffCookie != null && !onOffCookie.isEmpty()) {
//                            onOffAdvFunction = "NCTAdv.onAdv();";
//                            onOffAdvTitle = "Bật Quảng Cáo";
//                        } else {
//                            onOffAdvFunction = "NCTAdv.offAdv();";
//                            onOffAdvTitle = "Tắt Quảng Cáo";
//                        }
//                    }
//                } else {
//                    onOffAdvFunction = "NCTAdv.annouceOffAdv();";
//                    onOffAdvTitle = "Tắt Quảng Cáo";
//                }
//
//                dic.setVariable("ONOFF_ADV_FUNCTION", onOffAdvFunction);
//                dic.setVariable("ONOFF_ADV_TITLE", onOffAdvTitle);
//            }
//        } catch (Exception ex) {
//            _logger.error(LogUtil.stackTrace(ex));
//            ErrorUtils.logErrorLatency(_logger, ex);
//        }
//    }

    protected void renderCommonInfo(HttpServletRequest req, TemplateDataDictionary dic) {
//        //check allow auto reload page
//        boolean isReloadPage = true;
//        String servletName = ConvertUtils.toString(req.getAttribute("servletName"));
//        if (!StringUtils.isEmpty(servletName)) {
//            for (String NOT_ALLOW_RELOAD_PAGE_ITEM : NOT_ALLOW_RELOAD_PAGE) {
//                if (servletName.contains(NOT_ALLOW_RELOAD_PAGE_ITEM)) {
//                    isReloadPage = false;
//                    break;
//                }
//            }
//        }
//
//        //check allow render banner bottom
//        boolean isBannerBottom = true;
//        for (String NOT_ALLOW_BANNER_BOTTOM_ITEM : NOT_ALLOW_BANNER_BOTTOM) {
//            if (servletName.contains(NOT_ALLOW_BANNER_BOTTOM_ITEM)) {
//                isBannerBottom = false;
//                break;
//            }
//        }
//        if (isBannerBottom == true) {
//            dic.addSection("IS_BANNER_BOTTOM");
//        }
//
//        if (isReloadPage) {
//            if (Configuration.IS_VERSION_BETA) {
//                dic.addSection("IS_VERSION_BETA");
//            }
//            dic.addSection("IS_RELOAD_PAGE");
//        }
            dic.setVariable("latency_user_name", "gagbuzz");
            dic.setVariable("latency_time", ConvertUtils.toString(System.currentTimeMillis()));
            dic.setVariable("latency_token", "(&^*SAJSJAHKALJLSA");
    }

    

    protected void renderStaticVersion(TemplateDataDictionary dic) {
        renderCssVersion(dic);
        renderJsVersion(dic);
        renderStaticInfo(dic);
    }

    protected void renderStaticInfo(TemplateDataDictionary dic) {
        dic.setVariable("STATIC_URL", Configuration.STATIC_URL);
        dic.setVariable("STATIC_URL_PROMOTE", Configuration.STATIC_URL_PROMOTE);
        dic.setVariable("ROOT_URL", Configuration.ROOT_URL);
        dic.setVariable("ROOT_DOMAIN", Configuration.ROOT_DOMAIN);
    }

    protected void renderCssVersion(TemplateDataDictionary dic) {
        try {
//            dic.setVariable("YAN_CSS", Config.getParam("yan_widget", "YAN_CSS"));
//            dic.setVariable("SCREEN_CSS", Config.getParam("css_version", "SCREEN_CSS"));
            dic.setVariable("CUSTOM_CSS", Config.getParam("css_version", "CUSTOM_CSS"));
//            dic.setVariable("GCAFE_CSS", Config.getParam("css_version", "GCAFE_CSS"));
        } catch (Exception ex) {
            _logger.error(LogUtil.stackTrace(ex));
            //ErrorUtils.logErrorLatency(_logger, ex);
        }
    }

    protected void renderJsVersion(TemplateDataDictionary dic) {
        try {
//            dic.setVariable("JS_CORE_LIB", Config.getParam("js_version", "JS_CORE_LIB"));
//            dic.setVariable("YAN_JS", Config.getParam("yan_widget", "YAN_JS"));
//            dic.setVariable("JS_EXE_PACK", Config.getParam("js_version", "JS_EXE_PACK"));
//            dic.setVariable("STATIC_ADV", Config.getParam("js_version", "JS_NAS"));
        } catch (Exception ex) {
            _logger.error(LogUtil.stackTrace(ex));
            //ErrorUtils.logErrorLatency(_logger, ex);
        }
    }

    

//    protected void renderUserHeader(HttpServletRequest req, TemplateDataDictionary dic) {
//        try {
//            ProfileNCTStruct _profile = (ProfileNCTStruct) req.getAttribute("viewerInfo");
//            boolean isLogged = _profile.getLogged();
//
//            if (isLogged) {
//                dic.addSection("HEADER_USER_LOGIN");
//                //info user login
////                Map<String, Object> mapData = ModelSearch.getInstance().searchSongByUserName(_profile.getUserName(), 0, 0);
////                if (mapData != null && mapData.get("total") != null) {
////                    dic.setVariable("COUNT_MY_MUSIC", NumberUtils.formatNumberWithFlag(ConvertUtils.toLong(mapData.get("total"))));
////                } else {
////                    dic.setVariable("COUNT_MY_MUSIC", "#");
////                }
//
////                mapData = ModelSearch.getInstance().searchMvByUserName(_profile.getUserName(), 0, 0);
////                if (mapData != null && mapData.get("total") != null) {
////                    dic.setVariable("COUNT_MY_MV", NumberUtils.formatNumberWithFlag(ConvertUtils.toLong(mapData.get("total"))));
////                } else {
////                    dic.setVariable("COUNT_MY_MV", "#");
////                }
//                //TOwnerListValueResult listPlaylistOfSongPublic = ModelCloud.getInstance().getListPlaylistOfSongPublic(_profile.getUserId(), 1, 5);
//                //Map<String, Object> mapData = ModelSearch.getInstance().searchPlaylistByUserName(_profile.getUserName(), 0, 1);
//                //boolean checkHasPlayist = false;
//                //get playlist default
//                if (_profile.getUserId() == Configuration.DEFAULT_ACCOUNT_ID) {
//                    dic.setVariable("MY_PLAYLIST", DEFAULT_PLAYLIST_LINK);
//                } else {
//                    Map<String, String> myPlaylistData = UserUtils.renderUserHeaderPlaylist(_profile.getUserId(), _profile.getUserName());
//
//                    if (!MapUtils.isEmpty(myPlaylistData)) {
//                        dic.addSection("IS_HAVE_MY_PLAYLIST");
//                        dic.setVariable("MY_PLAYLIST_HTML",
//                                "<li style=\"max-height:24px;overflow:hidden;\"><a href=\"" + Configuration.ROOT_URL + "user/" + _profile.getUserName() + ".nghe-nhac-cua-tui.html\" title=\"Tất cả\">Tất cả</a></li>"
//                                + ConvertUtils.toString(myPlaylistData.get("html")));
//                    }
//
//                    dic.setVariable("MY_PLAYLIST", Configuration.ROOT_URL + "user/" + _profile.getUserName() + ".nghe-nhac-cua-tui.html");
//                }
//                //------------------------
//
//                //check power user
//                if (_profile.getPowerUser()) {
//                    dic.addSection("IS_POWER_USER");
//                }
//            } else {
//                dic.addSection("HEADER_USER_NOT_LOGIN");
//                dic.setVariable("MY_PLAYLIST", DEFAULT_PLAYLIST_LINK);
//            }
//
//            if (_profile.getUserId() != Configuration.DEFAULT_ACCOUNT_ID) {
//                dic.addSection("USER_UPLOAD");
//            } else {
//                dic.addSection("NOT_USER_UPLOAD");
//            }
//
//            dic.setVariable("ROOT_URL", Configuration.ROOT_URL);
//            dic.setVariable("STATIC_URL", Configuration.STATIC_URL);
//            dic.setVariable("userName", _profile.getUserName());
//            dic.setVariable("userId", ConvertUtils.toString(_profile.getUserId()));
//            dic.setVariable("isLogged", ConvertUtils.toString(_profile.getLogged()));
//            dic.setVariable("mineKey", _profile.getMineKey());
//
//            String avatarUrl = Configuration.STATIC_URL + "images/avatar_default.jpg";
//            if (!StringUtils.isEmpty(_profile.getAvatarUrl())) {
//                avatarUrl = _profile.getAvatarUrl();
//            }
//            dic.setVariable("avatarUrl", avatarUrl);
//
//            //render track latency based user info
//            String startTime = req.getAttribute("startNanoTime").toString();
//            String latencyToken = Utils.md5(Configuration.SECRET_PRIVATE_KEY + startTime + _profile.getUserName());
//
//            dic.setVariable("latency_user_name", _profile.getUserName());
//            dic.setVariable("latency_time", startTime);
//            dic.setVariable("latency_token", latencyToken);
//        } catch (Exception ex) {
//            _logger.error(LogUtil.stackTrace(ex));
//            ErrorUtils.logErrorLatency(_logger, ex);
//        }
//    }

    protected void renderSEOInfo(HttpServletRequest req, TemplateDataDictionary dic) {
        try {
            OpenGraph og = (OpenGraph) req.getAttribute("openGraph");

            if (og != null) {
                dic.setVariable("TITLE", og.Title);
                dic.setVariable("DESCRIPTION", og.Description);
                dic.setVariable("IMAGE", og.Image_Source);
                dic.setVariable("IMAGE_FB", og.FB_Images);
                dic.setVariable("KEYWORDS", og.Keywords);
                dic.setVariable("ROBOTS", og.Robots);
                dic.setVariable("CANONICAL", og.Canonical);
                dic.setVariable("ALTERNATE_URL", og.AlternateUrl);
                dic.setVariable("AUTHOR", og.Author);
                dic.setVariable("OG_FB_TITLE", og.OgFBTitle);
                dic.setVariable("OG_FB_DESCRIPTION", og.OgFBDescription);
                dic.setVariable("OG_GOOGLE_TITLE", og.OgGoogleTitle);
                dic.setVariable("OG_GOOGLE_DESCRIPTION", og.OgGoogleDescription);
                dic.setVariable("OG_FB_URL", og.OgFBUrl);
                dic.setVariable("OG_FB_SITE_NAME", og.OgFBSiteName);
            }
        } catch (Exception ex) {
            _logger.error(LogUtil.stackTrace(ex));
        }
    }

  
}
