package org.acme.messages;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.acme.rest.json.Content;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bean consuming data from the "prices" AMQP queue and applying some conversion.
 * The result is pushed to the "my-data-stream" stream which is an in-memory stream.
 */
@ApplicationScoped
public class Receiver {

 
    

private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
private Jsonb jsonb = JsonbBuilder.create();
    
    
    @Incoming("my-amqp-input") 

   
                                                      
    public CompletionStage<Void> receive(String input) {
        logger.info("received message with payload: " + input);
        Content c = jsonb.fromJson(input, Content.class);
        return CompletableFuture.runAsync(new DummyValidator(c));
}

}