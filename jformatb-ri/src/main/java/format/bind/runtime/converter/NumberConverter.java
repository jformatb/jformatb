/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConverter;

abstract class NumberConverter<N extends Number> implements FieldConverter<N> {

	@Override
	public String format(final FormatFieldSpec fieldSpec, final N number) {
		return StringUtils.leftPad(String.valueOf(valueOf(number).setScale(fieldSpec.scale()).unscaledValue().longValueExact()), fieldSpec.length(), "0");
	}

	@Override
	public N parse(final FormatFieldSpec fieldSpec, final String source) {
		return toValue(BigDecimal.valueOf(Long.valueOf(source), fieldSpec.scale()));
	}

	protected abstract BigDecimal valueOf(final N number);

	protected abstract N toValue(final BigDecimal value);

}
