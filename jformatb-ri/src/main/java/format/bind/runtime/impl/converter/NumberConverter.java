/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

abstract class NumberConverter<N extends Number> implements FieldConverter<N> {

	@Override
	public String format(final FormatFieldDescriptor descriptor, final N number) throws FieldConversionException {
		try {
			long longValue = valueOf(number).setScale(descriptor.scale()).unscaledValue().longValueExact();
			return StringUtils.leftPad(String.valueOf(longValue), descriptor.length(), "0");
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(descriptor, number, e);
		}
	}

	@Override
	public N parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException {
		try {
			return toValue(BigDecimal.valueOf(Long.valueOf(source), descriptor.scale()));
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(descriptor, source, e);
		}
	}

	protected abstract BigDecimal valueOf(final N number);

	protected abstract N toValue(final BigDecimal value);

}
