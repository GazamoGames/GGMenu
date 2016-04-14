package com.gazamo.menu.api.event.page;

import com.gazamo.menu.api.event.Event;
import com.gazamo.menu.api.event.EventType;
import com.gazamo.menu.api.event.menu.MenuClickEvent;
import com.gazamo.menu.api.event.menu.MenuCloseEvent;
import com.gazamo.menu.api.event.menu.MenuDrawEvent;
import com.gazamo.menu.api.event.menu.MenuOpenEvent;
import com.gazamo.menu.menus.component.PagedComponent;
import org.bukkit.entity.Player;

/**
 * @author DarkSeraphim.
 */
public abstract class PageEvent implements Event {

    private final Class<?> type;

    private final PagedComponent pagedComponent;

    private final Player player;

    private int page;

    protected PageEvent(Class<?> type, PagedComponent pagedComponent, Player player, int page) {
        this.type = type;
        this.pagedComponent = pagedComponent;
        this.player = player;
        this.page = page;
    }

    public PagedComponent getPagedComponent() {
        return pagedComponent;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public final Class<?> getType() {
        return this.type;
    }
}
