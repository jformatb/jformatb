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
@Format(pattern = "${workstationId}${bankCode}${branchCode}${notifications[\"LocalAccountingClosure,CentralAccountingClosure,CashLoading\"]}${cassettes[0..2]}${totals[\"RejectedNote,RecoveryOperation\"]}${operations[\"Withdrawal,CashDeposit,Utilities,Payment\"]}${notifications[\"DepositRecovery\"]}${operations[\"DepositRecovery\"]}${notifications[\"RetainedCardRecovery\"]}${totals[\"RetainedCard\"]}")
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
