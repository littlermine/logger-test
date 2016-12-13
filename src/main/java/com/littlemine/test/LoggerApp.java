package com.littlemine.test;

import java.nio.charset.StandardCharsets;
import org.apache.catalina.connector.Connector;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CharacterEncodingFilter;

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
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(port);
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {

            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                //设置最大连接数
                protocol.setMaxConnections(10000);
                //设置最大线程数
                protocol.setMaxThreads(200);
                protocol.setConnectionTimeout(3000);
            }
        });
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

}
