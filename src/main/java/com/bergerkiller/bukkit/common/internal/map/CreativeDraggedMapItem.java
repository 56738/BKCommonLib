package com.bergerkiller.bukkit.common.internal.map;

import org.bukkit.inventory.ItemStack;

/**
 * An item that a player picked up and is dragging through the inventory
 * while in creative mode.
 */
class CreativeDraggedMapItem {
    // How long a cached dragged item is kept around and tracked when in the creative player's control
    public static final int CACHED_ITEM_MAX_LIFE = 20*60*10; // 10 minutes
    public static final int CACHED_ITEM_CLEAN_INTERVAL = 60; //60 ticks

    public int life;
    public final ItemStack item;

    public CreativeDraggedMapItem(ItemStack item) {
        this.item = item;
        this.life = CACHED_ITEM_MAX_LIFE;
    }
}
