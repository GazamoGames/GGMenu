package com.gazamo.menu.menus.component;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.*;
import java.util.stream.IntStream;

/**
 * @author DarkSeraphim.
 */
public class SimpleMenuComponent extends MenuComponent {

    private ItemStack[][] contents;

    public SimpleMenuComponent(int width, int height) {
        super(width, height);
        this.contents = new ItemStack[getHeight()][getWidth()];
    }

    public void setItem(int x, int y, ItemStack stack) {
        this.contents[y][x] = stack;
    }

    public void setItem(int x, int y, ItemStack stack, Consumer<Player> action) {
        this.setItem(x, y, stack);
        this.addAction(stack, action, ClickType.LEFT);
    }

    public void setToggle(int x, int y, boolean onState, ItemStack on, ItemStack off, BiConsumer<Player, Boolean> toggle) {
        this.addAction(on, player -> {
            toggle.accept(player, false);
            this.setItem(player, x, y, off);
        }, ClickType.LEFT);
        this.addAction(off, player -> {
            toggle.accept(player, true);
            this.setItem(player, x, y, on);
        }, ClickType.LEFT);
        this.setItem(x, y, onState ? on : off);
    }

    public void setNextPage(int x, int y, PagedComponent component, BiFunction<Integer, Integer, ItemStack> pageStack, Function<Integer, ItemStack> last) {
        this.setItem(x, y, pageStack.apply(0, 0), player -> {
            component.nextPage(player);
        });
        component.onPage((player, page) -> {
            if (page < component.getPageCount(player) - 1) {
                this.setItem(player, x, y, pageStack.apply(component.getPageNumber(player) + 1, component.getPageCount(player)));
            } else {
                this.setItem(player, x, y, last.apply(component.getPageCount(player)));
            }
            return true;
        });
    }

    public void setPreviousPage(int x, int y, PagedComponent component, BiFunction<Integer, Integer, ItemStack> pageStack, Function<Integer, ItemStack> first) {
        this.setItem(x, y, pageStack.apply(0, 0), player -> {
            component.previousPage(player);
        });
        component.onPage((player, page) -> {
            if (0 < page) {
                this.setItem(player, x, y, pageStack.apply(component.getPageNumber(player) + 1, component.getPageCount(player)));
            } else {
                this.setItem(player, x, y, first.apply(component.getPageCount(player)));
            }
            return true;
        });
    }

    @Override
    public void draw(Player player) {
        IntStream.range(0, getHeight()).forEach(y -> {
            IntStream.range(0, getWidth()).forEach(x -> {
                this.setItem(player, x, y, this.contents[y][x]);
            });
        });
    }
}
