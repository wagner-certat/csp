package com.intrasoft.csp.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.exceptions.InvalidDataException;
import com.intrasoft.csp.libraries.restclient.exceptions.CspBusinessException;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by iskitsas on 4/8/17.
 */
@Service
public class CamelRestService {
    private static final Logger LOG = LoggerFactory.getLogger(CamelRestService.class);

    @Value("${server.camel.rest.service.is.async:true}")
    Boolean camelRestServiceIsAsync;

    @Autowired
    ObjectMapper objectMapper;

    @Produce
    private ProducerTemplate producerTemplate;

    public <T> List<T> sendAndGetList(String uri, Object obj , String httpMethod, Class<T> tClass, Map<String,Object> headers) throws IOException {
        String out = sendBodyAndHeaders(uri,obj, httpMethod,headers);
        //in case of deserialization exception GDelivery kicks in - we should find a workaround to just log the error. GDelivery SHOULD NOT kick in
        try {
            return objectMapper.readValue(out, objectMapper.getTypeFactory().constructCollectionType(List.class,tClass));
        } catch (IOException e) {
            throw new InvalidDataException(String.format("While parsing the response from this uri %s we got an exception. Response was: %s " +
                    "\n\n" +
                    "Initial exception message: %s",uri,out,e.getMessage()));
        }
    }

    public <T> List<T> sendAndGetList(String uri, Object obj, String httpMethod, Class<T> tClass, Map<String, Object> headers, boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery) throws IOException {
        String out = sendBodyAndHeaders(uri, obj, httpMethod, headers, checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery);
        if (out == null) {
            return null;
        }
        try {
            return objectMapper.readValue(out, objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
        } catch (IOException e) {
            throw new InvalidDataException(String.format("While parsing the response from this uri %s we got an exception. Response was: %s " +
                    "\n\n" +
                    "Initial exception message: %s", uri, out, e.getMessage()));
        }
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass) throws IOException {
        String out = send(uri,obj, httpMethod);
        try {
            return objectMapper.readValue(out, tClass);
        } catch (IOException e) {
            throw new InvalidDataException(String.format("While parsing the response from this uri %s we got an exception. Response was: %s " +
                    "\n\n" +
                    "Initial exception message: %s", uri, out, e.getMessage()));
        }
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass,boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery) throws IOException {
        String out = send(uri,obj, httpMethod,checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery);
        if(out == null){
            return null;
        }
        try {
            return objectMapper.readValue(out, tClass);
        } catch (IOException e) {
            throw new InvalidDataException(String.format("While parsing the response from this uri %s we got an exception. Response was: %s " +
                    "\n\n" +
                    "Initial exception message: %s",uri,out,e.getMessage()));
        }
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass,boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery, List<Integer> doNotLogErrorOnTheseStatusCodes) throws IOException {
        String out = send(uri,obj, httpMethod,checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery,doNotLogErrorOnTheseStatusCodes);
        if(out == null){
            return null;
        }
        try {
            return objectMapper.readValue(out, tClass);
        } catch (IOException e) {
            throw new InvalidDataException(String.format("While parsing the response from this uri %s we got an exception. Response was: %s " +
                    "\n\n" +
                    "Initial exception message: %s",uri,out,e.getMessage()));
        }
    }

    public <T> T send(String uri, Object obj ,String httpMethod, Class<T> tClass,Map<String,Object> headers) throws IOException {
        String out = sendBodyAndHeaders(uri,obj, httpMethod,headers);
        try{
            return objectMapper.readValue(out, tClass);
        } catch (IOException e) {
            throw new InvalidDataException(String.format("While parsing the response from this uri %s we got an exception. Response was: %s " +
                    "\n\n" +
                    "Initial exception message: %s",uri,out,e.getMessage()));
        }
    }

    public String send(String uri, Object obj, String httpMethod) throws IOException {
        return sendBodyAndHeaders(uri,obj,httpMethod,null);
    }

    public String send(String uri, Object obj, String httpMethod,boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery) throws IOException {
        return sendBodyAndHeaders(uri,obj,httpMethod,null,checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery);
    }

    public String send(String uri, Object obj, String httpMethod,boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery,List<Integer> doNotLogErrorOnTheseStatusCodes) throws IOException {
        return sendBodyAndHeaders(uri,obj,httpMethod,null,checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery,doNotLogErrorOnTheseStatusCodes);
    }

    public String sendBodyAndHeaders(String uri, Object obj, String httpMethod, Map<String,Object> headers) throws JsonProcessingException {
        return sendBodyAndHeaders(uri, obj, httpMethod, headers, false);
    }


    public String sendBodyAndHeaders(String uri, Object obj, String httpMethod, Map<String,Object> headers,
                                     boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery) throws JsonProcessingException {
//        //objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
//        //objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//        byte[] b = objectMapper.writeValueAsBytes(obj);
//
//        Exchange exchange = getExchange(camelRestServiceIsAsync,uri,b,httpMethod,headers);
//
//        String out = exchange.getOut().getBody(String.class);
//        Exception exception = exchange.getException();
//        Boolean isExternalRedelivered = exchange.isExternalRedelivered();
//        Boolean isFailed = exchange.isFailed();
//        if(isFailed && exception != null){
//            //TODO: Redelivery only at specific 5xx and maybe 408. More researched is needed. Check SXCSP-282
//            //HttpHostConnectException might be raised from TC calls
//            if(checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery
//                    &&  exception.getClass().equals(HttpOperationFailedException.class)
//                    && ((HttpOperationFailedException) exception).getStatusCode()>= 400
//                    && ((HttpOperationFailedException) exception).getStatusCode()< 500){
//                HttpOperationFailedException ef = (HttpOperationFailedException) exception;
//                LOG.error("ENDPOINT "+uri+" responded with statusCode "+ef.getStatusCode() + " "+ef.getStatusText());
//            }else {
//                throw new CspBusinessException("Exception in external request: " + exception.getMessage(), exception);
//                //exception.getClass().equals(ConnectException.class)
//            }
//        }
//        return out;
        return sendBodyAndHeaders(uri, obj, httpMethod,  headers, checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery,null);
    }

    public String sendBodyAndHeaders(String uri, Object obj, String httpMethod, Map<String,Object> headers,
                                     boolean checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery,List<Integer> doNotLogErrorOnTheseStatusCodes) throws JsonProcessingException {
        //objectMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
        //objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        byte[] b = objectMapper.writeValueAsBytes(obj);

        Exchange exchange = getExchange(camelRestServiceIsAsync,uri,b,httpMethod,headers);

        String out = exchange.getOut().getBody(String.class);
        Exception exception = exchange.getException();
        Boolean isExternalRedelivered = exchange.isExternalRedelivered();
        Boolean isFailed = exchange.isFailed();
        if(isFailed && exception != null){
            //TODO: Redelivery only at specific 5xx and maybe 408. More researched is needed. Check SXCSP-282
            //HttpHostConnectException might be raised from TC calls
            if(checkForHttp4xxFailedOperationAndJustLogWithNoGRedelivery
                    &&  exception.getClass().equals(HttpOperationFailedException.class)
                    && ((HttpOperationFailedException) exception).getStatusCode()>= 400
                    && ((HttpOperationFailedException) exception).getStatusCode()< 500){
                HttpOperationFailedException ef = (HttpOperationFailedException) exception;
                if(doNotLogErrorOnTheseStatusCodes!=null){
                    for(Integer code:doNotLogErrorOnTheseStatusCodes){
                        if(!code.equals(ef.getStatusCode())){
                            LOG.error("ENDPOINT " + uri + " responded with statusCode " + ef.getStatusCode() + " " + ef.getStatusText());
                        }
                    }
                }else {
                    LOG.error("ENDPOINT " + uri + " responded with statusCode " + ef.getStatusCode() + " " + ef.getStatusText());
                }
            }else {
                throw new CspBusinessException("Exception in external request: " + exception.getMessage(), exception);
                //exception.getClass().equals(ConnectException.class)
            }
        }
        return out;
    }

    private Exchange getExchange(Boolean isAsyncEnabled,String uri, byte[] data, String httpMethod, Map<String,Object> headers){
        Exchange ret = null;
        if(isAsyncEnabled){
            CompletableFuture<Exchange> exchangeCompletableFuture = producerTemplate.asyncSend(uri, exchange -> {
                exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                if(headers!=null){
                    for (Map.Entry<String, Object> entry : headers.entrySet()) {
                        exchange.getIn().setHeader(entry.getKey(), entry.getValue());
                    }

                }
                exchange.getIn().setBody(data);
            });

            try {
                ret = exchangeCompletableFuture.get();
            } catch (InterruptedException e) {
                LOG.error("Unrecoverable error occured.",e);
            } catch (ExecutionException e) {
                LOG.error("Unrecoverable error occured.",e);
            }
        }else{
            ret = producerTemplate.send(uri, exchange -> {
                exchange.getIn().setHeader(Exchange.HTTP_METHOD, httpMethod);
                exchange.getIn().setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                if(headers!=null){
                    for (Map.Entry<String, Object> entry : headers.entrySet()) {
                        exchange.getIn().setHeader(entry.getKey(), entry.getValue());
                    }

                }
                exchange.getIn().setBody(data);
            });
        }

        return ret;
    }

    public Exchange asyncSendInOnly(String uri, Object data,Map<String,Object> headers){
        Exchange futureExchange = null;
        CompletableFuture<Exchange> exchangeCompletableFuture = producerTemplate.asyncSend(uri, exchange -> {
            if(headers!=null){
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    exchange.getIn().setHeader(entry.getKey(), entry.getValue());
                }
            }
            exchange.setPattern(ExchangePattern.InOnly);
            exchange.getIn().setBody(data);
        });

        try {
            futureExchange = exchangeCompletableFuture.get();
        } catch (InterruptedException e) {
            LOG.error("Unrecoverable error occured.",e);
        } catch (ExecutionException e) {
            LOG.error("Unrecoverable error occured.",e);
        }
        return futureExchange;
    }
}
