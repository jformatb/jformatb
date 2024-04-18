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
package it.bancomat.message.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import format.bind.Formatter;
import it.bancomat.message.data.WorkstationInfo;
import it.bancomat.message.data.WorkstationInfo.Notification;
import it.bancomat.message.data.WorkstationInfo.Operation;

class WorkstationInfoFormatterTest {

	@ParameterizedTest(name = "format{0}")
	@ArgumentsSource(WorkstationInfoArgumentsProvider.class)
	void formatWorkstationInfo(String name, String expected, WorkstationInfo workstationInfo) {
		String actual = Formatter.of(WorkstationInfo.class)
				.format(workstationInfo);
		assertThat(actual).isEqualTo(expected);
	}

	@ParameterizedTest(name = "parse{0}")
	@ArgumentsSource(WorkstationInfoArgumentsProvider.class)
	void parseWorkstationInfo(String name, String text, WorkstationInfo expected) {
		WorkstationInfo actual = Formatter.of(expected.getClass())
				.createReader()
				.withListener((obj, fields) -> postProcess(obj))
				.read(text);
		assertThat(actual).isEqualTo(expected);
	}

	private <T extends WorkstationInfo> void postProcess(T workstationInfo) {
		workstationInfo.getNotifications().entrySet().forEach(entry -> {
			Notification notification = entry.getValue();
			notification.setName(entry.getKey());
			if (notification.getProperties() == null) {
				notification.setProperties(Collections.emptyMap());
			}
		});

		workstationInfo.getOperations().entrySet().forEach(entry -> {
			Operation operation = entry.getValue();
			operation.setName(entry.getKey());
		});
	}

}
