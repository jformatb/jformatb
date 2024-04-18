/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message.converter;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.Formatter;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;
import it.bancomat.message.data.WorkstationInfo.Cassette;;

public class WorkstationInfoCassetteConverter implements FieldConverter<Cassette> {

	private static final String PATTERN = "EUR${denomination}${initialCount}<0000>${dispensedCount}<0000>";
	private static final String AMOUNT = "<0000>";

	@Override
	public String format(FormatFieldDescriptor descriptor, Cassette value) throws FieldConversionException {
		String text = FieldConverter.provider().getConverter(Formatter.of(Cassette.class)
				.withPattern(PATTERN)).format(descriptor, value);

		if (!text.equals(descriptor.placeholder())) {
			int[] counts = new int[] { value.getInitialCount(), value.getDispensedCount() };
			for (int count : counts) {
				long amount = computeAmount(value.getDenomination(), count);
				text = text.replaceFirst(AMOUNT, formatAmount(amount));
			}
		}

		return text;
	}

	@Override
	public Cassette parse(FormatFieldDescriptor descriptor, String source) throws FieldConversionException {
		return FieldConverter.provider().getConverter(Formatter.of(Cassette.class)
				.withPattern(PATTERN)).parse(descriptor, source);
	}

	private static long computeAmount(int denomination, int count) {
		return BigDecimal.valueOf(denomination, 2)
				.multiply(BigDecimal.valueOf(count))
				.longValueExact();
	}

	private static String formatAmount(long amount) {
		return StringUtils.leftPad(String.valueOf(amount), AMOUNT.length(), "0");
	}

}
