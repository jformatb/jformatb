/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;

import format.bind.annotation.Format;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatFieldContainer;
import format.bind.annotation.FormatTypeValue;
import it.bancomat.message.DepositRequestMessage.CashItems;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("depositRecovery")
@Format(pattern = "${requestType:3}${workstationId:4}${transactionNumber:5}${dateTime:10}1${anomalyCode:2:00}${transmissionFlag:1:0}${total.count:5}${cash.count:5:00000}${check.count:5:00000}E${total.amount:11}E${cash.amount:11}E${check.amount:11}${cash.items[0..6]:9:000000000}${cash.level2Count:9:000000000}${cash.level3Count:9:000000000}")
@FormatTypeValue("A16")
public class DepositRecoveryRequestMessage extends OperatorRequestMessage {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = -6388377303021705421L;

	@Data
	@NoArgsConstructor
	@SuperBuilder
	public static class Base implements Serializable {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = 8791842941530184626L;

		@FormatField(length = 11)
		@Builder.Default
		private Long amount = 0L;

		@FormatField(length = 5)
		@Builder.Default
		private Integer count = 0;

	}

	@Data
	@NoArgsConstructor
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class Cash extends Base {

		/** The generated serial version of this class. */
		private static final long serialVersionUID = -1537724289201131240L;

		@FormatField(length = 9)
		@Builder.Default
		private Integer level2Count = 0;

		@FormatField(length = 9)
		@Builder.Default
		private Integer level3Count = 0;

		@FormatField(length = 9)
		@Builder.Default
		private List<Integer> items = CashItems.builder().values();

	}

	@FormatFieldContainer
	@Builder.Default
	private Cash cash = Cash.builder().build();

	@FormatFieldContainer
	@Builder.Default
	private Base check = Base.builder().build();

	@FormatFieldContainer
	@Builder.Default
	private Base total = Base.builder().build();

}
