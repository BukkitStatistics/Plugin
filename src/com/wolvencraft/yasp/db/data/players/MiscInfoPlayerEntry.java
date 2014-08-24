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
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.data.NormalData;
import com.wolvencraft.yasp.db.tables.DBTable;
import com.wolvencraft.yasp.db.tables.Normal.PlayerData;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.OnlineSessionCache;

/**
 * Represents all the miscellaneous information that does not fit any other category
 * @author bitWolfy
 *
 */
public class MiscInfoPlayerEntry extends NormalData {

    private final String playerName;
    private Map<DBTable, Object> values;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new MiscInfoPlayers object based on arguments provided
     * @param playerId Player ID
     * @param player Player object
     */
    public MiscInfoPlayerEntry(int playerId, Player player) {
        playerName = player.getName();
        
        values = new HashMap<DBTable, Object>();
        
        if(player.isOp()) values.put(PlayerData.IsOp, 1);
        else values.put(PlayerData.IsOp, 0);
        if(player.isBanned()) values.put(PlayerData.IsBanned, 1);
        else values.put(PlayerData.IsBanned, 0);
        
        InetAddress playerIp = player.getAddress().getAddress();
        if(playerIp == null) values.put(PlayerData.PlayerIp, "192.168.0.1");
        else values.put(PlayerData.PlayerIp, playerIp.getHostAddress());
        
        values.put(PlayerData.Gamemode, player.getGameMode().getValue());
        values.put(PlayerData.ExpPercent, player.getExp());
        values.put(PlayerData.ExpLevel, player.getLevel());
        values.put(PlayerData.FoodLevel, player.getFoodLevel());
        values.put(PlayerData.HealthLevel, player.getHealth());
        values.put(PlayerData.ArmorLevel, Util.getArmorRating(player.getInventory()));
               
        values.put(PlayerData.CurKillStreak, 0);
        values.put(PlayerData.MaxKillStreak, 0);
        
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
            values.put(PlayerData.MaxKillStreak, result.asInt(PlayerData.MaxKillStreak));
        }
    }

    @Override
    public boolean pushData(int playerId) {
        refreshPlayerData();
        boolean result = Query.table(PlayerData.TableName)
            .valueRaw(values)
            .condition(PlayerData.PlayerId, playerId)
            .condition(PlayerData.ServerId, Statistics.getServerStatistics().ServerId())
            .update();
        fetchData(playerId);
        return result;
    }
    
    public void clearData(int playerId) {       
        values.put(PlayerData.CurKillStreak, 0);
        values.put(PlayerData.MaxKillStreak, 0);
    }
    
    /**
     * Fetches the player data from the player, if he is online
     */
    public void refreshPlayerData() {
        Player player = Bukkit.getServer().getPlayerExact(playerName);
        if(player == null) return;

        if(player.isOp()) values.put(PlayerData.IsOp, 1);
        else values.put(PlayerData.IsOp, 0);
        if(player.isBanned()) values.put(PlayerData.IsBanned, 1);
        else values.put(PlayerData.IsBanned, 0);
        values.put(PlayerData.PlayerIp, player.getAddress().getAddress().getHostAddress());
        
        values.put(PlayerData.Gamemode, player.getGameMode().getValue());
        values.put(PlayerData.ExpPercent, player.getExp());
        values.put(PlayerData.ExpLevel, player.getLevel());
        values.put(PlayerData.FoodLevel, player.getFoodLevel());
        values.put(PlayerData.HealthLevel, player.getHealth());
        values.put(PlayerData.ArmorLevel, Util.getArmorRating(player.getInventory()));
    }
    
    
    /**
     * Logs player killing another player
     * @param player Player that was killed
     */
    public void killed(Player player) {
        OnlineSessionCache.fetch(player).died();
        int curKillStreak = ((Integer) values.get(PlayerData.CurKillStreak)).intValue() + 1;
        int maxKillStreak = ((Integer) values.get(PlayerData.MaxKillStreak)).intValue();
        values.put(PlayerData.CurKillStreak, curKillStreak);
        if(curKillStreak > maxKillStreak) {
            maxKillStreak++;
            values.put(PlayerData.MaxKillStreak, maxKillStreak);
        }
    }
    
    /**
     * Logs player being killed by mobs or natural causes
     */
    public void died() {
        int curKillStreak = ((Integer) values.get(PlayerData.CurKillStreak)).intValue();
        int maxKillStreak = ((Integer) values.get(PlayerData.MaxKillStreak)).intValue();
        if(curKillStreak > maxKillStreak) {
            maxKillStreak++;
            values.put(PlayerData.MaxKillStreak, maxKillStreak);
        }
        values.put(PlayerData.CurKillStreak, 0);
    }
}
