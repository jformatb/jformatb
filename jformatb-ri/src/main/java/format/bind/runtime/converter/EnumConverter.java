/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldSpec;
import format.bind.FormatFieldSpec.Type;
import format.bind.converter.FieldConverter;

final class EnumConverter<E extends Enum<E>> implements FieldConverter<E> {

	private final Class<E> enumType;

	private EnumConverter(final Class<E> enumType) {
		this.enumType = enumType;
	}

	public static <E extends Enum<E>> EnumConverter<E> of(final Class<E> enumType) {
		return new EnumConverter<>(enumType);
	}

	@Override
	public String format(final FormatFieldSpec fieldSpec, final E value) {
		if (fieldSpec.type() == Type.ALPHANUMERIC) {
			return StringUtils.rightPad(value.name(), fieldSpec.length());
		} else {
			return StringUtils.leftPad(String.valueOf(value.ordinal()), fieldSpec.length(), "0");
		}
	}

	@Override
	public E parse(final FormatFieldSpec fieldSpec, final String source) {
		if (fieldSpec.type() == Type.ALPHANUMERIC) {
			return Enum.valueOf(enumType, StringUtils.trim(source));
		} else {
			return enumType.getEnumConstants()[Integer.parseInt(StringUtils.trim(source))];
		}
	}

}
