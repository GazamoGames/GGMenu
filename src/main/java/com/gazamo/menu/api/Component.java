package com.gazamo.menu.api;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author GazamoGames Development Team.
 */
public interface Component {
    int getWidth();

    int getHeight();

    default void setItem(Player forWhom, int x, int y, ItemStack stack) {
        Preconditions.checkArgument(x >= 0 && x < getWidth(), "X has to be [0 - width)");
        Preconditions.checkArgument(y >= 0 && y < getHeight(), "Y has to be [0 - height)");
        getParent().setItem(forWhom, this, x, y, stack);
    }

    default void onOpen(Player player) {
    }

    default void onClose(Player player) {
    }

    default void onClick(Player player, ClickType click, int x, int y) {
    }

    Container getParent();

    void setParent(Container container);
}
