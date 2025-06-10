package com.nhnacademy.exam.hotel.controller;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserAuthzValidator {
    private static final Long VALID_USER_ID = 100L;

    public static Boolean isValid(Long userId) {
        if (Objects.isNull(userId))
            return false;

        return VALID_USER_ID.longValue() == userId.longValue();
    }

}
