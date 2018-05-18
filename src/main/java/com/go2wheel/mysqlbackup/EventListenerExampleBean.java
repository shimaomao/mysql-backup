package com.go2wheel.mysqlbackup;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventListenerExampleBean {

    private static final Logger LOG = LoggerFactory.getLogger(EventListenerExampleBean.class);
    
    public static int counter;
    
//    @Autowired
//    private MyAppSettings myAppSettings;
 
    /**
     *  @see ApplicationEvent
     *  
     * @param event
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LOG.info("Increment counter");
        counter++;
    }
    
    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) throws IOException {
//    	myAppSettings.post();
    }
    
}
