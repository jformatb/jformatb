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
@Format(pattern = "${bankCode:4}${branchCode:6}${accountNumber:8}")
@FormatTypeValue("GB")
public class GBBBAN extends BBAN {

	private static final long serialVersionUID = 1825299000395824603L;

	@Override
	public String toString() {
		return new StringBuilder()
				.append(getBankCode())
				.append(getBranchCode())
				.append(getAccountNumber())
				.toString();
	}

}
