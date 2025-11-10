/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package com.example.datatype;

import java.io.Serializable;

import org.apache.commons.validator.routines.IBANValidator;

import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Format(name = "IBAN", pattern = "${countryCode:2}${checkDigits:2}${bban}")
public class IBAN implements Serializable {

	private static final long serialVersionUID = -8396968741896336280L;

	@FormatField
	private String countryCode;

	@FormatField
	private String checkDigits;

	@FormatField
	@FormatTypeInfo(fieldName = "countryCode", length = 2)
	private BBAN bban;

	@Override
	public String toString() {
		return new StringBuilder(30)
				.append(countryCode)
				.append(checkDigits)
				.append(bban)
				.toString();
	}

	public String toFormattedString() {
		return toString().replaceAll("(.{4})", "$1 ").trim();
	}

	public boolean isValid() {
		return IBANValidator.getInstance().isValid(toString());
	}

}
