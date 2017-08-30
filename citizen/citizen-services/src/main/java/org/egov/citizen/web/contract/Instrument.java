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
package org.egov.citizen.web.contract;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.citizen.model.BankContract;
import org.egov.citizen.model.InstrumentType;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Instrument{

	/*
	 * id is the unique reference to Instrument Header entered in the system.
	 */
	private String id;

	/*
	 * transactionNumber unique number of the instrument. For cheque type this
	 * is cheque date. For DD type it is DD number
	 *
	 */
	@NotBlank
	@Size(max = 50, min = 6)
	private String transactionNumber;

	/*
	 * transactionDate is the date of instrument . For cheque type it is cheque
	 * date. for DD it is DD date
	 */
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date transactionDate;

    /**
     * Transaction date as long comes from UI in case of cheque and DD
     */
    private Long transactionDateInput;

	/*
	 * amount is the instrument amount. For cheque type it is cheque amount.
	 */
	@NotNull
	@Min(value = 1)
	@Max(value = 999999999)
	private BigDecimal amount;

	/*
	 * instrumentType specifies the type of the instrument - The folowing are
	 * the different types Cash,Cheque,DD,POC
	 *
	 */
	@NotNull	
	private InstrumentType instrumentType;

	/*
	 * bank references to the bank from which the payment/Receipt is made.
	 */
	private BankContract bank;

	/*
	 * branchName is the branch name entered in the collection Receipt.
	 */

	@Size(max = 50)
	private String branchName;

    private String tenantId;

}