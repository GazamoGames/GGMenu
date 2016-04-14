package com.gazamo.menu.api.event;

/**
 * @author DarkSeraphim.
 */
public interface Cancellable {
    void setCancelled(boolean flag);

    boolean isCancelled();
}
