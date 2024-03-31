/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.time.LocalTime;
import java.time.temporal.TemporalQuery;

final class LocalTimeConverter extends TemporalAccessorConverter<LocalTime> {

	@Override
	protected TemporalQuery<LocalTime> query() {
		return LocalTime::from;
	}

}
