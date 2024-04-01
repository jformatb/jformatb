/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.time.LocalDateTime;
import java.time.temporal.TemporalQuery;

final class LocalDateTimeConverter extends TemporalAccessorConverter<LocalDateTime> {

	@Override
	protected TemporalQuery<LocalDateTime> query() {
		return LocalDateTime::from;
	}

}
