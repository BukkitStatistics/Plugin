/*
 * DetailedBlockStats.java
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

package com.wolvencraft.yasp.db.data.blocks;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DetailedData;
import com.wolvencraft.yasp.db.tables.Detailed.BlocksBroken;
import com.wolvencraft.yasp.db.tables.Detailed.BlocksPlaced;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.MaterialCache;

public class DetailedBlockStats {
    
    /**
     * An immutable block breaking entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class BlockBreakEntry extends DetailedData {
        
        private final BlockState block;
        private final long timestamp;
        
        public BlockBreakEntry(BlockState block) {
            this.block = block;
            timestamp = Util.getTimestamp();
        }

        @Override
        public boolean pushData(int playerId) {
            Location location = block.getLocation();
            return Query.table(BlocksBroken.TableName)
                .value(BlocksBroken.PlayerId, playerId)
                .value(BlocksBroken.MaterialId, MaterialCache.parse(block))
                .value(BlocksBroken.World, location.getWorld().getName())
                .value(BlocksBroken.ServerId, Statistics.getServerStatistics().getID())
                .value(BlocksBroken.XCoord, location.getBlockX())
                .value(BlocksBroken.YCoord, location.getBlockY())
                .value(BlocksBroken.ZCoord, location.getBlockZ())
                .value(BlocksBroken.Timestamp, timestamp)
                .insert();
        }
    }
    
    /**
     * An immutable block placement entry
     * @author bitWolfy
     *
     */
    @Getter(AccessLevel.PUBLIC)
    public static class BlockPlaceEntry extends DetailedData {
        
        private final BlockState block;
        private final long timestamp;
        
        public BlockPlaceEntry(BlockState block) {
            this.block = block;
            timestamp = Util.getTimestamp();
        }

        @Override
        public boolean pushData(int playerId) {
            Location location = block.getLocation();
            return Query.table(BlocksPlaced.TableName)
                .value(BlocksPlaced.PlayerId, playerId)
                .value(BlocksPlaced.MaterialId, MaterialCache.parse(block))
                .value(BlocksPlaced.World, location.getWorld().getName())
                .value(BlocksBroken.ServerId, Statistics.getServerStatistics().getID())
                .value(BlocksPlaced.XCoord, location.getBlockX())
                .value(BlocksPlaced.YCoord, location.getBlockY())
                .value(BlocksPlaced.ZCoord, location.getBlockZ())
                .value(BlocksPlaced.Timestamp, timestamp)
                .insert();
        }
    }
}
