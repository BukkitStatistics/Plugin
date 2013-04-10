/*
 * Database.java
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

package com.wolvencraft.yasp.db;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.wolvencraft.yasp.Settings;
import com.wolvencraft.yasp.Statistics;
import com.wolvencraft.yasp.Settings.LocalConfiguration;
import com.wolvencraft.yasp.db.Query.QueryResult;
import com.wolvencraft.yasp.db.tables.Normal.SettingsTable;
import com.wolvencraft.yasp.exceptions.DatabaseConnectionException;
import com.wolvencraft.yasp.exceptions.RuntimeSQLException;
import com.wolvencraft.yasp.util.Message;

/**
 * Represents a running database instance.<br />
 * There can only be one instance running at any given time.
 * @author bitWolfy
 *
 */
public class Database {
    
    private static Connection connection = null;
    
    /**
     * Default constructor. Connects to the remote database, performs patches if necessary, and holds to the DB info.<br />
     * @throws DatabaseConnectionException Thrown if the plugin could not connect to the database
     */
    public Database() throws DatabaseConnectionException {
        
        try { Class.forName("com.mysql.jdbc.Driver"); }
        catch (ClassNotFoundException ex) { throw new DatabaseConnectionException("MySQL driver was not found!"); }
        
        try {
            connection = DriverManager.getConnection(
                LocalConfiguration.DBConnect.asString(),
                LocalConfiguration.DBUser.asString(),
                LocalConfiguration.DBPass.asString()
            );
        } catch (SQLException e) { throw new DatabaseConnectionException(e); }
        
        try { if (connection.getAutoCommit()) connection.setAutoCommit(false); }
        catch (Throwable t) { throw new RuntimeSQLException("Could not set AutoCommit to false. Cause: " + t, t); }
        
        if(!runPatcher(false)) Message.log("Target database is up to date");
        Statistics.setPaused(false);
    }
    
    /**
     * Patches the remote database to the latest version.<br />
     * This method will run in the <b>main server thread</b> and therefore will freeze the server until the patch is complete.
     * @param force <b>true</b> to drop all data in the database and start anew.
     * @return <b>true</b> if a patch was applied, <b>false</b> if it was not.
     * @throws DatabaseConnectionException Thrown if the plugin is unable to patch the remote database
     */
    public static boolean runPatcher(boolean force) throws DatabaseConnectionException {
        int databaseVersion;
        if(force) { databaseVersion = 1; }
        else { databaseVersion = Settings.RemoteConfiguration.DatabaseVersion.asInteger(); }
        int latestPatchVersion = databaseVersion;
        
        while (Statistics.getInstance().getClass().getClassLoader().getResourceAsStream("patches/" + (latestPatchVersion + 1) + ".yasp.sql") != null) {
            latestPatchVersion++;
        }
        
        if(databaseVersion >= latestPatchVersion) { return true; }
        Message.debug("Current version: " + databaseVersion + ", latest version: " + latestPatchVersion);
        databaseVersion++;
        
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        Message.log("+-------] Database Patcher [-------+");
        for(; databaseVersion <= latestPatchVersion; databaseVersion++) {
            Message.log("|       Applying patch " + databaseVersion + " / " + latestPatchVersion + "       |");
            executePatch(scriptRunner, databaseVersion + ".yasp");
            Settings.RemoteConfiguration.DatabaseVersion.update(databaseVersion);
        }
        Message.log("+----------------------------------+");
        return true;
    }
    
    /**
     * Applies the patch to the remote database
     * @param patchId Unique ID of the desired patch, i.e. <code>1.vault</code>.
     * @throws DatabaseConnectionException Thrown if the plugin is unable to patch the remote database
     * @return <b>true</b> if a patch was applied, <b>false</b> if it was not.
     */
    public static boolean executePatch(String patchId) throws DatabaseConnectionException {
        return executePatch(new ScriptRunner(connection), patchId);
    }
    
    /**
     * Applies the patch to the remote database
     * @param scriptRunner Script runner instance that executes the patch
     * @param patchId Unique ID of the desired patch, i.e. <code>1.vault</code>.
     * @throws DatabaseConnectionException Thrown if the plugin is unable to patch the remote database
     * @return <b>true</b> if a patch was applied, <b>false</b> if it was not.
     */
    private static boolean executePatch(ScriptRunner scriptRunner, String patchId) throws DatabaseConnectionException {
        InputStream is = Statistics.getInstance().getClass().getClassLoader().getResourceAsStream("patches/" + patchId + ".sql");
        if (is == null) return false;
        Message.log(Level.FINE, "Executing database patch: " + patchId + ".sql");
        try {scriptRunner.runScript(new InputStreamReader(is)); }
        catch (RuntimeSQLException e) { throw new DatabaseConnectionException("An error occured while executing database patch: " + patchId + ".sql", e); }
        finally {
            if(!Query.table(SettingsTable.TableName).column("patched").exists()) {
                Query.table(SettingsTable.TableName).value("patched", 1).insert();
            }
            Query.table(SettingsTable.TableName).value("patched", 1).update();
        }
        return true;
    }
    
    /**
     * Attempts to reconnect to the remote server
     * @return <b>true</b> if the connection was present, or reconnect is successful. <b>false</b> otherwise.
     */
    public static boolean reconnect() {
        try {
            if (connection.isValid(10)) {
                Message.log("Connection is still present. Malformed query detected.");
                return false;
            }
            Message.log(Level.WARNING, "Attempting to re-connect to the database");
            connection = DriverManager.getConnection(
                LocalConfiguration.DBConnect.asString(),
                LocalConfiguration.DBUser.asString(),
                LocalConfiguration.DBPass.asString()
            );
            Message.log("Connection re-established. No data is lost.");
            return true;
        } catch (Exception e) {
            Message.log(Level.SEVERE, "Failed to re-connect to the database. Data is being stored locally.");
            if (LocalConfiguration.Debug.asBoolean()) e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Pushes data to the remote database.<br />
     * This is a raw method and should never be used by itself. Use the <b>QueryUtils</b> wrapper for more options 
     * and proper error handling. This method is not to be used for regular commits to the database.
     * @param query SQL query
     * @return <b>true</b> if the sync is successful, <b>false</b> otherwise
     */
    public static boolean executeUpdate(String query) {
        int rowsChanged = 0;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            rowsChanged = statement.executeUpdate(query);
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            Message.log(Level.WARNING, "An error occurred while executing a database update.", e.getMessage(), query);
            if(LocalConfiguration.Debug.asBoolean()) e.printStackTrace();
            if(reconnect()) return executeUpdate(query);
            else return false;
        } finally {
            if (statement != null) {
                try { statement.close(); }
                catch (SQLException e) { Message.log(Level.SEVERE, "Error closing database connection"); }
            }
        }
        return rowsChanged > 0;
    }
    
    /**
     * Returns the data from the remote server according to the SQL query.<br />
     * This is a raw method and should never be used by itself. Use the <b>QueryUtils</b> wrapper for more options 
     * and proper error handling. This method is not to be used for regular commits to the database.
     * @param query SQL query
     * @return Data from the remote database
     */
    public static List<QueryResult> executeQuery(String query) {
        List<QueryResult> colData = new ArrayList<QueryResult>();
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                HashMap<String, String> rowToAdd = new HashMap<String, String>();
                for (int x = 1; x <= rs.getMetaData().getColumnCount(); ++x) {
                    rowToAdd.put(rs.getMetaData().getColumnName(x), rs.getString(x));
                }
                colData.add(Query.toQueryResult(rowToAdd));
            }
        } catch (SQLException e) {
            Message.log(Level.WARNING, "An error occurred while executing a database query.", e.getMessage(), query);
            if(LocalConfiguration.Debug.asBoolean()) e.printStackTrace();
            if(reconnect()) return executeQuery(query);
            else return new ArrayList<QueryResult>();
        } finally {
            if (rs != null) {
                try { rs.close(); }
                catch (SQLException e) { Message.log(Level.SEVERE, "Error closing database connection [ResultSet]"); }
            }
            if (statement != null) {
                try { statement.close(); }
                catch (SQLException e) { Message.log(Level.SEVERE, "Error closing database connection [Statement]"); }
            }
        }
        return colData;
    }
    
    /**
     * Closes the database connection and cleans up any leftover instances to prevent memory leaks
     */
    public static void close() {
        try { connection.close(); }
        catch (SQLException e) { Message.log(Level.SEVERE, "Error closing database connection"); }
        connection = null;
    }
    
    /**
     * Checks if the database connection is safe to use
     * @return <b>true</b> if the connection is closed, <b>false</b> if it is open.
     */
    public static boolean isClosed() {
        return connection == null;
    }
}
