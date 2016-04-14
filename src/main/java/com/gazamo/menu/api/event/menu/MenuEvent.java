package com.gazamo.menu.api.event.menu;

import com.gazamo.menu.Menu;
import com.gazamo.menu.api.event.Event;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

/**
 * @author DarkSeraphim.
 */
public abstract class MenuEvent implements Event {

    private final Class<?> type;

    private final Menu menu;

    private final Player player;

    protected MenuEvent(Class<?> type, Menu menu, Player player) {
        this.type = type;
        this.menu = menu;
        this.player = player;
        Preconditions.checkState(type.isAssignableFrom(this.getClass()), "Type must be a (super) type of " + this.type.getName());
    }

    public final Menu getMenu() {
        return menu;
    }

    public final Player getPlayer() {
        return player;
    }

    @Override
    public final Class<?> getType() {
        return type;
    }
}
