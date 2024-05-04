/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package com.example.datatype;

import java.io.Serializable;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatSubTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FormatSubTypes({
	BEBBAN.class,
	DEBBAN.class,
	FRBBAN.class,
	GBBBAN.class,
	ITBBAN.class
})
public abstract class BBAN implements Serializable {

	private static final long serialVersionUID = -3247585364393039853L;

	@FormatField
	private String bankCode;

	@FormatField
	private String branchCode;

	@FormatField
	private String accountNumber;

	@FormatField
	private String checkDigits;

}
