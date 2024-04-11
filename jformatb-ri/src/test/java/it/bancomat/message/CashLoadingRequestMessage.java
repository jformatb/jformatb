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
import format.bind.annotation.FormatFieldConverter;
import format.bind.annotation.FormatTypeValue;
import it.bancomat.message.converter.DenominationConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("cashLoading")
@Format(pattern = "${requestType:3}${workstationId:4}${transactionNumber:5}${dateTime:10}0${anomalyCode:2:00}${transmissionFlag:1:0}${cassettes[0]:7:0000000}${cassettes[1]:7:0000000}${cassettes[2]:7:0000000}${type:1}${cassettes[0].currency:3:000}${cassettes[1].currency:3:000}${cassettes[2].currency:3:000}")
@FormatTypeValue("A04")
public class CashLoadingRequestMessage extends OperatorRequestMessage {

	private static final long serialVersionUID = -1179665395401813835L;

	public enum Type {
		REPLACE,
		ADD
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Cassette implements Serializable {

		private static final long serialVersionUID = -6009239016960345633L;

		@FormatField(length = 3, placeholder = "000")
		@Builder.Default
		private String currency = "EUR";

		@FormatField(length = 3, scale = -2)
		@FormatFieldConverter(DenominationConverter.class)
		private Denomination denomination;

		@FormatField(length = 4)
		private Integer count;

	}

	@FormatField
	private Type type;

	@Format(pattern = "${count:4}${denomination:3}")
	@FormatField(length = 7, placeholder = "0000000")
	@FormatFieldContainer
	@Singular
	private List<Cassette> cassettes;

}
