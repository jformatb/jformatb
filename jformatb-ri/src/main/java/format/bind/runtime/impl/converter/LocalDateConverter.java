/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.time.LocalDate;
import java.time.temporal.TemporalQuery;

final class LocalDateConverter extends TemporalAccessorConverter<LocalDate> {

	@Override
	protected TemporalQuery<LocalDate> query() {
		return LocalDate::from;
	}

}
