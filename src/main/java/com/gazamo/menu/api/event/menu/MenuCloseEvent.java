package com.gazamo.menu.api.event.menu;

import com.gazamo.menu.Menu;
import org.bukkit.entity.Player;

/**
 * @author DarkSeraphim.
 */
public class MenuCloseEvent extends MenuEvent {
    public MenuCloseEvent(Menu menu, Player player) {
        super(MenuCloseEvent.class, menu, player);
    }
}
