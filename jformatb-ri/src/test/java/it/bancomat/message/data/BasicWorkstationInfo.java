/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.data;

import java.util.List;
import java.util.Map;

import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
import format.bind.annotation.FormatMapEntry;
import format.bind.annotation.FormatMapEntryField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BasicWorkstationInfo implements WorkstationInfo {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = -3126779751110807989L;

	@FormatField(length = 4, type = Type.NUMERIC)
	private String workstationId;

	@FormatField(length = 4, type = Type.NUMERIC)
	private String bankCode;

	@FormatField(length = 5, type = Type.NUMERIC)
	private String branchCode;

	@FormatField(length = 3, type = Type.NUMERIC)
	private String companyCode;

	@FormatField(length = 4, type = Type.NUMERIC)
	private String dependencyCode;

	@FormatField(length = 1)
	private Integer circolarityLevel;

	@FormatField(length = 1)
	private Integer workingRestriction;

	@FormatField(length = 1)
	private Integer workingState;

	@Format(pattern = "EUR${denomination}${initialCount}${initialAmount}${dispensedCount}${dispensedAmount}")
	@FormatField(length = 26, placeholder = "EUR00000000000000000000000")
	@Singular
	private List<Cassette> cassettes;

	@Format(pattern = "${dateTime:10}")
	@FormatField(length = 10, placeholder = "0000000000")
	@FormatMapEntry(keys = { "Anomaly" }, pattern = "${dateTime:10}${properties[\"AnomalyCode\"]:2:00}")
	@FormatMapEntry(keys = { "CompletedOperation" }, pattern = "${dateTime:10}${properties[\"TransactionNumber\"]:5:0}")
	@FormatMapEntryField(keys = { "Anomaly" }, field = @FormatField(length = 12, placeholder = "000000000000"))
	@FormatMapEntryField(keys = { "CompletedOperation" }, field = @FormatField(length = 15, placeholder = "000000000000000"))
	@Singular
	private Map<String, Notification> notifications;

	@FormatField
	@FormatMapEntry(keys = { "CashDeposit", "DepositRecovery", "Withdrawal" }, pattern = "${totalCount:4}${totalAmount:6}")
	@FormatMapEntry(keys = { "Payment", "Utilities" }, pattern = "${totalCount:4}${totalAmount:9}")
	@FormatMapEntryField(keys = { "CashDeposit", "DepositRecovery", "Withdrawal" }, field = @FormatField(length = 10, placeholder = "0000000000"))
	@FormatMapEntryField(keys = { "Payment", "Utilities" }, field = @FormatField(length = 13, placeholder = "0000000000000"))
	@Singular
	private Map<String, Operation> operations;

	@FormatField(length = 4, placeholder = "0")
	@Singular
	private Map<String, Integer> totals;

	protected UnsupportedOperationException unsupportedOperationException() {
		return new UnsupportedOperationException("This operation is not supported");
	}

}
