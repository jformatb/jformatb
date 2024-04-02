/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class BooleanConverter implements FieldConverter<Boolean> {

	@Override
	public String format(final FormatFieldDescriptor descriptor, final Boolean value) throws FieldConversionException {
		try {
			return StringUtils.leftPad(String.valueOf(BooleanUtils.toInteger(value.booleanValue())), descriptor.length());
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(descriptor, value, e);
		}
	}

	@Override
	public Boolean parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException {
		try {
			return BooleanUtils.toBooleanObject(Integer.parseInt(StringUtils.trim(source)));
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(descriptor, source, e);
		}
	}

}
