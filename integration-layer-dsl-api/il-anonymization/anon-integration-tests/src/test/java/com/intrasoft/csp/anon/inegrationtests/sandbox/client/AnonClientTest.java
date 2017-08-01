package com.intrasoft.csp.anon.inegrationtests.sandbox.client;

import com.intrasoft.csp.anon.AnonApp;
import com.intrasoft.csp.anon.client.AnonClient;
import com.intrasoft.csp.anon.client.config.AnonClientConfig;

import com.intrasoft.csp.anon.commons.model.AnonContextUrl;
import com.intrasoft.csp.anon.commons.model.IntegrationAnonData;
import com.intrasoft.csp.anon.commons.model.IntegrationDataType;
import com.intrasoft.csp.libraries.restclient.config.CspRestTemplateConfiguration;
import com.intrasoft.csp.libraries.restclient.service.RetryRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AnonApp.class, AnonClientConfig.class, CspRestTemplateConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port: 8585",
                "anon.server.protocol: http",
                "anon.server.host: localhost",
                "anon.server.port: 8585",
                "api.version: 1",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "key.update=10000"
        })
@ActiveProfiles("mysql") //TODO: to be changed to use H2 DB profile
public class AnonClientTest implements AnonContextUrl {
    private static final Logger LOG = LoggerFactory.getLogger(AnonClientTest.class);

    @Autowired
    @Qualifier("AnonRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Autowired
    @Qualifier("anonClient")
    AnonClient anonClient;

    @Test
    public void sendPostIntegrationDataTest() throws IOException {
        IntegrationAnonData data = new IntegrationAnonData();
        data.setDataType(IntegrationDataType.TRUSTCIRCLE);
        data.setCspId("9");
        ResponseEntity<String> response = anonClient.postAnonData(data, DATA_ANONYMIZATION);
        //TODO: on an empty DB AnonException is expected
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
    }
}

