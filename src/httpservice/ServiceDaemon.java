/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package httpservice;

import com.kyt.framework.util.ConvertUtils;
import configuration.Configuration;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import utils.RewriteUtils;


/**
 *
 * @author Y Sa
 */
public class ServiceDaemon {

    public static void main(String[] args) throws Exception {

        RewriteUtils.warmListRewriteEntity();
        //Layout.getMenuHtml();

        QueuedThreadPool threadPool = new QueuedThreadPool();

        threadPool.setMinThreads(Configuration.MIN_THREAD);
        threadPool.setMaxThreads(Configuration.MAX_THREAD);

        Server server = new Server(threadPool);

        //// Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);
        server.addBean(new ScheduledExecutorScheduler());

        LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);

        StatisticsHandler stats = new StatisticsHandler();  
        stats.setHandler(server.getHandler());

        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setOutputBufferSize(32768);
        httpConfig.setRequestHeaderSize(8192);
        httpConfig.setResponseHeaderSize(8192);
        httpConfig.setSendServerVersion(true);
        httpConfig.setSendDateHeader(true);

        String host_listen = Configuration.HOST_LISTEN;
        int port_listen = ConvertUtils.toInt(Configuration.PORT_LISTEN);

        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setHost(host_listen);
        connector.setPort(port_listen);

        server.setConnectors(new Connector[]{connector});

        // URL Rewrite handler
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRewriteRequestURI(true);
        rewriteHandler.setRewritePathInfo(true);
        rewriteHandler.setOriginalPathAttribute("requestedPath");

        RewriteRegexRule rewriteRegexRule_shorten = new RewriteRegexRule();
        rewriteRegexRule_shorten.setRegex("^/\\b(?!(tim-kiem|tim-nang-cao|profile|rss|favicon.ico)(?=\\d|\\b))([0-9a-zA-Z-_]+)$");
        rewriteRegexRule_shorten.setReplacement("/dispatch?s=$2");
        rewriteHandler.addRule(rewriteRegexRule_shorten);

        RewriteRegexRule rewriteRegexRule_shorten_withPrams = new RewriteRegexRule();
        rewriteRegexRule_shorten_withPrams.setRegex("^/\\b(?!(tim-kiem|tim-nang-cao|profile|rss|favicon.ico|m|l|m2|ifl)(?=\\d|\\b))([0-9a-zA-Z-_]+)/([0-9a-zA-Z-_]+)$");
        rewriteRegexRule_shorten_withPrams.setReplacement("/dispatch?s=$2&p=$3");
        rewriteHandler.addRule(rewriteRegexRule_shorten_withPrams);
        
        
        // Servlet Handlers
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);        
        
        // Common pages
        handler.addServlet("front.servlets.IndexServlet", "");
        handler.addServlet("front.servlets.NotFoundServlet", "/not_found");
        handler.addServlet("front.servlets.DetailServlet", "/detail");
        
        handler.addServlet("front.servlets.DispatchServlet", "/dispatch");
        handler.addServlet("front.servlets.DispatchServlet", "/*");
        
        //add filter request
       // handler.addFilter(MobileFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));
        handler.addFilter(GzipFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC));

        //// Set handlers
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(rewriteHandler);
        handlers.addHandler(handler);
        handlers.addHandler(stats);
        server.setHandler(handlers);

        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        server.start();
        server.join();
    }
}
