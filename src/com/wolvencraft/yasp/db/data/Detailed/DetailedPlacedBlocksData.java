package com.wolvencraft.yasp.db.data.Detailed;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.wolvencraft.yasp.DataCollector;
import com.wolvencraft.yasp.db.QueryUtils;
import com.wolvencraft.yasp.db.tables.Detailed.DetailedPlacedBlocks;
import com.wolvencraft.yasp.util.Util;

public class DetailedPlacedBlocksData implements _DetailedData {
	
	private boolean onHold = false;
	
	public DetailedPlacedBlocksData(Player player, MaterialData materialData) {
		this.playerId = DataCollector.getCachedPlayerId(player.getPlayerListName());
		this.materialData = materialData;
		this.timestamp = Util.getCurrentTime().getTime();
	}
	
	private int playerId;
	private MaterialData materialData;
	private long timestamp;

	@Override
	public boolean pushData() {
		return QueryUtils.insert(DetailedPlacedBlocks.TableName.toString(), getValues());
	}

	@Override
	public Map<String, Object> getValues() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(DetailedPlacedBlocks.PlayerId.toString(), playerId);
		map.put(DetailedPlacedBlocks.MaterialId.toString(), materialData.getItemTypeId());
		map.put(DetailedPlacedBlocks.Timestamp.toString(), timestamp);
		return map;
	}

	@Override
	public boolean isOnHold() { return onHold; }

	@Override
	public void setOnHold(boolean onHold) { this.onHold = onHold; }

	@Override
	public boolean refresh() { return onHold; }
	
}