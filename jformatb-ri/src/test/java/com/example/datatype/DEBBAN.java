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
@Format(pattern = "${bankCode:8}${accountNumber:10}")
@FormatTypeValue("DE")
public class DEBBAN extends BBAN {

	private static final long serialVersionUID = 3417090294644946293L;

	@Override
	public String toString() {
		return new StringBuilder()
				.append(getBankCode())
				.append(getAccountNumber())
				.toString();
	}

}
