/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;
import java.math.BigInteger;

final class BigIntegerConverter extends NumberConverter<BigInteger> {

	@Override
	protected BigDecimal valueOf(final BigInteger number) {
		return new BigDecimal(number);
	}

	@Override
	protected BigInteger toValue(final BigDecimal value) {
		return value.toBigIntegerExact();
	}

}
