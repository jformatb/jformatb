/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package com.example.datatype;

import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldOverride;
import format.bind.annotation.FormatTypeValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Format(pattern = "${cin:1}${abi:5}${cab:5}${accountNumber:12}")
@FormatTypeValue("IT")
@FormatFieldOverride(property = "bankCode", field = @FormatField(name = "abi"))
@FormatFieldOverride(property = "branchCode", field = @FormatField(name = "cab"))
@FormatFieldOverride(property = "checkDigits", field = @FormatField(name = "cin"))
public class ITBBAN extends BBAN {

	private static final long serialVersionUID = 1074556192726878231L;

	@Override
	public String toString() {
		return new StringBuilder()
				.append(getCheckDigits())
				.append(getBankCode())
				.append(getBranchCode())
				.append(getAccountNumber())
				.toString();
	}

}
