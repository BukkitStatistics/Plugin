/*
 * SessionHandler.java
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

package com.wolvencraft.yasp.listeners.handlers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import org.bukkit.entity.Player;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.tables.Normal;
import com.wolvencraft.yasp.session.OnlineSession;
import com.wolvencraft.yasp.settings.RemoteConfiguration;
import com.wolvencraft.yasp.util.Message;
import com.wolvencraft.yasp.util.cache.OnlineSessionCache;
import org.bukkit.Bukkit;
import org.bukkit.metadata.FixedMetadataValue;

public class SessionHandlers {
    
    /**
     * Executed on player login
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    public static class PlayerLogin implements Runnable {
        
        private Player player;
        
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            Statistics.getServerStatistics().playerLogin();
            OnlineSessionCache.fetch(player, true);
            
            //Update the server on every join. Necessary because the session can still be cached.
            Bukkit.getScheduler().runTaskAsynchronously(Statistics.getInstance(), new Runnable() {
            @Override
                 public void run(){
                     Query.table(Normal.PlayerStats.TableName)
                          .value(Normal.PlayerStats.ServerId, Statistics.getServerStatistics().ServerId())
                          .condition(Normal.PlayerStats.PlayerId, OnlineSessionCache.fetch(player).getId())
                          .update();
                     
                     Query.table(Normal.PlayerStats.TableName)
                          .value(Normal.PlayerStats.Logins, 1)
                          .condition(Normal.PlayerStats.PlayerId, OnlineSessionCache.fetch(player).getId())
                          .condition(Normal.PlayerStats.Online, false)
                          .increment();
               }   
             });
            
            long stop = System.currentTimeMillis();
            Message.debug("Took "+(stop-start)+"ms to execute the PlayerJoin event.");
        }
    }
    
    /**
     * Executed on player logout
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    public static class PlayerLogout implements Runnable {
        
        private Player player;
        
        @Override
        public void run() {
            OnlineSession session = OnlineSessionCache.fetch(player);
            if(session.isReady()){
                session.getPlayersData().addPlayerLog(player.getLocation(), false);     
                //set Metadata to -1 (This player will probably get removed form the databaes later)
                long delay = RemoteConfiguration.LogDelay.asInteger();
                if(delay != 0 && OnlineSessionCache.fetch(player).getPlayersData().getGeneralData().getTotalPlaytime() < delay){
                    player.setMetadata("stats_id", new FixedMetadataValue(Statistics.getInstance(), -1));
                }
            }
            else player.setMetadata("stats_id", new FixedMetadataValue(Statistics.getInstance(), -1));
        }
    }
    
}
