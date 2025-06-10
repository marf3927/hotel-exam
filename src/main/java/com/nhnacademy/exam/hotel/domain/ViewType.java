package com.nhnacademy.exam.hotel.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ViewType {

    CITY(0, "cityView"),
    OCEAN(1, "oceanView"),
    MOUNTAIN(2, "mountainView");

    private int dbValue;
    private String parameter;

    public static final Map<Integer, ViewType> dbValueMap = Arrays.stream(ViewType.values()).collect(Collectors.toMap(ViewType::getDbValue, Function.identity()));
    public static final Map<String, ViewType> parameterMap = Arrays.stream(ViewType.values()).collect(Collectors.toMap(ViewType::getParameter, Function.identity()));

    ViewType(int dbValue, String parameter) {
        this.dbValue = dbValue;
        this.parameter = parameter;
    }

    public static ViewType fromDbValue(Integer dbValue) {
        return dbValueMap.get(dbValue);
    }

    public static ViewType fromParameter(String parameter) {
        return parameterMap.get(parameter);
    }
}
