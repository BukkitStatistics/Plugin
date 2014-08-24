/*
 * DetailedPVEStats.java
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

package com.wolvencraft.yasp.db.data.pve;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DetailedData;
import com.wolvencraft.yasp.db.tables.Detailed.PlayerKillsPVE;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.EntityCache;
import com.wolvencraft.yasp.util.cache.MaterialCache;

public class DetailedPVEStats {
    
    /**
     * An immutable PVE entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class PVEEntry extends DetailedData {
        
        private final EntityType creatureType;
        private final MaterialData weapon;
        private final Location location;
        private final boolean playerKilled;
        private final long timestamp;
        
        public PVEEntry (EntityType creatureType, Location location, ItemStack weapon) {
            this.creatureType = creatureType;
            this.weapon = weapon.getData();
            this.location = location.clone();
            playerKilled = false;
            timestamp = Util.getTimestamp();
        }
        
        /**
         * <b>Creature killed a player</b><br />
         * Creates a new DetailedPVEEntry where the creature killed a player.
         * @param creatureType Type of the creature
         * @param location Location of the event
         */
        public PVEEntry (EntityType creatureType, Location location) {
            this.creatureType = creatureType;
            this.weapon = new MaterialData(Material.AIR);
            this.location = location.clone();
            playerKilled = true;
            timestamp = Util.getTimestamp();
        }
        
        @Override
        public boolean pushData(int playerId) {
            return Query.table(PlayerKillsPVE.TableName)
                    .value(PlayerKillsPVE.PlayerId, playerId)
                    .value(PlayerKillsPVE.CreatureId, EntityCache.parse(creatureType))
                    .value(PlayerKillsPVE.PlayerKilled, playerKilled)
                    .value(PlayerKillsPVE.MaterialId, MaterialCache.parse(weapon))
                    .value(PlayerKillsPVE.ServerId, Statistics.getServerStatistics().ServerId())
                    .value(PlayerKillsPVE.World, location.getWorld().getName())
                    .value(PlayerKillsPVE.XCoord, location.getBlockX())
                    .value(PlayerKillsPVE.YCoord, location.getBlockY())
                    .value(PlayerKillsPVE.ZCoord, location.getBlockZ())
                    .value(PlayerKillsPVE.Timestamp, timestamp)
                    .insert();
        }
    }
}
