/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.math.BigDecimal;

final class LongConverter extends NumberConverter<Long> {

	@Override
	protected BigDecimal valueOf(final Long number) {
		return BigDecimal.valueOf(number);
	}

	@Override
	protected Long toValue(final BigDecimal value) {
		return value.longValueExact();
	}

}
