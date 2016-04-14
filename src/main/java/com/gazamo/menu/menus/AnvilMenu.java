package com.gazamo.menu.menus;

import com.gazamo.menu.Menu;
import com.gazamo.menu.api.packet.AnvilInputProvider;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Map;

/**
 * @author DarkSeraphim.
 */
public class AnvilMenu extends Menu {

    private Map<Player, String> input;

    private AnvilInputProvider listener;

    private AnvilMenu(String name, AnvilInputProvider listener) {
        super(name, 1, 3);
        this.listener = listener;
        this.input = Maps.newHashMap();
    }

    @Override
    protected Inventory createInventory(Player player) {
        this.listener.register(player, this);
        return Bukkit.createInventory(player, InventoryType.ANVIL, this.getName(player));
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
        this.input.remove(player);
    }

    public void acceptInput(Player player, String input) {
        this.input.put(player, input != null ? input : "");
    }

    public String getInput(Player player) {
        return this.input.get(player);
    }

    public static AnvilMenu create(String name) {
        AnvilInputProvider listener = AnvilInputProvider.getProvider();
        Preconditions.checkNotNull(listener, "No PacketHandler found. We are unable to detect anvil input.");
        return new AnvilMenu(name, listener);
    }

}
