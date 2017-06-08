package org.egov.asset.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisposalIndex {

	private String tenantId;
	private Long disposalId;

	private Long assetId;
	private String assetCode;
	private String assetName;

	private String buyerName;
	private String buyerAddress;
	private String disposalReason;
	private Long disposalDate;
	private String panCardNumber;
	private String aadharCardNumber;
	private Double assetCurrentValue;
	private Double saleValue;
	private String transactionType;
	private Long assetSaleAccount;

	private String createdBy;
	private Long createdDate;
	private String lastModifiedBy;
	private Long lastModifiedDate;

	public void setDisposalData(final Disposal disposal) {
		tenantId = disposal.getTenantId();
		disposalId = disposal.getId();
		buyerName = disposal.getBuyerName();
		buyerAddress = disposal.getBuyerAddress();
		disposalReason = disposal.getDisposalReason();
		disposalDate = disposal.getDisposalDate();
		panCardNumber = disposal.getPanCardNumber();
		aadharCardNumber = disposal.getAadharCardNumber();
		assetCurrentValue = disposal.getAssetCurrentValue();
		saleValue = disposal.getSaleValue();
		transactionType = disposal.getTransactionType().toString();
		assetSaleAccount = disposal.getAssetSaleAccount();
	}

}
