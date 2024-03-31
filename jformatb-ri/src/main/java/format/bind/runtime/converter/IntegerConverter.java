/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;

final class IntegerConverter extends NumberConverter<Integer> {

	@Override
	protected BigDecimal valueOf(final Integer number) {
		return BigDecimal.valueOf(number.longValue());
	}

	@Override
	protected Integer toValue(final BigDecimal value) {
		return value.intValueExact();
	}

}
