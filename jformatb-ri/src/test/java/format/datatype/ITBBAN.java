/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.datatype;

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
@Format(pattern = "${checkDigits:1}${bankCode:5}${branchCode:5}${accountNumber:12}")
@FormatTypeValue("IT")
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
