/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import format.bind.annotation.FormatAccess;
import format.bind.annotation.FormatAccess.Type;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatSubTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@FormatSubTypes({
	CashierWorkstationInfo.class,
	FullWorkstationInfo.class
})
public interface WorkstationInfo extends Serializable {

	enum WorkstationType {
		INTERNAL, EXTERNAL, CORPORATE
	}

	enum WorkingState {
		OUT_OF_SERVICE, IN_SERVICE
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@FormatAccess(Type.PROPERTY)
	class Cassette implements Serializable {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = -5472985047729470086L;

		@Builder.Default
		private String currencyCode = "EUR";

		@Getter(onMethod_ = @FormatField(length = 3, scale = -2, placeholder = "0"))
		private int denomination;

		@Getter(onMethod_ = @FormatField(length = 4))
		private int initialCount;

		@Getter(onMethod_ = @FormatField(length = 4))
		private int dispensedCount;

		private long getAmount(int count) {
			return BigDecimal.valueOf(denomination, 2)
					.multiply(BigDecimal.valueOf(count))
					.longValueExact();
		}

//		@FormatField(length = 6, readOnly = true)
		public long getInitialAmount() {
			return getAmount(initialCount);
		}

//		@FormatField(length = 6, readOnly = true)
		public long getDispensedAmount() {
			return getAmount(dispensedCount);
		}

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class Notification implements Serializable {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = -2557254667969415488L;

		private String name;

		@FormatField(length = 10, format = "ddMMyyHHmm", placeholder = "0000000000")
		private LocalDateTime dateTime;

		@FormatField
		@Singular
		private Map<String, String> properties;

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class Operation implements Serializable {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = -8411459689673880318L;

		private String name;

		@FormatField(length = 4)
		private int totalCount;

		@FormatField(length = 6, scale = -2)
		private long totalAmount;

	}

	String EV_ANOMALY = "Anomaly";
	String EV_LOCAL_ACCOUNTING_CLOSURE = "LocalAccountingClosure";
	String EV_CENTRAL_ACCOUNTING_CLOSURE = "CentralAccountingClosure";
	String EV_CASH_LOADING = "CashLoading";
	String EV_DEPOSIT_RECOVERY = "DepositRecovery";
	String EV_RETAINED_CARD_RECOVERY = "RetainedCardRecovery";
	String EV_COMPLETED_OPERATION = "CompletedOperation";

	String EV_PROP_ANOMALY_CODE = "AnomalyCode";
	String EV_PROP_TRANSACTION_NUMBER = "TransactionNumber";

	String OP_WITHDRAWAL = "Withdrawal";
	String OP_CASH_DEPOSIT = "CashDeposit";
	String OP_UTILITIES = "Utilities";
	String OP_PAYMENT = "Payment";
	String OP_DEPOSIT_RECOVERY = "DepositRecovery";

	String TT_REJECTED_NOTE = "RejectedNote";
	String TT_RECOVERY_OPERATION = "RecoveryOperation";
	String TT_RETAINED_CARD = "RetainedCard";
	String TT_ACCOUNT_BALANCE = "AccountBalanceRequest";
	String TT_ACCOUNT_MOVEMENT = "AccountMovementRequest";
	String TT_FAILURE_OPERATION = "FailureOperation";
	String TT_SERVICE_OPERATION = "ServiceOperation";
	String TT_OVERALL_OPERATION = "OverallOperation";

	String getWorkstationId();

	String getBankCode();

	String getBranchCode();

	String getCompanyCode();

	String getDependencyCode();

	Integer getCircolarityLevel();

	Integer getWorkingRestriction();

	Integer getWorkingState();

	List<Cassette> getCassettes();

	Map<String, Notification> getNotifications();

	Map<String, Operation> getOperations();

	Map<String, Integer> getTotals();

}
