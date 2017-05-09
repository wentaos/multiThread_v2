package com.winchannel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.MultipartConfigElement;
import java.util.concurrent.TimeUnit;

@ComponentScan
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class})
@EnableAutoConfiguration
@EnableScheduling
@Configuration
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class})

public class App {
    @Value("${server.port}")
    private int port;
    @Value("${server.sessionTimeout}")
    private int sessionTimeout;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setPort(port);
        factory.setSessionTimeout(sessionTimeout, TimeUnit.SECONDS);
        return factory;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("102400KB");
        factory.setMaxRequestSize("102400KB");
        return factory.createMultipartConfig();
    }
}


@Configuration
@ImportResource("/spring/applicationContext.xml")
//@ImportResource(locations={"classpath:spring/applicationContext.xml"})
class XmlImportingConfiguration {
}