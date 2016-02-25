package com.gazamo.menu.menus.component;

import com.gazamo.menu.api.Component;
import com.gazamo.menu.api.Container;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

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

    private final Map<ItemStack, ClickAction> actions;

    private int width;

    private int height;

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
        this.actions.put(stack, new ClickAction(action, type, moreTypes));
    }

    @Override
    public void onClick(Player player, ClickType click, int x, int y) {
        player.sendMessage("MenuComponent#onClick was reached");
        player.sendMessage("Item was " + getParent().getItem(player, this, x, y).map(ItemStack::getType).map(Enum::name).orElse("null"));
        player.sendMessage("ClickType was " + click.name());
        getParent().getItem(player, this, x, y)
                   .map(this.actions::get).filter(Objects::nonNull)
                   .filter(action -> action.shouldAct(click)).ifPresent(action -> action.act(player));
    }

    @Override
    public final void onClose(Player player) {

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
