/*
 * DetailedDeathStats.java
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

package com.wolvencraft.yasp.db.data.deaths;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DetailedData;
import com.wolvencraft.yasp.db.tables.Detailed.PlayerDeaths;
import com.wolvencraft.yasp.util.Util;

public class DetailedDeathStats {
    
    /**
     * An immutable natural death entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC) 
    public static class NaturalDeathEntry extends DetailedData {
        
        private DamageCause cause;
        private Location location;
        private long timestamp;
        
        public NaturalDeathEntry(Location location, DamageCause cause) {
            this.cause = cause;
            this.location = location.clone();
            timestamp = Util.getTimestamp();
        }

        @Override
        public boolean pushData(int playerId) {
            return Query.table(PlayerDeaths.TableName)
                    .value(PlayerDeaths.PlayerId, playerId)
                    .value(PlayerDeaths.Cause, cause.name())
                    .value(PlayerDeaths.World, location.getWorld().getName())
                    .value(PlayerDeaths.ServerId, Statistics.getServerStatistics().getID())
                    .value(PlayerDeaths.XCoord, location.getBlockX())
                    .value(PlayerDeaths.YCoord, location.getBlockY())
                    .value(PlayerDeaths.ZCoord, location.getBlockZ())
                    .value(PlayerDeaths.Timestamp, timestamp)
                    .insert();
        }
    }
}
