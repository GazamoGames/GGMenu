package com.gazamo.menu.menus.component.paged;

import com.gazamo.menu.menus.component.PagedComponent;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author DarkSeraphim.
 */
public class SimplePagedComponent extends PagedComponent {

    private final List<ItemStack[][]> pages;

    public SimplePagedComponent(int width, int height) {
        super(width, height);
        this.pages = Lists.newArrayList();
    }

    public void setItem(int page, int x, int y, ItemStack stack) {
        this.getPage(page)[y][x] = stack;
    }

    @Override
    public ItemStack[][] getPage(Player player, int page) {
        return this.getPage(page);
    }

    public ItemStack[][] getPage(int page) {
        while (page >= this.getPageCount()) {
            this.pages.add(new ItemStack[getWidth()][getHeight()]);
        }
        return this.pages.get(page);
    }

    @Override
    public int getPageCount(Player player) {
        return this.getPageCount();
    }

    public int getPageCount() {
        return Math.max(this.pages.size(), 1);
    }
}
