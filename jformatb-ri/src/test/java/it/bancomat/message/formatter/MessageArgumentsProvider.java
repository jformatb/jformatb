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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import it.bancomat.message.AccountingClosureRequestMessage;
import it.bancomat.message.CashLoadingRequestMessage;
import it.bancomat.message.Denomination;
import it.bancomat.message.DepositRecoveryRequestMessage;
import it.bancomat.message.DepositRequestMessage;
import it.bancomat.message.CashLoadingRequestMessage.Cassette;
import it.bancomat.message.CashLoadingRequestMessage.Type;

public class MessageArgumentsProvider implements ArgumentsProvider {

	private static final String WORKSTATION_ID = "0199";
	private static final String PAN = "31047XXXXXXXXX428";
	private static final String TARGET_ACCOUNT = "XXXXXXXXX453";
	private static final String TRANSACTION_REFERENCE = "20062609540300124";

	static Date dateFrom(LocalDate localDate) {
		return dateFrom(localDate.atStartOfDay());
	}

	static Date dateFrom(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Stream.of(
				arguments("AccountingClosureRequestMessage", "A9001990032803042312470000", AccountingClosureRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(328)
						.dateTime(dateFrom(LocalDateTime.of(2023, 4, 3, 12, 47)))
						.build()),
				arguments("CashLoadingRequestMessage", "A04019900002030423125200002000020160005000000000EUREUR000", CashLoadingRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(2)
						.dateTime(dateFrom(LocalDateTime.of(2023, 4, 3, 12, 52)))
						.type(Type.REPLACE)
						.cassette(Cassette.builder().count(2000).denomination(Denomination.EURO_20).build())
						.cassette(Cassette.builder().count(1600).denomination(Denomination.EURO_50).build())
						.build()),
				arguments("CashDepositRecoveryRequestMessage", "A1601990006428012113061000000130001300000E00001809500E00001809500E00000000000000000001000000006000000254000000256000000001000000000000000000000000000000000001", DepositRecoveryRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(64)
						.dateTime(dateFrom(LocalDateTime.of(2021, 1, 28, 13, 6)))
						.totalCount(13)
						.totalAmount(1809500L)
						.cash(DepositRecoveryRequestMessage.Cash.builder()
								.count(13)
								.amount(1809500L)
								.level3Count(1)
								.items(DepositRequestMessage.CashItems.builder()
										.put(Denomination.EURO_5, 1)
										.put(Denomination.EURO_10, 6)
										.put(Denomination.EURO_20, 254)
										.put(Denomination.EURO_50, 256)
										.put(Denomination.EURO_100, 1)
										.values())
								.build())
						.build()),
				arguments("CheckDepositRecoveryRequestMessage", "A1601990013728012112561000000040000000004E00000301732E00000000000E00000301732000000000000000000000000000000000000000000000000000000000000000000000000000000000", DepositRecoveryRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(137)
						.dateTime(dateFrom(LocalDateTime.of(2021, 1, 28, 12, 56)))
						.totalCount(4)
						.totalAmount(301732L)
						.check(DepositRecoveryRequestMessage.Check.builder()
								.count(4)
								.amount(301732L)
								.build())
						.build()),
				arguments("UndefinedDepositRecoveryRequestMessage", "A1601990011728012113231000000000000000000E00000000000E00000000000E00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", DepositRecoveryRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(117)
						.dateTime(dateFrom(LocalDateTime.of(2021, 1, 28, 13, 23)))
						.build()),
				arguments("CashDepositRequestMessage", "W780199001262606200957100031047XXXXXXXXX42820153000000001100E0002000000000100000000000000000000                                 E000000000260620XXXXXXXXX453        20062609540300124000000010100", DepositRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(126)
						.dateTime(dateFrom(LocalDateTime.of(2020, 6, 26, 9, 57)))
						.pan(PAN)
						.startPeriod(dateFrom(LocalDate.ofYearDay(2020, 153)))
						.remainingPeriod(0L)
						.remainingSubPeriod(0L)
						.lastTransactionDate(dateFrom(LocalDate.of(2020, 6, 26)))
						.depositType(DepositRequestMessage.Type.CASH)
						.targetAccount(TARGET_ACCOUNT)
						.transactionReferenceNumber(TRANSACTION_REFERENCE)
						.cash(DepositRequestMessage.Cash.builder()
								.totalAmount(200000L)
								.noteCount(100)
								.items(DepositRequestMessage.CashItems.builder()
										.put(Denomination.EURO_20, 100)
										.build())
								.build())
						.build()),
				arguments("CheckDepositRequestMessage", "W780199001252606200954100031047XXXXXXXXX42820153000000002000E00000000000000000000000000000000005500679535885576540              E000061244260620XXXXXXXXX453        20062609540300124001000260620", DepositRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(125)
						.dateTime(dateFrom(LocalDateTime.of(2020, 6, 26, 9, 54)))
						.pan(PAN)
						.startPeriod(dateFrom(LocalDate.ofYearDay(2020, 153)))
						.lastTransactionDate(dateFrom(LocalDate.of(2020, 6, 26)))
						.depositType(DepositRequestMessage.Type.CHECK)
						.targetAccount(TARGET_ACCOUNT)
						.transactionReferenceNumber(TRANSACTION_REFERENCE)
						.check(DepositRequestMessage.Check.builder()
								.documentNumber(1)
								.codeLine("5500679535885576540")
								.userAmount(61244L)
								.issueDate(dateFrom(LocalDate.of(2020, 6, 26)))
								.build())
						.build()),
				arguments("EndDepositRequestMessage", "W780199001272606200957100031047XXXXXXXXX42820153000000003000E0000000000000000000000000000000000                                 E000000000260620XXXXXXXXX453        20062609540300124000000010100", DepositRequestMessage.builder()
						.workstationId(WORKSTATION_ID)
						.transactionNumber(127)
						.dateTime(dateFrom(LocalDateTime.of(2020, 6, 26, 9, 57)))
						.pan(PAN)
						.startPeriod(dateFrom(LocalDate.ofYearDay(2020, 153)))
						.lastTransactionDate(dateFrom(LocalDate.of(2020, 6, 26)))
						.depositType(DepositRequestMessage.Type.END)
						.targetAccount(TARGET_ACCOUNT)
						.transactionReferenceNumber(TRANSACTION_REFERENCE)
						.build()));
	}

}
