/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.converter;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConverter;
import it.bancomat.message.DepositRequestMessage.Type;

public class DepositTypeConverter implements FieldConverter<Type> {

	@Override
	public String format(FormatFieldDescriptor descriptor, Type depositType) {
		return String.valueOf(depositType.value());
	}

	@Override
	public Type parse(FormatFieldDescriptor descriptor, String source) {
		return Type.fromValue(Integer.valueOf(source));
	}

}
