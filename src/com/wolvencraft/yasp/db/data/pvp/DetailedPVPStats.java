/*
 * DetailedPVPStats.java
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

package com.wolvencraft.yasp.db.data.pvp;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DetailedData;
import com.wolvencraft.yasp.db.tables.Detailed.PlayerKillsPVP;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.MaterialCache;

public class DetailedPVPStats {
    
    /**
     * An immutable PVP entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class PVPEntry extends DetailedData {
        
        private final int victimId;
        private final MaterialData weapon;
        private final Location location;
        private final long timestamp;
        
        public PVPEntry(Location location, int victimId, ItemStack weapon) {
            this.victimId = victimId;
            this.weapon = weapon.getData();
            this.location = location.clone();
            timestamp = Util.getTimestamp();
        }
        
        @Override
        public boolean pushData(int killerId) {
            return Query.table(PlayerKillsPVP.TableName)
                    .value(PlayerKillsPVP.KillerId, killerId)
                    .value(PlayerKillsPVP.VictimId, victimId)
                    .value(PlayerKillsPVP.MaterialId, MaterialCache.parse(weapon))
                    .value(PlayerKillsPVP.World, location.getWorld().getName())
                    .value(PlayerKillsPVP.ServerId, Statistics.getServerStatistics().ServerId())
                    .value(PlayerKillsPVP.XCoord, location.getBlockX())
                    .value(PlayerKillsPVP.YCoord, location.getBlockY())
                    .value(PlayerKillsPVP.ZCoord, location.getBlockZ())
                    .value(PlayerKillsPVP.Timestamp, timestamp)
                    .insert();
        }
    }
}
