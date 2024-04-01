/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldSpec;
import format.bind.converter.FieldConverter;

final class BooleanConverter implements FieldConverter<Boolean> {

	@Override
	public String format(final FormatFieldSpec fieldSpec, final Boolean value) {
		return StringUtils.leftPad(String.valueOf(BooleanUtils.toInteger(value.booleanValue())), fieldSpec.length());
	}

	@Override
	public Boolean parse(final FormatFieldSpec fieldSpec, final String source) {
		return BooleanUtils.toBooleanObject(Integer.parseInt(StringUtils.trim(source)));
	}

}
