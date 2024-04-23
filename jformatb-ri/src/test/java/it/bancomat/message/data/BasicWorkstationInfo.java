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
import format.bind.annotation.FormatFieldContainer;
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

	@FormatFieldContainer
	@Singular
	private Map<String, Notification> notifications;

	@FormatFieldContainer
	@Singular
	private Map<String, Operation> operations;

	@FormatField(length = 4)
	@Singular
	private Map<String, Integer> totals;

	protected UnsupportedOperationException unsupportedOperationException() {
		return new UnsupportedOperationException("This operation is not supported");
	}

}
