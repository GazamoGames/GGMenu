package com.gazamo.menu.api.event.menu;

import com.gazamo.menu.Menu;
import org.bukkit.entity.Player;

/**
 * @author DarkSeraphim.
 */
public class MenuOpenEvent extends MenuEvent {
    public MenuOpenEvent(Menu menu, Player player) {
        super(MenuOpenEvent.class, menu, player);
    }
}
