package org.egov.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.ApplicationProperties;
import org.egov.contract.AssetCurrentValueRequest;
import org.egov.contract.DepreciationRequest;
import org.egov.contract.DepreciationResponse;
import org.egov.model.AssetCategory;
import org.egov.model.CurrentValue;
import org.egov.model.Depreciation;
import org.egov.model.DepreciationDetail;
import org.egov.model.DepreciationInputs;
import org.egov.model.criteria.DepreciationCriteria;
import org.egov.model.enums.Sequence;
import org.egov.model.enums.TransactionType;
import org.egov.repository.DepreciationRepository;
import org.egov.tracer.kafka.LogAwareKafkaTemplate;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepreciationService {

	//@Autowired private AssetService assetService;

	@Autowired
	private AssetCommonService assetCommonService;
	
	@Autowired
	private MasterDataService mDService;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private DepreciationRepository depreciationRepository;

	@Autowired
	private CurrentValueService currentValueService;

	@Autowired
	private SequenceGenService genService;

	@Autowired
	private LogAwareKafkaTemplate<String, Object> kafkaTemplate;

	public DepreciationResponse createDepreciationAsync(DepreciationRequest depreciationRequest) {
		
		DepreciationCriteria criteria = depreciationRequest.getDepreciationCriteria();
		RequestInfo requestInfo = depreciationRequest.getRequestInfo();

		if(criteria.getToDate() > new Date().getTime()) {
			Map<String,String> map = new HashMap<>();
			map.put("egasset_depreciation_depreciationdate","Assets cannot be depreciated for future dates");
			throw new CustomException(map);
		}
		calculateFinancialStartDate(criteria);

		Depreciation depreciation = depreciateAssets(depreciationRequest);

		depreciation.setAuditDetails(assetCommonService.getAuditDetails(requestInfo));

		// TODO put depreciation in kafka topic
		kafkaTemplate.send(applicationProperties.getSaveDepreciationTopic(), depreciation);

		return DepreciationResponse.builder().depreciation(depreciation).responseInfo(null).build();
	}

	/***
	 * Calculates the Depreciation and returns , Calculates the CurrentValue request
	 * and sends it to currentValue Service
	 * 
	 * @param depreciationCriteria
	 * @param requestInfo
	 * @return
	 */
	public Depreciation depreciateAssets(DepreciationRequest depreciationRequest) {

		DepreciationCriteria depreciationCriteria = depreciationRequest.getDepreciationCriteria();
		RequestInfo requestInfo = depreciationRequest.getRequestInfo();

		List<DepreciationInputs> depreciationInputsList = depreciationRepository
				.getDepreciationInputs(depreciationCriteria);

		List<DepreciationDetail> depreciationDetailsList = new ArrayList<>();
		List<CurrentValue> currentValues = new ArrayList<>();
		Depreciation depreciation = null;

		if (!depreciationInputsList.isEmpty())
			enrichDepreciationInputs(depreciationInputsList, requestInfo, depreciationCriteria.getTenantId());

		// calculating the depreciation and adding the currenVal and DepDetail to the
		// lists
		calculateDepreciationAndCurrentValue(depreciationInputsList, depreciationDetailsList, currentValues,
				depreciationCriteria.getFromDate(), depreciationCriteria.getToDate());

		// FIXME TODO voucher integration

		getDepreciationdetailsId(depreciationDetailsList);
		// sending dep/currval objects to respective create async methods
		depreciation = Depreciation.builder().depreciationCriteria(depreciationCriteria)
				.depreciationDetails(depreciationDetailsList).build();
		depreciation.setTenantId(depreciationCriteria.getTenantId());

		currentValueService.createCurrentValueAsync(
				AssetCurrentValueRequest.builder().assetCurrentValue(currentValues).requestInfo(requestInfo).build());

		return depreciation;
	}

	private void getDepreciationdetailsId(List<DepreciationDetail> depreciationDetailsList) {

		final List<Long> idList = genService.getIds(depreciationDetailsList.size(),
				Sequence.DEPRECIATIONSEQUENCE.toString());
		int i = 0;
		for (DepreciationDetail depreciationDetail : depreciationDetailsList)
			depreciationDetail.setId(idList.get(i++));
	}

	/***
	 * Calculate the Depreciation value and the current and populate the respective
	 * lists for the values
	 * 
	 * @param depreciationInputsList
	 * @param depDetList
	 * @param currValList
	 * @param fromDate
	 * @param toDate
	 */
	private void calculateDepreciationAndCurrentValue(List<DepreciationInputs> depreciationInputsList,
			List<DepreciationDetail> depDetList, List<CurrentValue> currValList, Long fromDate, Long toDate) {

		depreciationInputsList.forEach(a -> {
			// getting the amt to be depreciated
			BigDecimal amtToBeDepreciated = getAmountToBeDepreciated(a, fromDate, toDate);

			// calculating the valueAfterDepreciation
			BigDecimal valueAfterDep = a.getCurrentValue().subtract(amtToBeDepreciated);

			// adding the depreciation detail object to list
			depDetList.add(DepreciationDetail.builder().assetId(a.getAssetId())
					.depreciationRate(a.getDepreciationRate()).depreciationValue(amtToBeDepreciated)
					.valueAfterDepreciation(valueAfterDep).valueBeforeDepreciation(a.getCurrentValue()).build());

			// adding currval to the currval list
			currValList.add(CurrentValue.builder().assetId(a.getAssetId()).assetTranType(TransactionType.DEPRECIATION)
					.currentAmount(valueAfterDep).transactionDate(toDate).tenantId(a.getTenantId()).build());
		});
	}

	/***
	 * to find the Amount to be depreciated for every Asset from the
	 * DepreciationInput Object
	 * 
	 * @param depInputs
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	private BigDecimal getAmountToBeDepreciated(DepreciationInputs depInputs, Long fromDate, Long toDate) {

		// deciding the from date for the current depreciation from the last
		// depreciation date
		if (depInputs.getLastDepreciationDate().compareTo(fromDate) >= 0)
			fromDate = depInputs.getLastDepreciationDate();
		else {
			// set asset dateofcreation as from date if it is greater than financial from date
		}

		// getting the no of days betweeen the from and todate using ChronoUnit
		Long noOfDays = ((toDate - fromDate) / 1000 / 60 / 60 / 24)+1;

		// deprate for the no of days = no of days * calculated dep rate per day
		Double depRateForGivenPeriod = noOfDays * depInputs.getDepreciationRate() / 365;

		// returning the calculated amt to be depreciated using the currentvalue from
		// dep inputs and depreciation rate for given period
		return BigDecimal.valueOf(depInputs.getCurrentValue().doubleValue() * (depRateForGivenPeriod / 100));
	}

	/***
	 * Enrich DepreciationInputs using the masterDataService
	 * 
	 * @param depreciationInputsList
	 * @param requestInfo
	 * @param tenantId
	 */
	private void enrichDepreciationInputs(List<DepreciationInputs> depreciationInputsList, RequestInfo requestInfo,
			String tenantId) {

		Set<Long> assetCategoryIds = depreciationInputsList.stream().map(dil -> dil.getAssetCategory())
				.collect(Collectors.toSet());

		Map<String, Map<String, Map<String, String>>> moduleMap = new HashMap<>();
		Map<String, Map<String, String>> masterMap = new HashMap<>();
		Map<String, String> fieldMap = new HashMap<>();
		
		fieldMap.put("id", assetCommonService.getIdQuery(assetCategoryIds));
		masterMap.put("AssetCategory", fieldMap);
		moduleMap.put("ASSET", masterMap);
		
		Map<Long, AssetCategory> assetCatMap = mDService.getAssetCategoryMapFromJSONArray(mDService.getStateWideMastersByListParams(moduleMap, requestInfo, tenantId).get("ASSET"));

		depreciationInputsList
				.forEach(a -> a.setDepreciationRate(assetCatMap.get(a.getAssetCategory()).getDepreciationRate()));

		/*
		 * //FIXME remove after testing depreciationInputsList.forEach(a ->
		 * a.setDepreciationRate(10.0));
		 */
	}

	private void calculateFinancialStartDate(DepreciationCriteria criteria) {

		// setting the toDate hours to 23 and mins to 59
		Long todate = criteria.getToDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(todate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		criteria.setToDate(calendar.getTimeInMillis());

		// choosing the finacial year based on todate month
		if (month < 3) {
			criteria.setFinancialYear(year-1+"-"+year);
			year = year - 1;
		}else
			criteria.setFinancialYear(year+"-"+(year+1));

		// setting from date value
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		criteria.setFromDate(calendar.getTimeInMillis());
		System.err.println("from date calculated : " + criteria.getFromDate());
	}

}
