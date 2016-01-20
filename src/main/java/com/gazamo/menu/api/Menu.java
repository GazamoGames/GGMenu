package com.gazamo.menu.api;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

/**
 * @author Erik Rosemberg
 * @since 20.01.2016
 */
public abstract class Menu {

    @Getter
    private final String name;
    @Getter
    private final int size;

    public Menu(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public abstract Menu listenForClosure(BiConsumer<Player, Menu> consumer);

    public abstract Menu open(Player player);
}
