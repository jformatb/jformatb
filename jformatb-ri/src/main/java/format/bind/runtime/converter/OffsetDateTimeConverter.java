/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.converter;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalQuery;

final class OffsetDateTimeConverter extends TemporalAccessorConverter<OffsetDateTime> {

	@Override
	protected TemporalQuery<OffsetDateTime> query() {
		return OffsetDateTime::from;
	}

}
