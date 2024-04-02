/*
 * Copyright 2024 jFormat-B
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package format.bind.runtime.impl.converter;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import format.bind.FormatFieldDescriptor;
import format.bind.converter.FieldConversionException;
import format.bind.converter.FieldConverter;

abstract class NumberConverter<N extends Number> implements FieldConverter<N> {

	@Override
	public String format(final FormatFieldDescriptor descriptor, final N number) throws FieldConversionException {
		try {
			long longValue = valueOf(number).setScale(descriptor.scale()).unscaledValue().longValueExact();
			return StringUtils.leftPad(String.valueOf(longValue), descriptor.length(), "0");
		} catch (Exception e) {
			return FieldConverters.throwFormatFieldConversionException(descriptor, number, e);
		}
	}

	@Override
	public N parse(final FormatFieldDescriptor descriptor, final String source) throws FieldConversionException {
		try {
			return toValue(BigDecimal.valueOf(Long.valueOf(source), descriptor.scale()));
		} catch (Exception e) {
			return FieldConverters.throwParseFieldConversionException(descriptor, source, e);
		}
	}

	protected abstract BigDecimal valueOf(final N number);

	protected abstract N toValue(final BigDecimal value);

}
