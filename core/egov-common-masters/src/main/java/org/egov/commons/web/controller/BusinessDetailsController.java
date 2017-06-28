package org.egov.commons.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ErrorField;
import org.egov.commons.model.AuthenticatedUser;
import org.egov.commons.model.BusinessAccountSubLedgerDetails;
import org.egov.commons.model.BusinessCategory;
import org.egov.commons.model.BusinessDetails;
import org.egov.commons.model.BusinessDetailsCriteria;
import org.egov.commons.service.BusinessCategoryService;
import org.egov.commons.service.BusinessDetailsService;
import org.egov.commons.util.CollectionConstants;
import org.egov.commons.web.contract.BusinessAccountDetails;
import org.egov.commons.web.contract.BusinessAccountSubLedger;
import org.egov.commons.web.contract.BusinessDetailsGetRequest;
import org.egov.commons.web.contract.BusinessDetailsRequest;
import org.egov.commons.web.contract.BusinessDetailsRequestInfo;
import org.egov.commons.web.contract.BusinessDetailsResponse;
import org.egov.commons.web.contract.RequestInfo;
import org.egov.commons.web.contract.RequestInfoWrapper;
import org.egov.commons.web.contract.ResponseInfo;
import org.egov.commons.web.contract.factory.ResponseInfoFactory;
import org.egov.commons.web.errorhandlers.Error;
import org.egov.commons.web.errorhandlers.ErrorHandler;
import org.egov.commons.web.errorhandlers.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/businessDetails")
public class BusinessDetailsController {
	@Autowired
	BusinessDetailsService businessDetailsService;

	@Autowired
	BusinessCategoryService businessCategoryService;

	@Autowired
	private ErrorHandler errHandler;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	private static final Logger logger = LoggerFactory.getLogger(BusinessDetailsController.class);

	@PostMapping(value = "/_create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createBusinessDetails(@RequestBody BusinessDetailsRequest businessDetailsRequest,
			final BindingResult errors) {

		AuthenticatedUser user = businessDetailsRequest.toDomain();
		if (errors.hasErrors()) {
			final ErrorResponse errRes = populateErrors(errors);
			return new ResponseEntity<ErrorResponse>(errRes, HttpStatus.BAD_REQUEST);
		}
		logger.info("businessDetailsRequest::" + businessDetailsRequest);
		final List<ErrorResponse> errorResponses = validateBusinessDetailsRequest(businessDetailsRequest);
		if (!errorResponses.isEmpty())
			return new ResponseEntity<List<ErrorResponse>>(errorResponses, HttpStatus.BAD_REQUEST);
		BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		BusinessCategory modelCategory = businessCategoryService
				.getBusinessCategoryByIdAndTenantId(detailsInfo.getBusinessCategory(), detailsInfo.getTenantId());
		BusinessDetails modelDetails = new BusinessDetails(detailsInfo, modelCategory);
		List<BusinessAccountDetails> listContractAccountDetails = detailsInfo.getAccountDetails();
		List<org.egov.commons.model.BusinessAccountDetails> listModelAccountDetails = new ArrayList<>();
		for (BusinessAccountDetails details : listContractAccountDetails) {
			listModelAccountDetails
					.add(new org.egov.commons.model.BusinessAccountDetails(details, modelDetails, false));
		}
		List<BusinessAccountSubLedger> contractListOfSubledgers = detailsInfo.getSubledgerDetails();
		List<BusinessAccountSubLedgerDetails> listModelAccountSubledger = new ArrayList<>();
		for (BusinessAccountSubLedger subledger : contractListOfSubledgers) {
			listModelAccountSubledger.add(new BusinessAccountSubLedgerDetails(subledger, modelDetails, false));
		}
		List<BusinessDetailsRequestInfo> detailsRequestInfo = businessDetailsService
				.createBusinessDetails(modelDetails, listModelAccountDetails, listModelAccountSubledger, user)
				.toDomainContract();
		return getSuccessResponse(businessDetailsRequest.getRequestInfo(), detailsRequestInfo);
	}

	@PostMapping(value = "/{businessDetailsCode}/_update")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> updateBusinessDetails(@RequestBody BusinessDetailsRequest businessDetailsRequest,
			@PathVariable String businessDetailsCode) {
		AuthenticatedUser user = businessDetailsRequest.toDomain();
		BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		BusinessCategory modelCategory = businessCategoryService
				.getBusinessCategoryByIdAndTenantId(detailsInfo.getBusinessCategory(), detailsInfo.getTenantId());
		BusinessDetails modelDetails = new BusinessDetails(detailsInfo, modelCategory);
		List<BusinessAccountDetails> listContractAccountDetails = detailsInfo.getAccountDetails();
		List<org.egov.commons.model.BusinessAccountDetails> listModelAccountDetails = new ArrayList<>();
		for (BusinessAccountDetails details : listContractAccountDetails) {
			listModelAccountDetails.add(new org.egov.commons.model.BusinessAccountDetails(details, modelDetails, true));
		}
		List<BusinessAccountSubLedger> contractListOfSubledgers = detailsInfo.getSubledgerDetails();
		List<BusinessAccountSubLedgerDetails> listModelAccountSubledger = new ArrayList<>();
		for (BusinessAccountSubLedger subledger : contractListOfSubledgers) {
			listModelAccountSubledger.add(new BusinessAccountSubLedgerDetails(subledger, modelDetails, true));
		}
		List<BusinessDetailsRequestInfo> detailsRequestInfo = businessDetailsService
				.updateBusinessDetails(modelDetails, listModelAccountDetails, listModelAccountSubledger, user)
				.toDomainContract();
		return getSuccessResponse(businessDetailsRequest.getRequestInfo(), detailsRequestInfo);
	}

	@PostMapping(value = "/_search")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> searchBusinessDetails(
			@ModelAttribute @Valid final BusinessDetailsGetRequest detailsGetRequest,
			final BindingResult modelAttributeBindingResult,
			@RequestBody @Valid final RequestInfoWrapper requestInfoWrapper,
			final BindingResult requestBodyBindingResult) {

		BusinessDetailsCriteria detailsCriteria = BusinessDetailsCriteria.builder()
				.active(detailsGetRequest.getActive()).businessCategoryCode(detailsGetRequest.getBusinessCategoryCode())
				.businessDetailsCode(detailsGetRequest.getBusinessDetailsCode()).ids(detailsGetRequest.getIds())
				.sortBy(detailsGetRequest.getSortBy()).sortOrder(detailsGetRequest.getSortOrder())
				.tenantId(detailsGetRequest.getTenantId()).build();
		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		if (modelAttributeBindingResult.hasErrors())
			return errHandler.getErrorResponseEntityForMissingParameters(modelAttributeBindingResult, requestInfo);

		if (requestBodyBindingResult.hasErrors())
			return errHandler.getErrorResponseEntityForMissingRequestInfo(requestBodyBindingResult, requestInfo);
		List<BusinessDetailsRequestInfo> detailsRequestInfo = new ArrayList<>();
		try {
			detailsRequestInfo = businessDetailsService.getForCriteria(detailsCriteria).toDomainContract();

		} catch (final Exception exception) {
			logger.error("Error while processing request " + detailsGetRequest, exception);
			return errHandler.getResponseEntityForUnexpectedErrors(requestInfo);
		}
		return getSuccessResponse(requestInfo, detailsRequestInfo);
	}

	private ResponseEntity<?> getSuccessResponse(RequestInfo requestInfo,
			List<BusinessDetailsRequestInfo> detailsRequestInfo) {
		BusinessDetailsResponse response = new BusinessDetailsResponse();
		response.setBusinessDetails(detailsRequestInfo);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
		responseInfo.setStatus(HttpStatus.OK.toString());
		response.setResponseInfo(responseInfo);
		return new ResponseEntity<BusinessDetailsResponse>(response, HttpStatus.OK);
	}

	private List<ErrorResponse> validateBusinessDetailsRequest(final BusinessDetailsRequest businessDetailsRequest) {
		final List<ErrorResponse> errorResponses = new ArrayList<>();
		final ErrorResponse errorResponse = new ErrorResponse();
		final Error error = getError(businessDetailsRequest);
		errorResponse.setError(error);
		if (!errorResponse.getErrorFields().isEmpty())
			errorResponses.add(errorResponse);
		return errorResponses;
	}

	private Error getError(final BusinessDetailsRequest businessDetailsRequest) {
		final List<ErrorField> errorFields = getErrorFields(businessDetailsRequest);
		return Error.builder().code(HttpStatus.BAD_REQUEST.value())
				.message(CollectionConstants.INVALID_DETAILS_REQUEST_MESSAGE).errorFields(errorFields).build();
	}

	private List<ErrorField> getErrorFields(final BusinessDetailsRequest businessDetailsRequest) {
		final List<ErrorField> errorFields = new ArrayList<>();

		addTenantIdValidationErrors(businessDetailsRequest, errorFields);
		addNameValidationErrors(businessDetailsRequest, errorFields);
		addCodeValidationErrors(businessDetailsRequest, errorFields);
		addBusinessTypeValidationErrors(businessDetailsRequest, errorFields);
		addFundValidationErrors(businessDetailsRequest, errorFields);
		addFunctionValidationErrors(businessDetailsRequest, errorFields);
		addCategoryValidationErrors(businessDetailsRequest, errorFields);
		return errorFields;
	}

	private void addCategoryValidationErrors(BusinessDetailsRequest businessDetailsRequest,
			List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getBusinessCategory() == null) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_CATEGORY_MANDATORY_CODE)
					.message(CollectionConstants.DETAILS_CATEGORY_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_CATEGORY_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addFundValidationErrors(BusinessDetailsRequest businessDetailsRequest, List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getFund() == null || detailsInfo.getFund().isEmpty()) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_FUND_MANDATORY_CODE)
					.message(CollectionConstants.DETAILS_FUND_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_FUND_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addFunctionValidationErrors(BusinessDetailsRequest businessDetailsRequest,
			List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getFunction() == null || detailsInfo.getFunction().isEmpty()) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_FUNCTION_MANDATORY_CODE)
					.message(CollectionConstants.DETAILS_FUNCTION_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_FUNCTION_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addBusinessTypeValidationErrors(BusinessDetailsRequest businessDetailsRequest,
			List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getBusinessType() == null || detailsInfo.getBusinessType().isEmpty()) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_TYPE_MANDATORY_CODE)
					.message(CollectionConstants.DETAILS_TYPE_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_TYPE_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;

	}

	private void addTenantIdValidationErrors(final BusinessDetailsRequest businessDetailsRequest,
			final List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getTenantId() == null || detailsInfo.getTenantId().isEmpty()) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.TENANT_MANDATORY_CODE)
					.message(CollectionConstants.TENANT_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.TENANT_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addNameValidationErrors(final BusinessDetailsRequest businessDetailsRequest,
			final List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getName() == null || detailsInfo.getName().isEmpty()) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_NAME_MANDATORY_CODE)
					.message(CollectionConstants.DETAILS_NAME_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_NAME_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else if (!businessDetailsService.getBusinessDetailsByNameAndTenantId(detailsInfo.getName(),
				detailsInfo.getTenantId())) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_NAME_UNIQUE_CODE)
					.message(CollectionConstants.DETAILS_NAME_UNIQUE_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_NAME_UNIQUE_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addCodeValidationErrors(final BusinessDetailsRequest businessDetailsRequest,
			List<ErrorField> errorFields) {
		final BusinessDetailsRequestInfo detailsInfo = businessDetailsRequest.getBusinessDetails();
		if (detailsInfo.getCode() == null || detailsInfo.getCode().isEmpty()) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_CODE_MANDATORY_CODE)
					.message(CollectionConstants.DETAILS_CODE_MANADATORY_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_CODE_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else if (!businessDetailsService.getBusinessDetailsByCodeAndTenantId(detailsInfo.getCode(),
				detailsInfo.getTenantId())) {
			final ErrorField errorField = ErrorField.builder().code(CollectionConstants.DETAILS_CODE_UNIQUE_CODE)
					.message(CollectionConstants.DETAILS_CODE_UNIQUE_ERROR_MESSAGE)
					.field(CollectionConstants.DETAILS_CODE_UNIQUE_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private ErrorResponse populateErrors(final BindingResult errors) {
		final ErrorResponse errRes = new ErrorResponse();

		final Error error = new Error();
		error.setCode(1);
		error.setDescription("Error while binding request");
		if (errors.hasFieldErrors())
			for (final FieldError fieldError : errors.getFieldErrors())
				error.getFields().put(fieldError.getField(), fieldError.getRejectedValue());
		errRes.setError(error);
		return errRes;
	}
}
