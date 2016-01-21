package com.gazamo.menu.api.item;

import com.gazamo.menu.api.MenuAction;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Erik Rosemberg
 * @since 20.01.2016
 */
public abstract class MenuItem {

    @Getter
    protected ItemStack item;

    /**
     * Handles the click for the player.
     * @param player The {@link Player} who's clicking the item.
     * @param action The {@link MenuAction} called when a player clicks.
     * @return true if successful, false if not successful.
     */
    public abstract boolean handleClick(Player player, MenuAction action);

}
