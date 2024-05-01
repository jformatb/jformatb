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

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import it.bancomat.message.data.CashierWorkstationInfo;
import it.bancomat.message.data.FullWorkstationInfo;
import it.bancomat.message.data.WorkstationInfo;
import it.bancomat.message.data.WorkstationInfo.Notification;
import it.bancomat.message.data.WorkstationInfo.Operation;
import it.bancomat.message.data.WorkstationInfo.WorkstationType;

public class WorkstationInfoArgumentsProvider implements ArgumentsProvider {

	private static final String CURRENCY_CODE = "EUR";
	private static final String DATE_TIME_PATTERN = "ddMMyyHHmm";

	private static String workstationId = "0429";
	private static String bankCode = "3104";
	private static String branchCode = "40090";

	private static WorkstationInfo.Cassette buildCassette(int denomination, int initialCount) {
		return WorkstationInfo.Cassette.builder()
				.currencyCode(CURRENCY_CODE)
				.denomination(denomination)
				.initialCount(initialCount)
				.build();
	}

	private static WorkstationInfo.Cassette buildCassette(int denomination, int initialCount, int dispensedCount) {
		return WorkstationInfo.Cassette.builder()
				.currencyCode(CURRENCY_CODE)
				.denomination(denomination)
				.initialCount(initialCount)
				.dispensedCount(dispensedCount)
				.build();
	}

	private static Notification buildNotification(String name, String dateTime) {
		return Notification.builder()
				.name(name)
				.dateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
				.build();
	}

	private static Notification buildNotification(String name, String dateTime, String propertyName, Object propertyValue) {
		return Notification.builder()
				.name(name)
				.dateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)))
				.property(propertyName, propertyValue)
				.build();
	}

	private static Operation buildOperation(String name, int count, int amount) {
		return Operation.builder()
				.name(name)
				.totalCount(count)
				.totalAmount(amount)
				.build();
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Stream.of(
				arguments("CashierWorkstationInfo", "0429310440090240323084627032300052403230851EUR02007640152800037000740EUR05006280314000030001500EUR000000000000000000000000000000000100022400000000000000000000000000000000000000000000000000000000024032308460001", CashierWorkstationInfo.builder()
						.workstationId(workstationId)
						.bankCode(bankCode)
						.branchCode(branchCode)
						.notification(WorkstationInfo.EV_LOCAL_ACCOUNTING_CLOSURE, buildNotification(WorkstationInfo.EV_LOCAL_ACCOUNTING_CLOSURE, "2403230846"))
						.notification(WorkstationInfo.EV_CENTRAL_ACCOUNTING_CLOSURE, buildNotification(WorkstationInfo.EV_CENTRAL_ACCOUNTING_CLOSURE, "2703230005"))
						.notification(WorkstationInfo.EV_CASH_LOADING, buildNotification(WorkstationInfo.EV_CASH_LOADING, "2403230851"))
						.cassette(buildCassette(2000, 764, 37))
						.cassette(buildCassette(5000, 628, 30))
						.operation(WorkstationInfo.OP_WITHDRAWAL, buildOperation(WorkstationInfo.OP_WITHDRAWAL, 10, 224000))
						.notification(WorkstationInfo.EV_RETAINED_CARD_RECOVERY, buildNotification(WorkstationInfo.EV_RETAINED_CARD_RECOVERY, "2403230846"))
						.total(WorkstationInfo.TT_RETAINED_CARD, 1)
						.build()),
				arguments("FullWorkstationInfo", "042931044009000000240166300041003270323083632270323083727032300052403230851EUR02007270145400000000000EUR05005980299000000000000EUR000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000027032308370000    00000000000000000000000000000000000100", FullWorkstationInfo.builder()
						.workstationId(workstationId)
						.bankCode(bankCode)
						.branchCode(branchCode)
						.dependencyCode("0024")
						.workstationType(WorkstationType.INTERNAL)
						.workstationBrand("1663")
						.workstationModel("0004")
						.workingHour(1)
						.circolarityLevel(0)
						.workingRestriction(0)
						.workingState(3)
						.notification(WorkstationInfo.EV_ANOMALY, buildNotification(WorkstationInfo.EV_ANOMALY, "2703230836", WorkstationInfo.EV_PROP_ANOMALY_CODE, "32"))
						.notification(WorkstationInfo.EV_LOCAL_ACCOUNTING_CLOSURE, buildNotification(WorkstationInfo.EV_LOCAL_ACCOUNTING_CLOSURE, "2703230837"))
						.notification(WorkstationInfo.EV_CENTRAL_ACCOUNTING_CLOSURE, buildNotification(WorkstationInfo.EV_CENTRAL_ACCOUNTING_CLOSURE, "2703230005"))
						.notification(WorkstationInfo.EV_CASH_LOADING, buildNotification(WorkstationInfo.EV_CASH_LOADING, "2403230851"))
						.cassette(buildCassette(2000, 727))
						.cassette(buildCassette(5000, 598))
						.notification(WorkstationInfo.EV_RETAINED_CARD_RECOVERY, buildNotification(WorkstationInfo.EV_RETAINED_CARD_RECOVERY, "2703230837"))
						.total(WorkstationInfo.TT_OVERALL_OPERATION, 1)
						.build()));
	}

}
