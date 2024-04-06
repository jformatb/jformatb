/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import format.bind.annotation.FormatSubTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonSubTypes({
	@JsonSubTypes.Type(DepositRecoveryRequestMessage.class)
})
@FormatSubTypes({
	AccountingClosureRequestMessage.class,
	CashLoadingRequestMessage.class,
	DepositRecoveryRequestMessage.class
})
public abstract class OperatorRequestMessage extends RequestMessage {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = -2786304066040326316L;

}
