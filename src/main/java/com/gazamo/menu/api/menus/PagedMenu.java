package com.gazamo.menu.api.menus;

import com.gazamo.menu.api.Menu;
import com.gazamo.menu.api.item.MenuItem;
import com.gazamo.menu.api.size.MenuSize;

/**
 * @author Erik Rosemberg
 * @since 21.01.2016
 */
public class PagedMenu extends Menu {

    private Menu[] subMenus;

    public PagedMenu(String name, MenuSize size) {
        super(name, size);
    }

    @Override
    public Menu setItem(int index, MenuItem item) {
        return super.setItem(index, item); //TODO override.
    }
}
