/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;

final class BigDecimalConverter extends NumberConverter<BigDecimal> {

	@Override
	protected BigDecimal valueOf(final BigDecimal number) {
		return number;
	}

	@Override
	protected BigDecimal toValue(final BigDecimal value) {
		return value;
	}

}
