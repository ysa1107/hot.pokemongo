/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package front.render;

import hapax.Template;
import hapax.TemplateException;
import hapax.TemplateLoader;
import hapax.TemplateResourceLoader;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

/**
 *
 * @author Y Sa
 */
public class TemplateImpl {

    private static final Lock createLock_ = new ReentrantLock();
    private static final Map<String, TemplateLoader> _instances = new NonBlockingHashMap();

    public static TemplateLoader getTemplateLoader(String directory) {
        TemplateLoader tmpLoader = _instances.get(directory);
        if (tmpLoader == null) {
            createLock_.lock();
            try {
                tmpLoader = _instances.get(directory);
                if (tmpLoader == null) {
                    tmpLoader = TemplateResourceLoader.create(directory);
                    _instances.put(directory, tmpLoader);
                }
            } finally {
                createLock_.unlock();
            }
        }
        return tmpLoader;
    }

    public static Template getTemplate(String directory, String tplName) throws TemplateException {
        TemplateLoader temploader = getTemplateLoader(directory);
        return temploader.getTemplate(tplName);
    }
}
