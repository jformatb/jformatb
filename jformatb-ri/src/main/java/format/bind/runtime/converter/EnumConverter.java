/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import org.apache.commons.lang3.StringUtils;

import format.bind.annotation.FormatField;
import format.bind.annotation.FormatField.Type;
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
	public String format(final FormatField field, final E value) {
		if (field.type() == Type.ALPHANUMERIC) {
			return StringUtils.rightPad(value.name(), field.length());
		} else {
			return StringUtils.leftPad(String.valueOf(value.ordinal()), field.length(), "0");
		}
	}

	@Override
	public E parse(final FormatField field, final String source) {
		if (field.type() == Type.ALPHANUMERIC) {
			return Enum.valueOf(enumType, StringUtils.trim(source));
		} else {
			return enumType.getEnumConstants()[Integer.parseInt(StringUtils.trim(source))];
		}
	}

}
