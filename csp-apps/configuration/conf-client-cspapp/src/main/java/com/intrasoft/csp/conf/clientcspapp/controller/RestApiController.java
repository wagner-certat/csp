package com.intrasoft.csp.conf.clientcspapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.clientcspapp.context.ContextUrl;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import com.intrasoft.csp.conf.clientcspapp.model.forms.RegistrationForm;
import com.intrasoft.csp.conf.clientcspapp.service.BackgroundTaskService;
import com.intrasoft.csp.conf.clientcspapp.service.ExternalProcessService;
import com.intrasoft.csp.conf.clientcspapp.service.InstallationService;
import com.intrasoft.csp.conf.clientcspapp.service.SimpleStorageService;
import com.intrasoft.csp.conf.clientcspapp.util.TimeHelper;
import com.intrasoft.csp.conf.commons.context.ApiContextUrl;
import com.intrasoft.csp.conf.commons.model.api.ModulesInfoDTO;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import com.intrasoft.csp.conf.commons.model.api.ResponseDTO;
import com.intrasoft.csp.conf.commons.model.api.UpdateInformationDTO;
import com.intrasoft.csp.conf.commons.types.StatusResponseType;
import com.intrasoft.csp.conf.commons.utils.JodaConverter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@Slf4j
public class RestApiController implements ContextUrl, ApiContextUrl {

    @Value("${client.ui.logentries.max:500}")
    private Integer maxLines;

    @Autowired
    ExternalProcessService externalProcessService;

    @Autowired
    InstallationService installService;

    @Autowired
    BackgroundTaskService backgroundTaskService;

    @Autowired
    SimpleStorageService storageService;

    @RequestMapping(value = REST_REGISTER_FILES + "/{cspId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseDTO registerFiles(
            @PathVariable String cspId,
            @RequestParam("ca_bundle") MultipartFile bundle,
            @RequestParam("ssl_priv_key") MultipartFile privateKey,
            @RequestParam("ssl_pub_key") MultipartFile publicKey
            ) {
        try {
            String caBundleLocation = storageService.storeFileTemporarily(bundle.getInputStream(), "ca-bundle.crt");
            String sslPrivateKey = storageService.storeFileTemporarily(privateKey.getInputStream(), "sslprivate.key");
            String sslPublicKey = storageService.storeFileTemporarily(publicKey.getInputStream(), "sslpublic.crt");

            ObjectMapper mapper = new ObjectMapper();

            ResponseDTO resp = new ResponseDTO(StatusResponseType.OK.code(), mapper.writeValueAsString(
                    Arrays.asList(caBundleLocation,sslPrivateKey,sslPublicKey)
            ));
            return resp;
        } catch (IOException e) {
            log.error("IOException {}",e.getMessage(),e);
            return new ResponseDTO(StatusResponseType.FAILURE.code(), "IO Exception: "+e.getMessage());
        }

    }

    @RequestMapping(value = REST_REGISTER + "/{cspId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.POST)
    public ResponseDTO register(@PathVariable String cspId, @RequestBody RegistrationForm cspForm) {
        RegistrationDTO cspRegistration = new RegistrationDTO();
        cspRegistration.setName(cspForm.getName());
        cspRegistration.setDomainName(cspForm.getDomainName());
        cspRegistration.setRegistrationDate(JodaConverter.getCurrentJodaString());
        cspRegistration.setExternalIPs(cspForm.getExternalIps());
        cspRegistration.setInternalIPs(cspForm.getInternalIps());
        cspRegistration.setRegistrationIsUpdate(false);
        cspRegistration.setContacts(cspForm.getContactDetails());
        // on registration modules are empty.
        ModulesInfoDTO modulesInfo = new ModulesInfoDTO();
        cspRegistration.setModuleInfo(modulesInfo);

        SmtpDetails smtp = new SmtpDetails();
        smtp.setHost(cspForm.getSmtp_host());
        smtp.setPort(cspForm.getSmtp_port());
        smtp.setUserName(cspForm.getSmtp_user());
        smtp.setPassword(cspForm.getSmtp_pass());
        smtp.setSenderEmail(cspForm.getSender_email());
        smtp.setSenderName(cspForm.getSender_name());

        final ResponseDTO dto = installService.registerCsp(cspId, cspRegistration, smtp);
        if (dto.getResponseCode() == 0) {
            backgroundTaskService.scheduleInternalCertsGeneration();
            backgroundTaskService.scheduleExternalCertsGeneration();
            backgroundTaskService.scheduleEnvironmentCreation(true);
        } else {
            log.error("Not successful ; certificates creation has not been scheduled!");
        }
        return dto;
    }


    @GetMapping(value = "/regenerateEnv")
    public void regenerateEnv() {
        log.info("Regenerate environment API called [not deleting csp-sites]");
        backgroundTaskService.scheduleInternalCertsGeneration();
        backgroundTaskService.scheduleExternalCertsGeneration();
        backgroundTaskService.scheduleEnvironmentCreation(false);
    }

    @GetMapping(value = "/recreateOAMVH")
    public void recreateOamAndVhosts() {
        log.info("Request to recreate all OAM agents and VHost agents (will happen at next restart)");
        installService.resetAgentAndHostFlags();
    }

    @RequestMapping(value = REST_LOG,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.GET)
    public @ResponseBody List<LogEntry> log() {
        final List<LogEntry> lines = externalProcessService.getLastEntries(maxLines).stream()
                .map(le -> new LogEntry(TimeHelper.isoFormat(new LocalDateTime(le.getTimestamp())), le.getLevel(), le.getFormattedMessage())).collect(Collectors.toList());
        return lines;
    }

    @RequestMapping(value = REST_DASHSTATUS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity dashboardStatus() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        final String status = mapper.writeValueAsString(installService.mapInstallationStateToPct());

        return new ResponseEntity<>(status, HttpStatus.OK);
    }




    @RequestMapping(value = REST_MODULESERVICES + "/{cspId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<ServiceRow> services(@PathVariable String cspId) {


        return installService.queryCspServices().stream().map(s -> ServiceRow.builder()
                .name(s.getName())
                .startable(s.getStartable() ? "Yes" : "No")
                .startPriority(s.getModule().getStartPriority())
                .currentState(s.getServiceState() == ServiceState.NOT_RUNNING ? "Stopped" : "Running")
                .version(s.getModule().getVersion()).build()).collect(Collectors.toList());

    }


    @RequestMapping(value = REST_UPDATESFOUND + "/{cspId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updates(@PathVariable String cspId) {
        final UpdateInformationDTO cspUpdates = installService.queryCspUpdates(cspId);

        if (cspUpdates == null || cspUpdates.getAvailable().size() == 0) {
            return new ResponseEntity(HttpStatus.OK); // no updates.
        } else {
            List<UpdateVersion> list = cspUpdates.getAvailable().values().stream()
                    .flatMap(Collection::stream)
                    .map( mod -> {
                        final String versionInstalled = installService.queryModuleInstalledActiveVersion(mod.getName());

                        SystemModule module = installService.queryModuleByHash(mod.getHash());
                        StringBuilder actions = new StringBuilder();

                        if (module == null) { // unknown module! lets "initialize"
                            module = SystemModule.builder()
                                    .moduleState(ModuleState.UNKNOWN)
                                    .name(mod.getName())
                                    .description(mod.getDescription())
                                    .active(false)
                                    .hash(mod.getHash())
                                    .version(mod.getVersion())
                                    .startPriority(mod.getStartPriority())
                                    .build();

                            module = installService.saveSystemModule(module);
                        }

                        final long running = runningServices();

                        switch (module.getModuleState()) {
                            case UNKNOWN:
                                actions.append("&nbsp;<a class=\"btn btn-xs btn-primary\" title=\"Download ").append(module.getName()).append("\" href=\"").append(PAGE_DOWNLOADMODULE).append("/").append(module.getHash()).append("\"><i class=\"fa fa-download\"></i></a>");
                                break;
                            case DOWNLOADING:
                                actions.append("&nbsp;<a class=\"btn btn-xs btn-primary\" title=\"Show ").append(module.getName()).append(" progress\" href=\"").append(PAGE_STATUS).append("?moduleId=").append(module.getHash()).append("\"><i class=\"fa fa-cog fa-spin\"></i></a>");
                                break;
                            case DOWNLOADED:
                                if (running == 0) {
                                    actions.append("&nbsp;<a class=\"btn btn-xs btn-success\" title=\"Install ").append(module.getName()).append("\" href=\"").append(PAGE_INSTALLMODULE).append("/").append(module.getHash()).append("\"><i class=\"fa fa-cogs\"></i></a>");
                                    actions.append("&nbsp;<a class=\"btn btn-xs btn-danger\" title=\"Delete ").append(module.getName()).append("\" href=\"").append(PAGE_DELETEMODULE).append("/").append(module.getHash()).append("\"><i class=\"fa fa-trash\"></i></a>");
                                } else {
                                    log.warn("Cannot allow installations because there are {} services running",running);
                                    actions.append("&nbsp;State: RUN");
                                }
                                break;
                            case INSTALLED:
                                if (running == 0) {
                                    actions.append("&nbsp;<a class=\"btn btn-xs btn-warning\" title=\"Re-Install ").append(module.getName()).append("\" href=\"").append(PAGE_REINSTALLMODULE).append("/").append(module.getHash()).append("\"><i class=\"fa fa-refresh\"></i></a>");
                                    actions.append("&nbsp;<a class=\"btn btn-xs btn-danger\" title=\"Delete ").append(module.getName()).append("\" href=\"").append(PAGE_DELETEMODULE).append("/").append(module.getHash()).append("\"><i class=\"fa fa-trash\"></i></a>");
                                } else {
                                    log.warn("Cannot allow installations because there are {} services running",running);
                                    actions.append("&nbsp;State: RUN");
                                }
                                break;
                            case OBSOLETE:
                                actions.append("&nbsp;<a class=\"btn btn-xs btn-danger\" title=\"Delete ").append(module.getName()).append("\" href=\"").append(PAGE_DELETEMODULE).append("/").append(module.getHash()).append("\"><i class=\"fa fa-trash\"></i></a>");
                                break;
                            case REMOVED:
                                break;
                        }


                        return UpdateVersion.builder()
                                .name(mod.getName())
                                .description(mod.getDescription())
                                .version(mod.getVersion())
                                .versionInstalled(versionInstalled == null ? "Not yet installed" : versionInstalled)
                                .hash(mod.getHash())
                                .priority(mod.getStartPriority())
                                .released(mod.getReleased())
                                .btn(actions.toString()).build();
                    }).collect(Collectors.toList());
            return new ResponseEntity(list, HttpStatus.OK);
        }


    }

    private long runningServices() {
        // check services are not running!
        return installService.queryCspServices()
                .stream()
                .filter(SystemService::getStartable)
                .filter(s -> s.getServiceState() == ServiceState.RUNNING)
                .count();
    }
}
