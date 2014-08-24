/*
 * DetailedLogPlayerEntry.java
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

import lombok.AccessLevel;
import lombok.Getter;

import org.bukkit.Location;

import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.db.Query;
import com.wolvencraft.yasp.db.data.DetailedData;
import com.wolvencraft.yasp.db.tables.Detailed.PlayerLog;
import com.wolvencraft.yasp.util.Util;

/**
 * An immutable player login / logout entry
 * @author bitWolfy
 *
 */
@Getter(AccessLevel.PUBLIC)
public class DetailedLogPlayerEntry extends DetailedData {
    
    private final long time;
    private final boolean isLogin;
    private final Location location;
    
    public DetailedLogPlayerEntry(Location location, boolean isLogin) {
        time = Util.getTimestamp();
        this.isLogin = isLogin;
        this.location = location.clone();
    }
     
    @Override
    public boolean pushData(int playerId) {
        return Query.table(PlayerLog.TableName)
                .value(PlayerLog.PlayerId, playerId)
                .value(PlayerLog.ServerId, Statistics.getServerStatistics().ServerId())
                .value(PlayerLog.Timestamp, time)
                .value(PlayerLog.IsLogin, isLogin)
                .value(PlayerLog.World, location.getWorld().getName())
                .value(PlayerLog.XCoord, location.getBlockX())
                .value(PlayerLog.YCoord, location.getBlockY())
                .value(PlayerLog.ZCoord, location.getBlockZ())
                .insert();
    }
 
}
