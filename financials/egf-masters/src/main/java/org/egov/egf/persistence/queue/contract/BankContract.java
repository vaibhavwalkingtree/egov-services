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
 *         3) This license does not grant any rights to any Long of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.egf.persistence.queue.contract;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.egf.persistence.entity.Bank;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Data

public class BankContract extends AuditableContract {

    private Long id;

    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Long> ids = new ArrayList<Long>();

    @NotNull
    @Length(max = 50, min = 1)
    private String code;

    @NotNull
    @Length(max = 100, min = 2)
    private String name;

    @Length(max = 250)
    private String description;

    @NotNull
    private Boolean active;
    // is this required?

    @Length(max = 50)
    private String type;

    public Long getId() {
        return id;
    }

    public BankContract() {
        super();
    }

    public BankContract(final String id) {
        super();
        this.id = Long.valueOf(id);
    }

    /*
     * public void map(BankContractContract bank) { if(bank!=null) { if(bank.getActive()!=null) this.setActive(bank.getActive());
     * if(bank.getName()!=null) this.setName(bank.getName()); if(bank.getCode()!=null) this.setCode(bank.getCode());
     * if(bank.getDescription()!=null) this.setDescription(bank.getDescription()); } } public BankContractContract
     * mapContract(BankContractContract bank) { bank.setId(id); bank.setCode(code); bank.setName(name); bank.setActive(active);
     * bank.setDescription(description); return bank; }
     */

}