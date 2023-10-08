package com.example.demo.enumeration;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductSlotType {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15),
    SIXTEEN(16),
    SEVENTEEN(17),
    EIGHTEEN(18),
    NINETEEN(19),
    TWENTY(20);

    private final Integer value;

    public static ProductSlotType fromValue(Integer value) {
        return Arrays.stream(values())
            .filter(productSlotType -> productSlotType.value.equals(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid ProductSlotType value: " + value));
    }

}
