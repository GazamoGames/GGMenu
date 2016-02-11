package com.gazamo.menu.menus;

import com.gazamo.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * @author GazamoGames Development Team.
 */
public class ChestMenu extends Menu {

    public ChestMenu(String name, int rows) {
        super(name, rows, 9);
    }

    @Override
    protected Inventory createInventory(Player player) {
        return Bukkit.createInventory(player, this.getSize(), this.getName());
    }
}
