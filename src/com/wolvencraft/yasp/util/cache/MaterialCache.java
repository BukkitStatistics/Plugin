/* 
 * MaterialCache.java
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

package com.wolvencraft.yasp.util.cache;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;

import com.wolvencraft.yasp.Settings;
import com.wolvencraft.yasp.Settings.ItemsWithMetadata;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.tables.Miscellaneous.MaterialsTable;

/**
 * Caches material IDs server-side
 * @author bitWolfy
 *
 */
public class MaterialCache implements Runnable {
    
    private static List<String> materials;
    
    /**
     * <b>Default constructor</b><br />
     * Creates a new List for data storage
     */
    public MaterialCache() {
        materials = new ArrayList<String>();
    }
    
    @Override
    public void run() {
        materials.clear();
    }
    
    /**
     * Parses a block type ID and data value and returns a String representation of the material.<br />
     * Inserts a new entry into the materials table
     * @param type Type ID
     * @param data Data value
     * @param name Item name
     * @return Material string
     */
    private static String parse(int type, int data, String name) {
        String material = "";
    
        if(type == -1) return "-1:0";
        if(Material.getMaterial(type) == null) return "0:0";
        if(!Settings.ItemsWithMetadata.checkAgainst(type)) material = type + ":" + "0";
        else material = type + ":" + ItemsWithMetadata.get(type).getData(data);
        
        if(materials.contains(material)) return material;
        materials.add(material);
        if(!Query.table(MaterialsTable.TableName).condition(MaterialsTable.MaterialId, material).exists()) {
            Query.table(MaterialsTable.TableName)
                 .value(MaterialsTable.MaterialId, material)
                 .value(MaterialsTable.TpName, "invalid_" + name)
                 .insert();
        }
        return material;
    }
    
    /**
     * Parses an item stack and returns a String representation of the material
     * @param stack Item stack to parse
     * @return Material string
     */
    public static String parse(ItemStack stack) {
        return parse(stack.getTypeId(), stack.getDurability(), stack.getType().name().toLowerCase());
    }
    
    /**
     * Parses a block and returns a String representation of the material
     * @param block Block to parse
     * @return Material string
     */
    public static String parse(BlockState block) {
        return parse(block.getTypeId(), block.getRawData(), block.getType().name().toLowerCase());
    }
    
}
