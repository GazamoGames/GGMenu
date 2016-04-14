package com.gazamo.menu.api.event.menu;

import com.gazamo.menu.Menu;
import org.bukkit.entity.Player;

/**
 * @author DarkSeraphim.
 */
public class MenuDrawEvent extends MenuEvent {
    public MenuDrawEvent(Menu menu, Player player) {
        super(MenuDrawEvent.class, menu, player);
    }
}
