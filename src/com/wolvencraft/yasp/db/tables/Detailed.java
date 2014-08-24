/*
 * Detailed.java
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

package com.wolvencraft.yasp.db.tables;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents any detailed-type tables in the database.<br >
 * Stores table and column names, so that they can be safely used from the plugin
 * @author bitWolfy
 *
 */
public class Detailed {
    
    /**
     * Represents the <i>detailed_destroyed_blocks</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum BlocksBroken implements DBTable {
        TableName       ("detailed_destroyed_blocks"),
        MaterialId      ("material_id"),
        PlayerId        ("player_id"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_placed_blocks</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum BlocksPlaced implements DBTable {
        TableName       ("detailed_placed_blocks"),
        MaterialId      ("material_id"),
        PlayerId        ("player_id"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_dropped_items</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum ItemsDropped implements DBTable {
        TableName       ("detailed_dropped_items"),
        MaterialId      ("material_id"),
        PlayerId        ("player_id"),
        Amount          ("amount"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_pickedup_items</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum ItemsPickedUp implements DBTable {
        TableName       ("detailed_pickedup_items"),
        EntryId         ("detailed_pickedup_items_id"),
        Material        ("material_id"),
        PlayerId        ("player_id"),
        Amount          ("amount"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_used_items</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum ItemsConsumed implements DBTable {
        TableName       ("detailed_used_items"),
        MaterialId      ("material_id"),
        PlayerId        ("player_id"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_log_players</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum PlayerLog implements DBTable {
        TableName       ("detailed_log_players"),
        PlayerId        ("player_id"),
        Timestamp       ("time"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        IsLogin         ("is_login");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_death_players</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum PlayerDeaths implements DBTable {
        TableName       ("detailed_death_players"),
        PlayerId        ("player_id"),
        Cause           ("cause"),
        World           ("world"),
        ServerId        ("server"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_pve_kills</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum PlayerKillsPVE implements DBTable {
        TableName       ("detailed_pve_kills"),
        MaterialId      ("material_id"),
        CreatureId      ("entity_id"),
        PlayerId        ("player_id"),
        ServerId        ("server"),
        World           ("world"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time"),
        PlayerKilled    ("player_killed");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
    /**
     * Represents the <i>detailed_pvp_kills</i> table
     * @author bitWolfy
     *
     */
    @AllArgsConstructor(access=AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    public enum PlayerKillsPVP implements DBTable {
        TableName       ("detailed_pvp_kills"),
        MaterialId      ("material_id"),
        KillerId        ("player_id"),
        VictimId        ("victim_id"),
        Cause           ("cause"),
        ServerId        ("server"),
        World           ("world"),
        XCoord          ("x"),
        YCoord          ("y"),
        ZCoord          ("z"),
        Timestamp       ("time");
        
        private String columnName;
        
        @Override
        public String toString() { return columnName; }
    }
    
}
