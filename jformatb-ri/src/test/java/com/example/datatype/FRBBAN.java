/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package com.example.datatype;

import format.bind.annotation.Format;
import format.bind.annotation.FormatTypeValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Format(pattern = "${bankCode:5}${branchCode:5}${accountNumber:11}${checkDigits:2}")
@FormatTypeValue("FR")
public class FRBBAN extends BBAN {

	private static final long serialVersionUID = 3576067502835386208L;

	@Override
	public String toString() {
		return new StringBuilder()
				.append(getBankCode())
				.append(getBranchCode())
				.append(getAccountNumber())
				.append(getCheckDigits())
				.toString();
	}

}
