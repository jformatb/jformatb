/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;

final class FloatConverter extends NumberConverter<Float> {

	@Override
	protected BigDecimal valueOf(final Float number) {
		return BigDecimal.valueOf(number.doubleValue());
	}

	@Override
	protected Float toValue(final BigDecimal value) {
		return value.floatValue();
	}

}
