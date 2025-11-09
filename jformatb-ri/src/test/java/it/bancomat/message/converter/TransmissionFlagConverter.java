/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.converter;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConverter;
import it.bancomat.message.RequestMessage.TransmissionFlag;

public class TransmissionFlagConverter implements FieldConverter<TransmissionFlag> {

	@Override
	public byte[] formatBytes(FormatFieldDescriptor descriptor, TransmissionFlag transmissionFlag) {
		return String.valueOf(transmissionFlag.value()).getBytes(descriptor.charset());
	}

	@Override
	public TransmissionFlag parseBytes(FormatFieldDescriptor descriptor, byte[] source) {
		return TransmissionFlag.fromValue(Integer.valueOf(new String(source, descriptor.charset())));
	}

}
