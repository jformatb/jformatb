/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.math.BigDecimal;

final class DoubleConverter extends NumberConverter<Double> {

	@Override
	protected BigDecimal valueOf(final Double number) {
		return BigDecimal.valueOf(number);
	}

	@Override
	protected Double toValue(final BigDecimal value) {
		return value.doubleValue();
	}

}
