/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import format.bind.annotation.Format;
import format.bind.annotation.FormatTypeValue;
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
@JsonTypeName("accountingClosure")
@Format(pattern = "${requestType:3}${workstationId:4}${transactionNumber:5}${dateTime:10}0${anomalyCode:2:00}${transmissionFlag:1:0}")
@FormatTypeValue("A90")
public class AccountingClosureRequestMessage extends OperatorRequestMessage {

	private static final long serialVersionUID = -7736333895903406902L;

}
