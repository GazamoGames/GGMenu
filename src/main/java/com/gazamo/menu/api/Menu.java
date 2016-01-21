package com.gazamo.menu.api;

import com.gazamo.menu.api.size.MenuSize;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiConsumer;

/**
 * @author Erik Rosemberg
 * @since 20.01.2016
 */
public abstract class Menu implements Listener {

    @Getter
    private final String name;
    @Getter
    private final MenuSize size;
    @Getter
    private final Inventory inventory;

    @Getter
    private BiConsumer<Player, Menu> closeConsumer;

    //TODO: Store item array

    @Getter
    private final JavaPlugin holder = JavaPlugin.getProvidingPlugin(Menu.class);

    public Menu(String name, MenuSize size) {
        this.name = name;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, this.size.getSize(), this.name);

        this.getHolder().getServer().getPluginManager().registerEvents(this, this.getHolder());
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            //TODO: Handle multiple page menus?

            if (event.getPlayer() instanceof Player) {
                this.getCloseConsumer().accept((Player) event.getPlayer(), this);
            }
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {

    }

    @EventHandler
    public void on(InventoryMoveItemEvent event) {

    }

    @EventHandler
    public void on(InventoryDragEvent event) {

    }

    /**
     * Listens for the closing event of the inventory.
     * @param consumer {@link BiConsumer} that's going to be applied to the close event.
     * @return the {@link Menu} instance.
     */
    public Menu listenForClosure(BiConsumer<Player, Menu> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    /**
     * Open the menu to the specified player.
     * @param player The {@link Player} the inventory is going to be shown to.
     * @return the {@link Menu} instance.
     */
    public abstract Menu open(Player player);
}
