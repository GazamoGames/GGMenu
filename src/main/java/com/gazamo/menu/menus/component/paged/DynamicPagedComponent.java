package com.gazamo.menu.menus.component.paged;

import com.gazamo.menu.menus.component.PagedComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author DarkSeraphim.
 */
public class DynamicPagedComponent extends PagedComponent {

    public interface PageProvider {
        ItemStack[][] getPage(Player player, int page);

        int getPageCount(Player player);
    }

    private final PageProvider provider;

    public DynamicPagedComponent(int width, int height, PageProvider provider) {
        super(width, height);
        this.provider = provider;
    }

    @Override
    public ItemStack[][] getPage(Player player, int page) {
        return this.provider.getPage(player, page);
    }

    @Override
    public int getPageCount(Player player) {
        return Math.max(this.provider.getPageCount(player), 1);
    }
}
