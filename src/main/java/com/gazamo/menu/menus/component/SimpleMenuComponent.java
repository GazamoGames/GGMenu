package com.gazamo.menu.menus.component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    @Override
    public void onOpen(Player player) {
        IntStream.range(0, getHeight()).forEach(y -> {
            IntStream.range(0, getWidth()).forEach(x -> {
                this.setItem(player, x, y, this.contents[y][x]);
            });
        });
    }
}
