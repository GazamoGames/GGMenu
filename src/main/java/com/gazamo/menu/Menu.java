package com.gazamo.menu;

import com.gazamo.menu.api.Component;
import com.gazamo.menu.api.Container;
import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * @author GazamoGames Development Team.
 */
public abstract class Menu implements Listener, Container {
    private static final String MENU_KEY = "GGMENU:Menu";

    private String defaultName;

    private BiConsumer<Player, Menu> closeConsumer;

    private BiConsumer<Player, Menu> openConsumer;

    private final Map<Player, Inventory> byPlayer;

    private Function<Player, String> nameProvider;

    private Map<Component, Position> components;

    private Component[][] bySlot;

    private int height;

    private int width;

    private boolean nameSwitch;

    protected Menu(String name, int rows, int columns) {
        this.defaultName = name;
        this.closeConsumer = (player, menu) -> {};
        this.openConsumer = (player, menu) -> {};
        this.nameProvider = player -> null;
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

    public boolean isNameSwitch() {
        return nameSwitch;
    }

    public void setNameProvider(Function<Player, String> provider) {
        this.nameProvider = provider != null ? provider : player -> null;
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
            getHolder().getLogger().info("Duplicate component registry in " + getDefaultName());
            return false;
        }
        for (int j = y; j < y + component.getHeight(); j++) {
            for (int i = x; i < x + component.getWidth(); i++) {
                if (bySlot[j][i] != null) {
                    getHolder().getLogger().info("Component cannot be registered due to overlap" + getDefaultName());
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
        return getComponent(slot % getWidth(), slot / getWidth());
    }

    public Optional<Component> getComponent(int x, int y) {
        if (y < 0 || y >= this.bySlot.length) {
            return Optional.empty();
        }
        Component[] row =  this.bySlot[y];
        if (x < 0 || x >= row.length) {
            return Optional.empty();
        }
        return Optional.ofNullable(row[x]);
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
    private void on(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();
        if (!this.nameSwitch && this.hasOpen(player)) {
            this.components.keySet().forEach(component -> {
                try {
                    component.onClose(player);
                } catch (Throwable t) {
                    getHolder().getLogger().log(Level.WARNING, "An exception was caught whilst handling a component", t);
                }
            });
            this.onClose(player);
            Menu.closeMenu(player, this);
        }
    }

    @EventHandler
    private void on(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (this.hasOpen(player)) {
            int slot = event.getSlot();
            if (slot >= this.getSize()) {
                return;
            }

            event.setCancelled(true);
            getComponent(slot).ifPresent(component -> {
                Position pos = this.components.get(component);
                Position xy = Position.toPosition(this, slot);
                component.onClick(player, event.getClick(), xy.x - pos.x, xy.y - pos.y);
            });
        }
    }

    @EventHandler
    private void on(InventoryDragEvent event) {
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

    public String getDefaultName() {
        return this.defaultName;
    }

    public String getName(Player player) {
        String name = this.nameProvider.apply(player);
        return name != null ? name : getDefaultName();
    }

    public BiConsumer<Player, Menu> getCloseConsumer() {
        return closeConsumer;
    }
    public BiConsumer<Player, Menu> getOpenConsumer() {
        return openConsumer;
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

    public Menu onOpen(BiConsumer<Player, Menu> consumer) {
        this.openConsumer = consumer != null ? consumer : (player, menu) -> {};
        return this;
    }

    @Override
    public Optional<ItemStack> getItem(Player player, Component component, int x, int y) {
        Position position = this.components.get(component);
        int slot = Position.toSlot(this, position.x + x, position.y + y);
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
        if (this.hasOpen(player)) {
            // Nope
            return this;
        }
        InventoryView view = player.openInventory(getInventory(player));
        if (!view.getTopInventory().equals(getInventory(player))) {
            throw new IllegalStateException("Failed to open inventory (was the InventoryOpenEvent cancelled?)");
        }
        setMenu(player, this);
        this.onOpen(player);
        this.components.keySet().forEach(component -> {
            component.onOpen(player);
            component.draw(player);
        });
        return this;
    }

    public void refresh(Player player) {
        if (!this.hasOpen(player)) {
            return;
        }
        if (!this.nameProvider.apply(player).equals(getInventory(player).getTitle())) {
            this.byPlayer.remove(player);
            this.nameSwitch = true;
            InventoryView view = player.openInventory(getInventory(player));
            if (!view.getTopInventory().equals(getInventory(player))) {
                throw new IllegalStateException("Failed to open inventory (was the InventoryOpenEvent cancelled?)");
            }
            this.nameSwitch = false;
        }
        this.components.keySet().forEach(component -> {
            component.onOpen(player);
            component.draw(player);
        });
    }

    protected void onOpen(Player player) {
        this.openConsumer.accept(player, this);
    }

    protected void onClose(Player player) {
        this.closeConsumer.accept(player, this);
        this.byPlayer.remove(player);
    }

    public boolean hasOpen(Player player) {
        return getMenu(player).filter(this::equals).isPresent();
    }

    public static boolean hasAnyMenu(Player player) {
        return getMenu(player).isPresent();
    }

    protected static Optional<Menu> getMenu(Player player) {
        return player.getMetadata(MENU_KEY).stream()
                .filter(meta -> Objects.equals(meta.getOwningPlugin(), getHolder()))
                .map(MetadataValue::value)
                .filter(Menu.class::isInstance).map(Menu.class::cast)
                .findFirst();
    }

    protected static void closeMenu(Player player, Menu menu) {
        boolean same = getMenu(player).map(menu::equals).orElse(false);
        if (same) {
            player.removeMetadata(MENU_KEY, getHolder());
        }
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

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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
            return new Position(slot % menu.getWidth(), slot / menu.getWidth());
        }
    }
}
