package com.charter.provisioning.voiceportalgateway.handler;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.charter.provisioning.voiceportalgateway.config.EndpointClientConfig;
import com.charter.provisioning.voiceportalgateway.exception.ServiceException;
import com.charter.provisioning.voiceportalgateway.util.MockObjectCreator;

@RunWith(MockitoJUnitRunner.class)
public class VoicePortalGatewayHandlerTest {

	@InjectMocks
	private VoicePortalGatewayHandler voicePortalGatewayHandler;

	@Mock
	private EndpointClientConfig clientConfig;

	@Mock
	private RestTemplate restTemplate;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Before
	public void setUp() {
		
		voicePortalGatewayHandler.voicePortalServiceUrl = MockObjectCreator.VOICE_PORTAL_SERVICE_URL;
	}
	

	@Test
	public void getVoicemailSettings_InvalidRequest_ExpectsServiceException() {

		expectedEx.expect(ServiceException.class);
		expectedEx.expectMessage(String.format("Transaction id %s, unmarshal exception", MockObjectCreator.TRANS_ID));

		voicePortalGatewayHandler.getVoicemailSettings(MockObjectCreator.INVALID_TSDL_MESSAGE,
				MockObjectCreator.AUDIT_USER, MockObjectCreator.TRANS_ID);
	}

	@Test
	public void getVoicemailSettings_InvalidInputs_ExpectsServiceException() {

		expectedEx.expect(ServiceException.class);
		expectedEx.expectMessage(String.format(
				"Transaction id %s, TelephoneNumber or AccountGUID or CustomerGUID not found in TSDlRequest",
				MockObjectCreator.TRANS_ID));

		voicePortalGatewayHandler.getVoicemailSettings(MockObjectCreator.INVALID_INPUTS_TSDL_MESSAGE,
				MockObjectCreator.AUDIT_USER, MockObjectCreator.TRANS_ID);
	}

	@Test
	public void getVoicemailSettings_ValidRequest_ExpectsServiceException() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		when(restTemplate.exchange(MockObjectCreator.voicePortalurlString(), HttpMethod.GET, entity, String.class))
				.thenReturn(MockObjectCreator.validVoicePortalResponse());

		String response = voicePortalGatewayHandler.getVoicemailSettings(MockObjectCreator.VALID_TSDL_MESSAGE,
				MockObjectCreator.AUDIT_USER, MockObjectCreator.TRANS_ID);

		assertThat(response, instanceOf(String.class));
	}

}
