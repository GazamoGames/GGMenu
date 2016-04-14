package com.gazamo.menu.menus;

import com.gazamo.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author DarkSeraphim.
 */
public class HopperMenu extends Menu {

    public HopperMenu(String name) {
        super(name, 1, 5);
    }

    @Override
    protected Inventory createInventory(Player player) {
        return Bukkit.createInventory(player, InventoryType.HOPPER, getName(player));
    }
}
