package com.charter.provisioning.voiceportalgateway.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class MockObjectCreator {
	
	public static String TRANS_ID = "transId12345";

	public static String EMPTY = "";

	public static String AUDIT_USER = "pid12345";

	//public static String VALID_TSDL_MESSAGE = "<ASBMessage version='2.0'> <Header> </Header><Body><Query><ServiceProperties><Properties><Property><Value value='5129611310'/></Property></Properties></ServiceProperties></Query></Body></ASBMessage>";
	
	public static String VALID_TSDL_MESSAGE = "<ASBMessage version='2.0'><Header></Header><Body><Query><Account><ID ns='DSB' value='EBEDBED5-EBC8-083D-EE5E-94DBEF546576'/></Account></Query></Body></ASBMessage>";
			
	public static String VALID_VMAIL_SETTINGS_RESPONSE = "{accountNumber: sincodev4, siteCode: DV2, status: Enabled}";
	
	public static String VOICE_PORTAL_SERVICE_URL = "http://XX.XX.XX.XXX:8070/voice-portal-service/voicemail-settings";
	
	public static String INVALID_TSDL_MESSAGE ="<INVALID/>";
	
	public static String INVALID_INPUTS_TSDL_MESSAGE = "<ASBMessage version='2.0'><Header></Header><Body><Query><ServiceProperties><Properties><Property></Property></Properties></ServiceProperties></Query></Body></ASBMessage>";
	
	
	public static MockHttpServletRequest getHttpServletRequest() {
		return new MockHttpServletRequest();
	}

	public static MockHttpServletResponse getHttpServletResponse() {
		return new MockHttpServletResponse();
	}
	
	public static String voicePortalurlString() {
		return "http://XX.XX.XX.XXX:8070/voice-portal-service/voicemail-settings?telephone-number&account-guid=EBEDBED5-EBC8-083D-EE5E-94DBEF546576&customer-guid&audit-user=pid12345&transaction-id=transId12345";
	}
	
	public static ResponseEntity<String> validVoicePortalResponse() {
		return new ResponseEntity<>(VALID_VMAIL_SETTINGS_RESPONSE, HttpStatus.OK);
	}
	
}
