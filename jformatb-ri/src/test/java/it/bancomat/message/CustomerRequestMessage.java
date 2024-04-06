/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatSubTypes;
import lombok.Builder;
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
@JsonSubTypes({
	@JsonSubTypes.Type(DepositRequestMessage.class)
})
@FormatSubTypes({
	DepositRequestMessage.class
})
public abstract class CustomerRequestMessage extends RequestMessage {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = 1294432510245663738L;

	@FormatField(length = 17)
	private String pan;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@FormatField(length = 5, format = "yyDDD")
	private Date startPeriod;

	@FormatField(length = 4, scale = -2)
	@Builder.Default
	private Long remainingPeriod = 0L;

	@FormatField(length = 4, scale = -2)
	@Builder.Default
	private Long remainingSubPeriod = 0L;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@FormatField(length = 6, format = "ddMMyy")
	private Date lastTransactionDate;

}
