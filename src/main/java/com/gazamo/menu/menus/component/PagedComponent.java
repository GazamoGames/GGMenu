package com.gazamo.menu.menus.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author GazamoGames Development Team.
 */
public class PagedComponent extends MenuComponent {

    private final List<ItemStack[][]> pages;

    private final Map<Player, Integer> pageByPlayer;

    public PagedComponent(int width, int height) {
        super(width, height);
        this.pages = Lists.newArrayList();
        this.pageByPlayer = createPlayerMap(Maps::<Player, Integer>newHashMap);
    }

    public void setItem(int page, int x, int y, ItemStack stack) {
        while (page >= this.pages.size()) {
            this.pages.add(new ItemStack[getWidth()][getHeight()]);
        }
        this.pages.get(page)[y][x] = stack;
    }

    public void nextPage(Player player) {
        int next = (this.pageByPlayer.getOrDefault(player, 1) + 1) % this.pages.size();
        showPage(player, next);
    }

    public void previousPage(Player player) {
        int prev = (this.pageByPlayer.getOrDefault(player, 1) - 1) % this.pages.size();
        if (prev < 0) {
            prev += this.pages.size();
        }
        this.pageByPlayer.put(player, prev);
        showPage(player, prev);
    }

    public void showPage(Player player, int page) {
        ItemStack[][] pageData = this.pages.get(page);
        for (int y = 0; y < pageData.length; y++) {
            for (int x = 0; x < pageData[y].length; x++) {
                setItem(player, x, y, pageData[y][x]);
            }
        }
    }
}
