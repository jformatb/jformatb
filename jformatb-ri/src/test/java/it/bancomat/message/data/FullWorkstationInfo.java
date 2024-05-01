/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.data;

import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Format(pattern = "${workstationId}${bankCode}${branchCode}${companyCode}${dependencyCode}${workstationType}${workstationBrand}${workstationModel}${workingHour}${circolarityLevel}${workingRestriction}${workingState}${notifications[\"Anomaly,LocalAccountingClosure,CentralAccountingClosure,CashLoading\"]}${cassettes[0..2]}${totals[\"RejectedNote,RecoveryOperation\"]}${operations[\"Withdrawal,CashDeposit,Utilities,Payment\"]}${notifications[\"DepositRecovery\"]}${operations[\"DepositRecovery\"]}${notifications[\"RetainedCardRecovery\"]}${totals[\"RetainedCard\"]}${physicalAddress}${notifications[\"CompletedOperation\"]}${totals[\"AccountBalanceRequest,AccountMovementRequest,FailureOperation,ServiceOperation\"]}${totals[\"OverallOperation\"]:5}${printBackgroundJournal}${printAccountingClosure}")
public class FullWorkstationInfo extends BasicWorkstationInfo {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = 1746610243776584713L;

	@FormatField(length = 1)
	private WorkstationType workstationType;

	@FormatField(length = 4, type = Type.NUMERIC)
	private String workstationBrand;

	@FormatField(length = 4, type = Type.NUMERIC)
	private String workstationModel;

	@FormatField(length = 1)
	private Integer workingHour;

	@FormatField(length = 4)
	private String physicalAddress;

	@FormatField(length = 1)
	private boolean printBackgroundJournal;

	@FormatField(length = 1)
	private boolean printAccountingClosure;

}
