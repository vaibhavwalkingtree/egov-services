package org.egov.inv.domain.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.inv.domain.exception.ErrorCode;
import org.egov.inv.domain.exception.InvalidDataException;
import org.egov.inv.domain.model.OpeningBalanceSearchCriteria;
import org.egov.inv.persistence.entity.OpeningBalanceEntity;
import org.egov.inv.persistence.repository.OpeningBalanceRepository;
import org.egov.tracer.kafka.LogAwareKafkaTemplate;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.swagger.model.MaterialReceipt;
import io.swagger.model.OpeningBalanceRequest;
import io.swagger.model.OpeningBalanceResponse;
import io.swagger.model.Pagination;

@Service
public class OpeningBalanceService {
	
	@Autowired
	private InventoryUtilityService inventoryUtilityService;

	@Autowired
	private LogAwareKafkaTemplate<String, Object> kafkaTemplate;
	@Value("${inv.openbalance.save.topic}")
	private String createTopic;

	@Value("${inv.openbalance.update.topic}")
	private String updateTopic;
	
	@Autowired
	private  OpeningBalanceRepository openingBalanceRepository;
	
	public  List<MaterialReceipt> create(OpeningBalanceRequest headerRequest, String tenantId) {

		headerRequest.getMaterialReceipt().stream().forEach(materialReceipt -> {
			materialReceipt.setId(openingBalanceRepository.getSequence(materialReceipt));
			
			materialReceipt.getReceiptDetails().stream().forEach(detail ->{
				detail.setId(Integer.valueOf(openingBalanceRepository.getSequence(detail)));
				
				detail.getReceiptDetailsAddnInfo().stream().forEach(addinfo -> {
					addinfo.setId(Integer.valueOf(openingBalanceRepository.getSequence(addinfo)));
				});
			});
		});
		for (MaterialReceipt material : headerRequest.getMaterialReceipt()) {
			material.setAuditDetails(
					inventoryUtilityService.mapAuditDetails(headerRequest.getRequestInfo(), tenantId));
			material.setId(openingBalanceRepository.getSequence(material));
		}
		kafkaTemplate.send(createTopic, headerRequest);
		return headerRequest.getMaterialReceipt();
	}
	
	public List<MaterialReceipt> update(OpeningBalanceRequest openBalReq, String tenantId) {
		kafkaTemplate.send(updateTopic, openBalReq);
		return openBalReq.getMaterialReceipt();	
	}
	
	
	public Pagination<MaterialReceipt> search(OpeningBalanceSearchCriteria request) {
		return openingBalanceRepository.search(request);

	}

}