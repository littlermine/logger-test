package com.littlemine.test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class LoggerApp extends SpringBootServletInitializer {

    public static final boolean IS_SECURITY_ENABLED = System.getSecurityManager() != null;

    private static final int DEFAULT_PORT = 8106;
    private static int port = DEFAULT_PORT;

    public static void main(String[] args) throws Exception {
        // System.out.println(IS_SECURITY_ENABLED);
        port = NumberUtils.toInt(System.getProperty("port"), DEFAULT_PORT);
        SpringApplication.run(LoggerApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(LoggerApp.class);
    }

    // http://m.blog.csdn.net/article/details?id=51306140
    //    @Bean
    //    public EmbeddedServletContainerFactory servletContainer() {
    //        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
    //        factory.setPort(port);
    //        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
    //
    //            @Override
    //            public void customize(Connector connector) {
    //                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
    //                //设置最大连接数
    //                protocol.setMaxConnections(50000);
    //                //设置最大线程数
    //                protocol.setMaxThreads(200);
    //                protocol.setConnectionTimeout(3000);
    //            }
    //        });
    //        return factory;
    //    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer(
            @Value("${jetty.threadPool.maxThreads:200}") final String maxThreads,
            @Value("${jetty.threadPool.minThreads:100}") final String minThreads,
            @Value("${jetty.threadPool.idleTimeout:60000}") final String idleTimeout,
            @Value("${jetty.threadPool.maxJobs:6000}") final String maxJobs
    ) {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        factory.setPort(port);

        int _minThreads = NumberUtils.toInt(minThreads, 50);
        int _maxThreads = NumberUtils.toInt(maxThreads, 300);
        int _idleTimeout = NumberUtils.toInt(idleTimeout, 60000);
        int _maxJobs = NumberUtils.toInt(maxJobs, 6000);
        factory.addServerCustomizers(getJettyThreadPoolCustomizer(_maxThreads, _minThreads, _idleTimeout, _maxJobs));
        factory.addServerCustomizers(getJettyRequestLogServerCustomizer());
        factory.addServerCustomizers(setConnectionIdleTimeout(0, 3000));

        return factory;
    }

    @Bean
    public FilterRegistrationBean characterEncoding() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name());
        registration.setFilter(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    public static JettyServerCustomizer getJettyThreadPoolCustomizer(
            final int maxThreads, final int minThreads, final int idleTimeout, final int maxJobs
    ) {
        JettyServerCustomizer jettyServerCustomizer = new JettyServerCustomizer() {

            @Override
            public void customize(final Server server) {
                // Tweak the connection pool used by Jetty to handle incoming HTTP connections
                final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
                try {
                    Field field = QueuedThreadPool.class.getDeclaredField("_jobs");
                    field.setAccessible(true);

                    BlockingQueue<Runnable> queue = new BlockingArrayQueue<Runnable>(minThreads, minThreads, maxJobs);
                    field.set(threadPool, queue);
                } catch (Exception e) {
                    log.error("error when update server.threadPool._jobs", e);
                }

                threadPool.setMaxThreads(maxThreads);
                threadPool.setMinThreads(minThreads);
                threadPool.setIdleTimeout(idleTimeout);
            }
        };
        return jettyServerCustomizer;
    }

    public static JettyServerCustomizer getJettyRequestLogServerCustomizer() {
        JettyServerCustomizer jettyServerCustomizer = new JettyServerCustomizer() {

            @Override
            public void customize(Server server) {
                HandlerCollection handlers = new HandlerCollection();
                for (Handler handler : server.getHandlers()) {
                    handlers.addHandler(handler);
                }
                handlers.addHandler(getRequestLogHandler());

                server.setHandler(handlers);
            }
        };

        return jettyServerCustomizer;
    }

    public static RequestLogHandler getRequestLogHandler() {

        NCSARequestLog log = new NCSARequestLog("/data/logs/logger-test/access.yyyy_mm_dd.log");
        log.setFilenameDateFormat("yyyyMMdd");
        log.setLogDateFormat("HH:mm:ss.SSS");
        log.setAppend(true);
        log.setLogTimeZone("GMT+8");
        log.setLogLatency(true);
        log.setExtended(true);
        log.setRetainDays(5);

        RequestLogHandler handler = new RequestLogHandler();
        handler.setRequestLog(log);
        return handler;
    }

    public static JettyServerCustomizer setConnectionIdleTimeout(final int backlog, final int timeout) {
        JettyServerCustomizer customizer = new JettyServerCustomizer() {

            @Override
            public void customize(final Server server) {
                Connector[] connectors = server.getConnectors();
                for (Connector conn : connectors) {
                    if (conn instanceof ServerConnector) {
                        ServerConnector _con = (ServerConnector) conn;
                        _con.setAcceptQueueSize(backlog);
                        _con.setIdleTimeout(timeout);
                    }
                }
            }
        };
        return customizer;
    }

}
