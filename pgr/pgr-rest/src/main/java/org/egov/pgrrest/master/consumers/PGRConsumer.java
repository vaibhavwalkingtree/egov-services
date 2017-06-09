
/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.pgrrest.master.consumers;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.pgrrest.master.service.ReceivingCenterTypeService;
import org.egov.pgrrest.master.service.ReceivingModeTypeService;
import org.egov.pgrrest.master.web.contract.ReceivingCenterTypeReq;
import org.egov.pgrrest.master.web.contract.ReceivingModeTypeReq;
import org.egov.pgrrest.master.service.ServiceGroupService;
import org.egov.pgrrest.master.web.contract.ServiceGroupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PGRConsumer {

	public static final Logger LOGGER = LoggerFactory.getLogger(PGRConsumer.class);

	@Autowired
	private ReceivingCenterTypeService receivingCenterTypeService;

	@Autowired
	private ReceivingModeTypeService receivingModeTypeService;
	
	 @Autowired
	    private ServiceGroupService serviceGroupService;

	@KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = {
			"${kafka.topics.receivingcenter.create.name}", "${kafka.topics.receivingmode.create.name}",
			"${kafka.topics.receivingcenter.update.name}", "${kafka.topics.receivingmode.update.name}","${kafka.topics.categorytype.create.name}" })

	public void listen(final ConsumerRecord<String, String> record) {
		LOGGER.info("key:" + record.key() + ":" + "value:" + record.value() + "thread:" + Thread.currentThread());
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			if (record.topic().equals("${kafka.topics.receivingcenter.create.name}")) {
				LOGGER.info("Consuming create ReceivingCenterType request");
				receivingCenterTypeService.create(objectMapper.readValue(record.value(), ReceivingCenterTypeReq.class));
			} else if (record.topic().equals("${kafka.topics.receivingcenter.update.name}")) {
				
				LOGGER.info("Consuming update ReceivingCenterType request");
				receivingCenterTypeService.update(objectMapper.readValue(record.value(), ReceivingCenterTypeReq.class));

			} else if (record.topic().equals("${kafka.topics.receivingmode.create.name}")) {
				LOGGER.info("Consuming create ReceivingModeType request");
				receivingModeTypeService.create(objectMapper.readValue(record.value(), ReceivingModeTypeReq.class));

			} else if (record.topic().equals("${kafka.topics.receivingmode.update.name}")) {
				LOGGER.info("Consuming update ReceivingModeType request");
				receivingModeTypeService.update(objectMapper.readValue(record.value(), ReceivingModeTypeReq.class));
			} else if (record.topic().equals("${kafka.topics.categorytype.create.name}")){
        		LOGGER.info("Consuming create Category request");
                serviceGroupService.create(objectMapper.readValue(record.value(), ServiceGroupRequest.class));
        	}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}