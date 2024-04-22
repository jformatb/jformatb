/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.converter;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import it.bancomat.message.data.WorkstationInfo.Cassette;;

public class WorkstationInfoCassetteConverter implements FieldConverter<Cassette> {

	private static final String PATTERN = "EUR${denomination}${initialCount}${initialAmount}${dispensedCount}${dispensedAmount}";
	private static final String AMOUNT = "\\$\\{(initialAmount|dispensedAmount)\\}";

	@Override
	public String format(FormatFieldDescriptor descriptor, Cassette value) throws FieldConversionException {
		String text = FieldConverter.provider().getConverter(Formatter.of(Cassette.class)
				.withPattern(PATTERN)).format(descriptor, value);

		if (!text.equals(descriptor.placeholder())) {
			long[] amounts = new long[] { value.getInitialAmount(), value.getDispensedAmount() };
			for (long amount : amounts) {
				text = text.replaceFirst(AMOUNT, formatAmount(amount));
			}
		}

		return text;
	}

	@Override
	public Cassette parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
		return FieldConverter.provider().getConverter(Formatter.of(Cassette.class)
				.withPattern(PATTERN.replaceAll(AMOUNT, "000000"))).parse(descriptor, source);
	}

	private static String formatAmount(long amount) {
		return StringUtils.leftPad(String.valueOf(amount), 6, "0");
	}

}
