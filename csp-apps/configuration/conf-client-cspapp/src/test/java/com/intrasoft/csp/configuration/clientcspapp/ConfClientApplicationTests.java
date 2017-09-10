package com.intrasoft.csp.configuration.clientcspapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.clientcspapp.ConfClientCspApplication;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemInstallationStateRepository;
import com.intrasoft.csp.conf.clientcspapp.repo.SystemModuleRepository;
import com.intrasoft.csp.conf.clientcspapp.service.ExternalProcessService;
import com.intrasoft.csp.conf.clientcspapp.service.InstallationService;
import com.intrasoft.csp.conf.clientcspapp.service.TimeHelper;
import com.intrasoft.csp.conf.commons.model.api.RegistrationDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={ConfClientCspApplication.class}, properties = {"spring.datasource.url=jdbc:h2:mem:testdata;DB_CLOSE_ON_EXIT=FALSE"})
@Slf4j
public class ConfClientApplicationTests {

	private final String testUUID = "2513b89e-e073-4dc7-a43e-29a1ecce5a41";

	@Autowired
	SystemInstallationStateRepository repo;

	@Autowired
	SystemModuleRepository moduleRepository;

	@Autowired
	InstallationService installationService;

	@Autowired
	ExternalProcessService externalProcessService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void verifyExecutionOfExternalProcesses() {

		int code = externalProcessService.executeExternalProcess("/tmp", Optional.empty(), "ls", "-l");
		Assert.assertEquals("ls should return 0", code, 0);

		code = externalProcessService.executeExternalProcess("/tmp", Optional.empty(), "sh", "-c", "ls", "-l", "/bin");

		Assert.assertEquals("sh should return 0", code, 0);

		TimeHelper.sleepFor(2000);

		final List<String> list = externalProcessService.getLastEntries(20);
		log.info("Data in list: {}", list.size());
		list.forEach(log::info);
	}



	@Test
	@Transactional
	public void testDb() {
		SystemInstallationState state = new SystemInstallationState(null, testUUID, InstallationState.NOT_STARTED, new RegistrationDTO(), new SmtpDetails());
		SystemInstallationState stateSaved = repo.save(state);
		Assert.assertNotNull("Id should be allocated", stateSaved.getId());
		Assert.assertEquals("UUID should be same", stateSaved.getCspId(), testUUID);

		log.info("Saved entity ID {}", stateSaved.getId());
	}

	@Test
	@Transactional
	public void testMarshallingRegistration() throws Exception {

		String json = IOUtils.toString(this.getClass().getResourceAsStream("/register/register-valid-1.json"), "UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		RegistrationDTO registration = mapper.readValue(json, RegistrationDTO.class);

		SystemInstallationState state = new SystemInstallationState(null, testUUID, InstallationState.NOT_STARTED, registration, new SmtpDetails());

		SystemInstallationState saved = repo.save(state);

		saved = repo.findOne(saved.getId());

		Assert.assertNotNull("Id should be allocated", saved.getId());
		Assert.assertEquals("UUID should be same", saved.getCspId(), testUUID);

		Assert.assertEquals("registration object should be equal" , registration, saved.getCspRegistration());

		Assert.assertEquals("installation state should be NOT_STARTED", InstallationState.NOT_STARTED, saved.getInstallationState());
		log.info("Retrieved registration as {}",saved.getCspRegistration());
		//todo test marshalling smtpdetails

	}


	@Test
	@Transactional
	public void testPersistingModules() throws Exception {

		SystemModule mod = new SystemModule(null, "base", "DESC", new LocalDateTime(), true, "1.0.000","arcPath","modPath", ModuleState.UNKNOWN, "hash");

		SystemModule saved = moduleRepository.save(mod);

		Assert.assertNotNull("Id should be allocated", saved.getId());

		log.info("Saved: {}", saved);
		final SystemModule found = moduleRepository.findOne(saved.getId());
		log.info("ById : {}", found);

		Assert.assertEquals("should be same", saved, found);
		Assert.assertEquals("date should be same", saved.getInstallDate(), found.getInstallDate());
		Assert.assertEquals("hash should be same", found.getHash(), "hash");


		Assert.assertEquals("version should be the same", installationService.findModuleInstalledActiveVersion(mod.getName()), mod.getVersion());

		Assert.assertNull("should not be installed", installationService.findModuleInstalledActiveVersion("dummymodule"));


	}
}
