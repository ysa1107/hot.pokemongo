/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Y Sa
 */
public class LocalCached {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(LocalCached.class);
    public static final Map<String, Object> mapLocalCaches = new HashMap<>();

    public static Object get(String key, String instance) {
        try {
            return mapLocalCaches.get(key);
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));

        }
        return null;
    }

    public static void put(String key, Object value, long expire, String instance) {
        try {
            if (StringUtils.isEmpty(key) || value == null) {
                return;
            }
            synchronized (mapLocalCaches) {
                mapLocalCaches.put(key, value);
            }
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
        }
    }

    public static Object getMap(String key, String instance) {
        return get(key, instance);
    }
}
