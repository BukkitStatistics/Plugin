/*
 * MiscInfoPlayerEntry.java
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

package com.wolvencraft.yasp.db.data.players;

import com.wolvencraft.yasp.Statistics;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.data.NormalData;
import com.wolvencraft.yasp.db.tables.DBTable;
import com.wolvencraft.yasp.db.tables.Normal.PlayerData;

/**
 * Represents all the miscellaneous information that does not fit any other category
 * @author bitWolfy
 *
 */
public class MiscInfoPlayerStats extends NormalData {

    private final String playerName;
    private Map<DBTable, Object> values;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new MiscInfoPlayers object based on arguments provided
     * @param playerId Player ID
     * @param player Player object
     */
    public MiscInfoPlayerStats(int playerId, Player player) {
        playerName = player.getName();
        
        values = new HashMap<DBTable, Object>();
                
        values.put(PlayerData.ExpTotal,0);
        values.put(PlayerData.FishCaught, 0);
        values.put(PlayerData.TimesKicked, 0);
        values.put(PlayerData.EggsThrown, 0);
        values.put(PlayerData.FoodEaten, 0);
        values.put(PlayerData.ArrowsShot, 0);
        values.put(PlayerData.DamageTaken, 0);
        values.put(PlayerData.BedsEntered, 0);
        values.put(PlayerData.PortalsEntered, 0);
        values.put(PlayerData.WordsSaid, 0);
        values.put(PlayerData.CommandsSent, 0);
        values.put(PlayerData.TimesJumped, 0);
        
        fetchData(playerId);
    }
    
    @Override
    public void fetchData(int playerId) {
        QueryResult result = Query.table(PlayerData.TableName)
            .condition(PlayerData.PlayerId, playerId)
            .condition(PlayerData.ServerId, Statistics.getServerStatistics().ServerId())
            .select();
        if(result == null) {
            Query.table(PlayerData.TableName)
                .value(PlayerData.PlayerId, playerId)
                .value(PlayerData.ServerId, Statistics.getServerStatistics().ServerId())
                .valueRaw(values)
                .insert();
        } else {
            values.put(PlayerData.ExpTotal, result.asInt(PlayerData.ExpTotal));
            values.put(PlayerData.FishCaught, result.asInt(PlayerData.FishCaught));
            values.put(PlayerData.TimesKicked, result.asInt(PlayerData.TimesKicked));
            values.put(PlayerData.EggsThrown, result.asInt(PlayerData.EggsThrown));
            values.put(PlayerData.FoodEaten, result.asInt(PlayerData.FoodEaten));
            values.put(PlayerData.ArrowsShot, result.asInt(PlayerData.ArrowsShot));
            values.put(PlayerData.DamageTaken, result.asDouble(PlayerData.DamageTaken));
            values.put(PlayerData.BedsEntered, result.asInt(PlayerData.BedsEntered));
            values.put(PlayerData.PortalsEntered, result.asInt(PlayerData.PortalsEntered));
            values.put(PlayerData.WordsSaid, result.asInt(PlayerData.WordsSaid));
            values.put(PlayerData.CommandsSent, result.asInt(PlayerData.CommandsSent));
            values.put(PlayerData.TimesJumped, result.asInt(PlayerData.TimesJumped));
        }
    }

    @Override
    public boolean pushData(int playerId) {
        boolean result = Query.table(PlayerData.TableName)
            .valueRaw(values)
            .condition(PlayerData.PlayerId, playerId)
            .condition(PlayerData.ServerId, Statistics.getServerStatistics().ServerId())
            .update();
        clearData(playerId);
        return result;
    }
    
    public void clearData(int playerId) {
        values.put(PlayerData.ExpTotal, 0);
        values.put(PlayerData.FishCaught, 0);
        values.put(PlayerData.TimesKicked, 0);
        values.put(PlayerData.EggsThrown, 0);
        values.put(PlayerData.FoodEaten, 0);
        values.put(PlayerData.ArrowsShot, 0);
        values.put(PlayerData.DamageTaken, 0);
        values.put(PlayerData.BedsEntered, 0);
        values.put(PlayerData.PortalsEntered, 0);
        values.put(PlayerData.WordsSaid, 0);
        values.put(PlayerData.CommandsSent, 0);
        values.put(PlayerData.TimesJumped, 0);
    }
    
    
    /**
     * Increments the specified miscellaneous statistic by 1
     * @param type Statistic type
     */
    public void incrementStat(PlayerData type) {
        double value = 1;
        if(values.containsKey(type)) {
            Object valueObj = values.get(type);
            if(valueObj instanceof Double)
                value = ((Double) valueObj).doubleValue() + 1;
            else value = ((Integer) valueObj).doubleValue() + 1;
        }
        values.put(type, value);
    }
    
    /**
     * Increments the miscellaneous statistic by the specified amount
     * @param type Statistic type
     * @param value Amount
     */
    public void incrementStat(PlayerData type, double value) {
        if(values.containsKey(type)) {
            Object valueObj = values.get(type);
            if(valueObj instanceof Double)
                value += ((Double) valueObj).doubleValue();
            else value += ((Integer) valueObj).doubleValue();
        }
        values.put(type, value);
    }
}

  
