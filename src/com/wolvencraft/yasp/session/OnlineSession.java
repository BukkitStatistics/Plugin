/*
 * OnlineSession.java
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

package com.wolvencraft.yasp.session;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DataStore;
import com.wolvencraft.yasp.db.data.DataStore.DataStoreType;
import com.wolvencraft.yasp.db.data.deaths.DeathData;
import com.wolvencraft.yasp.db.data.players.PlayersData;
import com.wolvencraft.yasp.db.data.pve.PVEData;
import com.wolvencraft.yasp.db.data.pvp.PVPData;
import com.wolvencraft.yasp.db.tables.Normal.PlayerDistance;
import com.wolvencraft.yasp.db.tables.Normal.PlayerStats;
import com.wolvencraft.yasp.db.totals.PlayerTotals;
import com.wolvencraft.yasp.util.Message;
import com.wolvencraft.yasp.util.NamedInteger;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.OnlineSessionCache;
import com.wolvencraft.yasp.util.cache.PlayerCache;
import java.util.UUID;

/**
 * Represents a player session that is created when a player logs into the server.<br />
 * This session can only be created from a Player object, therefore, the player must have
 * logged into the server at least once.
 * @author bitWolfy
 *
 */
@SuppressWarnings("rawtypes")
@Getter(AccessLevel.PUBLIC)
public class OnlineSession implements PlayerSession {
    
    private final int id;
    private final String name;
    private final UUID uuid;
    private boolean isready;
    private PlayerTotals playerTotals;
    
    private PlayersData playersData;
    private List<DataStore> dataStores;
    
    private Scoreboard scoreboard;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new player session from the Player object
     * @param player Player object
     */
    public OnlineSession(Player player) {
        final Player tmp_player = player;
        this.uuid = tmp_player.getUniqueId();
        name = tmp_player.getName();
        id = PlayerCache.get(tmp_player);       
        this.isready = false;
        
        this.dataStores = new ArrayList<DataStore>();
        this.dataStores.addAll(Util.getModules(this));
        this.dataStores.addAll(Util.getHooks(this));
        
        this.scoreboard = null;
        
        //Run all database queries async to reduce lag on player join
        Bukkit.getScheduler().runTaskAsynchronously(Statistics.getInstance(), new Runnable() {
            @Override
            public void run(){
                        
                    playersData = new PlayersData(tmp_player, id);
                    
                    //If player is still online add an login location to the database
                    if(tmp_player.isOnline()){
                        playersData.addPlayerLog(tmp_player.getLocation(), true);
                    }
                    
                    setPlayersData(playersData);
                    playerTotals = new PlayerTotals(id);
                    setPlayersTotals(playerTotals);
        
                    Query.table(PlayerStats.TableName)
                    .value(PlayerStats.Online, true)
                    .value(PlayerStats.ServerId, Statistics.getServerStatistics().ServerId())
                    .condition(PlayerStats.PlayerId, id)
                    .update();  
                    
                    setReady();
            }
        });
    }
    
    private synchronized void setReady(){
        Message.debug("Session is ready!");
        this.isready = true;        
    }
    
    private synchronized void setPlayersTotals(PlayerTotals data){
        this.playerTotals = data;        
    }
    
    private synchronized void setPlayersData(PlayersData data){
        this.playersData = data;        
    }
    
    public boolean isReady() {
        return this.isready;
    }
    
    @Override
    public boolean isOnline() {
        if(Bukkit.getPlayerExact(name) == null) return false;
        return true;
    }
    
    @Override
    public UUID getUUID() {
        return this.uuid;
    }
    
    /**
     * Returns the Bukkit player object associated with this session
     * @return Player object
     */
    public Player getBukkitPlayer() {
        return Bukkit.getPlayerExact(name);
    }
    
    /**
     * Returns the data store with the specified type
     * @param type Data store type
     * @return Data store, or <b>null</b> if the type is not valid
     */
    public DataStore getDataStore(DataStoreType type) {
        for(DataStore store : dataStores) {
            if(store.getType().equals(type)) return store;
        }
        return null;
    }
    
    /**
     * Performs a database operation to push the locally stored data.
     */
    public void pushData() {
        playersData.sync();
        for(DataStore store : dataStores) store.pushData();
        
        playerTotals.fetchData();
    }
    
    /**
     * Dumps all locally stored data
     */
    public void dumpData() {
        for(DataStore store : dataStores) store.dump();
    }
    
    @Override
    public void finalize() {
        Query.table(PlayerStats.TableName)
            .value(PlayerStats.Online, false)
            .condition(PlayerStats.PlayerId, id)
            .condition(PlayerStats.ServerId, Statistics.getServerStatistics().ServerId())
            .update();
    }
    
    /**
     * Add distance of the specified type to the statistics
     * @param type Travel type
     * @param distance Distance traveled
     */
    public void addDistance(PlayerDistance type, double distance) {
        playersData.getDistanceData().addDistance(type, distance);
        playerTotals.addDistance(type, distance);
    }
    
    /**
     * Registers the player death in the data store
     * @param victim Player who was killed 
     * @param weapon Weapon used by killer
     */
    public void killedPlayer(Player victim, ItemStack weapon) {
        ((PVPData) getDataStore(DataStoreType.PVP)).playerKilledPlayer(victim, weapon);
        playersData.getMiscData().killed(victim);
        playerTotals.pvpKill();
        OnlineSessionCache.fetch(victim).getPlayerTotals().death();
    }
    
    /**
     * Registers the creature death in the data store
     * @param victim Creature killed
     * @param weapon Weapon used by killer
     */
    public void killedCreature(Entity victim, ItemStack weapon) {
        ((PVEData) getDataStore(DataStoreType.PVE)).playerKilledCreature(victim, weapon);
        playerTotals.pveKill();
    }
    
    /**
     * Registers the player death in the data store
     * @param killer Creature that killed the player
     * @param weapon Weapon used by killer
     */
    public void killedByCreature(Entity killer, ItemStack weapon) {
        ((PVEData) getDataStore(DataStoreType.PVE)).creatureKilledPlayer(killer, weapon);
        died();
    }
    
    /**
     * Runs when the session owner was killed by the environment
     * @param location Location of the death
     * @param cause Death cause
     */
    public void killedByEnvironment(Location location, DamageCause cause) {
        ((DeathData) getDataStore(DataStoreType.Deaths)).playerDied(location, cause);
        died();
    }
    
    /**
     * Runs when the player dies (any cause).<br />
     * This method is for internal use; you do not need to run it from listener
     */
    public void died() {
        playersData.getMiscData().died();
        playerTotals.death();
    }
    
    /**
     * Toggles the scoreboard state
     * @return <b>true</b> if the scoreboard has been turned on, <b>false</b> if it is now off
     */
    public boolean toggleScoreboard() {
        if(scoreboard != null) {
            clearScoreboard();
            scoreboard = null;
            return false;
        }
        
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        Bukkit.getServer().getPlayer(getUUID()).setScoreboard(scoreboard);
        
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        Objective stats = scoreboard.registerNewObjective("stats", "dummy");
        stats.setDisplaySlot(DisplaySlot.SIDEBAR);
        stats.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Statistics");        
        return true;
    }
    
    /**
     * Refreshes the statistics displayed on a scoreboard
     */
    public void refreshScoreboard() {
        if(scoreboard == null) return;

        Objective stats = scoreboard.getObjective("stats");
        
        for(NamedInteger value : playerTotals.getNamedValues()) {
            for(String name : value.getPossibleNames())
            stats.getScoreboard().resetScores(name);
            stats.getScore(value.getName())
                 .setScore((Integer) (value.getValue()));                
        }
    }
    
    /**
     * Clears the scoreboard from all content
     */
    public void clearScoreboard() {
        if(scoreboard == null) return;
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }
}
