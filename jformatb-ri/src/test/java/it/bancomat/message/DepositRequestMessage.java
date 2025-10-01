/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;

import format.bind.annotation.Format;
import format.bind.annotation.FormatFactory;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldContainer;
import format.bind.annotation.FormatTypeValue;
import format.bind.annotation.FormatValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("deposit")
@Format(pattern = "${requestType:3}${workstationId:4}${transactionNumber:5}${dateTime:10}1${transmissionFlag:1:0}${anomalyCode:2:00}${pan:17}${startPeriod:5}${remainingPeriod:4}${remainingSubPeriod:4}${depositType:1}${cash.noteCount:3:000}E${cash.totalAmount:7}${cash.items[\"500,1000,2000,5000,10000,20000,50000\"]:3:000}${cash.level2Count:3:000}${cash.level3Count:3:000}${check.codeLine:33}E${check.userAmount:9}${lastTransactionDate:6}${targetAccount:20}${transactionReferenceNumber:17}${check.documentNumber:3:000}000${check.issueDate:6:010100}")
@FormatTypeValue("W78")
public class DepositRequestMessage extends CustomerRequestMessage {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = 8088557650010981512L;

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Accessors(fluent = true)
	@Getter
	public enum Type {

		CASH(1),
		CHECK(2),
		END(3);

		private static final Map<Integer, Type> VALUES = Arrays.stream(values())
				.collect(Collectors.toMap(Type::value, Function.identity()));

		@JsonValue
		@FormatValue
		private final Integer value;

		@JsonCreator
		@FormatFactory
		public static Type fromValue(Integer value) {
			return VALUES.get(value);
		}

	}

	public static class CashItems {

		private final Map<String, Integer> items;

		private CashItems(final Map<String, Integer> items) {
			this.items = new TreeMap<>(Comparator.comparingInt(Integer::valueOf));
			this.items.putAll(items);
		}

		public static CashItems builder() {
			return from(Denomination.values());
		}

		public static CashItems from(Denomination[] denominations) {
			return new CashItems(Arrays.stream(denominations)
					.filter(denomination -> denomination.type() == Denomination.Type.NOTE)
					.collect(Collectors.toMap(Denomination::toString, denomination -> 0)));
		}

		public Integer get(Denomination denomination) {
			return items.get(denomination.toString());
		}

		public CashItems put(Denomination denomination, Integer value) {
			items.put(denomination.toString(), value);
			return this;
		}

		public CashItems putAll(Map<Denomination, Integer> map) {
			map.forEach(this::put);
			return this;
		}

		public Set<Denomination> denominations() {
			return items.keySet().stream()
					.map(key -> Denomination.valueOf(Integer.valueOf(key)))
					.collect(Collectors.toCollection(TreeSet::new));
		}

		public List<Integer> values() {
			return items.values().stream().collect(Collectors.toList());
		}

		public Map<String, Integer> build() {
			return items;
		}

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Cash implements Serializable {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = -1537724289201131240L;

		@FormatField(length = 7, scale = -2)
		@Builder.Default
		private Long totalAmount = 0L;

		@FormatField(length = 3)
		@Builder.Default
		private Integer noteCount = 0;

		@FormatField(length = 3)
		@Builder.Default
		private Integer level2Count = 0;

		@FormatField(length = 3)
		@Builder.Default
		private Integer level3Count = 0;

		@FormatField(length = 3)
		@Builder.Default
		private Map<String, Integer> items = CashItems.builder().build();

	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Check implements Serializable {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = 4564255384580744151L;

		@FormatField(length = 3)
		@Builder.Default
		private Integer documentNumber = 0;

		@FormatField(length = 33)
		private String codeLine;

		@FormatField(length = 9)
		@Builder.Default
		private Long userAmount = 0L;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
		@FormatField(length = 6, format = "ddMMyy", placeholder = "010100")
		private Date issueDate;

	}

	@FormatField(length = 1)
	private Type depositType;

	@FormatField(length = 20)
	private String targetAccount;

	@FormatField(length = 17)
	private String transactionReferenceNumber;

	@FormatFieldContainer
	@Builder.Default
	private Cash cash = Cash.builder().build();

	@FormatFieldContainer
	@Builder.Default
	private Check check = Check.builder().build();

}
