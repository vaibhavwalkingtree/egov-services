package org.egov.common;

import org.egov.inv.model.RequestInfo;
import org.egov.inv.model.ResponseInfo;
import org.egov.inv.model.ResponseInfo.StatusEnum;
import org.springframework.stereotype.Service;

@Service
public class DomainService {
	public ResponseInfo getResponseInfo(RequestInfo requestInfo) {
		return new ResponseInfo().apiId(requestInfo.getApiId()).ver(requestInfo.getVer())
				.resMsgId(requestInfo.getMsgId()).resMsgId("placeholder").status(StatusEnum.SUCCESSFUL);
	}

}
