package com.intrasoft.csp.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.commons.apiHttpStatusResponse.HttpStatusResponseType;
import com.intrasoft.csp.commons.model.*;
import com.intrasoft.csp.commons.model.elastic.ElasticSearchResponse;
import com.intrasoft.csp.commons.model.elastic.search.Hit;
import com.intrasoft.csp.commons.model.elastic.search.Hits;
import com.intrasoft.csp.commons.routes.ContextUrl;
import com.intrasoft.csp.server.service.ApiDataHandler;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by iskitsas on 4/10/17.
 */
@Component
public class MockUtils implements ContextUrl {
    private static final Logger LOG = LoggerFactory.getLogger(MockUtils.class);

    SpringCamelContext springCamelContext;

    @Autowired
    ObjectMapper objectMapper;

    String dataObjectToTest;

    @Autowired
    MockUtils mockUtils;

    @Autowired
    ApiDataHandler apiDataHandler;

    Map<IntegrationDataType, String> dataObjectMap = new HashMap<>();

    @PostConstruct
    public void init(){
        dataObjectMap.put(IntegrationDataType.VULNERABILITY,vulnerabilityDataObject);
        dataObjectMap.put(IntegrationDataType.ARTEFACT,artefactDataObject);
        dataObjectMap.put(IntegrationDataType.THREAT,threatDataObject);
        dataObjectMap.put(IntegrationDataType.INCIDENT,incidentDataObject);
        dataObjectMap.put(IntegrationDataType.TRUSTCIRCLE,"{}");
        dataObjectMap.put(IntegrationDataType.CONTACT,"{}");
    }

    String vulnerabilityDataObject = "{\n\"xml_advisory\": {\n    \"meta_info\": {\n      \"system_information\": {\n        \"systemdetail\": {\n          \"affected_products_text\": \"Apple iTunes\",\n          \"affected_platform\": {\n            \"platform\": {\n              \"producer\": \"Microsoft\",\n              \"name\": \"Windows\",\n              \"version\": \"10\"\n            }\n          },\n          \"affected_products_versions_text\": \"11\",\n          \"affected_platforms_text\": \"Microsoft Windows 10\",\n          \"affected_product\": {\n            \"product\": {\n              \"producer\": \"Apple\",\n              \"name\": \"iTunes\",\n              \"version\": \"11\"\n            }\n          }\n        }\n      },\n      \"probability\": \"high\",\n      \"version_history\": {\n        \"version_instance\": { \"version\": \"1.00\" }\n      },\n      \"title\": \"Advisory regarding recent Taranis 3.3.3 vulnerability\",\n      \"taranis_version\": \"3.0\",\n      \"vulnerability_effect\": {\n         \n      },\n      \"damage\": \"high\",\n      \"issuer\": \"Taranis\",\n      \"availability\": \"https://kennisbank.ncsc.nl/\",\n      \"reference_number\": \"Taranis-2017-XXXX\",\n      \"vulnerability_identifiers\": {\n        \"cve\": { \"id\": \"CVE-2017-7578\" }\n      }\n    },\n    \"rating\": {\n      \"publisher_analysis\": {\n        \"ques_pro_expect\": \"3\",\n        \"ques_pro_exploit\": \"6\",\n        \"ques_pro_complexity\": \"3\",\n        \"ques_pro_access\": \"6\",\n        \"ques_pro_details\": \"2\",\n        \"ques_pro_solution\": \"3\",\n        \"ques_dmg_privesc\": \"1\",\n        \"ques_pro_standard\": \"3\",\n        \"ques_dmg_infoleak\": \"2\",\n        \"ques_pro_credent\": \"2\",\n        \"ques_pro_userint\": \"3\",\n        \"ques_dmg_remrights\": \"1\",\n        \"ques_pro_exploited\": \"3\",\n        \"ques_dmg_dos\": \"1\",\n        \"ques_dmg_codeexec\": \"2\"\n      }\n    },\n    \"content\": {\n      \"additional_resources\": { \"resource\": \"http://cve.mitre.org/cve/\" },\n      \"solution\": \"Uninstall iTunes, Taranis and MacOS.\",\n      \"disclaimer\": \"Door gebruik van deze security advisory gaat u akkoord met de navolgende voorwaarden.\",\n      \"abstract\": \"Taranis 3.3.3 when combined with Itunes 11 under Windows 10 cause the Safari browser to crush.\",\n      \"consequences\": \"The computer may freeze.\",\n      \"description\": \"Taranis 3.3.3 when combined with Itunes 11 under Windows 10 cause the Safari browser to crush.\"\n    }\n  }\n}";
    String threatDataObject = "{\n    \"response\":[{\n      \"Event\": {\n        \"id\": \"643\",\n        \"orgc_id\": \"8\",\n        \"org_id\": \"1\",\n        \"date\": \"2017-05-12\",\n        \"threat_level_id\": \"1\",\n        \"info\": \"Ransomware spreading through SMB attacking multiple companies\",\n        \"published\": true,\n        \"uuid\": \"5915b22e-c3e8-4f13-9449-7f3fc0a80a8e\",\n        \"attribute_count\": \"64\",\n        \"analysis\": \"1\",\n        \"timestamp\": \"1494853808\",\n        \"distribution\": \"3\",\n        \"proposal_email_lock\": false,\n        \"locked\": false,\n        \"publish_timestamp\": \"1496300557\",\n        \"sharing_group_id\": \"0\",\n        \"disable_correlation\": false,\n        \"Org\": {\n          \"id\": \"1\",\n          \"name\": \"MISP\",\n          \"uuid\": \"56ef3277-1ad4-42f6-b90b-04e5c0a83832\"\n        },\n        \"Orgc\": {\n          \"id\": \"8\",\n          \"name\": \"INCIBE\",\n          \"uuid\": \"56fa4fe4-f528-4480-8332-1ba3c0a80a8c\"\n        },\n        \"Attribute\": [\n          {\n            \"id\": \"158560\",\n            \"type\": \"link\",\n            \"category\": \"External analysis\",\n            \"to_ids\": false,\n            \"uuid\": \"5915b3c2-fcc0-49fb-be03-7ed3c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494853795\",\n            \"comment\": \"ed01ebfbc9eb5bbea545af4d01bf5f1071661840480439c6e5babe8e080e41aa\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"https:\\/\\/www.bleepingcomputer.com\\/news\\/security\\/telefonica-tells-employees-to-shut-down-computers-amid-massive-ransomware-outbreak\\/\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158561\",\n            \"type\": \"link\",\n            \"category\": \"External analysis\",\n            \"to_ids\": false,\n            \"uuid\": \"5915b3e4-5928-485f-9795-565fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494853804\",\n            \"comment\": \"\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"https:\\/\\/www.hybrid-analysis.com\\/sample\\/ed01ebfbc9eb5bbea545af4d01bf5f1071661840480439c6e5babe8e080e41aa?environmentId=100\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158562\",\n            \"type\": \"comment\",\n            \"category\": \"Network activity\",\n            \"to_ids\": false,\n            \"uuid\": \"5915b926-baf4-4bc1-b930-7f3ec0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494634206\",\n            \"comment\": \"\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"Performs connections to tor network\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158563\",\n            \"type\": \"md5\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b282-0bb4-4057-ab3a-7ed3c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594178\",\n            \"comment\": \"taskdl.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"4fef5e34143e646dbf9907c4374276f5\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158564\",\n            \"type\": \"md5\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b30b-6f00-433e-9c26-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594315\",\n            \"comment\": \"taskse.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"8495400f199ac77853c53b5a3f278f3e\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158565\",\n            \"type\": \"sha1\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b282-b5a4-448f-ba81-7ed3c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594178\",\n            \"comment\": \"taskdl.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"47a9ad4125b6bd7c55e4e7da251e23f089407b8f\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158566\",\n            \"type\": \"sha1\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b30b-b388-4106-b603-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594315\",\n            \"comment\": \"taskse.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"be5d6279874da315e3080b06083757aad9b32c23\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158567\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b282-27a8-4aa2-b550-7ed3c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594178\",\n            \"comment\": \"taskdl.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"4a468603fdcb7a2eb5770705898cf9ef37aade532a7964642ecd705a74794b79\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158568\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b2f7-7298-4fa9-af0b-557ec0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594295\",\n            \"comment\": \"wannacry.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"ed01ebfbc9eb5bbea545af4d01bf5f1071661840480439c6e5babe8e080e41aa\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158569\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b30c-5670-438a-81ad-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594316\",\n            \"comment\": \"taskse.exe\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"2ca2d550e603d74dedda03156023135b38da3630cb014e3d00b1263358c5f00d\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158570\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5915b33e-bf0c-49c0-bdf9-5582c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494594366\",\n            \"comment\": \"u.wnry\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"b9c5d4339809e0ad9a00d4d3dd26fdf44a32819a54abf846bb9b560d81391c25\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158571\",\n            \"type\": \"mutex\",\n            \"category\": \"Artifacts dropped\",\n            \"to_ids\": true,\n            \"uuid\": \"59164ac8-180c-419c-bf20-0387c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494633160\",\n            \"comment\": \"https:\\/\\/twitter.com\\/gN3mes1s\\/status\\/863149075159543808\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"MsWinZonesCacheCounterMutexA\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158572\",\n            \"type\": \"link\",\n            \"category\": \"External analysis\",\n            \"to_ids\": false,\n            \"uuid\": \"59164b00-ea34-4a56-b2e3-7f3ec0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494853808\",\n            \"comment\": \"\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"https:\\/\\/gist.github.com\\/rain-1\\/989428fa5504f378b993ee6efbc0b168\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158573\",\n            \"type\": \"domain\",\n            \"category\": \"Network activity\",\n            \"to_ids\": true,\n            \"uuid\": \"59164b98-41d4-4fa5-85d4-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494633368\",\n            \"comment\": \"C&C tor servers - https:\\/\\/twitter.com\\/hackerfantastic\\/status\\/863115568181850113\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"gx7ekbenv2riucmf.onion\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158574\",\n            \"type\": \"domain\",\n            \"category\": \"Network activity\",\n            \"to_ids\": true,\n            \"uuid\": \"59164b98-4350-4c3e-a5a2-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494633368\",\n            \"comment\": \"C&C tor servers - https:\\/\\/twitter.com\\/hackerfantastic\\/status\\/863115568181850113\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"57g7spgrzlojinas.onion\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158575\",\n            \"type\": \"domain\",\n            \"category\": \"Network activity\",\n            \"to_ids\": true,\n            \"uuid\": \"59164b99-5768-454a-b81b-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494633369\",\n            \"comment\": \"C&C tor servers - https:\\/\\/twitter.com\\/hackerfantastic\\/status\\/863115568181850113\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"xxlvbrloxvriy2c5.onion\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158576\",\n            \"type\": \"domain\",\n            \"category\": \"Network activity\",\n            \"to_ids\": true,\n            \"uuid\": \"59164b99-d354-4572-8500-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494633369\",\n            \"comment\": \"C&C tor servers - https:\\/\\/twitter.com\\/hackerfantastic\\/status\\/863115568181850113\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"76jdd2ir2embyv47.onion\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158577\",\n            \"type\": \"domain\",\n            \"category\": \"Network activity\",\n            \"to_ids\": true,\n            \"uuid\": \"59164b99-d7f0-4703-87c8-7f3fc0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494633369\",\n            \"comment\": \"C&C tor servers - https:\\/\\/twitter.com\\/hackerfantastic\\/status\\/863115568181850113\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"cwwnhwhlz52maqm7.onion\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158578\",\n            \"type\": \"yara\",\n            \"category\": \"Artifacts dropped\",\n            \"to_ids\": true,\n            \"uuid\": \"59178ab4-6504-46fc-9ff1-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494715059\",\n            \"comment\": \"https:\\/\\/www.us-cert.gov\\/ncas\\/alerts\\/TA17-132A\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"rule Wanna_Cry_Ransomware_Generic {\\r\\n       meta:\\r\\n              description = \\\"Detects WannaCry Ransomware on disk and in virtual page\\\"\\r\\n              author = \\\"US-CERT Code Analysis Team\\\"\\r\\n              reference = \\\"not set\\\"                                        \\r\\n              date = \\\"2017\\/05\\/12\\\"\\r\\n              hash0 = \\\"4DA1F312A214C07143ABEEAFB695D904\\\"\\r\\n      \\r\\n       strings:\\r\\n              $s0 = {410044004D0049004E0024}\\r\\n              $s1 = \\\"WannaDecryptor\\\"\\r\\n              $s2 = \\\"WANNACRY\\\"\\r\\n              $s3 = \\\"Microsoft Enhanced RSA and AES Cryptographic\\\"\\r\\n              $s4 = \\\"PKS\\\"\\r\\n              $s5 = \\\"StartTask\\\"\\r\\n              $s6 = \\\"wcry@123\\\"\\r\\n              $s7 = {2F6600002F72}\\r\\n              $s8 = \\\"unzip 0.15 Copyrigh\\\"\\r\\n\\r\\n       condition:\\r\\n              $s0 and $s1 and $s2 and $s3 or $s4 or $s5 or $s6 or $s7 or $s8\\r\\n}\\r\\n\\r\\n\\/*The following Yara ruleset is under the GNU-GPLv2 license (http:\\/\\/www.gnu.org\\/licenses\\/gpl-2.0.html) \\r\\nand open to any user or organization, as long as you use it under this license.*\\/\\r\\nrule MS17_010_WanaCry_worm {\\r\\n       meta:\\r\\n              description = \\\"Worm exploiting MS17-010 and dropping WannaCry Ransomware\\\"\\r\\n              author = \\\"Felipe Molina (@felmoltor)\\\"\\r\\n              reference = \\\"https:\\/\\/www.exploit-db.com\\/exploits\\/41987\\/\\\"\\r\\n              date = \\\"2017\\/05\\/12\\\"\\r\\n\\r\\n       strings:\\r\\n              $ms17010_str1=\\\"PC NETWORK PROGRAM 1.0\\\"\\r\\n              $ms17010_str2=\\\"LANMAN1.0\\\"\\r\\n              $ms17010_str3=\\\"Windows for Workgroups 3.1a\\\"\\r\\n              $ms17010_str4=\\\"__TREEID__PLACEHOLDER__\\\"\\r\\n              $ms17010_str5=\\\"__USERID__PLACEHOLDER__\\\"\\r\\n              $wannacry_payload_substr1 = \\\"h6agLCqPqVyXi2VSQ8O6Yb9ijBX54j\\\"\\r\\n              $wannacry_payload_substr2 = \\\"h54WfF9cGigWFEx92bzmOd0UOaZlM\\\"\\r\\n              $wannacry_payload_substr3 = \\\"tpGFEoLOU6+5I78Toh\\/nHs\\/RAP\\\"\\r\\n\\r\\n       condition:\\r\\n              all of them\\r\\n}\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158579\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867d-0130-4055-b361-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713981\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"176641494574290.bat\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158580\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867d-caf8-4e4c-8b5d-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713981\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"@Please_Read_Me@.txt\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158581\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867d-1fac-4084-bb8b-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713981\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"@WanaDecryptor@.exe\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158582\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867d-ad24-4b97-a77a-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713981\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"@WanaDecryptor@.exe.lnk\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158583\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867d-8cb8-4905-93f0-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713981\",\n            \"comment\": \"(Older variant) - https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"Please Read Me!.txt\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158584\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-db5c-4ca5-8aa8-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"%WINDIR%\\\\tasksche.exe\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158585\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-b8b4-456a-9098-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"%WINDIR%\\\\qeriuwjhrf\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158586\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-9c90-4715-ae88-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"131181494299235.bat\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158587\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-4b3c-47f8-978a-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"217201494590800.bat\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158588\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": false,\n            \"uuid\": \"5917867e-bf70-410e-a68c-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"[0-9]{15}.bat\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158589\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-6ebc-425a-beae-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"!WannaDecryptor!.exe.lnk\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158590\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-6488-42f1-abb6-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"00000000.pky\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158591\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-8648-438d-9087-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"00000000.eky\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158592\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-ad3c-48eb-afa9-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"00000000.res\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158593\",\n            \"type\": \"filename\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867e-72fc-4114-b3f2-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713982\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"%WINDIR%\\\\system32\\\\taskdl.exe\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158594\",\n            \"type\": \"md5\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917867d-791c-4fd8-a73e-43f4c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713981\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"fefe6b30d0819f1a1775e14730a10e0e\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158595\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-9470-43b7-acbe-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"85ce324b8f78021ecfc9b811c748f19b82e61bb093ff64f2eab457f9ef19b186\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158596\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-f5a4-4f64-bfb0-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"3f3a9dde96ec4107f67b0559b4e95f5f1bca1ec6cb204bfe5fea0230845e8301\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158597\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917858e-99d8-458d-96cb-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713742\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"dff26a9a44baa3ce109b8df41ae0a301d9e4a28ad7bd7721bbb7ccd137bfd696\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158598\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917858f-5e10-4534-b16f-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713743\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"201f42080e1c989774d05d5b127a8cd4b4781f1956b78df7c01112436c89b2c9\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158599\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917858f-9c64-47de-8999-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713743\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"c365ddaa345cfcaff3d629505572a484cff5221933d68e4a52130b8bb7badaf9\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158600\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917858f-c9d4-4db1-9950-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713743\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"09a46b3e1be080745a6d8d88d6b5bd351b1c7586ae0dc94d0c238ee36421cafa\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158601\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917858f-e220-4492-a1e2-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713743\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"aae9536875784fe6e55357900519f97fee0a56d6780860779a36f06765243d56\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158602\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-2db8-432a-8ca9-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"21ed253b796f63b9e95b4e426a82303dfac5bf8062bfe669995bde2208b360fd\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158603\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-10a8-4cc1-927b-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"2372862afaa8e8720bc46f93cb27a9b12646a7cbc952cc732b8f5df7aebb2450\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158604\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-80e0-4c92-a255-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"24d004a104d4d54034dbcffc2a4b19a11f39008a575aa614ea04703480b1022c\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158605\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-b68c-4f8c-8b10-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"f8812f1deb8001f3b7672b6fc85640ecb123bc2304b563728e6235ccbe782d85\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158606\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-2638-4f4e-b40e-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"4b76e54de0243274f97430b26624c44694fbde3289ed81a160e0754ab9f56f32\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158607\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-f04c-46b6-97db-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"9cc32c94ce7dc6e48f86704625b6cdc0fda0d2cd7ad769e4d0bb1776903e5a13\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158608\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-7ea0-4bfa-abb8-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"78e3f87f31688355c0f398317b2d87d803bd87ee3656c5a7c80f0561ec8606df\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158609\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178590-8f04-4e97-80dd-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713744\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"be22645c61949ad6a077373a7d6cd85e3fae44315632f161adc4c99d5a8e6844\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158610\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-0ed0-4445-be26-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"5d26835be2cf4f08f2beeff301c06d05035d0a9ec3afacc71dff22813595c0b9\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158611\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-0268-488a-afa9-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"76a3666ce9119295104bb69ee7af3f2845d23f40ba48ace7987f79b06312bbdf\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158612\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-f96c-4d4b-b388-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"fc626fe1e0f4d77b34851a8c60cdd11172472da3b9325bfe288ac8342f6c710a\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158613\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-21c8-4d66-92c2-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"eeb9cd6a1c4b3949b2ff3134a77d6736b35977f951b9c7c911483b5caeb1c1fb\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158614\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-3e7c-4c80-ae7b-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"043e0d0d8b8cda56851f5b853f244f677bd1fd50f869075ef7ba1110771f70c2\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158615\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-5aa8-455e-8ebc-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"57c12d8573d2f3883a8a0ba14e3eec02ac1c61dee6b675b6c0d16e221c3777f4\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158616\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"5917859e-96a0-47c6-8a1b-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713758\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"ca29de1dc8817868c93e54b09f557fe14e40083c0955294df5bd91f52ba469c8\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158617\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-0430-4db2-9490-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"f7c7b5e4b051ea5bd0017803f40af13bed224c4b0fd60b890b6784df5bd63494\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158618\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-da0c-494e-b6da-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"3e6de9e2baacf930949647c399818e7a2caea2626df6a468407854aaa515eed9\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158619\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-9514-44a9-8dbb-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"9b60c622546dc45cca64df935b71c26dcf4886d6fa811944dbc4e23db9335640\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158620\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-ed1c-4e2b-945d-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"5ad4efd90dcde01d26cc6f32f7ce3ce0b4d4951d4b94a19aa097341aff2acaec\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158621\",\n            \"type\": \"sha256\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"591785a7-22a8-42e2-be59-43f2c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494713767\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"12d67c587e114d8dde56324741a8f04fb50cc3160653769b8015bc5aec64d20b\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158622\",\n            \"type\": \"yara\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59178759-a46c-4cd8-9548-43f3c0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494714195\",\n            \"comment\": \"https:\\/\\/securingtomorrow.mcafee.com\\/executive-perspectives\\/analysis-wannacry-ransomware-outbreak\\/\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"rule wannacry_1 : ransom\\r\\n{\\r\\n    meta:\\r\\n        author = \\\"Joshua Cannell\\\"\\r\\n        description = \\\"WannaCry Ransomware strings\\\"\\r\\n        weight = 100\\r\\n        date = \\\"2017-05-12\\\"\\r\\n     \\r\\n    Strings:\\r\\n        $s1 = \\\"Ooops, your files have been encrypted!\\\" wide ascii nocase\\r\\n        $s2 = \\\"Wanna Decryptor\\\" wide ascii nocase\\r\\n        $s3 = \\\".wcry\\\" wide ascii nocase\\r\\n        $s4 = \\\"WANNACRY\\\" wide ascii nocase\\r\\n        $s5 = \\\"WANACRY!\\\" wide ascii nocase\\r\\n        $s7 = \\\"icacls . \\/grant Everyone:F \\/T \\/C \\/Q\\\" wide ascii nocase\\r\\n     \\r\\n    Condition:\\r\\n        any of them\\r\\n}\\r\\n\\r\\nrule wannacry_2\\r\\n{\\r\\n    meta:\\r\\n        author = \\\"Harold Ogden\\\"\\r\\n        description = \\\"WannaCry Ransomware Strings\\\"\\r\\n        date = \\\"2017-05-12\\\"\\r\\n        weight = 100\\r\\n\\r\\n    strings:\\r\\n        $string1 = \\\"msg\\/m_bulgarian.wnry\\\"\\r\\n        $string2 = \\\"msg\\/m_chinese (simplified).wnry\\\"\\r\\n        $string3 = \\\"msg\\/m_chinese (traditional).wnry\\\"\\r\\n        $string4 = \\\"msg\\/m_croatian.wnry\\\"\\r\\n        $string5 = \\\"msg\\/m_czech.wnry\\\"\\r\\n        $string6 = \\\"msg\\/m_danish.wnry\\\"\\r\\n        $string7 = \\\"msg\\/m_dutch.wnry\\\"\\r\\n        $string8 = \\\"msg\\/m_english.wnry\\\"\\r\\n        $string9 = \\\"msg\\/m_filipino.wnry\\\"\\r\\n        $string10 = \\\"msg\\/m_finnish.wnry\\\"\\r\\n        $string11 = \\\"msg\\/m_french.wnry\\\"\\r\\n        $string12 = \\\"msg\\/m_german.wnry\\\"\\r\\n        $string13 = \\\"msg\\/m_greek.wnry\\\"\\r\\n        $string14 = \\\"msg\\/m_indonesian.wnry\\\"\\r\\n        $string15 = \\\"msg\\/m_italian.wnry\\\"\\r\\n        $string16 = \\\"msg\\/m_japanese.wnry\\\"\\r\\n        $string17 = \\\"msg\\/m_korean.wnry\\\"\\r\\n        $string18 = \\\"msg\\/m_latvian.wnry\\\"\\r\\n        $string19 = \\\"msg\\/m_norwegian.wnry\\\"\\r\\n        $string20 = \\\"msg\\/m_polish.wnry\\\"\\r\\n        $string21 = \\\"msg\\/m_portuguese.wnry\\\"\\r\\n        $string22 = \\\"msg\\/m_romanian.wnry\\\"\\r\\n        $string23 = \\\"msg\\/m_russian.wnry\\\"\\r\\n        $string24 = \\\"msg\\/m_slovak.wnry\\\"\\r\\n        $string25 = \\\"msg\\/m_spanish.wnry\\\"\\r\\n        $string26 = \\\"msg\\/m_swedish.wnry\\\"\\r\\n        $string27 = \\\"msg\\/m_turkish.wnry\\\"\\r\\n        $string28 = \\\"msg\\/m_vietnamese.wnry\\\"\\r\\n\\r\\n    condition:\\r\\n        any of ($string*)\\r\\n}\",\n            \"ShadowAttribute\": []\n          },\n          {\n            \"id\": \"158623\",\n            \"type\": \"yara\",\n            \"category\": \"Payload delivery\",\n            \"to_ids\": true,\n            \"uuid\": \"59176758-fcc4-4fab-849a-44dec0a80a8e\",\n            \"event_id\": \"643\",\n            \"distribution\": \"5\",\n            \"timestamp\": \"1494706008\",\n            \"comment\": \"https:\\/\\/pastebin.com\\/FKgEjYHu\",\n            \"sharing_group_id\": \"0\",\n            \"deleted\": false,\n            \"disable_correlation\": false,\n            \"value\": \"\\/*\\r\\nFour YARA rules to check for payloads on systems. Thanks to sinkholing, encyrption may not occur, BUT you may still have binaries lying around.\\r\\nIf you get a match for \\\"WannaDecryptor\\\" and not for Wanna_Sample, then you may have a variant!\\r\\n\\r\\nCheck out http:\\/\\/yara.readthedocs.io on how to write and add a rule as below and index your\\r\\nrule by the sample hashes.  Add, share, rinse and repeat!\\r\\n*\\/\\r\\n\\r\\nrule WannaDecryptor: WannaDecryptor\\r\\n{\\r\\n        meta:\\r\\n                description = \\\"Detection for common strings of WannaDecryptor\\\"\\r\\n\\r\\n        strings:\\r\\n                $id1 = \\\"taskdl.exe\\\"\\r\\n                $id2 = \\\"taskse.exe\\\"\\r\\n                $id3 = \\\"r.wnry\\\"\\r\\n                $id4 = \\\"s.wnry\\\"\\r\\n                $id5 = \\\"t.wnry\\\"\\r\\n                $id6 = \\\"u.wnry\\\"\\r\\n                $id7 = \\\"msg\\/m_\\\"\\r\\n\\r\\n        condition:\\r\\n                3 of them\\r\\n}\\r\\nrule Wanna_Sample_84c82835a5d21bbcf75a61706d8ab549: Wanna_Sample_84c82835a5d21bbcf75a61706d8ab549\\r\\n{\\r\\n        meta:\\r\\n                description = \\\"Specific sample match for WannaCryptor\\\"\\r\\n                MD5 = \\\"84c82835a5d21bbcf75a61706d8ab549\\\"\\r\\n                SHA1 = \\\"5ff465afaabcbf0150d1a3ab2c2e74f3a4426467\\\"\\r\\n                SHA256 = \\\"ed01ebfbc9eb5bbea545af4d01bf5f1071661840480439c6e5babe8e080e41aa\\\"\\r\\n                INFO = \\\"Looks for 'taskdl' and 'taskse' at known offsets\\\"\\r\\n\\r\\n        strings:\\r\\n                $taskdl = { 00 74 61 73 6b 64 6c }\\r\\n                $taskse = { 00 74 61 73 6b 73 65 }\\r\\n\\r\\n        condition:\\r\\n                $taskdl at 3419456 and $taskse at 3422953\\r\\n}\\r\\nrule Wanna_Sample_4da1f312a214c07143abeeafb695d904: Wanna_Sample_4da1f312a214c07143abeeafb695d904\\r\\n{\\r\\n        meta:\\r\\n                description = \\\"Specific sample match for WannaCryptor\\\"\\r\\n                MD5 = \\\"4da1f312a214c07143abeeafb695d904\\\"\\r\\n                SHA1 = \\\"b629f072c9241fd2451f1cbca2290197e72a8f5e\\\"\\r\\n                SHA256 = \\\"aee20f9188a5c3954623583c6b0e6623ec90d5cd3fdec4e1001646e27664002c\\\"\\r\\n                INFO = \\\"Looks for offsets of r.wry and s.wry instances\\\"\\r\\n\\r\\n        strings:\\r\\n                $rwnry = { 72 2e 77 72 79 }\\r\\n                $swnry = { 73 2e 77 72 79 }\\r\\n\\r\\n        condition:\\r\\n                $rwnry at 88195 and $swnry at 88656 and $rwnry at 4495639\\r\\n}\\r\\nrule NHS_Strain_Wanna: NHS_Strain_Wanna\\r\\n{\\r\\n        meta:\\r\\n                description = \\\"Detection for worm-strain bundle of Wcry, DOublePulsar\\\"\\r\\n                MD5 = \\\"db349b97c37d22f5ea1d1841e3c89eb4\\\"\\r\\n                SHA1 = \\\"e889544aff85ffaf8b0d0da705105dee7c97fe26\\\"\\r\\n                SHA256 = \\\"24d004a104d4d54034dbcffc2a4b19a11f39008a575aa614ea04703480b1022c\\\"\\r\\n                INFO = \\\"Looks for specific offsets of c.wnry and t.wnry strings\\\"\\r\\n\\r\\n        strings:\\r\\n                $cwnry = { 63 2e 77 6e 72 79 }\\r\\n                $twnry = { 74 2e 77 6e 72 79 }\\r\\n\\r\\n        condition:\\r\\n                $cwnry at 262324 and $twnry at 267672 and $cwnry at 284970\\r\\n}\",\n            \"ShadowAttribute\": []\n          }\n        ],\n        \"ShadowAttribute\": [],\n        \"RelatedEvent\": [\n          {\n            \"Event\": {\n              \"id\": \"633\",\n              \"date\": \"2017-05-14\",\n              \"threat_level_id\": \"2\",\n              \"info\": \"OSINT - Alert (TA17-132A) Indicators Associated With WannaCry Ransomware\",\n              \"published\": true,\n              \"uuid\": \"59186a46-6d0c-4359-a644-c061950d210f\",\n              \"analysis\": \"2\",\n              \"timestamp\": \"1494773442\",\n              \"distribution\": \"3\",\n              \"org_id\": \"1\",\n              \"orgc_id\": \"2\",\n              \"Org\": {\n                \"id\": \"1\",\n                \"name\": \"MISP\",\n                \"uuid\": \"56ef3277-1ad4-42f6-b90b-04e5c0a83832\"\n              },\n              \"Orgc\": {\n                \"id\": \"2\",\n                \"name\": \"CIRCL\",\n                \"uuid\": \"55f6ea5e-2c60-40e5-964f-47a8950d210f\"\n              }\n            }\n          },\n          {\n            \"Event\": {\n              \"id\": \"620\",\n              \"date\": \"2017-05-13\",\n              \"threat_level_id\": \"2\",\n              \"info\": \"OSINT -  Player 3 Has Entered the Game: Say Hello to 'WannaCry'\",\n              \"published\": true,\n              \"uuid\": \"5916cc1f-cb18-4db1-b4f4-a535950d210f\",\n              \"analysis\": \"2\",\n              \"timestamp\": \"1494772885\",\n              \"distribution\": \"3\",\n              \"org_id\": \"1\",\n              \"orgc_id\": \"2\",\n              \"Org\": {\n                \"id\": \"1\",\n                \"name\": \"MISP\",\n                \"uuid\": \"56ef3277-1ad4-42f6-b90b-04e5c0a83832\"\n              },\n              \"Orgc\": {\n                \"id\": \"2\",\n                \"name\": \"CIRCL\",\n                \"uuid\": \"55f6ea5e-2c60-40e5-964f-47a8950d210f\"\n              }\n            }\n          },\n          {\n            \"Event\": {\n              \"id\": \"618\",\n              \"date\": \"2017-05-12\",\n              \"threat_level_id\": \"3\",\n              \"info\": \"OSINT - Massive outbreak of ransomware variant infects large amounts of computers around the world\",\n              \"published\": true,\n              \"uuid\": \"5916342f-3134-435f-9ce2-48e802de0b81\",\n              \"analysis\": \"2\",\n              \"timestamp\": \"1494772913\",\n              \"distribution\": \"3\",\n              \"org_id\": \"1\",\n              \"orgc_id\": \"2\",\n              \"Org\": {\n                \"id\": \"1\",\n                \"name\": \"MISP\",\n                \"uuid\": \"56ef3277-1ad4-42f6-b90b-04e5c0a83832\"\n              },\n              \"Orgc\": {\n                \"id\": \"2\",\n                \"name\": \"CIRCL\",\n                \"uuid\": \"55f6ea5e-2c60-40e5-964f-47a8950d210f\"\n              }\n            }\n          }\n        ],\n        \"Galaxy\": [],\n        \"Tag\": [\n          {\n            \"id\": \"4\",\n            \"name\": \"malware_classification:malware-category=\\\"Ransomware\\\"\",\n            \"colour\": \"#2c4f00\",\n            \"exportable\": true,\n            \"hide_tag\": false\n          },\n          {\n            \"id\": \"18\",\n            \"name\": \"circl:incident-classification=\\\"vulnerability\\\"\",\n            \"colour\": \"#478f00\",\n            \"exportable\": true,\n            \"hide_tag\": false\n          },\n          {\n            \"id\": \"2\",\n            \"name\": \"tlp:white\",\n            \"colour\": \"#ffffff\",\n            \"exportable\": true,\n            \"hide_tag\": false\n          },\n          {\n            \"id\": \"124\",\n            \"name\": \"misp-galaxy:ransomware=\\\"WannaCry\\\"\",\n            \"colour\": \"#0088cc\",\n            \"exportable\": true,\n            \"hide_tag\": false\n          }\n        ]\n      }\n    }]}";
    String artefactDataObject = "{\n    \"default\":\n    {\n      \"sha1\":\"13da502ab0d75daca5e5075c60e81bfe3b7a637f\",\n      \"name\":\"darkcomet.exe\",\n      \"tags\":[\n        \"rat\",\n        \"darkcomet\"\n      ],\n      \"sha512\":\"7e81e0c4f49f1884ebebdf6e53531e7836721c2ae41729cf5bc0340f3369e7d37fe4168a7434b2b0420b299f5c1d9a4f482f1bda8e66e40345757d97e5602b2d\",\n      \"created_at\":\"2015-03-30 23:13:20.595238\",\n      \"crc32\":\"2238B48E\",\n      \"ssdeep\":\"12288:D9HFJ9rJxRX1uVVjoaWSoynxdO1FVBaOiRZTERfIhNkNCCLo9Ek5C/hlg:NZ1xuVVjfFoynPaVBUR8f+kN10EB/g\",\n      \"sha256\":\"2d79fcc6b02a2e183a0cb30e0e25d103f42badda9fbf86bbee06f93aa3855aff\",\n      \"type\":\"PE32 executable (GUI) Intel 80386, for MS Windows\",\n      \"id\":10,\n      \"md5\":\"9f2520a3056543d49bb0f822d85ce5dd\",\n      \"size\":774144\n    },\n    \"analysis\":[\n      {\n        \"module\" : \"virustotal\",\n        \"report\" : \"Antiy-AVL | Trojan/Generic.ASMalwGH.1\"\n      },\n      {\n        \"module\" : \"office\",\n        \"report\" : \"Not a valid office document\"\n      },\n      {\n        \"module\" : \"idx\",\n        \"report\" : \"Invalid IDX header found\"\n      }\n    ]\n  }";
    String incidentDataObject = "{\n    \"incident\":\n    {\n      \"event\": {\n        \"classification.identifier\": \"heartbleed\",\n        \"classification.taxonomy\": \"attack on the critical infrastructure\",\n        \"classification.type\": \"command and control servers for example\",\n        \"comment\": \"Very serious issue\",\n        \"destination.abuse_contact\": \"Mr Bill Gates\",\n        \"destination.account\": \"abuse@microsoft.com\",\n        \"destination.allocated\": \"1487827718\",\n        \"destination.as_name\": \"System Name\",\n        \"destination.asn\": 123654789,\n        \"destination.fqdn\": \"www.microsoft.com\",\n        \"destination.geolocation.cc\": \"US\",\n        \"destination.geolocation.city\": \"Seattle\",\n        \"destination.geolocation.country\": \"United States\",\n        \"destination.geolocation.latitude\": 32.543234,\n        \"destination.geolocation.longitude\": 24.654321,\n        \"destination.geolocation.region\": \"Americas\",\n        \"destination.geolocation.state\": \"Washington\",\n        \"destination.ip\": \"127.0.0.1\",\n        \"destination.local_hostname\": \"hostname\",\n        \"destination.local_ip\": \"192.168.1.1\",\n        \"destination.network\": \"701 1239 42 206.24. 14.0/24\",\n        \"destination.port\": 22,\n        \"destination.registry\": \"IP registry code\",\n        \"destination.reverse_dns\": \"www.microsoft.com\",\n        \"destination.tor_node\": false,\n        \"destination.url\": \"http://somephishingsite.com\",\n        \"event_description.target\": \"ENISA\",\n        \"event_description.text\": \"A very serious attack against ENISA\",\n        \"event_description.url\": \"https://www.enisa.europa.eu/\",\n        \"event_hash\": \"13da502ab0d75daca5e5075c60e81bfe3b7a637f\",\n        \"extra\": \" {inlinejson:{}}\",\n        \"feed.accuracy\": 95,\n        \"feed.code\": \"HSDAG\",\n        \"feed.documentation\": \"https://www.enisa.europa.eu/topics/trust-services\",\n        \"feed.name\": \"SomeFeedName\",\n        \"feed.provider\": \"SomeCSIRT\",\n        \"feed.url\": \"https://www.enisa.europa.eu/topics/trust-services\",\n        \"malware.hash.md5\": \"9f2520a3056543d49bb0f822d85ce5dd\",\n        \"malware.hash.sha1\": \"13da502ab0d75daca5e5075c60e81bfe3b7a637f\",\n        \"malware.hash.sha256\": \"2d79fcc6b02a2e183a0cb30e0e25d103f42badda9fbf86bbee06f93aa3855aff\",\n        \"malware.name\": \"NSA HD firmware hacks\",\n        \"malware.version\": \"crime-ware kit\",\n        \"misp.attribute_uuid\": \"586cb1ff-6bcc-4029-88b0-4fa9950d210f\",\n        \"misp.event_uuid\": \"586cb1ff-6bcc-4029-88b0-4fa9950d210f\",\n        \"protocol.application\": \"ssh\",\n        \"protocol.transport\": \"tcp\",\n        \"raw\": \"U29tZSBiaW5hcnkgYmxvYg==\",\n        \"rtir_id\": 123456,\n        \"screenshot_url\": \"https://www.enisa.europa.eu/logo.png\",\n        \"source.abuse_contact\": \"abuse@enisa.eu, abuse@microsoft.com\",\n        \"source.account\": \"source@abuse.com\",\n        \"source.allocated\": \"1487827718\",\n        \"source.as_name\": \"Some System Name\",\n        \"source.asn\": 123654789,\n        \"source.fqdn\": \"www.microsoft.com\",\n        \"source.geolocation.cc\":  \"US\",\n        \"source.geolocation.city\":  \"New York\",\n        \"source.geolocation.country\": \"United States\",\n        \"source.geolocation.cymru_cc\": \"US\",\n        \"source.geolocation.geoip_cc\": \"US\",\n        \"source.geolocation.latitude\": 28.543234,\n        \"source.geolocation.longitude\": 26.543234,\n        \"source.geolocation.region\":  \"Americas\",\n        \"source.geolocation.state\": \"New York\",\n        \"source.ip\": \"192.168.1.1\",\n        \"source.local_hostname\": \"myhost\",\n        \"source.local_ip\": \"192.168.1.2\",\n        \"source.network\": \"701 1239 42 206.24. 14.0/24\",\n        \"source.port\": 1088,\n        \"source.registry\": \"this IP registry code\",\n        \"source.reverse_dns\": \"www.microsoft.com\",\n        \"source.tor_node\": true,\n        \"source.url\": \"https://somesite.org\",\n        \"status\": \"offline\",\n        \"time.observation\": \"1487827718\",\n        \"time.source\": \"1487827715\"\n      },\n      \"report\": {\n        \"feed.accuracy\": 56.5,\n        \"feed.code\": \"DFGS\",\n        \"feed.documentation\": \"https://www.enisa.europa.eu/topics/trust-services\",\n        \"feed.name\": \"MyFeedName\",\n        \"feed.provider\": \"Some CSIRT\",\n        \"feed.url\": \"https://www.enisa.europa.eu/\",\n        \"raw\": \"U29tZSBiaW5hcnkgYmxvYg==\",\n        \"rtir_id\": 123654789,\n        \"time.observation\": \"1487827715\"\n      }\n    }\n  }";
    /**
     * examples: getMockedTrustCircle(3, "http://external.csp%s.com")
     * */
    public TrustCircle getMockedTrustCircle(int count){
        TrustCircle trustCircle = new TrustCircle();
        trustCircle.setId("dummyId");
        trustCircle.setShortName("CTC::SHARING_DATA_INCIDENT");
        //List<String> listCsps = new ArrayList<>();
        List<String> teamList = new ArrayList<>();
        for(int i=0; i< count;i++) {
            //listCsps.add(String.format(strWithCountArg,""+(i+1)));
            teamList.add(i+"");
        }
        trustCircle.setTeams(teamList);
        return trustCircle;
    }

    public List<TrustCircle> getAllMockedTrustCircles(int count){
        List<TrustCircle> ret= new ArrayList<>();
        ret.add(getMockedTrustCircle(count));
        return ret;
    }


    public TrustCircle getMockedTrustCircle(int count, String shortName){
        TrustCircle trustCircle = new TrustCircle();
        trustCircle.setId("dummyId");
        trustCircle.setShortName(shortName);
        //List<String> listCsps = new ArrayList<>();
        List<String> teamList = new ArrayList<>();
        for(int i=0; i< count;i++) {
            //listCsps.add(String.format(strWithCountArg,""+(i+1)));
            teamList.add(i+"");
        }
        trustCircle.setTeams(teamList);
        return trustCircle;
    }

    public List<TrustCircle> getAllMockedTrustCircles(int count, String shortName){
        List<TrustCircle> ret= new ArrayList<>();
        ret.add(getMockedTrustCircle(count, shortName));
        return ret;
    }


    public List<Team> getMockedTeams(int count, String strWithCountArg){
        List<Team> ret = new ArrayList<>();
        for(int i=0; i< count;i++) {
            Team t= new Team();
            t.setUrl(String.format(strWithCountArg,""+(i+1)));
            t.setShortName("sname"+i);
            t.setCspId("sname"+i);
            ret.add(t);
        }
        return ret;
    }

    public Team getMockedTeam(int id, String strWithCountArg) {
        Team team = new Team();
        team.setUrl(String.format(strWithCountArg, "" + id));
        team.setShortName("sname"+id);
        team.setCspId("sname"+id);

        return team;
    }

    public Team getMockedTeam(int id, String strWithCountArg, String shortName) {
        Team team = new Team();
        team.setUrl(String.format(strWithCountArg, "" + id));
        team.setShortName(shortName);
        team.setCspId(shortName);

        return team;
    }

    public String getMockedElasticSearchResponse(int countHits) throws JsonProcessingException {
        ElasticSearchResponse elasticSearchResponse = new ElasticSearchResponse();
        Hits hits = new Hits();
        List<Hit> hitList = new ArrayList<>();
        for(int i=0;i<countHits;i++){
            Hit hit = new Hit();
            hit.setId("hit id "+(i+1));
            hitList.add(hit);
        }
        hits.setHits(hitList);
        elasticSearchResponse.setHits(hits);

        return objectMapper.writeValueAsString(elasticSearchResponse);
    }

    /*@Deprecated
    public TrustCircle getMockedTrustCircle(String... mockedECsp){
        TrustCircle trustCircle = new TrustCircle();
        List<String> listCsps = new ArrayList<>();
        for(String s:mockedECsp) {
            listCsps.add(s);
        }
        trustCircle.setCsps(listCsps);
        return trustCircle;
    }*/

    public void sendFlow1IntegrationData(MockMvc mvc, Boolean isExternal) throws Exception {
        sendFlow1IntegrationData(mvc,isExternal, null, null);
    }

    public void sendFlow1IntegrationData(MockMvc mvc, Boolean isExternal, String tcId, String teamId) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(IntegrationDataType.INCIDENT);

        DataParams dataParams = new DataParams();
        dataParams.setApplicationId("test1");
        dataParams.setCspId("testCspId");
        dataParams.setRecordId("recordId");
        dataParams.setOriginCspId("origin-testCspId");
        dataParams.setOriginApplicationId("origin-test1");
        dataParams.setOriginRecordId("origin-recordId");
        dataParams.setDateTime(DateTime.now());
        dataParams.setUrl("http://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(true);
        if(!StringUtils.isEmpty(tcId)){
            List<String> list = new ArrayList<>();
            list.add(tcId);
            sharingParams.setTrustCircleIds(list);
        }
        if(!StringUtils.isEmpty(teamId)){
            List<String> list = new ArrayList<>();
            list.add(teamId);
            sharingParams.setTeamIds(list);
        }
        integrationData.setDataParams(dataParams);
        String data = dataObjectToTest!=null?dataObjectToTest:dataObjectMap.get(integrationData.getDataType());
        JsonNode jsonNode = objectMapper.readTree(data);
        integrationData.setDataObject(jsonNode);
        integrationData.setSharingParams(sharingParams);
        mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                .content(TestUtil.convertObjectToJsonBytes(integrationData))
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
    }



    /**
     * Will be deleted in the future. Use the one in which you pass the applicationId argument
     * */
    @Deprecated
    public void sendFlow1Data(MockMvc mvc,String cspId, Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow1Data(mvc,cspId,"taranis", isExternal,toShare,dataType,httpMethod);
    }

    public void sendFlow1Data(MockMvc mvc, String cspId, String applicationId, Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow1Data(mvc,cspId,applicationId,null,null,isExternal,toShare,dataType,httpMethod);
    }

    public void sendFlow1Data(MockMvc mvc, String cspId, String applicationId, String tcId, String teamId,
                              Boolean isExternal, Boolean toShare, IntegrationDataType dataType, String httpMethod) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(dataType);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(toShare);
        if(!StringUtils.isEmpty(tcId)){
            List<String> list = new ArrayList<>();
            list.add(tcId);
            sharingParams.setTrustCircleIds(list);
        }
        if(!StringUtils.isEmpty(teamId)){
            List<String> list = new ArrayList<>();
            list.add(teamId);
            sharingParams.setTeamIds(list);
        }
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setDateTime(DateTime.now());
        dataParams.setApplicationId(applicationId);
        dataParams.setCspId(cspId);
        dataParams.setOriginCspId("origin-"+cspId);
        dataParams.setOriginApplicationId("origin-"+applicationId);
        dataParams.setOriginRecordId("origin-222");
        dataParams.setUrl("https://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        integrationData.setDataParams(dataParams);
        String data = dataObjectToTest!=null?dataObjectToTest:dataObjectMap.get(integrationData.getDataType());
        JsonNode jsonNode = objectMapper.readTree(data);
        integrationData.setDataObject(jsonNode);


        if (httpMethod.toLowerCase().equals("post")) {
            mvc.perform(post("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
        else if (httpMethod.toLowerCase().equals("put")) {
            mvc.perform(put("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
        else if (httpMethod.toLowerCase().equals("delete")) {
            mvc.perform(delete("/v"+REST_API_V1+"/"+DSL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
    }


    public void sendFlow2Data(MockMvc mvc, String applicationId, Boolean isExternal, Boolean toShare, String cspId,
                              IntegrationDataType dataType, String httpMethod) throws Exception {
        sendFlow2Data(mvc,applicationId,null,null,isExternal,toShare,cspId,dataType,httpMethod);
    }

    public void sendFlow2Data(MockMvc mvc, String applicationId,String tcId,String teamId, Boolean isExternal, Boolean toShare, String cspId, IntegrationDataType dataType, String httpMethod) throws Exception {
        IntegrationData integrationData = new IntegrationData();
        integrationData.setDataType(dataType);
        SharingParams sharingParams = new SharingParams();
        sharingParams.setIsExternal(isExternal);
        sharingParams.setToShare(toShare);
        if(!StringUtils.isEmpty(tcId)){
            List<String> list = new ArrayList<>();
            list.add(tcId);
            sharingParams.setTrustCircleIds(list);
        }
        if(!StringUtils.isEmpty(teamId)){
            List<String> list = new ArrayList<>();
            list.add(teamId);
            sharingParams.setTeamIds(list);
        }
        integrationData.setSharingParams(sharingParams);
        DataParams dataParams = new DataParams();
        dataParams.setRecordId("222");
        dataParams.setApplicationId(applicationId);
        dataParams.setOriginCspId("origin-"+cspId);
        dataParams.setOriginApplicationId("origin-"+applicationId);
        dataParams.setOriginRecordId("origin-222");
        dataParams.setUrl("http://rt.cert-gr.melecertes.eu/Ticket/Display.html?id=23453");
        dataParams.setDateTime(DateTime.now());
        dataParams.setCspId(cspId);
        integrationData.setDataParams(dataParams);
        String data = dataObjectToTest!=null?dataObjectToTest:dataObjectMap.get(integrationData.getDataType());
        JsonNode jsonNode = objectMapper.readTree(data);
        integrationData.setDataObject(jsonNode);


        if (httpMethod.toLowerCase().equals("post")) {
            mvc.perform(post("/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .header(apiDataHandler.getCheckCspIdCertHeader(),integrationData.getDataParams().getCspId())
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
        else if (httpMethod.toLowerCase().equals("put")) {
            mvc.perform(put("/v"+REST_API_V1+"/"+DCL_INTEGRATION_DATA).accept(MediaType.TEXT_PLAIN)
                    .content(TestUtil.convertObjectToJsonBytes(integrationData))
                    .header(apiDataHandler.getCheckCspIdCertHeader(),integrationData.getDataParams().getCspId())
                    .contentType(TestUtil.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().string(HttpStatusResponseType.SUCCESSFUL_OPERATION.getReasonPhrase()));
        }
    }


    public RouteDefinition getRoute(String uri){
        List<RouteDefinition> list = springCamelContext.getRouteDefinitions();
        return list.stream().filter(r->r.getInputs().stream().anyMatch(i->i.getUri().equalsIgnoreCase(uri))).findFirst().get();
    }

    public void mockRoute(String mockPrefix,String uri) throws Exception {
        RouteDefinition dslRoute = getRoute(uri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(uri)
                        //.skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockPrefix+":"+uri);
            }
        });
    }

    public void mockRoute(String mockPrefix,String originalUri, String mockUri) throws Exception {
        RouteDefinition dslRoute = getRoute(originalUri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(originalUri)
                        //.skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockUri);
            }
        });
    }

    public void mockRouteSkipSendToOriginalEndpoint(String mockPrefix,String uri) throws Exception {
        RouteDefinition dslRoute = getRoute(uri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(uri)
                        .skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockPrefix+":"+uri);
            }
        });
    }

    public void mockRouteSkipSendToOriginalEndpoint(String mockPrefix,String originalUri, String mockUri) throws Exception {
        RouteDefinition dslRoute = getRoute(originalUri);
        dslRoute.adviceWith(springCamelContext, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sending to direct:uri and do something else
                interceptSendToEndpoint(originalUri)
                        .skipSendToOriginalEndpoint()
                        //.to("log:foo")
                        .to(mockUri);
            }
        });
    }

    public SpringCamelContext getSpringCamelContext() {
        return springCamelContext;
    }

    public void setSpringCamelContext(SpringCamelContext springCamelContext) {
        this.springCamelContext = springCamelContext;
    }

    public String getDataObjectToTest() {
        return dataObjectToTest;
    }

    public void setDataObjectToTest(String dataObjectToTest) {
        this.dataObjectToTest = dataObjectToTest;
    }

    public Map<IntegrationDataType, String> getDataObjectMap() {
        return dataObjectMap;
    }

    public void setDataObjectMap(Map<IntegrationDataType, String> dataObjectMap) {
        this.dataObjectMap = dataObjectMap;
    }

    public Integer getExpectedInternalAppsCount(String applicationId, String appsStr){
        List<String> apps = new ArrayList<>();
        if(!StringUtils.isEmpty(appsStr)){
            String[] appsArr =  appsStr.split(",");
            apps = Arrays.asList(appsArr).stream().map(s->s.trim()).collect(Collectors.toList());
            //remove myself from internal apps
            apps.remove(applicationId.toLowerCase());
        }

        return apps.size();
    }
}
