package com.charter.provisioning.voiceportalgateway.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.charter.provisioning.voiceportalgateway.exception.ErrorResponse;
import com.charter.provisioning.voiceportalgateway.exception.ServiceException;
import com.charter.provisioning.voiceportalgateway.handler.VoicePortalGatewayHandler;
import com.charter.provisioning.voiceportalgateway.util.MockObjectCreator;

@RunWith(MockitoJUnitRunner.class)
public class VoicePortalGatewayControllerTest {

	@InjectMocks
	private VoicePortalGatewayController voicePortalGatewayController;

	@Mock
	private VoicePortalGatewayHandler voicePortalGatewayHandler;

	@Test
	public void getVoicemailSettings_ValidRequest_ExpectsHTTPOk() {
		when(voicePortalGatewayHandler.getVoicemailSettings(MockObjectCreator.VALID_TSDL_MESSAGE,
				MockObjectCreator.AUDIT_USER, MockObjectCreator.TRANS_ID))
						.thenReturn(MockObjectCreator.VALID_VMAIL_SETTINGS_RESPONSE);

		ResponseEntity<String> responseEntity = voicePortalGatewayController.getVoicemailSettings(
				MockObjectCreator.VALID_TSDL_MESSAGE, MockObjectCreator.AUDIT_USER, MockObjectCreator.TRANS_ID);
		assertThat(responseEntity.getBody(), instanceOf(String.class));
		assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void handleException_BadRequest_ExpectsServiceException() {
		String errorMessage = "Invalid telephone number";
		ServiceException ex = new ServiceException(HttpServletResponse.SC_BAD_REQUEST, errorMessage);

		ErrorResponse errorResponse = voicePortalGatewayController.handleException(
				MockObjectCreator.getHttpServletRequest(), MockObjectCreator.getHttpServletResponse(), ex);

		assertThat(errorResponse.getErrorMessage(), is(errorMessage));
	}

	@Test
	public void handleException_ClientError_ExpectsInternalServerException() {
		String errorMessage = "Internal server error";
		HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);

		ErrorResponse errorResponse = voicePortalGatewayController.handleException(
				MockObjectCreator.getHttpServletRequest(), MockObjectCreator.getHttpServletResponse(), ex);

		assertThat(errorResponse.getErrorMessage(),
				is(String.format("%s %s", HttpStatus.INTERNAL_SERVER_ERROR, errorMessage)));
	}

}
