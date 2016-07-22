/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.kyt.framework.config.LogUtil;
import configuration.Configuration;
import configuration.Const;
import entity.FeedEnt;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Y Sa
 */
public class OpenGraph {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(OpenGraph.class);
    public String Title = "";
    public String Description = "";
    public String Keywords = "";
    public String Image_Source = "";
    public String Robots = "";
    public String Canonical = "";
    public String VideoSource = "";
    public String VideoWidth = "";
    public String VideoHeight = "";
    public String VideoType = "";
    public String AlternateUrl = "";
    public String Author = Configuration.ROOT_DOMAIN;
    public String FB_Images = "";
    //Og FB
    public boolean OgFBEnable = false;
    public String OgFBTitle = "<meta property=\"og:og:title\" content=\"%s\" />";
    public String OgFBDescription = "<meta property=\"og:description\" content=\"%s\" />";
    public String OgFBUrl = "<meta property=\"og:url\" content=\"%s\" />";
    public String OgFBSiteName = "<meta property=\"og:site_name\" content=\"GagBuzz\" />";
    public String OgFBType = "<meta property=\"og:type\" content=\"%s\" />";
    public String OgFBVideo = "<meta property=\"og:video\" content=\"%s\" />";
    public String OgFBVideoType = "<meta property=\"og:video:type\" content=\"application/x-shockwave-flash\" />";
    public String OgFBVideoWidth = "<meta property=\"og:video:width\" content=\"%s\" />";
    public String OgFBVideoHeight = "<meta property=\"og:video:height\" content=\"%s\" />";
    public String OgFBVideoSecure = "<meta property=\"og:video:secure_url\" content=\"%s\" />";
    //public String OgFBAudioSecure = "";
    
    //Og Google
    public String OgGoogleTitle = "<meta itemprop=\"name\" content=\"%s\" />";
    public String OgGoogleDescription = "<meta itemprop=\"description\" content=\"%s\" />";

    public OpenGraph() {
        Title = "Hot Pokemon Go";
        Description = "description";
        Keywords = "key_word";
        Image_Source = Configuration.STATIC_URL + "images/logo_600x600.jpg";
        Robots = "index, follow";
        AlternateUrl = "";
        FB_Images = Image_Source;
        OgFBTitle = Title;
        OgFBDescription = Description;
        OgGoogleTitle = Title;
        OgGoogleDescription = Description;
    }

    public OpenGraph setSEOError(HttpServletRequest request) {
        try {
            String robots = "noindex, nofollow, noarchive, noodp, noydir";

            this.Robots = robots;

            this.OgFBTitle = this.Title;
            this.OgFBDescription = this.Description;
            this.OgGoogleTitle = this.Title;
            this.OgGoogleDescription = this.Description;

            return this;
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
            return new OpenGraph();
        }
    }

    public OpenGraph setSEODetail(HttpServletRequest request,FeedEnt ent) {
        try {
            if (ent != null) {
                String shareURL = Configuration.ROOT_URL + "detail/" + JUtils.Strings.IDEncrypt(ent.id);
                this.Title = ent.title;
                this.Canonical ="<link rel=\"canonical\" href=\"" + shareURL + "\" />";
                this.Description = ent.description;
                this.Image_Source = ent.thumbnail.replace("360", "720");
                this.FB_Images = ent.thumbnail.replace("360", "720");

                this.OgFBEnable = true;
                if(ent.media_type == Const.MediaType.GIF){
                    this.OgFBUrl = String.format(this.OgFBUrl, ent.media_url);
                    this.OgFBType = "video.other";
                    this.Image_Source = ent.media_url;
                    this.FB_Images = ent.media_url;
                }else{
                    this.OgFBUrl = String.format(this.OgFBUrl, shareURL);
                }
                
                String ogDescription = this.Description.replaceAll("<br />", ".").replaceAll("<br>", ".").replaceAll("<br/>", ".");
                
                this.OgFBTitle = this.Title;
                this.OgFBDescription = ogDescription;
                this.OgGoogleTitle = this.Title;
                this.OgGoogleDescription = ogDescription;
            }
            return this;
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
            return new OpenGraph();
        }
    }
}
