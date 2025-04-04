package io.mosip.kernel.masterdata.test.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.spi.PublisherClient;
import io.mosip.kernel.masterdata.test.TestBootApplication;
import io.mosip.kernel.masterdata.test.utils.MasterDataTest;
import io.mosip.kernel.masterdata.uispec.dto.UISpecDto;
import io.mosip.kernel.masterdata.utils.AuditUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.mockito.Mockito.doNothing;

/**
 * 
 * @author Nagarjuna
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootApplication.class)
@AutoConfigureMockMvc
public class UISpecControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@MockBean
	private PublisherClient<String, EventModel, HttpHeaders> publisher;
	@MockBean
	private AuditUtil auditUtil;

	private ObjectMapper mapper;

	private RequestWrapper<UISpecDto> request = new RequestWrapper<UISpecDto>();

	@Before
	public void setUp() {

		doNothing().when(auditUtil).auditRequest(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString());
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		String json = "[{\"order\":1,\"name\":\"ConsentDetails\",\"label\":{\"ara\":\"مافقة\",\"fra\":\"Consentement\",\"eng\":\"Consent\"},\"caption\":{\"ara\":\"موافقة\",\"fra\":\"Consentement\",\"eng\":\"Consent\"},\"fields\":[\"consentText\",\"consent\"],\"layoutTemplate\":null,\"preRegFetchRequired\":false,\"active\":false},{\"order\":2,\"name\":\"DemographicDetails\",\"label\":{\"ara\":\"التفاصيل الديموغرافية\",\"fra\":\"Détails démographiques\",\"eng\":\"Demographic Details\"},\"caption\":{\"ara\":\"التفاصيل الديموغرافية\",\"fra\":\"Détails démographiques\",\"eng\":\"Demographic Details\"},\"fields\":[\"fullName\",\"dateOfBirth\",\"gender\",\"residenceStatus\",\"addressLine1\",\"addressLine2\",\"addressLine3\",\"referenceIdentityNumber\",\"region\",\"province\",\"city\",\"zone\",\"postalCode\",\"phone\",\"email\",\"introducerName\",\"introducerRID\",\"introducerUIN\"],\"layoutTemplate\":null,\"preRegFetchRequired\":true,\"active\":false},{\"order\":3,\"name\":\"DocumentsDetails\",\"label\":{\"ara\":\"تحميل الوثيقة\",\"fra\":\"Des documents\",\"eng\":\"Document Upload\"},\"caption\":{\"ara\":\"وثائق\",\"fra\":\"Des documents\",\"eng\":\"Documents\"},\"fields\":[\"proofOfAddress\",\"proofOfIdentity\",\"proofOfRelationship\",\"proofOfDateOfBirth\",\"proofOfException\",\"proofOfException-1\"],\"layoutTemplate\":null,\"preRegFetchRequired\":false,\"active\":false},{\"order\":4,\"name\":\"BiometricDetails\",\"label\":{\"ara\":\"التفاصيل البيومترية\",\"fra\":\"Détails biométriques\",\"eng\":\"Biometric Details\"},\"caption\":{\"ara\":\"التفاصيل البيومترية\",\"fra\":\"Détails biométriques\",\"eng\":\"Biometric Details\"},\"fields\":[\"individualBiometrics\",\"individualAuthBiometrics\",\"introducerBiometrics\"],\"layoutTemplate\":null,\"preRegFetchRequired\":false,\"active\":false}]";
		JsonNode jn;
		UISpecDto dto = new UISpecDto();
		try {
			jn = mapper.readTree(json);
			dto.setJsonspec(jn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dto.setDescription("screen spec");
		dto.setDomain("reg-client");
		dto.setIdentitySchemaId("3");

		dto.setTitle("screenspec");
		dto.setType("screen");
		request.setRequest(dto);

	}

	@Test
	@WithUserDetails("global-admin")
	public void defineUISpecTest_Success() throws Exception {
		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.post("/uispec")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request))).andReturn(),
				null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void updateUISpecTest_FailWithAlreadyPublishedSpec() throws Exception {
		MasterDataTest
				.checkResponse(
						mockMvc.perform(MockMvcRequestBuilders.put("/uispec").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(request)).param("id", "1")).andReturn(),
						"KER-UIS-006");
	}

	@Test
	@WithUserDetails("global-admin")
	public void updateUISpecTest_FailWithInvalidUISpec() throws Exception {
		MasterDataTest
				.checkResponse(
						mockMvc.perform(MockMvcRequestBuilders.put("/uispec").contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(request)).param("id", "10")).andReturn(),
						"KER-UIS-004");
	}

	@Test
	@WithUserDetails("global-admin")
	public void updateUISpecTest_Success() throws Exception {
		MasterDataTest.checkResponse(
				mockMvc.perform(MockMvcRequestBuilders.put("/uispec").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)).param("id", "4")).andReturn(),
				null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void defineUISpecTest_FailWithAlreadyExistingSpec() throws Exception {

		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.post("/uispec")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request))).andReturn(),
				"KER-UIS-010");

	}

	@Test
	@WithUserDetails("global-admin")
	public void updateUISpec_FailWithInvalidUISpec() throws Exception {

		MasterDataTest.checkResponse(mockMvc
				.perform(MockMvcRequestBuilders.put("/uispec").param("id", "test-test-test-test")
						.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
				.andReturn(), "KER-UIS-004");

	}

	@Test
	@WithUserDetails("global-admin")
	public void updateUISpec_Success() throws Exception {

		MasterDataTest
				.checkResponse(mockMvc
						.perform(MockMvcRequestBuilders.put("/uispec").param("id", "4")
								.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
						.andReturn(), null);

	}

	@Test
	@WithUserDetails("global-admin")
	public void publishUISpec_Success() throws Exception {
		MasterDataTest.checkResponse(mockMvc
				.perform(MockMvcRequestBuilders.put("/uispec/publish").contentType(MediaType.APPLICATION_JSON).content(
						"{\"id\":\"string\",\"version\":\"string\",\"requesttime\":\"2018-12-17T07:15:06.724Z\",\"request\":"
								+ "{\"id\":\"4\",\"effectiveFrom\":\"2048-12-17T07:15:06.724Z\"}}"))
				.andReturn(), null);

	}

	@Test
	@WithUserDetails("global-admin")
	public void deleteUISpec_FailWithInvalidUISpec() throws Exception {

		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.delete("/uispec")
				.param("id", "test-test-test-test").contentType(MediaType.APPLICATION_JSON)).andReturn(),
				"KER-UIS-004");// .andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	@WithUserDetails("global-admin")
	public void fetchAllUISpec_Success() throws Exception {

		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/all")).andReturn(), null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestUISpecTest_Success() throws Exception {

		MasterDataTest.checkResponse(
				mockMvc.perform(MockMvcRequestBuilders.get("/uispec/REGCLIENT/latest")).andReturn(), null);

	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestUISpec_Success() throws Exception {

		MasterDataTest.checkResponse(
				mockMvc.perform(MockMvcRequestBuilders.get("/uispec/regclient/latest")).andReturn(), null);

	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema_Success() throws Exception {

		MasterDataTest.checkResponse(
				mockMvc.perform(MockMvcRequestBuilders.get("/uispec/REG/latest").param("version", "1.0")).andReturn(),
				null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema_FailWithInvalidUISpec() throws Exception {

		MasterDataTest.checkResponse(
				mockMvc.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "0.1")).andReturn(),
				"KER-UIS-004");
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema_SuccessWithValidInput() throws Exception {

		MasterDataTest.checkResponse(mockMvc
				.perform(MockMvcRequestBuilders.get("/uispec/REG-CLIENT/latest").param("version", "0.1")).andReturn(),
				null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchemaForReg_Success() throws Exception {

		MasterDataTest.checkResponse(
				mockMvc.perform(MockMvcRequestBuilders.get("/uispec/REG/latest").param("version", "1.0")).andReturn(),
				null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema_WithScreenType_Success() throws Exception {

		MasterDataTest.checkResponse(mockMvc.perform(
				MockMvcRequestBuilders.get("/uispec/REG/latest").param("version", "1.0").param("type", "screens"))
				.andReturn(), null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchemaForRegClient_WithScreenType_Success() throws Exception {

		MasterDataTest.checkResponse(mockMvc.perform(MockMvcRequestBuilders.get("/uispec/REG-CLIENT/latest")
				.param("version", "0.100").param("type", "screens")).andReturn(), null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchemaForRegClient_Success() throws Exception {

		MasterDataTest.checkResponse(mockMvc
				.perform(MockMvcRequestBuilders.get("/uispec/REGCLIENT/latest").param("version", "1.0")).andReturn(),
				null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema_SuccessWithType() throws Exception {
		MasterDataTest.checkResponse(mockMvc.perform(
				MockMvcRequestBuilders.get("/uispec/REG/latest").param("version", "1.0").param("type", "screens"))
				.andReturn(), null);
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchemaTest_FailWithInvalidUISpec() throws Exception {
		MasterDataTest.checkResponse(mockMvc
				.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "1.0").param("type", "dom"))
				.andReturn(), "KER-UIS-004");
	}

	@Test
	@WithUserDetails("global-admin")
	public void getLatestPublishedSchema_FailWithInvalidType() throws Exception {
		MasterDataTest.checkResponse(mockMvc
				.perform(MockMvcRequestBuilders.get("/uispec/reg/latest").param("version", "0.1").param("type", "dom"))
				.andReturn(), "KER-UIS-004");
	}
}
