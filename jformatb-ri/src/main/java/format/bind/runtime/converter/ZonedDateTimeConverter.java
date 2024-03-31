/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalQuery;

final class ZonedDateTimeConverter extends TemporalAccessorConverter<ZonedDateTime> {

	@Override
	protected TemporalQuery<ZonedDateTime> query() {
		return ZonedDateTime::from;
	}

}
