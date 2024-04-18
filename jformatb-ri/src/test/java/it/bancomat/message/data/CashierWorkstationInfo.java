/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.data;

import format.bind.annotation.Format;
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
@Format(pattern = "${workstationId}${bankCode}${branchCode}${notifications[\"LocalAccountingClosure,CentralAccountingClosure,CashLoading\"].dateTime}${cassettes[0..2]}${totals[\"RejectedNote,RecoveryOperation\"]}${operations[\"Withdrawal\"].totalCount}${operations[\"Withdrawal\"].totalAmount}${operations[\"CashDeposit\"].totalCount}${operations[\"CashDeposit\"].totalAmount}${operations[\"Utilities\"].totalCount}${operations[\"Utilities\"].totalAmount:9}${operations[\"Payment\"].totalCount}${operations[\"Payment\"].totalAmount:9}${notifications[\"DepositRecovery\"].dateTime}${operations[\"DepositRecovery\"].totalCount}${operations[\"DepositRecovery\"].totalAmount}${notifications[\"RetainedCardRecovery\"].dateTime}${totals[\"RetainedCard\"]}")
public class CashierWorkstationInfo extends BasicWorkstationInfo {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = -4504216402102097182L;

	@Override
	public Integer getCircolarityLevel() {
		return null;
	}

	@Override
	public Integer getWorkingRestriction() {
		return null;
	}

	@Override
	public void setCircolarityLevel(Integer circolarityLevel) {
		throw unsupportedOperationException();
	}

	@Override
	public void setWorkingRestriction(Integer workingRestriction) {
		throw unsupportedOperationException();
	}

}
