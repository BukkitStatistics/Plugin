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

package com.mctrakr.session;

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

import com.mctrakr.db.Query;
import com.mctrakr.db.data.DataStore;
import com.mctrakr.db.data.DataStore.ModuleType;
import com.mctrakr.db.data.deaths.DeathsDataStore;
import com.mctrakr.db.data.distance.DistancesDataStore;
import com.mctrakr.db.data.distance.Tables.DistancesTable;
import com.mctrakr.db.data.misc.MiscDataStore;
import com.mctrakr.db.data.player.Tables.PlayersTable;
import com.mctrakr.db.data.pve.PveDataStore;
import com.mctrakr.db.data.pvp.PvpDataStore;
import com.mctrakr.db.totals.PlayerTotals;
import com.mctrakr.managers.ModuleManager;
import com.mctrakr.util.NamedInteger;
import com.mctrakr.util.cache.PlayerCache;
import com.mctrakr.util.cache.SessionCache;

/**
 * Represents a player session that is created when a player logs into the server.<br />
 * This session can only be created from a Player object, therefore, the player must have
 * logged into the server at least once.
 * @author bitWolfy
 *
 */
@Getter(AccessLevel.PUBLIC)
public class OnlineSession implements PlayerSession {
    
    private final int id;
    private final String name;
    private PlayerTotals playerTotals;
    
    private List<DataStore> dataStores;
    
    private Scoreboard scoreboard;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new player session from the Player object
     * @param player Player object
     */
    public OnlineSession(Player player) {
        name = player.getName();
        id = PlayerCache.get(player);
        
        this.dataStores = new ArrayList<DataStore>();
        this.dataStores.addAll(ModuleManager.getModules(this));
        
        this.playerTotals = new PlayerTotals(id);
        this.scoreboard = null;
        
        Query.table(PlayersTable.TableName)
            .value(PlayersTable.Online, true)
            .condition(PlayersTable.PlayerId, id)
            .update();
    }
    
    @Override
    public boolean isOnline() {
        if(Bukkit.getPlayerExact(name) == null) return false;
        return true;
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
    public DataStore getDataStore(ModuleType type) {
        return getDataStore(type.getAlias());
    }
    
    /**
     * Returns the data store with the specified type
     * @param type Data store type
     * @return Data store, or <b>null</b> if the type is not valid
     */
    public DataStore getDataStore(String type) {
        for(DataStore store : dataStores) {
            if(store.getType().equals(type)) return store;
        }
        return null;
    }
    
    /**
     * Performs a database operation to push the locally stored data.
     */
    public void pushData() {
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
        Query.table(PlayersTable.TableName)
            .value(PlayersTable.Online, false)
            .condition(PlayersTable.PlayerId, id)
            .update();
    }
    
    /**
     * Add distance of the specified type to the statistics
     * @param type Travel type
     * @param distance Distance traveled
     */
    public void addDistance(DistancesTable type, double distance) {
        ((DistancesDataStore) getDataStore(ModuleType.Distance)).playerTravel(type, distance);
        playerTotals.addDistance(type, distance);
    }
    
    /**
     * Registers the player death in the data store
     * @param victim Player who was killed 
     * @param weapon Weapon used by killer
     */
    public void killedPlayer(Player victim, ItemStack weapon) {
        ((PvpDataStore) getDataStore(ModuleType.PVP)).playerKilledPlayer(victim, weapon);
        ((MiscDataStore) getDataStore(ModuleType.Misc)).getNormalData().killed(victim);
        playerTotals.pvpKill();
        SessionCache.fetch(victim).getPlayerTotals().death();
    }
    
    /**
     * Registers the creature death in the data store
     * @param victim Creature killed
     * @param weapon Weapon used by killer
     */
    public void killedCreature(Entity victim, ItemStack weapon) {
        ((PveDataStore) getDataStore(ModuleType.PVE)).playerKilledCreature(victim, weapon);
        playerTotals.pveKill();
    }
    
    /**
     * Registers the player death in the data store
     * @param killer Creature that killed the player
     * @param weapon Weapon used by killer
     */
    public void killedByCreature(Entity killer, ItemStack weapon) {
        ((PveDataStore) getDataStore(ModuleType.PVE)).creatureKilledPlayer(killer, weapon);
        died();
    }
    
    /**
     * Runs when the session owner was killed by the environment
     * @param location Location of the death
     * @param cause Death cause
     */
    public void killedByEnvironment(Location location, DamageCause cause) {
        ((DeathsDataStore) getDataStore(ModuleType.Deaths)).playerDied(location, cause);
        died();
    }
    
    /**
     * Runs when the player dies (any cause).<br />
     * This method is for internal use; you do not need to run it from listener
     */
    public void died() {
        ((MiscDataStore) getDataStore(ModuleType.Misc)).getNormalData().died();
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
        Bukkit.getServer().getPlayer(getName()).setScoreboard(scoreboard);
        
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
            for(String name : value.getPossibleNames()) scoreboard.resetScores(Bukkit.getOfflinePlayer(name));
            stats.getScore(Bukkit.getOfflinePlayer(value.getName()))
                 .setScore((Integer) (value.getValue()));
        }
    }
    
    /**
     * Clears the scoreboard from all content
     */
    public void clearScoreboard() {
        if(scoreboard == null) return;
        scoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + "PVP Kills"));
        scoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + "PVE Kills"));
        scoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.RED + "Deaths"));
        
        scoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.AQUA + "Blocks Broken"));
        scoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.AQUA + "Blocks Placed"));

        scoreboard.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "Travelled"));
    }
}