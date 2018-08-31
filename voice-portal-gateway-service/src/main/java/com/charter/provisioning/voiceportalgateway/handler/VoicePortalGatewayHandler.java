package com.charter.provisioning.voiceportalgateway.handler;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.charter.provisioning.dsb.model.ASBMessage;
import com.charter.provisioning.dsb.model.Customer;
import com.charter.provisioning.dsb.model.ID;
import com.charter.provisioning.dsb.model.Properties;
import com.charter.provisioning.dsb.model.Property;
import com.charter.provisioning.dsb.model.Query;
import com.charter.provisioning.dsb.model.ServiceProperties;
import com.charter.provisioning.dsb.util.ASBMessageParser;
import com.charter.provisioning.voiceportalgateway.exception.ServiceException;
import com.charter.provisioning.voiceportalgateway.util.VoicePortalGatewayConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VoicePortalGatewayHandler {

	@Value("${voice-portal-service.url}")
	public String voicePortalServiceUrl;

	private RestTemplate restTemplate;

	@Autowired
	public VoicePortalGatewayHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Return all Voice mail settings for all the telephones associated with
	 * requested telephoneNumber or accountGuid or customerGuid.
	 * 
	 * @param tsdlMessage
	 *            TSDL Request consists either telephone Number or account-guid
	 *            customer-guid to get voicemail settings
	 * @param transactionId
	 *            TransactionId for this request.
	 * @return String String representing VMSettings
	 */
	public String getVoicemailSettings(String tsdlMessage, String auditUser, String transactionId) {

		ASBMessage asbRequest;

		try {
			asbRequest = ASBMessageParser.unmarshal(tsdlMessage);

		} catch (Exception ex) {
			log.error("Transaction id {}, unmarshal exception {}", transactionId, ex.getMessage());

			throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST,
					String.format("Transaction id %s, unmarshal exception", transactionId));
		}

		log.info("Transaction id {}, asbRequest {}", transactionId, asbRequest);

		if (asbRequest == null || asbRequest.getBody() == null
				|| CollectionUtils.isEmpty(asbRequest.getBody().getQuery())) {
			log.error("Transaction id {}, TSDLRequest is not valid {}", transactionId, asbRequest);

			throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST,
					String.format("Transaction id %s, TSDL request is not valid", transactionId));
		}

		String telephoneNumber = getTN(asbRequest.getBody().getQuery().get(0));
		log.info("Transaction id {}, telephoneNumber {}", transactionId, telephoneNumber);

		String accountGUID = getAccountGUID(asbRequest.getBody().getQuery().get(0));
		log.info("Transaction id {}, accountGUID {}", transactionId, accountGUID);

		String customerGUID = getCustomerGUID(asbRequest.getBody().getQuery().get(0));
		log.info("Transaction id {}, customerGUID {}", transactionId, customerGUID);

		if (telephoneNumber == null && accountGUID == null && customerGUID == null) {

			log.error("Transaction id {}, TelephoneNumber or AccountGUID or CustomerGUID not found in TSDlRequest",
					transactionId);
			throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, String.format(
					"Transaction id %s, TelephoneNumber or AccountGUID or CustomerGUID not found in TSDlRequest",
					transactionId));

		}

		ResponseEntity<String> response = getVoicePortalResponse(telephoneNumber, accountGUID, customerGUID, auditUser,
				transactionId);

		return response.getBody();

	}

	private ResponseEntity<String> getVoicePortalResponse(String telephoneNumber, String accountGUID, String customerGUID,
			String auditUser, String transactionId) {

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(voicePortalServiceUrl)
				.queryParam("telephone-number", telephoneNumber).queryParam("account-guid", accountGUID)
				.queryParam("customer-guid", customerGUID).queryParam("audit-user", auditUser)
				.queryParam("transaction-id", transactionId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<?> entity = new HttpEntity<>(headers);

		log.info("Transaction id {}, Voiceportal Url {}", transactionId, builder.toUriString());

		return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
	}

	private String getCustomerGUID(Query query) {

		List<Customer> customers = query.getCustomerList();

		if (CollectionUtils.isEmpty(customers))
			return null;

		for (Customer customer : customers) {

			if (CollectionUtils.isEmpty(customer.getID()))
				continue;

			Optional<ID> result = customer.getID().stream()
					.filter(p -> p.getNs().equals(VoicePortalGatewayConstants.DSB)).findFirst();

			if (result.isPresent())
				return result.get().getValue();

		}

		return null;

	}

	private String getAccountGUID(Query query) {

		if (query.getAccount() == null || CollectionUtils.isEmpty(query.getAccount().getID()))
			return null;

		return query.getAccount().getID().stream().filter(p -> p.getNs().equals(VoicePortalGatewayConstants.DSB))
				.findFirst().get().getValue();
	}

	private String getTN(Query query) {

		List<ServiceProperties> servicePropertiesList = query.getServicePropertiesList();

		if (CollectionUtils.isEmpty(servicePropertiesList))
			return null;

		for (ServiceProperties serviceProperties : servicePropertiesList) {

			List<Properties> propertiesList = serviceProperties.getProperties();

			for (Properties properties : propertiesList) {
				if (!CollectionUtils.isEmpty(properties.getProperty())) {
					Property prop = properties.getProperty().get(0);
					if (prop != null && !CollectionUtils.isEmpty(prop.getValue())) {
						return prop.getValue().get(0).getValue();
					}

				}
			}
		}

		return null;
	}

}
