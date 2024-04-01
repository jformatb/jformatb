/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

final class BooleanConverter implements FieldConverter<Boolean> {

	@Override
	public String format(final FormatFieldSpec fieldSpec, final Boolean value) throws FieldConversionException {
		try {
			return StringUtils.leftPad(String.valueOf(BooleanUtils.toInteger(value.booleanValue())), fieldSpec.length());
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionFormatException(fieldSpec, value, e);
		}
	}

	@Override
	public Boolean parse(final FormatFieldSpec fieldSpec, final String source) throws FieldConversionException {
		try {
			return BooleanUtils.toBooleanObject(Integer.parseInt(StringUtils.trim(source)));
		} catch (Exception e) {
			return FieldConverterUtil.throwFieldConversionParseException(fieldSpec, source, e);
		}
	}

}
