package com.charter.provisioning.voiceportalgateway.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.charter.provisioning.voiceportalgateway.exception.ErrorResponse;
import com.charter.provisioning.voiceportalgateway.exception.ServiceException;
import com.charter.provisioning.voiceportalgateway.handler.VoicePortalGatewayHandler;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(value = "Voicemail Gateway Service")
@RequestMapping(value = "/voicemail-settings")
public class VoicePortalGatewayController {

	private VoicePortalGatewayHandler voicePortalGatewayHandler;

	@Autowired
	public VoicePortalGatewayController(VoicePortalGatewayHandler voicePortalGatewayHandler) {
		this.voicePortalGatewayHandler = voicePortalGatewayHandler;
	}

	@ApiOperation(value = "Endpoint for retrieving Voicemail Settings by TSDL Message.")
	@ApiResponses(value = {
			@ApiResponse(code = HttpServletResponse.SC_OK, message = "Success", response = String.class, responseContainer = "Return Object"),
			@ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Bad Request"),
			@ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "Not Found"),
			@ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Internal Server Error") })
	@PostMapping(consumes = "text/plain", produces = "application/json")
	public ResponseEntity<String> getVoicemailSettings(
			@ApiParam(value = "TSDL Request consists either telephone Number or account-guid customer-guid to get voicemail settings.") @RequestBody String tsdlMessage,
			@ApiParam(value = "Audit user used to track who sends request.") @RequestHeader(value = "audit-user", required = false) String auditUser,
			@ApiParam(value = "UUID used to trace a transaction through all systems end to end.") @RequestHeader(value = "transaction-id", required = false) String transactionId) {

		log.info("Transaction id {}, Retrieving Voicemail settings request for TSDLMessage: {}, Audit user {}",
				transactionId, tsdlMessage, auditUser);

		String response = voicePortalGatewayHandler.getVoicemailSettings(tsdlMessage, auditUser, transactionId);

		log.info("Transaction id {}, Voicemail settings response {}", transactionId, response);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ExceptionHandler
	public ErrorResponse handleException(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		if (ex instanceof ServiceException) {
			response.setStatus(((ServiceException) ex).getHttpStatus());
		}

		log.error("Transaction id {}, Voicemail settings exception response {}", request.getHeader("transaction-id"),
				ex.toString());

		return new ErrorResponse(ex.getMessage());
	}

}
