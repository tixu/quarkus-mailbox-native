package org.acme.messages;

import org.acme.rest.json.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyValidator implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DummyValidator.class);

    private Content payload;

    public DummyValidator(Content payload) {
        this.payload = payload;
    }

    @Override
    public void run() {
        try {
            logger.info("starting validator task " + payload);
            Thread.sleep(1000); // simulate long task
            payload.setState("validated");
            logger.info("long validator finished " + payload);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
