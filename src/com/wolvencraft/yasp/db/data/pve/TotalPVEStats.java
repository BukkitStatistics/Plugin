/*
 * TotalPVEEntry.java
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

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.data.NormalData;
import com.wolvencraft.yasp.db.tables.Normal.PVETotals;
import com.wolvencraft.yasp.settings.RemoteConfiguration;
import com.wolvencraft.yasp.util.cache.EntityCache;
import com.wolvencraft.yasp.util.cache.MaterialCache;

/**
 * Represents an entry in the PVE data store.
 * It is dynamic, i.e. it can be edited once it has been created.
 * @author bitWolfy
 *
 */
public class TotalPVEStats extends NormalData {
    
    private EntityType creatureType;
    private ItemStack weapon;
    private int playerDeaths;
    private int creatureDeaths;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new TotalPVE object based on the player and creature in question
     * @param playerId Player in question
     * @param creatureType Creature in question
     * @param weapon Weapon used
     */
    public TotalPVEStats(int playerId, EntityType creatureType, ItemStack weapon) {
        this.creatureType = creatureType;
        this.weapon = weapon.clone();
        this.weapon.setAmount(1);
        playerDeaths = 0;
        creatureDeaths = 0;
        
        fetchData(playerId); //checks if an entry for this data exists if not create a new entry
        
    }
    
    @Override
    public void fetchData(int playerId) {
        QueryResult result = Query.table(PVETotals.TableName)
                .column(PVETotals.PlayerKilled)
                .column(PVETotals.CreatureKilled)
                .condition(PVETotals.PlayerId, playerId)
                .condition(PVETotals.ServerId, Statistics.getServerStatistics().ServerId())
                .condition(PVETotals.CreatureId, EntityCache.parse(creatureType))
                .condition(PVETotals.MaterialId, MaterialCache.parse(weapon))
                .select();
        if(result == null) {
            Query.table(PVETotals.TableName)
                .value(PVETotals.PlayerId, playerId)
                .value(PVETotals.ServerId, Statistics.getServerStatistics().ServerId())
                .value(PVETotals.CreatureId, EntityCache.parse(creatureType))
                .value(PVETotals.MaterialId, MaterialCache.parse(weapon))
                .value(PVETotals.PlayerKilled, playerDeaths)
                .value(PVETotals.CreatureKilled, creatureDeaths)
                .insert();
        }
    }

    @Override
    public boolean pushData(int playerId) {
        boolean result = Query.table(PVETotals.TableName)
                .value(PVETotals.PlayerKilled, playerDeaths)
                .value(PVETotals.CreatureKilled, creatureDeaths)
                .condition(PVETotals.PlayerId, playerId)
                .condition(PVETotals.ServerId, Statistics.getServerStatistics().ServerId())
                .condition(PVETotals.CreatureId, EntityCache.parse(creatureType))
                .condition(PVETotals.MaterialId, MaterialCache.parse(weapon))
                .increment();
        if(result) clearData(playerId);
        return result;
    }
    
    @Override
    public void clearData(int playerId) {
        playerDeaths = 0;
        creatureDeaths = 0;
    }

    /**
     * Matches data provided in the arguments with the one in the entry.
     * @param creatureType Type of the creature
     * @param weapon Weapon used in the event
     * @return <b>true</b> if the data matches, <b>false</b> otherwise.
     */
    public boolean equals(EntityType creatureType, ItemStack weapon) {
        if(!this.creatureType.equals(creatureType)) return false;
        ItemStack comparableWeapon = weapon.clone();
        comparableWeapon.setAmount(1);
        return comparableWeapon.equals(this.weapon);
    }
    
    /**
     * Increments the number of times the player has died
     */
    public void addPlayerDeaths() {
        playerDeaths++;
    }
    
    /**
     * Increments the number of times the creature has died
     */
    public void addCreatureDeaths() {
        creatureDeaths++;
    }
}
