package com.gazamo.menu.menus.component;

import com.gazamo.menu.api.Component;
import com.gazamo.menu.api.Container;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author GazamoGames Development Team.
 */
public abstract class MenuComponent implements Component {

    @FunctionalInterface
    public interface ClickHandler {
        boolean handle(Player player, ClickType type, int x, int y);
    }

    private static class ItemStackKey {

        private final Object[] keys;

        private ItemStackKey(ItemStack stack) {
            if (!stack.hasItemMeta()) {
                this.keys = new Object[]{stack.getType(), stack.getDurability()};
            } else {
                ItemMeta meta = stack.getItemMeta();
                this.keys = new Object[]{stack.getType(), stack.getDurability(), meta.getDisplayName()};
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.keys);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ItemStackKey && Objects.deepEquals(this.keys, ((ItemStackKey) obj).keys);
        }

        @Override
        public String toString() {
            if (keys.length == 2) {
                return String.format("%s:%d", keys);
            } else {
                return String.format("%s:%d - %s", keys);
            }
        }

        public static ItemStackKey of(ItemStack stack) {
            return new ItemStackKey(stack);
        }
    }

    private static class ClickAction {
        private static final ClickAction NONE = new ClickAction(null, ClickType.DROP) {
            @Override
            boolean shouldAct(ClickType clickType) {
                return false;
            }
        };

        private final Consumer<Player> action;

        private final short types;

        ClickAction(Consumer<Player> action, ClickType type, ClickType...types) {
            this.action = action;
            this.types = Arrays.stream(types).map(Enum::ordinal).map(ord -> 1 << ord)
                                             .reduce(1 << type.ordinal(), (a, b) -> a | b).shortValue() ;
        }

        boolean shouldAct(ClickType clickType) {
            return (this.types & (1 << clickType.ordinal())) > 0;
        }

        void act(Player player) {
            this.action.accept(player);
        }
    }

    private Container parent;

    private final Map<ItemStackKey, ClickAction> actions;

    private int width;

    private int height;

    private ClickHandler clickHandler;

    private Collection<Consumer<Player>> cleanupTasks;

    protected MenuComponent(int width, int height) {
        this.width = width;
        this.height = height;
        this.actions = Maps.newHashMap();
        this.cleanupTasks = Lists.newLinkedList();
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void addAction(ItemStack stack, Consumer<Player> action, ClickType type, ClickType... moreTypes) {
        this.actions.put(ItemStackKey.of(stack), new ClickAction(action, type, moreTypes));
    }

    @Override
    public void onClick(Player player, ClickType click, int x, int y) {
        if (this.clickHandler != null && !this.clickHandler.handle(player, click, x, y)) {
            return;
        }
        getParent().getItem(player, this, x, y)
                   .map(ItemStackKey::of)
                   .map(this.actions::get).filter(Objects::nonNull)
                   .filter(action -> action.shouldAct(click)).ifPresent(action -> action.act(player));
    }

    public void onClick(ClickHandler handler) {
        this.clickHandler = handler;
    }

    @Override
    public void draw(Player player) {

    }

    @Override
    public final void onClose(Player player) {
        this.onClosure(player);
    }

    public void onClosure(Player player){
    }

    @Override
    public final Container getParent() {
        return this.parent;
    }

    @Override
    public final void setParent(Container parent) {
        this.parent = parent;
    }

    protected <T> Map<Player, T> createPlayerMap(Supplier<Map<Player, T>> constructor) {
        Map<Player, T> map = constructor.get();
        this.cleanupTasks.add(map::remove);
        return map;
    }

    protected <T extends Collection<Player>> T createPlayerCollection(Supplier<T> constructor) {
        T collection = constructor.get();
        this.cleanupTasks.add(collection::remove);
        return collection;
    }
}
