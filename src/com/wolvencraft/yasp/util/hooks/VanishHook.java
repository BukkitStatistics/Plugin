/*
 * VanishHook.java
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

package com.wolvencraft.yasp.util.hooks;

import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

import com.wolvencraft.yasp.settings.Module;

public class VanishHook extends PluginHook {

    private static VanishPlugin instance;
    
    public VanishHook() {
        super(Module.Vanish, "VanishNoPacket", "vanish");
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        
        Plugin plugin = getPlugin();
        
        if (plugin != null && plugin instanceof VanishPlugin) {
            module.setActive(true);
            instance = (VanishPlugin) plugin;
        }
    }
    
    @Override
    public void onDisable() {
        instance = null;
    }
    
    /**
     * Checks if the player is vanished or not
     * @param playerName Player to look up
     * @return <b>true</b> if the player is vanished, <b>false</b> otherwise
     */
    public static boolean isVanished(String playerName) {
        return instance.getManager().isVanished(playerName);
    }
}