package org.egov.egf.persistence.repository;

import java.util.HashMap;

import org.egov.egf.persistence.queue.contract.FundContractRequest;
import org.egov.egf.producer.FinancialProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FundQueueRepository {

	@Autowired
	private FinancialProducer financialProducer;

	@Value("${kafka.topics.egf.masters.validated.topic}")
	private String fundValidatedTopic;

	@Value("${kafka.topics.egf.masters.fund.validated.key}")
	private String fundValidatedKey;

	public void push(FundContractRequest fundContractRequest) {
		HashMap<String, Object> fundContractRequestMap = new HashMap<String, Object>();
		if (fundContractRequest.getFunds() != null && !fundContractRequest.getFunds().isEmpty())
			fundContractRequestMap.put("FundCreate", fundContractRequest);
		else if (fundContractRequest.getFund() != null && fundContractRequest.getFund().getId() != null)
			fundContractRequestMap.put("FundUpdate", fundContractRequest);
		financialProducer.sendMessage(fundValidatedTopic, fundValidatedKey, fundContractRequestMap);
	}
}
