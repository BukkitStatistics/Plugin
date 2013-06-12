/*
 * BookUtil.java
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

package com.wolvencraft.yasp.util;

import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import net.minecraft.server.v1_5_R3.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.wolvencraft.yasp.db.totals.PlayerTotals;
import com.wolvencraft.yasp.util.VariableManager.PlayerVariable;
import com.wolvencraft.yasp.util.cache.OfflineSessionCache;

public class BookUtil {
    
    private BookUtil() { }
    
    /**
     * Creates a new statistics book based for the specified player.<br />
     * Version-specific method. Include methods to check for CraftBukkit version in the implementation.
     * @param player Player to use for statistics
     * @return Book with player's statistics
     */
    public static ItemStack compileStatsBook (Player player) {
        net.minecraft.server.v1_5_R3.ItemStack item = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(387, 1));
        
        NBTTagCompound tags = item.getTag();
        if (tags == null) {
            tags = new NBTTagCompound();
            item.setTag(tags);
        }
        
        tags.setString("title", player.getPlayerListName() + " Statistics");
        tags.setString("author", "Statistics");
        
        NBTTagList pages = new NBTTagList("pages");
        String[] newPages = getBookPages(player.getPlayerListName());
        
        for(int i = 0; i < newPages.length; i++) {
            pages.add(new NBTTagString("" + i + "", newPages[i]));
        }
        tags.set("pages", pages);
        item.setTag(tags);
        return CraftItemStack.asBukkitCopy(item);
    }
    
    /**
     * Returns the pages for the book with player's statistics. Could be used for offline players.<br />
     * It is safe to use this method with any version of CraftBukkit.
     * @param playerName Player name to use for the statistics
     * @return Array of strings, each of them representing a new page in the book.
     */
    public static String[] getBookPages(String playerName) {
        PlayerTotals stats = OfflineSessionCache.fetch(playerName).getTotals();
        return new String[] {
                ChatColor.DARK_RED + "\n\n" + " + " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.RESET + " + \n\n" + 
                ChatColor.BLACK + "Current session: \n\n" + stats.getValue(PlayerVariable.SESSION_LENGTH) + "\n\n" + 
                ChatColor.BLACK + "Total playtime: \n\n" + stats.getValue(PlayerVariable.TOTAL_PLAYTIME),

                ChatColor.DARK_RED + "" + " + " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.RESET + " + \n\n" + 
                ChatColor.BLACK + ChatColor.BOLD + "  Blocks and items \n" + 
                ChatColor.RED + ChatColor.BOLD + " - Blocks " + ChatColor.RESET + "\n" + 
                ChatColor.BLACK + " Broken: " + stats.getValue(PlayerVariable.BLOCKS_BROKEN) + "\n" + 
                ChatColor.BLACK + " Placed: " + stats.getValue(PlayerVariable.BLOCKS_PLACED) + "\n\n" +
                ChatColor.RED + ChatColor.BOLD + "- Items" + ChatColor.RESET + "\n" +
                ChatColor.BLACK + " Crafted: " + stats.getValue(PlayerVariable.ITEMS_CRAFTED) + "\n" + 
                ChatColor.BLACK + " Broken: " + stats.getValue(PlayerVariable.ITEMS_BROKEN) + "\n" + 
                ChatColor.BLACK + " Eaten: " + stats.getValue(PlayerVariable.ITEMS_EATEN),
                
                ChatColor.DARK_RED + "" + " + " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.RESET + " + \n\n" + 
                ChatColor.RED + ChatColor.BOLD + "  Travel log \n" + 
                ChatColor.BLACK + " Total: " + stats.getValue(PlayerVariable.DISTANCE_TRAVELED) + "\n\n" + 
                ChatColor.BLACK + " By foot: " + stats.getValue(PlayerVariable.DISTANCE_FOOT) + "\n" +
                ChatColor.BLACK + " Swimmed: " + stats.getValue(PlayerVariable.DISTANCE_SWIM) + "\n" +
                ChatColor.BLACK + " In minecart: " + stats.getValue(PlayerVariable.DISTANCE_CART) + "\n" + 
                ChatColor.BLACK + " In a boat: " + stats.getValue(PlayerVariable.DISTANCE_BOAT) + "\n" + 
                ChatColor.BLACK + " On a pig: " + stats.getValue(PlayerVariable.DISTANCE_PIG),
                
                ChatColor.DARK_RED + "" + " + " + ChatColor.BOLD + ChatColor.UNDERLINE + playerName + ChatColor.RESET + " + \n\n" + 
                ChatColor.BLACK + ChatColor.BOLD + "  Kills and Deaths \n" + 
                ChatColor.BLACK + " PVP Kills: " + stats.getValue(PlayerVariable.PVP_KILLS) + "\n" + 
                ChatColor.BLACK + " PVE kills: " + stats.getValue(PlayerVariable.PVE_KILLS) + "\n" + 
                ChatColor.BLACK + " Deaths: " + stats.getValue(PlayerVariable.DEATHS) + "\n" + 
                ChatColor.BLACK + " K/D: " + stats.getValue(PlayerVariable.KILL_DEATH_RATIO) + "\n\n"
        };
    }
    
    /**
     * Placeholder method. If it throws an error, server's CraftBukkit version differs from the one
     * the plugin was compiled with.
     */
    public static void isBukkitCompatible() { }
    
}
