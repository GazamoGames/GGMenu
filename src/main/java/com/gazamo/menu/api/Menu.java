package com.gazamo.menu.api;

import com.gazamo.menu.api.item.MenuItem;
import com.gazamo.menu.api.size.MenuSize;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    @Getter
    protected final Map<Integer, MenuItem> items = Maps.newHashMap();

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
                if (this.getCloseConsumer() != null) {
                    this.getCloseConsumer().accept((Player) event.getPlayer(), this);
                }
            }
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!Objects.equals(event.getInventory(), inventory))
            return;
        int slot = event.getRawSlot();
        if (slot >= this.getSize().getSize())
            return;

        Optional<MenuItem> item = this.getItemAt(slot);

        if (item.isPresent()) {
            MenuAction action = new MenuAction(event.getClick(), this, slot);
            boolean cancel = item.get().handleClick((Player) event.getWhoClicked(), action);
            event.setCancelled(cancel);
        }
    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        if (!Objects.equals(event.getInventory(), inventory))
            return;

        event.setCancelled(true);
        //TODO: Figure out how to handle this properly.
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
    public Menu open(Player player) {
        player.openInventory(this.getInventory());
        return this;
    }

    /**
     * Sets an item in the menu to be show to the player.
     * @param index The slot where the item is going to be set at
     * @param item  The item going to be set.
     * @return the {@link Menu} instance.
     */
    public Menu setItem(int index, MenuItem item) {
        Preconditions.checkArgument(index < 0, "index has to be greater than 0");

        this.getInventory().setItem(index, (item == null ? null : item.getItem()));
        this.getItems().put(index, item);
        return this;
    }

    /**
     * Easier way of checking if menu contains a certain item.
     * @param index slot we're analyzing to see if there's an item.
     * @return {@link Optional} instance, might be a nulled optional.
     */
    public Optional<MenuItem> getItemAt(int index) {
        return Optional.ofNullable(this.getItems().get(index));
    }
}
