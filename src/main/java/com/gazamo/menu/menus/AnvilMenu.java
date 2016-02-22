package com.gazamo.menu.menus;

import com.gazamo.menu.Menu;
import com.gazamo.menu.PacketRecipient;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author DarkSeraphim.
 */
public class AnvilMenu extends Menu implements PacketRecipient {

    private String input = "";

    public AnvilMenu(String name) {
        super(name, 1, 3);
    }

    @Override
    protected Inventory createInventory(Player player) {
        return Bukkit.createInventory(player, InventoryType.ANVIL, this.getName());
    }

    @Override
    public void onAnvilNameChange(String input) {
        this.input = input != null ? input : "";
    }

    public String getInput() {
        return this.input;
    }
}
