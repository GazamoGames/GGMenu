package com.gazamo.menu.api.item.defaults;

import com.gazamo.menu.api.Menu;
import com.gazamo.menu.api.MenuAction;
import com.gazamo.menu.api.item.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Erik Rosemberg
 * @since 21.01.2016
 */
public class PreviousPageItem extends MenuItem {

    private Menu parent;

    public PreviousPageItem(ItemStack item, Menu parent) {
        super(item);
        this.parent = parent;
    }

    @Override
    public boolean handleClick(Player player, MenuAction action) {
        player.closeInventory();

        parent.open(player);
        return true;
    }
}
