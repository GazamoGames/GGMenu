package com.gazamo.menu.api.size;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Erik Rosemberg
 * @since 20.01.2016
 */
@AllArgsConstructor
public enum MenuSize {

    ONE_ROW(9),
    TWO_ROWS(18),
    THREE_ROWS(27),
    FOUR_ROWS(36),
    FIVE_ROWS(45),
    SIX_ROWS(54);

    @Getter
    int size;

}
