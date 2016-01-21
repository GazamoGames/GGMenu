package com.gazamo.menu.api;

import lombok.Data;
import org.bukkit.event.inventory.ClickType;

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
