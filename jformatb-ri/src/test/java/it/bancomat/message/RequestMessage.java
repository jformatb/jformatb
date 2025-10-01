/*
 * Copyright (c) 2019 by Diebold Nixdorf
 * This software is the confidential and proprietary information of Diebold Nixdorf.
 */
package it.bancomat.message;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import format.bind.annotation.FormatFactory;
import format.bind.annotation.FormatField;
import format.bind.annotation.FormatSubTypes;
import format.bind.annotation.FormatTypeInfo;
import format.bind.annotation.FormatValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "requestType")
@JsonSubTypes({
	@JsonSubTypes.Type(CustomerRequestMessage.class),
	@JsonSubTypes.Type(OperatorRequestMessage.class)
})
@FormatTypeInfo(fieldName = "requestType", length = 3)
@FormatSubTypes({
	CustomerRequestMessage.class,
	OperatorRequestMessage.class
})
public abstract class RequestMessage implements Serializable {

	/** The generated serial version of this class. */
	private static final long serialVersionUID = -7215724733224660765L;

	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@Accessors(fluent = true)
	@Getter
	public enum TransmissionFlag {

		ONLINE(0),
		OFFLINE(1);

		private static final Map<Integer, TransmissionFlag> VALUES = Arrays.stream(values())
				.collect(Collectors.toMap(TransmissionFlag::value, Function.identity()));

		@JsonValue
		@FormatValue
		private final Integer value;

		@JsonCreator
		@FormatFactory
		public static TransmissionFlag fromValue(Integer value) {
			return VALUES.get(value);
		}

	}

	@FormatField(length = 5)
	private Integer transactionNumber;

	@FormatField(length = 4)
	private String workstationId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	@FormatField(length = 10, format = "ddMMyyHHmm")
	private Date dateTime;

	@FormatField(length = 1)
	@Builder.Default
	private Integer futureUse = 1;

	@FormatField(length = 1)
	@Builder.Default
	private TransmissionFlag transmissionFlag = TransmissionFlag.ONLINE;

	@FormatField(length = 2)
	@Builder.Default
	private String anomalyCode = "00";

}
