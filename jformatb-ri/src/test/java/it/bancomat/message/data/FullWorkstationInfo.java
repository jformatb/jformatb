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
@Format(pattern = "${workstationId}${bankCode}${branchCode}${companyCode}${dependencyCode}${workstationType}${workstationBrand}${workstationModel}${workingHour}${circolarityLevel}${workingRestriction}${workingState}${notifications[\"Anomaly\"].dateTime}${notifications[\"Anomaly\"].properties[\"AnomalyCode\"]:2:00}${notifications[\"LocalAccountingClosure,CentralAccountingClosure,CashLoading\"].dateTime}${cassettes[0..2]}${totals[\"RejectedNote,RecoveryOperation\"]}${operations[\"Withdrawal\"].totalCount}${operations[\"Withdrawal\"].totalAmount}${operations[\"CashDeposit\"].totalCount}${operations[\"CashDeposit\"].totalAmount}${operations[\"Utilities\"].totalCount}${operations[\"Utilities\"].totalAmount:9}${operations[\"Payment\"].totalCount}${operations[\"Payment\"].totalAmount:9}${notifications[\"DepositRecovery\"].dateTime}${operations[\"DepositRecovery\"].totalCount}${operations[\"DepositRecovery\"].totalAmount}${notifications[\"RetainedCardRecovery\"].dateTime}${totals[\"RetainedCard\"]}${physicalAddress}${notifications[\"CompletedOperation\"].dateTime}${notifications[\"CompletedOperation\"].properties[\"TransactionNumber\"]:5:00000}${totals[\"AccountBalanceRequest,AccountMovementRequest,FailureOperation,ServiceOperation\"]}${totals[\"OverallOperation\"]:5}${printBackgroundJournal}${printAccountingClosure}")
public class FullWorkstationInfo extends BasicWorkstationInfo {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = 1746610243776584713L;

	@FormatField(length = 1)
	private WorkstationType workstationType;

	@FormatField(length = 4, type = Type.NUMERIC, format = "0000", placeholder = "0000")
	private String workstationBrand;

	@FormatField(length = 4, type = Type.NUMERIC, format = "0000", placeholder = "0000")
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
