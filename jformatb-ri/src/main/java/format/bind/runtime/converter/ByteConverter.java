/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;

final class ByteConverter extends NumberConverter<Byte> {

	@Override
	protected BigDecimal valueOf(final Byte number) {
		return BigDecimal.valueOf(number.longValue());
	}

	@Override
	protected Byte toValue(final BigDecimal value) {
		return value.byteValueExact();
	}

}
