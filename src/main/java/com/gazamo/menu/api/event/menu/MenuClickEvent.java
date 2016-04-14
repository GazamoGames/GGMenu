package com.gazamo.menu.api.event.menu;

import com.gazamo.menu.Menu;
import com.gazamo.menu.api.event.Cancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 * @author DarkSeraphim.
 */
public class MenuClickEvent extends MenuEvent implements Cancellable {
    private boolean cancelled;

    private final int x;

    private final int y;

    private final ClickType clickType;

    public MenuClickEvent(Menu menu, Player player, int x, int y, ClickType clickType) {
        super(MenuClickEvent.class, menu, player);
        this.x = x;
        this.y = y;
        this.clickType = clickType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
