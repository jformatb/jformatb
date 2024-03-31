/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;

final class ShortConverter extends NumberConverter<Short> {

	@Override
	protected BigDecimal valueOf(final Short number) {
		return BigDecimal.valueOf(number.longValue());
	}

	@Override
	protected Short toValue(final BigDecimal value) {
		return value.shortValueExact();
	}

}
