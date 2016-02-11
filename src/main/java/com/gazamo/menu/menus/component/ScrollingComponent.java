package com.gazamo.menu.menus.component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * @author GazamoGames Development Team.
 */
public class ScrollingComponent extends MenuComponent {
    enum ScrollMode {
        HORIZONTAL(ScrollingComponent::getWidth) {
            @Override
            protected void apply(Player player, ScrollingComponent component, int index, ItemStack[] slice) {
                for (int y = 0; y < slice.length; y++) {
                    component.setItem(player, index, y, slice[y]);
                }
            }
        },
        VERTICAL(ScrollingComponent::getHeight) {
            @Override
            protected void apply(Player player, ScrollingComponent component, int index, ItemStack[] slice) {
                for (int x = 0; x < slice.length; x++) {
                    component.setItem(player, x, index, slice[x]);
                }
            }
        };

        private final ToIntFunction<ScrollingComponent> getViewSize;

        ScrollMode(ToIntFunction<ScrollingComponent> getViewSize) {
            this.getViewSize = getViewSize;
        }

        public int getViewSize(ScrollingComponent component) {
            return this.getViewSize.applyAsInt(component);
        }

        protected abstract void apply(Player player, ScrollingComponent component, int index, ItemStack[] slice);
    }

    private final ScrollMode mode;

    private final List<ItemStack[]> items;

    private final Map<Player, Integer> offset;

    private final boolean wrap;

    public ScrollingComponent(int width, int height, ScrollMode mode) {
        this(width, height, mode, false);
    }

    public ScrollingComponent(int width, int height, ScrollMode mode, boolean wrap) {
        super(width, height);
        this.mode = mode;
        this.items = Lists.newArrayList();
        this.offset = createPlayerMap(Maps::<Player, Integer>newHashMap);
        this.wrap = wrap;
    }

    public void scroll(Player player, int steps) {
        int cursor = this.offset.getOrDefault(player, 0) + steps;
        if (!this.wrap && cursor >= this.items.size() - this.mode.getViewSize(this)) {
            cursor = this.items.size() - this.mode.getViewSize(this);
        }
        this.offset.put(player, cursor);

        for (int i = 0; i < this.mode.getViewSize(this); i++) {
            this.mode.apply(player, this, i, this.items.get((cursor + i) % this.items.size()));
        }
    }

}
