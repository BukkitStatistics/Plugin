package com.wolvencraft.yasp.Database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wolvencraft.yasp.StatsPlugin;
import com.wolvencraft.yasp.Database.DataValues.DBDataValues_Players;
import com.wolvencraft.yasp.Database.DataValues.DataStores;
import com.wolvencraft.yasp.Database.DataValues.IDataValues;
import com.wolvencraft.yasp.Stats.KillTag;
import com.wolvencraft.yasp.Utils.DBProcedure;

public abstract class DBSynchDataGetSet {
	public static String getValue(DataStores dataStore, IDataValues dataStoreValue, IDataValues controlStoreValue, String valueEquals) {
		List<Map<String, String>> results =
				StatsPlugin.getInstance().getDB().fetchData("SELECT " + dataStoreValue.getColumnName() + " FROM " +
						dataStore.getTableName() + " WHERE " + controlStoreValue.getColumnName() +
						" = '" + valueEquals + "' LIMIT 1");
		if (results != null) {
			if (results.size() > 0) {
				if (results.get(0).containsKey(dataStoreValue.getColumnName())) return results.get(0).get(dataStoreValue.getColumnName());
			}
		}
		return null;
	}

	public static List<String> getValues(DataStores dataStore, IDataValues dataStoreValue, IDataValues controlStoreValue, String valueEquals) {
		List<Map<String, String>> results =
				StatsPlugin.getInstance().getDB().fetchData("SELECT " + dataStoreValue.getColumnName() + " FROM " +
						dataStore.getTableName() + " WHERE " + controlStoreValue.getColumnName() +
						" = '" + valueEquals + "'");

		if (results != null) {
			if (results.size() > 0) {
				if (results.get(0).containsKey(dataStoreValue.getColumnName())) {
					List<String> returnString = new ArrayList<String>();
					Iterator<Map<String, String>> itr = results.iterator();
					while (itr.hasNext()) {
						returnString.add(itr.next().get(dataStoreValue.getColumnName()));
					}
					return returnString;
				}
			}
		}
		return null;
	}

	public static List<Map<String, String>> getValues(DataStores dataStore, IDataValues controlStoreValue, String valueEquals) {
		return StatsPlugin.getInstance().getDB().fetchData("SELECT * FROM " + dataStore.getTableName() +
				" WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'");
	}

	public static List<Map<String, String>> customQuery(String customQuery) {
		return StatsPlugin.getInstance().getDB().fetchData(customQuery);
	}

	public static boolean customUpdateQuery(String customQuery) {
		return StatsPlugin.getInstance().getDB().pushData(customQuery);
	}

	public static boolean setValue(DataStores dataStore, IDataValues storeValue, String value, IDataValues controlStoreValue, String valueEquals) {
		return StatsPlugin.getInstance().getDB().pushData("UPDATE " + dataStore.getTableName() + " SET " + storeValue.getColumnName()
				+ " = '" + value + "' WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'");
	}

	public static boolean incrementValue(DataStores dataStore, IDataValues storeValue, Integer byVal, IDataValues controlStoreValue, String valueEquals, IDataValues controlStoreValue2, String valueEquals2) {
		return StatsPlugin.getInstance().getDB().pushData("UPDATE " + dataStore.getTableName() + " SET " + storeValue.getColumnName()
				+ " = " + storeValue.getColumnName() + " + " + byVal + " WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'" + " AND " + controlStoreValue2.getColumnName() + " = '" + valueEquals2 + "'");
	}

	public static boolean incrementValue(DataStores dataStore, IDataValues storeValue, Integer byVal, IDataValues controlStoreValue, String valueEquals) {
		return StatsPlugin.getInstance().getDB().pushData("UPDATE " + dataStore.getTableName() + " SET " + storeValue.getColumnName()
				+ " = " + storeValue.getColumnName() + " + " + byVal + " WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'");
	}

	public static boolean incrementValue(DataStores dataStore, IDataValues storeValue, IDataValues controlStoreValue, String valueEquals) {
		return DBSynchDataGetSet.incrementValue(dataStore, storeValue, 1, controlStoreValue, valueEquals);
	}

	public static boolean incrementValue(DataStores dataStore, IDataValues storeValue, IDataValues controlStoreValue, String valueEquals, IDataValues controlStoreValue2, String valueEquals2) {
		return DBSynchDataGetSet.incrementValue(dataStore, storeValue, 1, controlStoreValue, valueEquals, controlStoreValue2, valueEquals2);
	}

	public static boolean decrementValue(DataStores dataStore, IDataValues storeValue, Integer byVal, IDataValues controlStoreValue, String valueEquals) {
		return StatsPlugin.getInstance().getDB().pushData("UPDATE " + dataStore.getTableName() + " SET " + storeValue.getColumnName()
				+ " = " + storeValue.getColumnName() + " - " + byVal + " WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'");
	}

	public static boolean decrementValue(DataStores dataStore, IDataValues storeValue, Integer byVal, IDataValues controlStoreValue, String valueEquals, IDataValues controlStoreValue2, String valueEquals2) {
		return StatsPlugin.getInstance().getDB().pushData("UPDATE " + dataStore.getTableName() + " SET " + storeValue.getColumnName()
				+ " = " + storeValue.getColumnName() + " - " + byVal + " WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'" + " AND " + controlStoreValue2.getColumnName() + " = '" + valueEquals2 + "'");
	}

	public static boolean decrementValue(DataStores dataStore, IDataValues storeValue, IDataValues controlStoreValue, String valueEquals) {
		return DBSynchDataGetSet.decrementValue(dataStore, storeValue, 1, controlStoreValue, valueEquals);
	}

	public static boolean decrementValue(DataStores dataStore, IDataValues storeValue, IDataValues controlStoreValue, String valueEquals, IDataValues controlStoreValue2, String valueEquals2) {
		return DBSynchDataGetSet.decrementValue(dataStore, storeValue, 1, controlStoreValue, valueEquals, controlStoreValue2, valueEquals2);
	}

	public static boolean batchSetValue(DataStores dataStore, IDataValues storeValue, List<Map<IDataValues, String>> batchValues, IDataValues controlStoreValue, String valueEquals) {
		// Build Query
		String Query = "UPDATE " + dataStore.getTableName() + " SET ";

		Iterator<Map<IDataValues, String>> itr = batchValues.iterator();
		while (itr.hasNext()) {
			Map<IDataValues, String> thisMap = itr.next();
			Entry<IDataValues, String> entry = thisMap.entrySet().iterator().next();
			Query += entry.getKey().getColumnName() + " = '" + entry.getValue() + "' ,";
		}
		Query = Query.substring(0, Query.length() - 1);

		Query += " WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'";

		return StatsPlugin.getInstance().getDB().pushData(Query);
	}

	public static boolean batchIncrementDecrement(DataStores dataStore, IDataValues storeValue, List<Map<IDataValues, Integer>> batchValues, IDataValues controlStoreValue, String valueEquals) {
		// Build Query
		String Query = "UPDATE " + dataStore.getTableName() + " SET ";

		Iterator<Map<IDataValues, Integer>> itr = batchValues.iterator();
		while (itr.hasNext()) {
			Map<IDataValues, Integer> thisMap = itr.next();
			Entry<IDataValues, Integer> entry = thisMap.entrySet().iterator().next();
			if (entry.getValue() > 0) {
				Query += entry.getKey().getColumnName() + " = " + entry.getKey().getColumnName() + " + " + entry.getValue() + " ,";
			} else {
				Query += entry.getKey().getColumnName() + " = " + entry.getKey().getColumnName() + " - " + entry.getValue() * -1 + " ,";
			}
		}
		Query = Query.substring(0, Query.length() - 1);

		Query += " WHERE " + controlStoreValue.getColumnName() + " = '" + valueEquals + "'";

		return StatsPlugin.getInstance().getDB().pushData(Query);
	}

	public static boolean isPlayerInDB(String UUID) {
		if (DBSynchDataGetSet.getValue(DataStores.PLAYER, DBDataValues_Players.PLAYER_NAME, DBDataValues_Players.UUID, UUID) != null)
			return true;
		return false;
	}

	public static boolean incrementBlockDestroy(String UUID, Integer blockID, Integer numDestroyed) {
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		vars.add(blockID.toString());
		vars.add(numDestroyed.toString());
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.INCREMENT_BLOCK_DESTROY, vars);
	}

	public static boolean incrementBlockPlaced(String UUID, Integer blockID, Integer numPlaced) {
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		vars.add(blockID.toString());
		vars.add(numPlaced.toString());
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.INCREMENT_BLOCK_PLACED, vars);
	}

	public static boolean playerCreate(String UUID, String PlayerName) {
		// This will prob get called synch allot, so it's created as a stored proc, offloaded to the DB
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		vars.add(PlayerName);
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.PLAYER_JOIN, vars);
	}

	public static boolean playerLogin(String UUID) {
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.PLAYER_LOGIN, vars);
	}

	public static boolean playerLogout(String UUID) {
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.PLAYER_LOGOUT, vars);
	}

	public static boolean newKill(KillTag kt) {
		List<String> vars = new ArrayList<String>();
		vars.add(kt.Killed.getID().toString());
		vars.add(kt.KilledBy.getID().toString());
		vars.add(kt.KillType.getID().toString());
		vars.add(kt.KilledUsing.toString());
		vars.add(kt.KillProjectile.getID().toString());
		vars.add(kt.KilledBy_UUID);
		vars.add(kt.Killed_UUID);
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.KILL, vars);
	}

	public static boolean incrementItemPickup(String UUID, Integer itemID, Integer numPickedUp) {
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		vars.add(itemID.toString());
		vars.add(numPickedUp.toString());
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.INCREMENT_ITEM_PICKEDUP, vars);
	}

	public static boolean incrementItemDrop(String UUID, Integer itemID, Integer numDropped) {
		List<String> vars = new ArrayList<String>();
		vars.add(UUID);
		vars.add(itemID.toString());
		vars.add(numDropped.toString());
		return StatsPlugin.getInstance().getDB().callStoredProcedure(DBProcedure.INCREMENT_ITEM_DROPPED, vars);
	}
}
