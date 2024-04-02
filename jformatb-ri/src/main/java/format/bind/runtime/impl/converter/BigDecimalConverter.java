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

final class BigDecimalConverter extends NumberConverter<BigDecimal> {

	@Override
	protected BigDecimal valueOf(final BigDecimal number) {
		return number;
	}

	@Override
	protected BigDecimal toValue(final BigDecimal value) {
		return value;
	}

}
