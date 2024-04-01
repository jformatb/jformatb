/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.runtime.impl.converter;

import java.time.OffsetTime;
import java.time.temporal.TemporalQuery;

final class OffsetTimeConverter extends TemporalAccessorConverter<OffsetTime> {

	@Override
	protected TemporalQuery<OffsetTime> query() {
		return OffsetTime::from;
	}

}
