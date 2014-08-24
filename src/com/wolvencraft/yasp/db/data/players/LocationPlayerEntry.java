/*
 * LocationPlayerEntry.java
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
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.NormalData;
import com.wolvencraft.yasp.db.tables.Normal.PlayerLocations;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter(AccessLevel.PUBLIC)
public class LocationPlayerEntry extends NormalData {
    
    private final String playerName;
    
    public LocationPlayerEntry(int playerId, String playerName) {
        this.playerName = playerName;
        fetchData(playerId);
    }

    @Override
    public void fetchData(int playerId) {
        Player player = Bukkit.getPlayerExact(playerName);
        if(player == null) return;
        
        Location location = player.getLocation();
        
        if(!Query.table(PlayerLocations.TableName)
                .column(PlayerLocations.PlayerId)
                .condition(PlayerLocations.PlayerId, playerId)
                .exists())
            
            Query.table(PlayerLocations.TableName)
                .value(PlayerLocations.PlayerId, playerId)
                .value(PlayerLocations.ServerId, Statistics.getServerStatistics().ServerId())
                .value(PlayerLocations.World, location.getWorld().getName())
                .value(PlayerLocations.XCoord, location.getBlockX())
                .value(PlayerLocations.YCoord, location.getBlockY())
                .value(PlayerLocations.ZCoord, location.getBlockZ())
                .value(PlayerLocations.Biome, location.getWorld().getBiome(location.getBlockX(), location.getBlockZ()).name())
                .value(PlayerLocations.Humidity, location.getWorld().getHumidity(location.getBlockX(), location.getBlockZ()))
                .insert();
    }

    @Override
    public boolean pushData(int playerId) {
        Player player = Bukkit.getPlayerExact(playerName);
        if(player == null) return false;
        
        Location location = player.getLocation();
        
        return Query.table(PlayerLocations.TableName)
                .value(PlayerLocations.World, location.getWorld().getName())
                .value(PlayerLocations.XCoord, location.getBlockX())
                .value(PlayerLocations.YCoord, location.getBlockY())
                .value(PlayerLocations.ZCoord, location.getBlockZ())
                .value(PlayerLocations.Biome, location.getWorld().getBiome(location.getBlockX(), location.getBlockZ()).name())
                .value(PlayerLocations.Humidity, location.getWorld().getHumidity(location.getBlockX(), location.getBlockZ()))
                .condition(PlayerLocations.PlayerId, playerId)
                .update();
    }

    @Override
    @Deprecated
    public void clearData(int playerId) { }
    
}
