package com.gazamo.menu;

import com.gazamo.menu.api.Component;
import com.gazamo.menu.api.Container;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * @author GazamoGames Development Team.
 */
public abstract class Menu implements Listener, Container {
    private static final String MENU_KEY = "GGMENU:Menu";

    private final String name;

    private BiConsumer<Player, Menu> closeConsumer;

    private final Map<Player, Inventory> byPlayer;

    private Map<Component, Position> components;

    private Component[][] bySlot;

    private int height;

    private int width;

    protected Menu(String name, int rows, int columns) {
        this.name = name;
        this.closeConsumer = (player, menu) -> {};
        this.byPlayer = Maps.newHashMap();
        this.components = new HashMap<>();
        this.height = rows;
        this.width = columns;
        this.bySlot = new Component[getHeight()][getWidth()];
        getHolder().getServer().getPluginManager().registerEvents(this, getHolder());
    }

    @Override
    public final int getWidth() {
        return this.width;
    }

    @Override
    public final int getHeight() {
        return this.height;
    }

    private void fill(Component component, Position where) {
        Component what = component;
        if (where == null) {
            where = this.components.get(component);
        } else {
            what = null;
        }
        int x = where.x;
        int y = where.y;
        for (int j = y; j < y + component.getHeight(); j++) {
            for (int i = x; i < x + component.getWidth(); i++) {
                this.bySlot[j][i] = what;
            }
        }
    }

    @Override
    public boolean addComponent(int x, int y, Component component) {
        Position in = Position.of(x, y);
        Position old = this.components.put(component, in);
        if (old != null) {
            if (!Objects.equals(old, in)) {
                this.components.put(component, old);
            }
            getHolder().getLogger().info("Duplicate component registry in " + getName());
            return false;
        }
        for (int j = y; j < y + component.getHeight(); j++) {
            for (int i = x; i < x + component.getWidth(); i++) {
                if (bySlot[j][i] != null) {
                    getHolder().getLogger().info("Component cannot be registered due to overlap" + getName());
                    this.components.remove(component);
                    return false; // Nope
                }
            }
        }
        fill(component, null);
        component.setParent(this);
        return true;
    }

    public Optional<Component> getComponent(int slot) {
        return getComponent(slot % getHeight(), slot / getWidth());
    }

    public Optional<Component> getComponent(int x, int y) {
        return Optional.ofNullable(this.bySlot[y][x]);
    }

    @Override
    public boolean removeComponent(Component component) {
        Position coords = this.components.remove(component);
        if (coords == null) {
            return false;
        }
        fill(component, coords);
        return true;
    }

    @Override
    public void setItem(Player player, Component component, int x, int y, ItemStack stack) {
        Position pos = this.components.get(component);
        x += pos.x;
        y += pos.y;
        int slot = y * getWidth() + x;
        getInventory(player).setItem(slot, stack);
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (this.hasOpen(player)) {
            this.components.keySet().forEach(component -> component.onOpen(player));
            this.getCloseConsumer().accept(player, this);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (this.hasOpen(player)) {
            int slot = event.getSlot();
            if (slot >= this.getSize())
                return;

            player.sendMessage("Component present: " + getComponent(slot).isPresent());

            getComponent(slot).ifPresent(component -> {
                Position pos = this.components.get(component);
                player.sendMessage("Position of component: " + pos);
                Position xy = Position.toPosition(this, slot);
                player.sendRawMessage("Position of slot: " + xy);
                component.onClick(player, event.getClick(), xy.x - pos.x, xy.y - pos.y);
            });

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (hasOpen(player)) {
            event.setCancelled(true);
            //TODO: Figure out how to handle this properly.
        }
    }

    protected abstract Inventory createInventory(Player player);

    protected Inventory getInventory(Player player) {
        return this.byPlayer.computeIfAbsent(player, this::createInventory);
    }

    protected int getSize() {
        return getWidth() * getHeight();
    }

    public String getName() {
        return this.name;
    }

    public BiConsumer<Player, Menu> getCloseConsumer() {
        return closeConsumer;
    }

    /**
     * Listens for the closing event of the inventory.
     * @param consumer {@link BiConsumer} that's going to be applied to the close event.
     * @return the {@link Menu} instance.
     */
    public Menu onClose(BiConsumer<Player, Menu> consumer) {
        this.closeConsumer = consumer != null ? consumer : (player, menu) -> {};
        return this;
    }

    @Override
    public Optional<ItemStack> getItem(Player player, Component component, int x, int y) {
        Position position = this.components.get(component);
        int slot = Position.toSlot(this, position.x + x, position.y * y);
        Inventory inv = getInventory(player);
        if (slot < 0 || slot > inv.getSize()) {
            return Optional.empty();
        }
        return Optional.ofNullable(inv.getItem(slot)).filter(stack -> stack.getType() != Material.AIR);
    }

    /**
     * Open the menu to the specified player.
     * @param player The {@link Player} the inventory is going to be shown to.
     * @return the {@link Menu} instance.
     */
    public final Menu open(Player player) {
        setMenu(player, this);
        player.openInventory(getInventory(player));
        this.components.keySet().forEach(component -> component.onOpen(player));
        return this;
    }

    protected void onOpen(Player player) {
    }

    protected void onClose(Player player) {
    }

    protected boolean hasOpen(Player player) {
        return getMenu(player).filter(this::equals).isPresent();
    }

    protected static Optional<Menu> getMenu(Player player) {
        return player.getMetadata(MENU_KEY).stream()
                .filter(meta -> Objects.equals(meta.getOwningPlugin(), getHolder()))
                .map(MetadataValue::value)
                .filter(Menu.class::isInstance).map(Menu.class::cast)
                .findFirst();
    }

    protected static void setMenu(Player player, Menu menu) {
        player.setMetadata(MENU_KEY, new FixedMetadataValue(getHolder(), menu));
    }

    public static JavaPlugin getHolder() {
        return JavaPlugin.getProvidingPlugin(Menu.class);
    }

    public static class Position {
        private final int x, y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            Position other = (Position) obj;
            return this.x == other.x && this.y == other.y;
        }

        public static Position of(int x, int y) {
            return new Position(x, y);
        }

        public static int toSlot(Menu menu, int x, int y) {
            return y * menu.getWidth() + x;
        }

        public static int toSlot(Menu menu, Position position) {
            return toSlot(menu, position.x, position.y);
        }

        @Override
        public String toString() {
            return String.format("[%d,%d]", this.x, this.y);
        }

        public static Position toPosition(Menu menu, int slot) {
            Bukkit.broadcastMessage("Slot: " + slot);
            Bukkit.broadcastMessage("Width: " + menu.getWidth());
            Bukkit.broadcastMessage("Height: " + menu.getHeight());
            Bukkit.broadcastMessage("x: " + (slot % menu.getWidth()));
            Bukkit.broadcastMessage("y: " + (slot / menu.getHeight()));
            return new Position(slot % menu.getWidth(), slot / menu.getHeight());
        }
    }
}
