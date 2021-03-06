package com.intrasoft.csp.integration.sandbox.server.internal;

import com.intrasoft.csp.commons.constants.AppProperties;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.routes.CamelRoutes;
import com.intrasoft.csp.server.CspApp;
import com.intrasoft.csp.server.policy.domain.model.EvaluatedPolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.PolicyDTO;
import com.intrasoft.csp.server.policy.domain.model.SharingPolicyAction;
import com.intrasoft.csp.server.policy.service.SharingPolicyService;
import com.intrasoft.csp.server.processors.TcProcessor;
import com.intrasoft.csp.server.routes.RouteUtils;
import com.intrasoft.csp.server.service.CamelRestService;
import com.intrasoft.csp.server.utils.MockUtils;
import org.apache.camel.*;
import org.apache.camel.component.http.HttpMethods;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpointsAndSkip;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.contains;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CspApp.class, MockUtils.class},
        properties = {
                "spring.datasource.url:jdbc:h2:mem:csp_policy",
                "flyway.enabled:false",
                "server.name:CERT-GR",
                "csp.retry.backOffPeriod:10",
                "csp.retry.maxAttempts:1",
                "embedded.activemq.start:false",
                "apache.camel.use.activemq:false",
                "server.camel.rest.service.is.async:false" //make it sync for better handling in tests (gracefull shutdown etc.)
        })
@MockEndpointsAndSkip("http:*")
public class CspServerInternalSandboxTestFlow1dataTypes implements CamelRoutes {
    private static final Logger LOG = LoggerFactory.getLogger(CspServerInternalSandboxTest.class);

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DSL)
    private MockEndpoint mockedDsl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + DDL)
    private MockEndpoint mockedDdl;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + TC)
    private MockEndpoint mockedTC;

    //deprecated
    /*@EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+TCT)
    private MockEndpoint mockedTCT;*/

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX + ":" + DIRECT + ":" + ECSP)
    private MockEndpoint mockedEcsp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+ELASTIC)
    private MockEndpoint mockedElastic;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+APP)
    private MockEndpoint mockedApp;

    @EndpointInject(uri = CamelRoutes.MOCK_PREFIX+":"+DIRECT+":"+DCL)
    private MockEndpoint mockedDcl;

    @MockBean
    CamelRestService camelRestService;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    RouteUtils routes;

    @Autowired
    SpringCamelContext springCamelContext;

    @MockBean
    SharingPolicyService sharingPolicyService;

    @Autowired
    Environment env;

    @Autowired
    TcProcessor tcProcessor;

    @Rule
    public OutputCapture outputCapture = new OutputCapture();

    private Integer numOfCspsToTest = 3;
    private Integer currentCspId = 0;
    private HashMap<IntegrationDataType, Integer> internalApps = new HashMap<>();

    String elasticUri;
    String serverName;
    String applicationId = "taranis";
    String tcId = "tcId";
    String teamId = "teamId";
    String tcShortNameToTest = IntegrationDataType.CTC_CSP_SHARING;//default

    @Before
    public void init() throws Exception {
        this.outputCapture.flush();
        String tcIdArg = env.getProperty("extTcId");
        if(!StringUtils.isEmpty(tcIdArg)){
            tcId = tcIdArg;
        }

        String teamIdArg = env.getProperty("extTeamId");
        if(!StringUtils.isEmpty(teamIdArg)){
            teamId = teamIdArg;
        }

        String tcShortNameToTestArg = env.getProperty("tcShortNameToTest");
        if(!StringUtils.isEmpty(tcShortNameToTestArg)){
            tcShortNameToTest = tcShortNameToTestArg;
        }

        String dataObjectArg = env.getProperty("dataObject");
        if(!StringUtils.isEmpty(dataObjectArg)){
            mockUtils.setDataObjectToTest(dataObjectArg);
        }

        serverName = env.getProperty("server.name");
        mvc = webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        mockUtils.setSpringCamelContext(springCamelContext);


        String elasticProtocol = env.getProperty("elastic.protocol");
        String elasticHost= env.getProperty("elastic.host");
        String elasticPort= env.getProperty("elastic.port");
        String elasticPath= env.getProperty("elastic.path");

        elasticUri = elasticProtocol + "://" + elasticHost + ":" + elasticPort + elasticPath;


        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(DSL), mockedDsl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(DDL), mockedDdl.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(ECSP), mockedEcsp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(TC), mockedTC.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(ELASTIC), mockedElastic.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(APP), mockedApp.getEndpointUri());
        mockUtils.mockRoute(CamelRoutes.MOCK_PREFIX, routes.wrap(DCL), mockedDcl.getEndpointUri());

        //Initialize internalApps Hashmap according application.properties (internal section)
        internalApps.put(IntegrationDataType.THREAT, 1);
        internalApps.put(IntegrationDataType.ARTEFACT, 2);
        internalApps.put(IntegrationDataType.TRUSTCIRCLE, 1);

        Mockito.when(camelRestService.send(anyString(), anyObject(), eq("GET"), eq(Team.class)))
                .thenReturn(mockUtils.getMockedTeam(1, "http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(2, "http://external.csp%s.com"))
                .thenReturn(mockUtils.getMockedTeam(3, "http://external.csp%s.com"));

        Mockito.when(camelRestService.send(contains(elasticUri),anyObject(),anyString()))
                .thenReturn(mockUtils.getMockedElasticSearchResponse(2));

        EvaluatedPolicyDTO evaluatedPolicyDTO = new EvaluatedPolicyDTO();
        evaluatedPolicyDTO.setSharingPolicyAction(SharingPolicyAction.NO_ACTION_FOUND);
        PolicyDTO mockedPolicyDTO = new PolicyDTO();
        evaluatedPolicyDTO.setPolicyDTO(mockedPolicyDTO);
        Mockito.when(sharingPolicyService.evaluate(anyObject(),anyObject())).thenReturn(evaluatedPolicyDTO);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeThreatTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"misp",false, true, IntegrationDataType.THREAT, HttpMethods.POST.name());
        assertFlows("misp",IntegrationDataType.THREAT, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTcIdThreatTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"misp",tcId,null,false, true, IntegrationDataType.THREAT, HttpMethods.POST.name());
        assertFlows("misp",IntegrationDataType.THREAT, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdThreatTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"misp",null,teamId,false, true, IntegrationDataType.THREAT, HttpMethods.POST.name());
        assertFlows("misp",IntegrationDataType.THREAT, 1,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeThreatTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"misp",false, true, IntegrationDataType.THREAT, HttpMethods.PUT.name());
        assertFlows("misp",IntegrationDataType.THREAT, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdThreatTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"misp", tcId, null, false, true, IntegrationDataType.THREAT, HttpMethods.PUT.name());
        assertFlows("misp",IntegrationDataType.THREAT, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdThreatTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"misp", null, teamId, false, true, IntegrationDataType.THREAT, HttpMethods.PUT.name());
        assertFlows("misp",IntegrationDataType.THREAT, 1,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeArtefactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc,serverName, "viper",false, true, IntegrationDataType.ARTEFACT, HttpMethods.POST.name());
        assertFlows("viper",IntegrationDataType.ARTEFACT, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTcIdArtefactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc,serverName, "viper",tcId,null,false, true, IntegrationDataType.ARTEFACT, HttpMethods.POST.name());
        assertFlows("viper",IntegrationDataType.ARTEFACT, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdArtefactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc,serverName, "viper",null,teamId,false, true, IntegrationDataType.ARTEFACT, HttpMethods.POST.name());
        assertFlows("viper",IntegrationDataType.ARTEFACT, 1,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeArtefactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"viper",false, true, IntegrationDataType.ARTEFACT, "PUT");
        assertFlows("viper",IntegrationDataType.ARTEFACT, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdArtefactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"viper",tcId,null,false, true, IntegrationDataType.ARTEFACT, "PUT");
        assertFlows("viper",IntegrationDataType.ARTEFACT, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdArtefactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"viper",null,teamId,false, true, IntegrationDataType.ARTEFACT, "PUT");
        assertFlows("viper",IntegrationDataType.ARTEFACT, 1,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeTrustcircleTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertFlows("trustcircle",IntegrationDataType.TRUSTCIRCLE, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1TcIdTypeTrustcircleTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",tcId,null,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertFlows("trustcircle",IntegrationDataType.TRUSTCIRCLE, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdTrustcircleTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",null,teamId,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.POST.name());
        assertFlows("trustcircle",IntegrationDataType.TRUSTCIRCLE, 1,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeTrustcircleTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.PUT.name());
        assertFlows("trustcircle",IntegrationDataType.TRUSTCIRCLE, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdTrustcircleTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",tcId,null,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.PUT.name());
        assertFlows("trustcircle",IntegrationDataType.TRUSTCIRCLE, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdTrustcircleTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",null,teamId,false, true, IntegrationDataType.TRUSTCIRCLE, HttpMethods.PUT.name());
        assertFlows("trustcircle",IntegrationDataType.TRUSTCIRCLE, 1,false);
    }


    /**
     * Testing Contact data type
     * */

    @DirtiesContext
    @Test
    public void dslFlow1PostDataTypeContactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",false, true, IntegrationDataType.CONTACT, HttpMethods.POST.name());
        assertFlows("trustcircle",IntegrationDataType.CONTACT, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1TcIdTypeContactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",tcId,null,false, true, IntegrationDataType.CONTACT, HttpMethods.POST.name());
        assertFlows("trustcircle",IntegrationDataType.CONTACT, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PostTeamIdContactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",null,teamId,false, true, IntegrationDataType.CONTACT, HttpMethods.POST.name());
        assertFlows("trustcircle",IntegrationDataType.CONTACT, 1,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutDataTypeContactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",false, true, IntegrationDataType.CONTACT, HttpMethods.PUT.name());
        assertFlows("trustcircle",IntegrationDataType.CONTACT, 3,true);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTcIdContactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",tcId,null,false, true, IntegrationDataType.CONTACT, HttpMethods.PUT.name());
        assertFlows("trustcircle",IntegrationDataType.CONTACT, 3,false);
    }

    @DirtiesContext
    @Test
    public void dslFlow1PutTeamIdContactTest() throws Exception {
        mockitoWhen(HttpMethod.GET.name(),TrustCircle.class);
        mockUtils.sendFlow1Data(mvc, serverName,"trustcircle",null,teamId,false, true, IntegrationDataType.CONTACT, HttpMethods.PUT.name());
        assertFlows("trustcircle",IntegrationDataType.CONTACT, 1,false);
    }





//    @Deprecated
//    void mockitoWhen(String httpMethod, Class aClass, IntegrationDataType integrationDataType) throws IOException {
//        Mockito.when(camelRestService.sendAndGetList(anyString(), anyObject(), eq(httpMethod), eq(aClass), anyObject()))
//                .thenReturn(mockUtils.getAllMockedTrustCircles(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(integrationDataType)));
//
//        Mockito.when(camelRestService.send(anyString(), anyObject(), eq(httpMethod), eq(aClass)))
//                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, IntegrationDataType.tcNamingConventionForShortName.get(integrationDataType)));
//    }

    void mockitoWhen(String httpMethod, Class aClass) throws IOException {
        String urlShouldContain = tcProcessor.getTcCirclesURI();
        if(tcShortNameToTest.equalsIgnoreCase(IntegrationDataType.LTC_CSP_SHARING)){
            urlShouldContain = tcProcessor.getLocalCirclesURI();
        }
        Mockito.when(camelRestService.send(contains(urlShouldContain), anyObject(), eq(httpMethod), eq(aClass), anyObject()))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, tcShortNameToTest));

        Mockito.when(camelRestService.send(contains(urlShouldContain), anyObject(), eq(httpMethod), eq(aClass)))
                .thenReturn(mockUtils.getMockedTrustCircle(this.numOfCspsToTest, tcShortNameToTest));
    }

    private void assertFlows(String applicationId, IntegrationDataType dataType, Integer expectedEcspMessages, boolean assertTcShortName) throws Exception {
       /*
        DSL
         */
        //Expect 1-message
        mockedDsl.expectedMessageCount(1);
        mockedDsl.assertIsSatisfied();


        List<Exchange> list = mockedDsl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        /*
        APP
         */
        //Expect message count according to application.properties
        Integer expectedInternalAppsCount = mockUtils.getExpectedInternalAppsCount(applicationId,env.getProperty(AppProperties.INTERNAL+"."+dataType.name().toLowerCase()+".apps"));
        mockedApp.expectedMessageCount(expectedInternalAppsCount);
        mockedApp.assertIsSatisfied();

        list = mockedApp.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //DDL
        mockedDdl.expectedMessageCount(1);
        mockedDdl.assertIsSatisfied();

        list = mockedDdl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //DCL
        mockedDcl.expectedMessageCount(1);
        mockedDcl.assertIsSatisfied();

        list = mockedDcl.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //TC
        mockedTC.expectedMessageCount(1);
        mockedTC.assertIsSatisfied();

        list = mockedTC.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }

        //ESCP
        mockedEcsp.expectedMessageCount(expectedEcspMessages);
        mockedEcsp.assertIsSatisfied();

        list = mockedEcsp.getReceivedExchanges();
        int i=0;
        for (Exchange exchange : list) {
            i++;
            Message in = exchange.getIn();
            EnhancedTeamDTO enhancedTeamDTO = in.getBody(EnhancedTeamDTO.class);
            assertThat(enhancedTeamDTO.getTeam().getUrl(), is("http://external.csp"+i+".com"));
        }

        //ELASTIC
        if(IntegrationDataType.TRUSTCIRCLE.equals(dataType) || IntegrationDataType.CONTACT.equals(dataType)){
            mockedElastic.expectedMessageCount(0);
        }else {
            mockedElastic.expectedMessageCount(1);
        }
        mockedElastic.assertIsSatisfied();

        list = mockedElastic.getReceivedExchanges();
        for (Exchange exchange : list) {
            Message in = exchange.getIn();
            IntegrationData data = in.getBody(IntegrationData.class);
            assertThat(data.getDataType(), is(dataType));
        }



        if(assertTcShortName) {
            String output = this.outputCapture.toString();
            //assertTrue( output, output.contains("Using "+tcShortNameToTest+".."));
            assertThat(output, containsString("Using " +
                    (dataType.equals(IntegrationDataType.TRUSTCIRCLE)?IntegrationDataType.CTC_CSP_ALL:tcShortNameToTest) + ".."));
        }
    }

}