/*
 * PlayersData.java
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

package com.wolvencraft.yasp.db.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.data.DataStore.DetailedData;
import com.wolvencraft.yasp.db.data.DataStore.NormalData;
import com.wolvencraft.yasp.db.tables.DBTable;
import com.wolvencraft.yasp.db.tables.Detailed.LogPlayers;
import com.wolvencraft.yasp.db.tables.Normal.DistancePlayersTable;
import com.wolvencraft.yasp.db.tables.Normal.MiscInfoPlayersTable;
import com.wolvencraft.yasp.db.tables.Normal.PlayersInv;
import com.wolvencraft.yasp.db.tables.Normal.PlayersTable;
import com.wolvencraft.yasp.settings.LocalConfiguration;
import com.wolvencraft.yasp.settings.Constants.StatPerms;
import com.wolvencraft.yasp.settings.Module;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.cache.OnlineSessionCache;
import com.wolvencraft.yasp.util.serializable.EffectsSerializable;
import com.wolvencraft.yasp.util.serializable.InventorySerializable;

/**
 * A unique data store that contains basic information about the player
 * @author bitWolfy
 *
 */
public class PlayersData {
    
    private int playerId;
    private Players generalData;
    private DistancePlayers distanceData;
    private MiscInfoPlayers miscData;
    private InventoryData inventoryData;
    
    private List<DetailedData> detailedData;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new PlayersData object based on the data provided
     * @param player Player object
     * @param playerId Player ID
     */
    public PlayersData(Player player, int playerId) {
        this.playerId = playerId;
        generalData = new Players(playerId, player);
        distanceData = new DistancePlayers(playerId);
        miscData = new MiscInfoPlayers(playerId, player);
        if(Module.Inventory.isEnabled()) inventoryData = new InventoryData(playerId, player);
        
        detailedData = new ArrayList<DetailedData>();
    }
    
    /**
     * Returns a static copy of DetailedData to prevent ConcurrentModificationException occurrences
     * @return List of DetailedData objects
     */
    private List<DetailedData> getDetailedData() {
        List<DetailedData> temp = new ArrayList<DetailedData>();
        for(DetailedData value : detailedData) temp.add(value);
        return temp;
    }
    
    /**
     * Pushes the data to the database
     */
    public void sync() {
        generalData.pushData(playerId);
        distanceData.pushData(playerId);
        miscData.pushData(playerId);
        if(Module.Inventory.isEnabled()) inventoryData.pushData(playerId);
        
        for(DetailedData entry : getDetailedData()) {
            if(entry.pushData(playerId)) { detailedData.remove(entry); }
        }
    }
    
    /**
     * Erases all locally stored data
     */
    public void dump() {
        for(DetailedData entry : getDetailedData()) {
            detailedData.remove(entry);
        }
    }
    
    /**
     * Returns the generic player data from the Players table.<br />
     * This information rarely changes
     * @return Players data store
     */
    public Players getGeneralData() {
        return generalData;
    }
    
    /**
     * Returns the information from the Distances table.
     * @return Distances data store
     */
    public DistancePlayers getDistanceData() {
        return distanceData;
    }
    
    /**
     * Returns the information from the Miscellaneous table.<br />
     * This information is likely to change rapidly.
     * @return Miscellaneous data store
     */
    public MiscInfoPlayers getMiscData() {
        return miscData;
    }
    
    /**
     * Logs player's login/logout location
     * @param location Location of the login
     * @param isLogin <b>true</b> if the player has logged in, <b>false</b> otherwise
     */
    public void addPlayerLog(Location location, boolean isLogin) {
        detailedData.add(new DetailedLogPlayersEntry(location, isLogin));
    }
    
    /**
     * Represents the Player data that is being tracked.<br />
     * Each entry must have a unique player name.
     * @author bitWolfy
     *
     */
    public class Players implements NormalData {
        
        private String username;
        private long lastSync;
        
        private long longestSession;
        private long currentSession;
        
        private long totalPlaytime;
        
        public Players (int playerId, Player player) {
            username = player.getName();
            lastSync = Util.getTimestamp();
            
            currentSession = 0;
            longestSession = 0;
            
            long firstLogin = lastSync;
            int logins = 0;
            
            QueryResult result = Query.table(PlayersTable.TableName)
                .column(PlayersTable.Logins)
                .column(PlayersTable.FirstLogin)
                .column(PlayersTable.Playtime)
                .column(PlayersTable.LongestSession)
                .condition(PlayersTable.PlayerId, playerId)
                .select();
            
            if(result == null) {
                Query.table(PlayersTable.TableName)
                     .value(PlayersTable.Name, username)
                     .insert();
            } else {
                firstLogin = result.asLong(PlayersTable.FirstLogin);
                if(firstLogin == -1) { firstLogin = lastSync; }
                logins = result.asInt(PlayersTable.Logins);
                this.totalPlaytime = result.asLong(PlayersTable.Playtime);
                longestSession = result.asLong(PlayersTable.LongestSession);
            }
            
            Query.table(PlayersTable.TableName)
                .value(PlayersTable.LoginTime, lastSync)
                .value(PlayersTable.FirstLogin, firstLogin)
                .value(PlayersTable.Logins, ++logins)
                .condition(PlayersTable.PlayerId, playerId)
                .update();
        }
        
        @Override
        @Deprecated
        public void fetchData(int playerId) { }
        
        @Override
        @Deprecated
        public void clearData(int playerId) { }
        
        @Override
        public boolean pushData(int playerId) {
            currentSession += Util.getTimestamp() - lastSync;
            if(longestSession > currentSession) longestSession = currentSession;
            totalPlaytime += Util.getTimestamp() - lastSync;
            lastSync = Util.getTimestamp();
            
            return Query.table(PlayersTable.TableName)
                .value(PlayersTable.Playtime, totalPlaytime)
                .value(PlayersTable.LongestSession, longestSession)
                .condition(PlayersTable.PlayerId, playerId)
                .update();
        }

        /**
         * Returns the total playtime for the player
         * @return Total playtime
         */
        public long getPlaytime() {
            return totalPlaytime;
        }
        
        /**
         * Returns the current session length
         * @return Current session length
         */
        public long getCurrentSession() {
            return currentSession;
        }
        
        /**
         * Returns the length of the longest play session
         * @return Longest session length
         */
        public long getLongestSession() {
            return longestSession;
        }
    }
    
    /**
     * Represents the distances a player traveled.
     * Only one entry per player is allowed.
     * @author bitWolfy
     *
     */
    public class DistancePlayers implements NormalData {
        
        private double foot;
        private double swim;
        private double flight;
        private double boat;
        private double minecart;
        private double pig;
        
        /**
         * Default constructor. Takes in the Player object and pulls corresponding values from the remote database.<br />
         * If no data is found in the database, the default values are inserted.
         * @param playerId ID of the tracked player
         */
        public DistancePlayers(int playerId) {
            foot = 0;
            swim = 0;
            flight = 0;
            boat = 0;
            minecart = 0;
            pig = 0;
            
            fetchData(playerId);
        }
        
        @Override
        public void fetchData(int playerId) {
            if(!LocalConfiguration.Standalone.asBoolean()) {
                clearData(playerId);
                return;
            }
            
            QueryResult result = Query.table(DistancePlayersTable.TableName)
                    .column(DistancePlayersTable.Foot)
                    .column(DistancePlayersTable.Swim)
                    .column(DistancePlayersTable.Flight)
                    .column(DistancePlayersTable.Boat)
                    .column(DistancePlayersTable.Minecart)
                    .column(DistancePlayersTable.Pig)
                    .condition(DistancePlayersTable.PlayerId, playerId)
                    .select();
            if(result == null) {
                Query.table(DistancePlayersTable.TableName)
                    .value(DistancePlayersTable.PlayerId, playerId)
                    .value(DistancePlayersTable.Foot, foot)
                    .value(DistancePlayersTable.Swim, swim)
                    .value(DistancePlayersTable.Flight, flight)
                    .value(DistancePlayersTable.Boat, boat)
                    .value(DistancePlayersTable.Minecart, minecart)
                    .value(DistancePlayersTable.Pig, pig)
                    .insert();
            } else {
                foot = result.asInt(DistancePlayersTable.Foot);
                swim = result.asInt(DistancePlayersTable.Swim);
                flight = result.asInt(DistancePlayersTable.Flight);
                boat = result.asInt(DistancePlayersTable.Boat);
                minecart = result.asInt(DistancePlayersTable.Minecart);
                pig = result.asInt(DistancePlayersTable.Pig);
            }
        }

        @Override
        public boolean pushData(int playerId) {
            boolean result = Query.table(DistancePlayersTable.TableName)
                .value(DistancePlayersTable.Foot, foot)
                .value(DistancePlayersTable.Swim, swim)
                .value(DistancePlayersTable.Flight, flight)
                .value(DistancePlayersTable.Boat, boat)
                .value(DistancePlayersTable.Minecart, minecart)
                .value(DistancePlayersTable.Pig, pig)
                .condition(DistancePlayersTable.PlayerId, playerId)
                .update(LocalConfiguration.Standalone.asBoolean());
            return result;
        }
        
        @Override
        public void clearData(int playerId) {
            foot = 0;
            swim = 0;
            flight = 0;
            boat = 0;
            minecart = 0;
            pig = 0;
        }
        
        /**
         * Increments the distance of the specified type by the amount
         * @param type Travel type
         * @param distance Distance travelled
         */
        public void addDistance(DistancePlayersTable type, double distance) {
            switch(type) {
                case Foot:
                    foot += distance;
                    break;
                case Swim:
                    swim += distance;
                    break;
                case Flight:
                    flight  += distance;
                    break;
                case Boat:
                    boat += distance;
                    break;
                case Minecart:
                    minecart += distance;
                    break;
                case Pig:
                    pig += distance;
                    break;
                default:
                    break;
            }
        }
    }
    
    /**
     * Represents all the miscellaneous information that does not fit any other category
     * @author bitWolfy
     *
     */
    public class MiscInfoPlayers implements NormalData {
        
        private Map<DBTable, Object> values;
        private String playerName;
        
        /**
         * <b>Default constructor</b><br />
         * Creates a new MiscInfoPlayers object based on arguments provided
         * @param playerId Player ID
         * @param player Player object
         */
        public MiscInfoPlayers(int playerId, Player player) {
            playerName = player.getPlayerListName();
            
            values = new HashMap<DBTable, Object>();
            
            if(player.isOp()) values.put(MiscInfoPlayersTable.IsOp, 1);
            else values.put(MiscInfoPlayersTable.IsOp, 0);
            if(player.isBanned()) values.put(MiscInfoPlayersTable.IsBanned, 1);
            else values.put(MiscInfoPlayersTable.IsBanned, 0);
            values.put(MiscInfoPlayersTable.PlayerIp, player.getAddress().getHostAddress());
            
            values.put(MiscInfoPlayersTable.Gamemode, player.getGameMode().getValue());
            values.put(MiscInfoPlayersTable.ExperiencePercent, player.getExp());
            values.put(MiscInfoPlayersTable.ExperienceTotal, player.getTotalExperience());
            values.put(MiscInfoPlayersTable.ExperienceLevel, player.getLevel());
            values.put(MiscInfoPlayersTable.FoodLevel, player.getFoodLevel());
            values.put(MiscInfoPlayersTable.HealthLevel, player.getHealth());
            values.put(MiscInfoPlayersTable.ArmorLevel, Util.getArmorRating(player.getInventory()));
            
            values.put(MiscInfoPlayersTable.FishCaught, 0);
            values.put(MiscInfoPlayersTable.TimesKicked, 0);
            values.put(MiscInfoPlayersTable.EggsThrown, 0);
            values.put(MiscInfoPlayersTable.FoodEaten, 0);
            values.put(MiscInfoPlayersTable.ArrowsShot, 0);
            values.put(MiscInfoPlayersTable.DamageTaken, 0);
            values.put(MiscInfoPlayersTable.BedsEntered, 0);
            values.put(MiscInfoPlayersTable.PortalsEntered, 0);
            values.put(MiscInfoPlayersTable.WordsSaid, 0);
            values.put(MiscInfoPlayersTable.CommandsSent, 0);
            values.put(MiscInfoPlayersTable.TimesJumped, 0);
            
            values.put(MiscInfoPlayersTable.CurKillStreak, 0);
            values.put(MiscInfoPlayersTable.MaxKillStreak, 0);
            
            fetchData(playerId);
        }
        
        @Override
        public void fetchData(int playerId) {
            if(!LocalConfiguration.Standalone.asBoolean()) {
                clearData(playerId);
                return;
            }
            
            QueryResult result = Query.table(MiscInfoPlayersTable.TableName)
                .condition(MiscInfoPlayersTable.PlayerId, playerId)
                .select();
            if(result == null) {
                Query.table(MiscInfoPlayersTable.TableName)
                    .value(MiscInfoPlayersTable.PlayerId, playerId)
                    .valueRaw(values)
                    .insert();
            } else {
                values.put(MiscInfoPlayersTable.FishCaught, result.asInt(MiscInfoPlayersTable.FishCaught));
                values.put(MiscInfoPlayersTable.TimesKicked, result.asInt(MiscInfoPlayersTable.TimesKicked));
                values.put(MiscInfoPlayersTable.EggsThrown, result.asInt(MiscInfoPlayersTable.EggsThrown));
                values.put(MiscInfoPlayersTable.FoodEaten, result.asInt(MiscInfoPlayersTable.FoodEaten));
                values.put(MiscInfoPlayersTable.ArrowsShot, result.asInt(MiscInfoPlayersTable.ArrowsShot));
                values.put(MiscInfoPlayersTable.DamageTaken, result.asInt(MiscInfoPlayersTable.DamageTaken));
                values.put(MiscInfoPlayersTable.BedsEntered, result.asInt(MiscInfoPlayersTable.BedsEntered));
                values.put(MiscInfoPlayersTable.PortalsEntered, result.asInt(MiscInfoPlayersTable.PortalsEntered));
                values.put(MiscInfoPlayersTable.WordsSaid, result.asInt(MiscInfoPlayersTable.WordsSaid));
                values.put(MiscInfoPlayersTable.CommandsSent, result.asInt(MiscInfoPlayersTable.CommandsSent));
                values.put(MiscInfoPlayersTable.MaxKillStreak, result.asInt(MiscInfoPlayersTable.MaxKillStreak));
                values.put(MiscInfoPlayersTable.TimesJumped, result.asInt(MiscInfoPlayersTable.TimesJumped));
            }
        }

        @Override
        public boolean pushData(int playerId) {
            refreshPlayerData();
            boolean result = Query.table(MiscInfoPlayersTable.TableName)
                .valueRaw(values)
                .condition(MiscInfoPlayersTable.PlayerId, playerId)
                .update(LocalConfiguration.Standalone.asBoolean());
            fetchData(playerId);
            return result;
        }
        
        public void clearData(int playerId) {
            values.put(MiscInfoPlayersTable.FishCaught, 0);
            values.put(MiscInfoPlayersTable.TimesKicked, 0);
            values.put(MiscInfoPlayersTable.EggsThrown, 0);
            values.put(MiscInfoPlayersTable.FoodEaten, 0);
            values.put(MiscInfoPlayersTable.ArrowsShot, 0);
            values.put(MiscInfoPlayersTable.DamageTaken, 0);
            values.put(MiscInfoPlayersTable.BedsEntered, 0);
            values.put(MiscInfoPlayersTable.PortalsEntered, 0);
            values.put(MiscInfoPlayersTable.WordsSaid, 0);
            values.put(MiscInfoPlayersTable.CommandsSent, 0);
            values.put(MiscInfoPlayersTable.TimesJumped, 0);
            
            values.put(MiscInfoPlayersTable.CurKillStreak, 0);
            values.put(MiscInfoPlayersTable.MaxKillStreak, 0);
        }
        
        /**
         * Fetches the player data from the player, if he is online
         */
        public void refreshPlayerData() {
            Player player = Bukkit.getServer().getPlayerExact(playerName);
            if(player == null) return;

            if(player.isOp()) values.put(MiscInfoPlayersTable.IsOp, 1);
            else values.put(MiscInfoPlayersTable.IsOp, 0);
            if(player.isBanned()) values.put(MiscInfoPlayersTable.IsBanned, 1);
            else values.put(MiscInfoPlayersTable.IsBanned, 0);
            values.put(MiscInfoPlayersTable.PlayerIp, player.getAddress().getAddress());
            
            values.put(MiscInfoPlayersTable.Gamemode, player.getGameMode().getValue());
            values.put(MiscInfoPlayersTable.ExperiencePercent, player.getExp());
            values.put(MiscInfoPlayersTable.ExperienceTotal, player.getTotalExperience());
            values.put(MiscInfoPlayersTable.ExperienceLevel, player.getLevel());
            values.put(MiscInfoPlayersTable.FoodLevel, player.getFoodLevel());
            values.put(MiscInfoPlayersTable.HealthLevel, player.getHealth());
            values.put(MiscInfoPlayersTable.ArmorLevel, Util.getArmorRating(player.getInventory()));
        }
        
        /**
         * Increments the specified miscellaneous statistic by 1
         * @param type Statistic type
         */
        public void incrementStat(MiscInfoPlayersTable type) {
            int value = 1;
            if(values.containsKey(type)) value = ((Integer) values.get(type)).intValue() + 1;
            values.put(type, value);
        }
        
        /**
         * Increments the miscellaneous statistic by the specified amount
         * @param type Statistic type
         * @param value Amount
         */
        public void incrementStat(MiscInfoPlayersTable type, int value) {
            if(values.containsKey(type)) value += ((Integer) values.get(type)).intValue();
            values.put(type, value);
        }
        
        /**
         * Logs player killing another player
         * @param player Player that was killed
         */
        public void killed(Player player) {
            OnlineSessionCache.fetch(player).died();
            int curKillStreak = ((Integer) values.get(MiscInfoPlayersTable.CurKillStreak)).intValue() + 1;
            int maxKillStreak = ((Integer) values.get(MiscInfoPlayersTable.MaxKillStreak)).intValue();
            values.put(MiscInfoPlayersTable.CurKillStreak, curKillStreak);
            if(curKillStreak > maxKillStreak) {
                maxKillStreak++;
                values.put(MiscInfoPlayersTable.MaxKillStreak, maxKillStreak);
            }
        }
        
        /**
         * Logs player being killed by mobs or natural causes
         */
        public void died() {
            int curKillStreak = ((Integer) values.get(MiscInfoPlayersTable.CurKillStreak)).intValue();
            int maxKillStreak = ((Integer) values.get(MiscInfoPlayersTable.MaxKillStreak)).intValue();
            if(curKillStreak > maxKillStreak) {
                maxKillStreak++;
                values.put(MiscInfoPlayersTable.MaxKillStreak, maxKillStreak);
            }
            values.put(MiscInfoPlayersTable.CurKillStreak, 0);
        }
    }
    
    /**
     * Represents player inventory and potion effects
     * @author bitWolfy
     *
     */
    public class InventoryData implements NormalData {
        
        String playerName;
        
        /**
         * <b>Default constructor</b><br />
         * Creates a new InventoryData object based on arguments provided
         * @param playerId Player ID
         */
        public InventoryData(int playerId, Player player) {
            this.playerName = player.getName();
            
            if(!Query.table(PlayersInv.TableName)
                    .column(PlayersInv.PlayerId)
                    .condition(PlayersInv.PlayerId, playerId)
                    .exists())
                Query.table(PlayersInv.TableName)
                    .value(PlayersInv.PlayerId, playerId)
                    .insert();
        }
        
        @Override
        @Deprecated
        public void fetchData(int playerId) { }
        
        @Override
        @Deprecated
        public void clearData(int playerId) { }
        
        @Override
        public boolean pushData(int playerId) {
            Player player = Bukkit.getPlayerExact(playerName);
            if(player == null) return false;
            if(!StatPerms.PlayerInventory.has(player)) return false;
            
            PlayerInventory inv = player.getInventory();
            List<ItemStack> invRow = new ArrayList<ItemStack>();
            
            for(int i = 9; i < 18; i++) { invRow.add(inv.getItem(i)); }
            String rowOne = InventorySerializable.serialize(invRow);
            invRow.clear();

            for(int i = 18; i < 27; i++) { invRow.add(inv.getItem(i)); }
            String rowTwo = InventorySerializable.serialize(invRow);
            invRow.clear();
            
            for(int i = 27; i < 36; i++) { invRow.add(inv.getItem(i)); }
            String rowThree = InventorySerializable.serialize(invRow);
            invRow.clear();

            for(int i = 0; i < 9; i++) { invRow.add(inv.getItem(i)); }
            String hotbar = InventorySerializable.serialize(invRow);
            invRow.clear();
            
            invRow.add(inv.getHelmet());
            invRow.add(inv.getChestplate());
            invRow.add(inv.getLeggings());
            invRow.add(inv.getBoots());
            String armor = InventorySerializable.serialize(invRow);
            
            String potionEffects = EffectsSerializable.serialize(player.getActivePotionEffects());
            
            Query.table(PlayersInv.TableName)
                .value(PlayersInv.Armor, armor)
                .value(PlayersInv.RowOne, rowOne)
                .value(PlayersInv.RowTwo, rowTwo)
                .value(PlayersInv.RowThree, rowThree)
                .value(PlayersInv.Hotbar, hotbar)
                .value(PlayersInv.PotionEffects, potionEffects)
                .condition(PlayersInv.PlayerId, playerId)
                .update();
            return false;
        }
    }
    
    /**
     * Tracks player's login and logout locations
     * @author bitWolfy
     *
     */
    public class DetailedLogPlayersEntry implements DetailedData {
        
        private long time;
        private boolean isLogin;
        private Location location;
         
        /**
         * <b>Default constructor</b><br />
         * Creates a new DetailedLogPlayersEntry object based on arguments provided
         * @param location Location of the event
         * @param isLogin <b>true</b> if the player has logged in, <b>false</b> if he logged off
         */
        public DetailedLogPlayersEntry(Location location, boolean isLogin) {
            time = Util.getTimestamp();
            this.isLogin = isLogin;
            this.location = location.clone();
        }
         
        @Override
        public boolean pushData(int playerId) {
            return Query.table(LogPlayers.TableName)
                    .value(LogPlayers.PlayerId, playerId)
                    .value(LogPlayers.Timestamp, time)
                    .value(LogPlayers.IsLogin, isLogin)
                    .value(LogPlayers.World, location.getWorld().getName())
                    .value(LogPlayers.XCoord, location.getBlockX())
                    .value(LogPlayers.YCoord, location.getBlockY())
                    .value(LogPlayers.ZCoord, location.getBlockZ())
                    .insert();
        }
     
    }
}
