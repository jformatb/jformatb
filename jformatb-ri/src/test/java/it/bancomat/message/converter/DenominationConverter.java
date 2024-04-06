/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.converter;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConverter;
import it.bancomat.message.Denomination;

public final class DenominationConverter implements FieldConverter<Denomination> {

	@Override
	public String format(FormatFieldDescriptor descriptor, Denomination denomination) {
		return FieldConverter.provider().getConverter(Integer.class).format(descriptor, denomination.value());
	}

	@Override
	public Denomination parse(FormatFieldDescriptor descriptor, String source) {
		return Denomination.valueOf(FieldConverter.provider().getConverter(Integer.class).parse(descriptor, source));
	}

}
