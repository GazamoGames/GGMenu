package com.gazamo.menu.menus;

import com.gazamo.menu.Menu;
import com.gazamo.menu.api.packet.AnvilInputProvider;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * @author DarkSeraphim.
 */
public class AnvilMenu extends Menu {

    private String input = "";

    private AnvilInputProvider listener;

    private AnvilMenu(String name, AnvilInputProvider listener) {
        super(name, 1, 3);
        this.listener = listener;
    }

    @Override
    protected Inventory createInventory(Player player) {
        return Bukkit.createInventory(player, InventoryType.ANVIL, this.getName());
    }

    @Override
    protected void onOpen(Player player) {
        super.onOpen(player);
        this.listener.register(player, this);
    }

    @Override
    protected void onClose(Player player) {
        super.onClose(player);
        this.listener.unregister(player, this);
    }

    public void acceptInput(String input) {
        this.input = input != null ? input : "";
    }

    public String getInput() {
        return this.input;
    }

    public static AnvilMenu create(String name) {
        AnvilInputProvider listener = AnvilInputProvider.getProvider();
        Preconditions.checkNotNull(listener, "No PacketHandler found. We are unable to detect anvil input.");
        return new AnvilMenu(name, listener);
    }

}
