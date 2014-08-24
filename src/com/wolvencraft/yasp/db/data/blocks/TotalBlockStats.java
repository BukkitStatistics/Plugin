/*
 * TotalBlocksEntry.java
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

import com.wolvencraft.yasp.Statistics;
import org.bukkit.block.BlockState;

import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.data.NormalData;
import com.wolvencraft.yasp.db.tables.Normal.BlockTotals;
import com.wolvencraft.yasp.settings.Constants.ItemsWithMetadata;
import com.wolvencraft.yasp.settings.RemoteConfiguration;
import com.wolvencraft.yasp.util.cache.MaterialCache;

/**
 * Represents an entry in the PVP data store.
 * It is dynamic, i.e. it can be edited once it has been created.
 * @author bitWolfy
 *
 */
public class TotalBlockStats extends NormalData {
    
    private BlockState block;
    private int broken;
    private int placed;

    /**
     * <b>Default constructor</b><br />
     * Creates a new TotalItemsEntry based on the data provided
     * @param block BlockState of the block
     */
    public TotalBlockStats(int playerId, BlockState block) {
        this.block = block;
        broken = 0;
        placed = 0;
        
        fetchData(playerId); //checks if an entry for this data exists if not create a new entry 
    }
    
    @Override
    public void fetchData(int playerId) {        
        QueryResult result = Query.table(BlockTotals.TableName)
                .column(BlockTotals.Destroyed)
                .column(BlockTotals.Placed)
                .condition(BlockTotals.PlayerId, playerId)
                .condition(BlockTotals.MaterialId, MaterialCache.parse(block))
                .condition(BlockTotals.ServerId, Statistics.getServerStatistics().ServerId())
                .select();
        
        if(result == null) {
            Query.table(BlockTotals.TableName)
                .value(BlockTotals.PlayerId, playerId)
                .value(BlockTotals.ServerId, Statistics.getServerStatistics().ServerId())
                .value(BlockTotals.MaterialId, MaterialCache.parse(block))
                .value(BlockTotals.Destroyed, broken)
                .value(BlockTotals.Placed, placed)
                .insert();
        }
    }

    @Override
    public boolean pushData(int playerId) {
        boolean result = Query.table(BlockTotals.TableName)
            .value(BlockTotals.Destroyed, broken)
            .value(BlockTotals.Placed, placed)
            .condition(BlockTotals.PlayerId, playerId)
            .condition(BlockTotals.MaterialId, MaterialCache.parse(block))
            .condition(BlockTotals.ServerId, Statistics.getServerStatistics().ServerId())
            .increment();
        if(result) clearData(playerId);
        return result;
    }
    
    @Override
    public void clearData(int playerId) {
        broken = 0;
        placed = 0;
    }
    
    /**
     * Checks if the object corresponds to provided parameters
     * @param block Block to compare to
     * @return <b>true</b> if the conditions are met, <b>false</b> otherwise
     */
    public boolean equals(BlockState block) {
        if(ItemsWithMetadata.contains(block.getTypeId())) {
            return block.getType().equals(this.block.getType()) && block.getData().getData() == this.block.getData().getData();
        } else {
            return block.getType().equals(this.block.getType());
        }
    }
    
    /**
     * Increments the number of blocks to the total number of blocks destroyed
     */
    public void addBroken() {
        broken ++;
    }
    
    /**
     * Increments the number of blocks to the total number of blocks placed
     */
    public void addPlaced() {
        placed ++;
    }
}
