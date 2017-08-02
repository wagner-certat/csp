package com.intrasoft.csp.server.service;

import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.routes.RouteUtils;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by iskitsas on 5/5/17.
 */
@Service
public class ErrorMessageHandler implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorMessageHandler.class);

    @Autowired
    ConsumerTemplate consumer;

    @Autowired
    ProducerTemplate producer;

    @Autowired
    RouteUtils endpoint;

    @Value("${consume.errorq.on.interval}")
    Boolean consumeErrorqOnIterval;

    @Value("${consume.errorq.fixed.delay}")
    Long fixedDelay;

    @Value("${consume.errorq.initial.delay}")
    Long initialDelay;

    @Value("${consume.errorq.message.consumption.delay}")
    Long consumptionDelay;

    @PostConstruct
    public void init(){
        LOG.info(" -- consume.errorq.on.interval = "+consumeErrorqOnIterval);
        if(consumeErrorqOnIterval) {
            LOG.info("  -- consume.errorq.fixed.delay = " + fixedDelay+"ms");
            LOG.info("  -- consume.errorq.initial.delay = " + initialDelay+"ms");
        }
    }


    @Scheduled(fixedDelayString = "${consume.errorq.fixed.delay}", initialDelayString = "${consume.errorq.initial.delay}")
    public void checkForErrorMessages(){
        if(consumeErrorqOnIterval) {
            consumeErrorMessagesOnIterval();
        }
    }

    public String consumeErrorMessages(Integer maxMessagesToConsume, Long msDelay, String reportLB){
        StringBuilder ret = new StringBuilder();
        for(int i=0; i<maxMessagesToConsume;i++){
            String line = "Consuming from "+endpoint.apply(ERROR)+", Count "+(i+1)+": ";
            String c = consumeErrorMessage(msDelay);
            if(c!=null){
                line += c;
            }else{
                line += "no message found.";
            }
            LOG.warn(line);
            ret.append(line).append(reportLB);
        }
        return ret.toString();
    }

    public String consumeErrorMessage(Long msDelay){
        String ret = "";
        Exchange exchange = consumer.receive(endpoint.apply(ERROR),msDelay);

        if(exchange!=null) { // if a message is found in error Q
            //CRITICAL: The message has been consumed, in case of exception we should not loose the message
            try {
                String endpointUri = (String) exchange.getIn().getHeader(Exchange.INTERCEPTED_ENDPOINT);
                LOG.info("Consuming from DQL. Error was at: " + endpointUri);

                //try to redeliver the message
                Object body = exchange.getIn().getBody();
                Map<String, Object> headers = exchange.getIn().getHeaders();
                producer.sendBodyAndHeaders(endpointUri, body, headers);
                ret = "found message for "+endpointUri+", consuming and redelivering..";
            }catch (Exception e){
                LOG.error("CRITICAL error, message from "+endpoint.apply(ERROR)+" will be lost." +
                        "Headers:"+exchange.getIn().getHeaders().toString()+", Body:"+exchange.getIn().getBody().toString(), e);
                //TODO: recover this state
            }

        }else{
            return null;
        }
        return ret;
    }

    public void consumeErrorMessagesOnIterval(){
        LOG.info("Consume any messages found in "+endpoint.apply(ERROR)+" on specific time interval.");
        boolean consumeWhileFound = true;
        int count = 0;
        while(consumeWhileFound || count < 10){
            String c = consumeErrorMessage(consumptionDelay);
            if(c == null){
                consumeWhileFound = false;
            }
            count++;
        }
    }
}
