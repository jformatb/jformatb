/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package it.bancomat.message;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
@Getter
public enum Denomination {

	CENT_1(1, Type.COIN),
	CENT_2(2, Type.COIN),
	CENT_5(5, Type.COIN),
	CENT_10(10, Type.COIN),
	CENT_20(20, Type.COIN),
	CENT_50(50, Type.COIN),
	EURO_1(100, Type.COIN),
	EURO_2(200, Type.COIN),
	EURO_5(500, Type.NOTE),
	EURO_10(1000, Type.NOTE),
	EURO_20(2000, Type.NOTE),
	EURO_50(5000, Type.NOTE),
	EURO_100(10000, Type.NOTE),
	EURO_200(20000, Type.NOTE),
	EURO_500(50000, Type.NOTE);

	private static final Map<Integer, Denomination> VALUES = Arrays.stream(values())
			.collect(Collectors.toMap(Denomination::value, Function.identity()));

	@JsonValue
	private final Integer value;

	private final Type type;

	@JsonCreator
	public static Denomination valueOf(Integer value) {
		return VALUES.get(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public enum Type {

		COIN, NOTE

	}

}
