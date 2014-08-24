/*
 * ServerStatistics.java
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

import com.wolvencraft.yasp.Statistics;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.tables.Server.ServerStatsTable;
import com.wolvencraft.yasp.db.tables.Normal;
import com.wolvencraft.yasp.settings.LocalConfiguration;
import com.wolvencraft.yasp.util.Util;
import com.wolvencraft.yasp.util.VariableManager.ServerVariable;
import com.wolvencraft.yasp.util.tasks.TickTask;

/**
 * Data collector that stores server-specific information.
 * @author bitWolfy
 *
 */
public class ServerStatistics {
    
    private long lastSyncTime;
    
    private long firstStartup;
    private long lastStartup;
    private long currentUptime;
    private long totalUptime;
    private long lastShutdown;

    private long totalMemory;
    private long freeMemory;
    private int ticksPerSecond;
    private int availableProcessors;

    private String serverIP;
    private int serverId;
    private String serverName;
    private int serverPort;
    private String serverMOTD;
    private String bukkitVersion;
    private int plugins;

    private long serverTime;
    private boolean weather;
    private int weatherDuration;
    
    private int maxPlayersOnline;
    private long maxPlayersOnlineTime;
    
    private int maxPlayersAllowed;
    
    /**
     * <b>Default constructor</b><br />
     * Creates an empty data store to save the statistics until database synchronization.
     */
    public ServerStatistics() {
        long curTime = Util.getTimestamp();
        
        lastSyncTime = curTime;
        
        firstStartup = 0;
        lastStartup = curTime;
        currentUptime = 0;
        totalUptime = 0;
        lastShutdown = curTime;
        
        Runtime runtime = Runtime.getRuntime();
        totalMemory = runtime.totalMemory();
        freeMemory = runtime.freeMemory();
        ticksPerSecond = TickTask.getTicksPerSecond();
        availableProcessors = runtime.availableProcessors();
        
        serverPort = Bukkit.getPort();
        serverMOTD = Bukkit.getMotd();
        bukkitVersion = Bukkit.getBukkitVersion();
        plugins = Bukkit.getServer().getPluginManager().getPlugins().length;

        World mainWorld = Bukkit.getWorlds().get(0);
        serverTime = mainWorld.getTime();
        weather = mainWorld.hasStorm();
        weatherDuration = mainWorld.getWeatherDuration();
        
        maxPlayersOnline = 0;
        maxPlayersOnlineTime = 0;
        maxPlayersAllowed = Bukkit.getMaxPlayers();
        
        serverIP = Bukkit.getIp();
        if(serverIP.equals("")) {
            try {
                InetAddress in = InetAddress.getLocalHost();
                InetAddress[] all = InetAddress.getAllByName(in.getHostName());
                serverIP = all[0].getHostAddress();
            } catch (Throwable t) { serverIP = ""; }
        }
        
        if(LocalConfiguration.Bungee.toBoolean()){
            serverName = LocalConfiguration.ServerName.toString();
            do{
                serverId = -1;
                QueryResult serverRow = Query.table(ServerStatsTable.TableName)
                    .column(ServerStatsTable.Id)
                    .condition(ServerStatsTable.Name, serverName)
                    .select();
                
                if(serverRow == null) {
                    this.createEntry();
                } else {
                    serverId = serverRow.asInt(ServerStatsTable.Id);
                }      
            } while(serverId == -1);
            
        } else {
            serverName = "default";
            serverId = 1;
        }
        
        if(Query.table(ServerStatsTable.TableName).condition(ServerStatsTable.Id, serverId).exists()){
            QueryResult result = Query.table(ServerStatsTable.TableName).condition(ServerStatsTable.Id, serverId).select();         
            firstStartup = result.asLong(ServerStatsTable.FirstStartup);
            totalUptime = result.asLong(ServerStatsTable.TotalUptime);
            lastShutdown = result.asLong(ServerStatsTable.LastShutdown);
            maxPlayersOnline = result.asInt(ServerStatsTable.MaxPlayers);
            maxPlayersOnlineTime = result.asLong(ServerStatsTable.MaxPlayersTime);      
        } else {
            this.createEntry();
        }
            
        if(firstStartup == 0 || firstStartup == -1) firstStartup = curTime;
        if(lastShutdown == 0 || lastShutdown == -1) lastShutdown = curTime;
                
        pushStaticData();
    }
    
    /**
     * Performs a database operation to push the local data to the remote database.
     * @return <b>true</b> if the insertion was successful, <b>false</b> otherwise
     */
    public boolean pushData() {
        long curTime = Util.getTimestamp();
        currentUptime = curTime - lastStartup;
        totalUptime += (curTime - lastSyncTime);
        lastSyncTime = curTime;
        serverTime = Bukkit.getWorlds().get(0).getFullTime();
        Runtime runtime = Runtime.getRuntime();
        totalMemory = runtime.totalMemory();
        freeMemory = runtime.freeMemory();
        ticksPerSecond = TickTask.getTicksPerSecond();
                
        Query.table(ServerStatsTable.TableName)
                .condition(ServerStatsTable.Id, serverId)
                .value(ServerStatsTable.CurrentUptime, currentUptime)
                .value(ServerStatsTable.TotalUptime, totalUptime)
                .value(ServerStatsTable.MaxPlayers, maxPlayersOnline)
                .value(ServerStatsTable.MaxPlayersTime, maxPlayersOnlineTime)
                .value(ServerStatsTable.FreeMemory, freeMemory)
                .value(ServerStatsTable.TPS, ticksPerSecond)
                .value(ServerStatsTable.ServerTime, serverTime)
                .value(ServerStatsTable.Weather, weather)
                .value(ServerStatsTable.WeatherDuration, weatherDuration)
                .update();
                
        return true;
    }
    
    /**
     * Performs a one-time database operation to push the local data to the remote database.<br /.
     * Only performed on plugin startup.
     */
    public void pushStaticData() {
        Query.table(ServerStatsTable.TableName)
                .condition(ServerStatsTable.Id, serverId)
                .value(ServerStatsTable.FirstStartup, firstStartup)
                .value(ServerStatsTable.LastStartup, lastStartup)
                .value(ServerStatsTable.Plugins, plugins)
                .value(ServerStatsTable.Version, bukkitVersion)
                .value(ServerStatsTable.Ip, serverIP)
                .value(ServerStatsTable.Port, serverPort)
                .value(ServerStatsTable.Motd, serverMOTD)
                .value(ServerStatsTable.PlayersAllowed, maxPlayersAllowed)
                .value(ServerStatsTable.TotalMemory, totalMemory)
                .value(ServerStatsTable.JavaVendor, System.getProperty("java.vendor"))
                .value(ServerStatsTable.JavaVendorUrl, System.getProperty("java.vendor.url"))
                .value(ServerStatsTable.JavaVersion, System.getProperty("java.version"))
                .value(ServerStatsTable.JavaVmName, System.getProperty("java.vm.name"))
                .value(ServerStatsTable.JavaVmVendor, System.getProperty("java.vm.vendor"))
                .value(ServerStatsTable.JavaVmVersion, System.getProperty("java.vm.version"))
                .value(ServerStatsTable.OsArch, System.getProperty("os.arch"))
                .value(ServerStatsTable.OsName, System.getProperty("os.name"))
                .value(ServerStatsTable.OsVersion, System.getProperty("os.version"))
                .value(ServerStatsTable.PlayersAllowed, maxPlayersAllowed)
                .update();
    }
    
     /**
     * Creates a new server entry in the database.<br /.
     * Only performed on plugin startup.
     */
    private void createEntry(){
        Query.table(ServerStatsTable.TableName)
                .value(ServerStatsTable.Name, serverName)
                .value(ServerStatsTable.Ip, serverIP)
                .value(ServerStatsTable.Motd, serverMOTD)
                .value(ServerStatsTable.Port, serverPort)
                .value(ServerStatsTable.Version, bukkitVersion)
                .value(ServerStatsTable.FirstStartup, firstStartup)
                .value(ServerStatsTable.FreeMemory, freeMemory)
                .value(ServerStatsTable.JavaVendor, System.getProperty("java.vendor"))
                .value(ServerStatsTable.JavaVendorUrl, System.getProperty("java.vendor.url"))
                .value(ServerStatsTable.JavaVersion, System.getProperty("java.version"))
                .value(ServerStatsTable.JavaVmName, System.getProperty("java.vm.name"))
                .value(ServerStatsTable.JavaVmVendor, System.getProperty("java.vm.vendor"))
                .value(ServerStatsTable.JavaVmVersion, System.getProperty("java.vm.version"))
                .value(ServerStatsTable.LastStartup, lastStartup)
                .value(ServerStatsTable.OsArch, System.getProperty("os.arch"))
                .value(ServerStatsTable.OsName, System.getProperty("os.name"))
                .value(ServerStatsTable.OsVersion, System.getProperty("os.version"))
                .value(ServerStatsTable.PlayersAllowed, maxPlayersAllowed)
                .value(ServerStatsTable.Plugins, plugins)
                .value(ServerStatsTable.ServerTime, serverTime)
                .value(ServerStatsTable.TPS, ticksPerSecond)
                .value(ServerStatsTable.TotalMemory, totalMemory)
                .value(ServerStatsTable.Weather, weather)
                .value(ServerStatsTable.WeatherDuration, weatherDuration)
                .insert();
        
    }
    
    /**
     * Indicates that the plugin is shutting down and registers the current shutdown time and set all online players to offline.
     */
    public void pluginShutdown() {
        
       Query.table(ServerStatsTable.TableName)
               .condition(ServerStatsTable.Id, serverId)
               .value(ServerStatsTable.LastShutdown, Util.getTimestamp())
               .value(ServerStatsTable.CurrentUptime, 0)
               .update();
       
        Query.table(Normal.PlayerStats.TableName)
                .value(Normal.PlayerStats.Online, false)
                .condition(Normal.PlayerStats.Online, true)
                .condition(Normal.PlayerStats.ServerId, serverId).update();
    }
    
    /**
     * Registers the player login in the server statistics
     */
    public void playerLogin() {
        int playersOnline = Bukkit.getOnlinePlayers().length;
        if(playersOnline > maxPlayersOnline) {
            this.maxPlayersOnline = playersOnline;
            this.maxPlayersOnlineTime = Util.getTimestamp();
        }
    }
    
    /**
     * Registers the weather change in the server statistics
     */
    public void weatherChange(boolean isStorming, int duration) {
        weather = isStorming;
        weatherDuration = duration;
        Bukkit.getScheduler().runTaskAsynchronously(Statistics.getInstance(), new Runnable() {
            @Override
            public void run(){            
            Query.table(ServerStatsTable.TableName)
                .condition(ServerStatsTable.Id, serverId)
                .value(ServerStatsTable.Weather, weather)
                .value(ServerStatsTable.WeatherDuration, weatherDuration)
                .update();
           }
        });
    }
    
    /**
     * Registers the change in number of plugins in the server statistics
     */
    public void pluginNumberChange() {
        plugins = Bukkit.getServer().getPluginManager().getPlugins().length;
        Query.table(ServerStatsTable.TableName)
                .condition(ServerStatsTable.Id, serverId)
                .value(ServerStatsTable.Plugins, plugins)
                .update();
    }
    
    /**
     * Returns a map of variables and their values to replace variables on signs.
     * @return Map of statistical variables
     */
    public Map<ServerVariable, Object> getValueMap() {
        @SuppressWarnings("serial")
        Map<ServerVariable, Object> values = new HashMap<ServerVariable, Object>()
        {{
            put(ServerVariable.LAST_SYNC, lastSyncTime);
            
            put(ServerVariable.FIRST_STARTUP, firstStartup);
            put(ServerVariable.LAST_STARTUP, lastStartup);
            put(ServerVariable.CURRENT_UPTIME, currentUptime);
            put(ServerVariable.TOTAL_UPTIME, totalUptime);
            put(ServerVariable.LAST_SHUTDOWN, lastShutdown);

            put(ServerVariable.TOTAL_MEMORY, totalMemory);
            put(ServerVariable.FREE_MEMORY, freeMemory);
            put(ServerVariable.TICKS_PER_SECOND, ticksPerSecond);

            put(ServerVariable.SERVER_IP, serverIP);
            put(ServerVariable.SERVER_NAME, serverName);
            put(ServerVariable.SERVER_PORT, serverPort);
            put(ServerVariable.SERVER_MOTD, serverMOTD);
            put(ServerVariable.BUKKIT_VERSION, bukkitVersion);
            put(ServerVariable.PLUGINS_COUNT, plugins);

            put(ServerVariable.SERVER_TIME, serverTime);
            put(ServerVariable.WEATHER, weather);
            put(ServerVariable.WEATHER_DURATION, weatherDuration);
            
            put(ServerVariable.MAX_PLAYERS_ONLINE, maxPlayersOnline);
            put(ServerVariable.MAX_PLAYERS_TIME, maxPlayersOnlineTime);
            
            put(ServerVariable.MAX_PLAYERS_ALLOWED, maxPlayersAllowed);
        }};
        return values;
    }
    
    /**
     * Updates the tickrate to the one specified
     * @param ticksPerSecond Tickrate
     */
    public void updateTPS(int ticksPerSecond) {
        this.ticksPerSecond = ticksPerSecond;
    }
    
     /**
     * Retruns the server Id
     * @return serverID
     */
    public int ServerId () {
        return this.serverId;
    }
}
