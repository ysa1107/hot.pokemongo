package configuration;


import com.kyt.framework.config.Config;
import com.kyt.framework.util.ConvertUtils;

public class Configuration {

    public static int MIN_THREAD;
    public static int MAX_THREAD;
    public static String ROOT_URL;
    public static String MOBILE_URL;
    public static String ROOT_DOMAIN;
    public static String STATIC_URL;
    public static String STATIC_URL_PROMOTE;
    public static String REWRITE_PATH;
    public static String PORT_LISTEN;
    public static String HOST_LISTEN;
    public static String REWRITE_ENTITY_MEM_KEY;
    public static String API_URL;
    public static String CUSTOM_CSS;
    public static String DB_NAME = "gagbuzz_db";
    
    static {
        try {
            MIN_THREAD = ConvertUtils.toInt(Config.getParam("thread-pool", "min"));
            MAX_THREAD = ConvertUtils.toInt(Config.getParam("thread-pool", "max"));
            ROOT_URL = Config.getParam("gagbuzz-url", "url");
            MOBILE_URL = Config.getParam("gagbuzz-url", "mobile-url");
            STATIC_URL = Config.getParam("gagbuzz-url", "static_url");
            STATIC_URL_PROMOTE = Config.getParam("gagbuzz-url", "static_url_promote");
            
            ROOT_DOMAIN = Config.getParam("gagbuzz-url", "domain");
            REWRITE_PATH = Config.getParam("rewrite-path", "path");
            HOST_LISTEN = Config.getParam("rest", "host_listen");
            PORT_LISTEN = Config.getParam("rest", "port_listen");
            REWRITE_ENTITY_MEM_KEY = "rewrite_entity_key";
            API_URL = Config.getParam("api", "url");
            CUSTOM_CSS = Config.getParam("css_version", "CUSTOM_CSS");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
