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
public class NextPageItem extends MenuItem {

    private Menu next;

    public NextPageItem(ItemStack item, Menu next) {
        super(item);
        this.next = next;
    }

    @Override
    public boolean handleClick(Player player, MenuAction action) {
        player.closeInventory();

        next.open(player);
        return true;
    }
}
