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
@Format(pattern = "${bankCode:3}${accountNumber:7}${checkDigits:2}")
@FormatTypeValue("BE")
public class BEBBAN extends BBAN {

	private static final long serialVersionUID = 5111671995761204500L;

	@Override
	public String toString() {
		return new StringBuilder()
				.append(getBankCode())
				.append(getAccountNumber())
				.append(getCheckDigits())
				.toString();
	}

}
