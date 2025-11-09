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
	public byte[] formatBytes(FormatFieldDescriptor descriptor, Type depositType) {
		return String.valueOf(depositType.value()).getBytes(descriptor.charset());
	}

	@Override
	public Type parseBytes(FormatFieldDescriptor descriptor, byte[] source) {
		return Type.fromValue(Integer.valueOf(new String(source, descriptor.charset())));
	}

}
