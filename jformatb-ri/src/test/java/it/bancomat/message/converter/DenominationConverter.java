/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.converter;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConverter;
import it.bancomat.message.Denomination;

public final class DenominationConverter implements FieldConverter<Denomination> {

	private static FieldConverter<Integer> converter = FieldConverter.provider().getConverter(Integer.class);

	@Override
	public byte[] formatBytes(FormatFieldDescriptor descriptor, Denomination denomination) {
		return converter.formatBytes(descriptor, denomination.value());
	}

	@Override
	public Denomination parseBytes(FormatFieldDescriptor descriptor, byte[] source) {
		return Denomination.valueOf(converter.parseBytes(descriptor, source));
	}

}
