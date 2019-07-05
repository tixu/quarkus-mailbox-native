package org.acme.events;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("ListenerBean");

    void onStart(@Observes StartupEvent ev) {        
          Properties properties = new Properties();
        properties.setProperty("lorg.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
          
               
        LOGGER.info("The application is starting...");
    }

    void onStop(@Observes ShutdownEvent ev) {               
        LOGGER.info("The application is stopping...");
    }

}