package com.gazamo.menu.api.packet;

import com.gazamo.menu.menus.AnvilMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author DarkSeraphim.
 */
public interface AnvilInputProvider {

    static AnvilInputProvider getProvider() {
        RegisteredServiceProvider<AnvilInputProvider> rsp = Bukkit.getServicesManager().getRegistration(AnvilInputProvider.class);
        return rsp != null ? rsp.getProvider() : null;
    }

    void register(Player player, AnvilMenu menu);

    void unregister(Player player, AnvilMenu menu);
}
