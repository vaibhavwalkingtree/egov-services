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

package org.egov.egf.master.persistence.entity;

import javax.validation.constraints.NotNull;

import org.egov.common.persistence.entity.AuditableEntity;
import org.egov.egf.master.domain.model.Fund;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FundEntity extends AuditableEntity  {

	public static final String TABLE_NAME = "egf_fund";
	public static final String ALIAS = "fund";

	private static final long serialVersionUID = 7977534010758407945L;

	protected String id;

	@Length(max = 50, min = 2)
	@NotNull
	protected String name;

	@Length(max = 50, min = 2)
	@NotNull
	protected String code;
	@NotNull
	protected Character identifier;

	@NotNull
	protected Long level;

	protected Boolean isParent;

	@NotNull
	protected Boolean active;
	
	protected String parentId;
	
	public Fund toDomain()
	{
		
		Fund parent=Fund.builder().id(parentId).build();
		return Fund.builder()
		.id(id)
		.active(active)
		.code(code)
		.name(name)
		.identifier(identifier)
		.parent(parent)
		.build();
	}
	
	
  public FundEntity toEntity(Fund fund)
	{
		
	 return this.builder().id(fund.getId())
	  .active(fund.getActive())
	  .name(fund.getName())
	  .code(fund.getCode())
	  .identifier(fund.getIdentifier())
	  .active(fund.getActive())
	  .level(Long.valueOf(1))
	  .parentId(fund.getParent()!=null?fund.getParent().getId():null).build();
	 
	}

	 


}