/*
 * DetailedItemStats.java
 * 
 * Statistics
 * Copyright (C) 2013 bitWolfy <http://www.wolvencraft.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.wolvencraft.yasp.db.data.items;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DetailedData;
import com.wolvencraft.yasp.db.tables.Detailed.ItemsConsumed;
import com.wolvencraft.yasp.db.tables.Detailed.ItemsDropped;
import com.wolvencraft.yasp.db.tables.Detailed.ItemsPickedUp;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.MaterialCache;

public class DetailedItemStats {
    
    /**
     * An immutable item drop entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class ItemDropEntry extends DetailedData {
        
        private final ItemStack stack;
        private final Location location;
        private final long timestamp;
        
        public ItemDropEntry(Location location, ItemStack stack) {
            this.stack = stack.clone();
            //this.stack.setAmount(1);
            this.location = location.clone();
            timestamp = Util.getTimestamp();
        }
        
        @Override
        public boolean pushData(int playerId) {
            return Query.table(ItemsDropped.TableName)
                    .value(ItemsDropped.PlayerId, playerId)
                    .value(ItemsDropped.ServerId, Statistics.getServerStatistics().ServerId())
                    .value(ItemsDropped.MaterialId, MaterialCache.parse(stack))
                    .value(ItemsDropped.Amount, stack.getAmount())
                    .value(ItemsDropped.World, location.getWorld().getName())
                    .value(ItemsDropped.XCoord, location.getBlockX())
                    .value(ItemsDropped.YCoord, location.getBlockY())
                    .value(ItemsDropped.ZCoord, location.getBlockZ())
                    .value(ItemsDropped.Timestamp, timestamp)
                    .insert();
        }
    }
    
    /**
     * An immutable item pickup entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class ItemPickupEntry extends DetailedData {
        
        private final ItemStack stack;
        private final int amount;
        private final Location location;
        private final long timestamp;
        
        public ItemPickupEntry(Location location, ItemStack stack, int amount) {
            this.stack = stack.clone();
            this.amount = amount;
            this.location = location.clone();
            timestamp = Util.getTimestamp();
        }
        
        @Override
        public boolean pushData(int playerId) {
            return Query.table(ItemsPickedUp.TableName)
                    .value(ItemsPickedUp.PlayerId, playerId)
                    .value(ItemsDropped.ServerId, Statistics.getServerStatistics().ServerId())
                    .value(ItemsPickedUp.Material, MaterialCache.parse(stack))
                    .value(ItemsDropped.Amount, this.amount)
                    .value(ItemsPickedUp.World, location.getWorld().getName())
                    .value(ItemsPickedUp.XCoord, location.getBlockX())
                    .value(ItemsPickedUp.YCoord, location.getBlockY())
                    .value(ItemsPickedUp.ZCoord, location.getBlockZ())
                    .value(ItemsPickedUp.Timestamp, timestamp)
                    .insert();
        }
    }
    
    /**
     * An immutable item consume entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class ItemConsumeEntry extends DetailedData {

        private final ItemStack stack;
        private final Location location;
        private final long timestamp;
        
        public ItemConsumeEntry(Location location, ItemStack stack) {
            this.stack = stack.clone();
            this.stack.setAmount(1);
            this.location = location.clone();
            timestamp = Util.getTimestamp();
        }
        
        @Override
        public boolean pushData(int playerId) {
            return Query.table(ItemsConsumed.TableName)
                    .value(ItemsConsumed.PlayerId, playerId)
                    .value(ItemsDropped.ServerId, Statistics.getServerStatistics().ServerId())
                    .value(ItemsConsumed.MaterialId, MaterialCache.parse(stack))
                    .value(ItemsConsumed.World, location.getWorld().getName())
                    .value(ItemsConsumed.XCoord, location.getBlockX())
                    .value(ItemsConsumed.YCoord, location.getBlockY())
                    .value(ItemsConsumed.ZCoord, location.getBlockZ())
                    .value(ItemsConsumed.Timestamp, timestamp)
                    .insert();
        }
    }
}
