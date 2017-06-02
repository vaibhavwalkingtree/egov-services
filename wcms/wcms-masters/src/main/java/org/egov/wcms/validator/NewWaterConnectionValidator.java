/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.wcms.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.egov.common.contract.response.ErrorField;
import org.egov.wcms.model.Donation;
import org.egov.wcms.model.PropertyTypeUsageType;
import org.egov.wcms.repository.DonationRepository;
import org.egov.wcms.service.DonationService;
import org.egov.wcms.service.PropertyCategoryService;
import org.egov.wcms.service.PropertyUsageTypeService;
import org.egov.wcms.util.WcmsConstants;
import org.egov.wcms.web.contract.DonationGetRequest;
import org.egov.wcms.web.contract.PropertyTypeUsageTypeReq;
import org.egov.wcms.web.contract.WaterConnectionReq;
import org.egov.wcms.web.errorhandlers.Error;
import org.egov.wcms.web.errorhandlers.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
public class NewWaterConnectionValidator {
	
	@Autowired
	private DonationService donationService; 
	
	@Autowired
	private PropertyUsageTypeService propertyUsageTypeService;
	
	@Autowired
	private PropertyCategoryService propertyCategoryService;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(DonationRepository.class);
	
	
	
	public ErrorResponse populateErrors(final BindingResult errors) {
        final ErrorResponse errRes = new ErrorResponse();
        final Error error = new Error();
        error.setCode(1);
        error.setDescription("Error while binding request");
        if (errors.hasFieldErrors())
            for (final FieldError fieldError : errors.getFieldErrors()) {
                error.getFields().put(fieldError.getField(), fieldError.getRejectedValue());
            }
        errRes.setError(error);
        return errRes;
    }
    
    public List<ErrorResponse> validateWaterConnectionRequest(final WaterConnectionReq waterConnectionRequest) {
        final List<ErrorResponse> errorResponses = new ArrayList<>();
        ErrorResponse errorResponse = new ErrorResponse();
        final Error error = getError(waterConnectionRequest);
        errorResponse.setError(error);
        if(!errorResponse.getErrorFields().isEmpty())
            errorResponses.add(errorResponse);
        return errorResponses;
    }
    
    public Error getError(final WaterConnectionReq waterConnectionRequest) {
        List<ErrorField> errorFields = new ArrayList<>();
        if (waterConnectionRequest.getConnection().getBillingType() == null || 
        		waterConnectionRequest.getConnection().getBillingType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.BILLING_TYPE_INVALID_CODE)
                    .message(WcmsConstants.BILLING_TYPE_INVALID_ERROR_MESSAGE)
                    .field(WcmsConstants.BILLING_TYPE_INVALID_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getCategoryType() == null || 
        		waterConnectionRequest.getConnection().getCategoryType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.CATEGORY_NAME_MANDATORY_CODE)
                    .message(WcmsConstants.CATEGORY_NAME_MANADATORY_ERROR_MESSAGE)
                    .field(WcmsConstants.CATEGORY_NAME_MANADATORY_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getConnectionType() == null || 
        		waterConnectionRequest.getConnection().getConnectionType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.CONNECTION_TYPE_INVALID_CODE)
                    .message(WcmsConstants.CONNECTION_INVALID_ERROR_MESSAGE)
                    .field(WcmsConstants.CONNECTION_TYPE_INVALID_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getDocuments() == null || 
        		waterConnectionRequest.getConnection().getDocuments().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.DOCUMENTS_INVALID_CODE)
                    .message(WcmsConstants.DOCUMENTS_INVALID_ERROR_MESSAGE)
                    .field(WcmsConstants.DOCUMENTS_INVALID_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getHscPipeSizeType() == null || 
        		waterConnectionRequest.getConnection().getHscPipeSizeType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.PIPESIZE_SIZEINMM_MANDATORY_CODE)
                    .message(WcmsConstants.PIPESIZE_SIZEINMM__MANADATORY_ERROR_MESSAGE)
                    .field(WcmsConstants.PIPESIZE_SIZEINMM__MANADATORY_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getProperty() == null || 
        		waterConnectionRequest.getConnection().getProperty().getPropertyType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.PROPERTY_TYPE_MANDATORY_CODE)
                    .message(WcmsConstants.PROPERTY_TYPE_MANDATORY_ERROR_MESSAGE)
                    .field(WcmsConstants.PROPERTY_TYPE_MANDATORY_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getProperty() == null || 
        		waterConnectionRequest.getConnection().getProperty().getUsageType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.USAGETYPE_NAME_MANDATORY_CODE)
                    .message(WcmsConstants.USAGETYPE_NAME_MANADATORY_ERROR_MESSAGE)
                    .field(WcmsConstants.USAGETYPE_NAME_MANADATORY_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getSourceType() == null || 
        		waterConnectionRequest.getConnection().getSourceType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.SOURCE_TYPE_INVALID_CODE)
                    .message(WcmsConstants.SOURCE_TYPE_INVALID_ERROR_MESSAGE)
                    .field(WcmsConstants.SOURCE_TYPE_INVALID_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getSumpCapacity() == 0L) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.SUMP_CAPACITY_INVALID_CODE)
                    .message(WcmsConstants.SUMP_CAPACITY_INVALID_ERROR_MESSAGE)
                    .field(WcmsConstants.SUMP_CAPACITY_INVALID_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        } else if (waterConnectionRequest.getConnection().getSupplyType() == null || 
        		waterConnectionRequest.getConnection().getSupplyType().isEmpty()) {
            final ErrorField errorField = ErrorField.builder()
                    .code(WcmsConstants.SUPPLY_TYPE_INVALID_CODE)
                    .message(WcmsConstants.SUPPLY_TYPE_INVALID_ERROR_MESSAGE)
                    .field(WcmsConstants.SUPPLY_TYPE_INVALID_FIELD_NAME)
                    .build();
            errorFields.add(errorField);
        }
       
        return Error.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(WcmsConstants.INVALID_CATEGORY_REQUEST_MESSAGE)
                .errorFields(errorFields)
                .build();
    }
	
	
	public boolean validateNewConnectionBusinessRules(WaterConnectionReq waterConnectionRequest){
		boolean isRequestValid = false;
		
		isRequestValid = validatePropertyUsageMapping(waterConnectionRequest);
		if(!isRequestValid){
			LOGGER.info("Property - Usage Mapping is invalid, Enter correct values.");
			return isRequestValid;
		}
		
		isRequestValid = validatePropertyCategoryMapping(waterConnectionRequest);
		if(!isRequestValid){
			LOGGER.info("Property - Category Mapping is invalid, Enter correct values.");
			return isRequestValid;
		}
		
		
		isRequestValid =  validateDonationAmount(waterConnectionRequest);
		
		
		return isRequestValid;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean validateDonationAmount(WaterConnectionReq waterConnectionRequest){
		String donationCharges = "1000";   // Receive this from Connection Request
		List<Donation> donationList = donationService.getDonationList(prepareDonationGetRequest(waterConnectionRequest));
		Iterator itr = donationList.iterator();
		while(itr.hasNext()){
			Donation donation = (Donation) itr.next();
			if(donationCharges.equals(donation.getDonationAmount())){
				return true;
			}
		}
		return false;
	}
	
	private boolean validatePropertyUsageMapping(WaterConnectionReq waterConnectionRequest){
		PropertyTypeUsageTypeReq propUsageTypeRequest = new PropertyTypeUsageTypeReq();
		PropertyTypeUsageType propertyTypeUsageType = new PropertyTypeUsageType();
		
		propertyTypeUsageType.setPropertyType(waterConnectionRequest.getConnection().getProperty().getPropertyType());
		propertyTypeUsageType.setUsageType(waterConnectionRequest.getConnection().getProperty().getUsageType());
		propertyTypeUsageType.setTenantId(waterConnectionRequest.getConnection().getTenantId());
		
		propUsageTypeRequest.setPropertyTypeUsageType(propertyTypeUsageType);
		
		
		return propertyUsageTypeService.checkPropertyUsageTypeExists(propUsageTypeRequest);
		
	}
	
	private boolean validatePropertyCategoryMapping(WaterConnectionReq waterConnectionRequest){		
		return propertyCategoryService.checkIfMappingExists(waterConnectionRequest.getConnection().getProperty().getPropertyType(),
				waterConnectionRequest.getConnection().getCategoryType(), waterConnectionRequest.getConnection().getTenantId());
		
	}
	
	private DonationGetRequest prepareDonationGetRequest(WaterConnectionReq waterConnectionRequest){
		// Receive new connection request as a parameter for this method
		// Then using the values in the New Connection Request, prepare a Donation Get Request Object
		// Pass this Object to Get Method of Donation Service
		DonationGetRequest donationGetRequest = new DonationGetRequest();
		donationGetRequest.setPropertyType(waterConnectionRequest.getConnection().getProperty().getPropertyType());
		donationGetRequest.setUsageType(waterConnectionRequest.getConnection().getProperty().getUsageType());
		donationGetRequest.setCategoryType(waterConnectionRequest.getConnection().getCategoryType());
		donationGetRequest.setMaxHSCPipeSize(waterConnectionRequest.getConnection().getHscPipeSizeType());
		donationGetRequest.setMinHSCPipeSize(waterConnectionRequest.getConnection().getHscPipeSizeType());
		donationGetRequest.setTenantId(waterConnectionRequest.getConnection().getTenantId());
		return donationGetRequest; 
	}
	
	
	
}
