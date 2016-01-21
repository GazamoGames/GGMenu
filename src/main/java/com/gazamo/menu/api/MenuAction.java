package com.gazamo.menu.api;

import com.gazamo.menu.api.item.ClickType;
import lombok.Data;

/**
 * @author Erik Rosemberg
 * @since 20.01.2016
 */
@Data
public class MenuAction {

    private final ClickType type;
    private final Menu menu;
    private final int slot;

}
