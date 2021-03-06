package com.gazamo.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Optional;

/**
 * @author GazamoGames Development Team.
 */
public interface Container {

    int getWidth();

    int getHeight();

    String getName(Player player);

    boolean addComponent(int x, int y, Component component);

    boolean removeComponent(Component component);

    void setItem(Player player, Component component, int x, int y, ItemStack stack);

    Optional<ItemStack> getItem(Player player, Component menuComponent, int x, int y);
}
