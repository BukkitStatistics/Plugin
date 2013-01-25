package com.wolvencraft.statistician.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.wolvencraft.statistician.EventDataHandlers.EDHPlayer;

public class PlayerListener implements Listener {
	private EDHPlayer edhPlayer;

	public PlayerListener(EDHPlayer passedEDH) {
		this.edhPlayer = passedEDH;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.edhPlayer.PlayerJoin(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.edhPlayer.PlayerQuit(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		this.edhPlayer.PlayerMove(event.getPlayer(), event.getPlayer().getVehicle() != null ? event.getPlayer().getVehicle().getClass() : null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		this.edhPlayer.PlayerPickedUpItem(event.getPlayer(), event.getItem().getItemStack().getTypeId(), event.getItem().getItemStack().getAmount());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		this.edhPlayer.PlayerDroppedItem(event.getPlayer(), event.getItemDrop().getItemStack().getTypeId(), event.getItemDrop().getItemStack().getAmount());
	}
}
