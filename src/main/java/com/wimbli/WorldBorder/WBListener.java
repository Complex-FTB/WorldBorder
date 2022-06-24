package com.wimbli.WorldBorder;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.Location;


public class WBListener implements Listener
{
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		// if knockback is set to 0, simply return
		if (Config.KnockBack() == 0.0)
			return;

		if (Config.Debug())
			Config.log("Teleport cause: " + event.getCause().toString());

		Location newLoc = BorderCheckTask.checkPlayer(event.getPlayer(), event.getTo(), true, true);
		if (newLoc != null)
		{
			if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && Config.getDenyEnderpearl())
			{
				event.setCancelled(true);
				return;
			}

			event.setTo(newLoc);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event)
	{
		// if knockback is set to 0, or portal redirection is disabled, simply return
		if (Config.KnockBack() == 0.0 || !Config.portalRedirection())
			return;

		Location newLoc = BorderCheckTask.checkPlayer(event.getPlayer(), event.getTo(), true, false);
		if (newLoc != null)
			event.setTo(newLoc);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockDamageEvent event)
	{
		if (WorldBorder.plugin.getWorldBorder(Bukkit.getServer().getWorlds().get(0).getName()) != null)
		{
			Block blockBroken = event.getBlock();
			BorderData data = Config.Border(event.getPlayer().getWorld().getName());
			if ((blockBroken.getX() == data.getX() + data.getRadiusX()
					|| blockBroken.getX() == data.getX() - data.getRadiusX()
					|| blockBroken.getZ() == data.getZ() + data.getRadiusZ()
					|| blockBroken.getZ() == data.getZ() - data.getRadiusZ())
					&& !Config.isPlayerBypassing(event.getPlayer().getUniqueId()))
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (WorldBorder.plugin.getWorldBorder(Bukkit.getServer().getWorlds().get(0).getName()) != null)
		{
			Block blockPlaced = event.getBlock();
			BorderData data = Config.Border(event.getPlayer().getWorld().getName());
			if ((blockPlaced.getX() == data.getX() + data.getRadiusX()
					|| blockPlaced.getX() == data.getX() - data.getRadiusX()
					|| blockPlaced.getZ() == data.getZ() + data.getRadiusZ()
					|| blockPlaced.getZ() == data.getZ() - data.getRadiusZ())
					&& event.getPlayer().getGameMode() != GameMode.CREATIVE
					&& !Config.isPlayerBypassing(event.getPlayer().getUniqueId()))
			{
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event)
	{
/*		// tested, found to spam pretty rapidly as client repeatedly requests the same chunks since they're not being sent
		// definitely too spammy at only 16 blocks outside border
		// potentially useful at standard 208 block padding as it was triggering only occasionally while trying to get out all along edge of round border, though sometimes up to 3 triggers within a second corresponding to 3 adjacent chunks
		// would of course need to be further worked on to have it only affect chunks outside a border, along with an option somewhere to disable it or even set specified distance outside border for it to take effect; maybe  send client chunk composed entirely of air to shut it up

		// method to prevent new chunks from being generated, core method courtesy of code from NoNewChunk plugin (http://dev.bukkit.org/bukkit-plugins/nonewchunk/)
		if(event.isNewChunk())
		{
			Chunk chunk = event.getChunk();
			chunk.unload(false, false);
			Config.logWarn("New chunk generation has been prevented at X " + chunk.getX() + ", Z " + chunk.getZ());
		}
*/
		// make sure our border monitoring task is still running like it should
		if (Config.isBorderTimerRunning()) return;

		Config.logWarn("Border-checking task was not running! Something on your server apparently killed it. It will now be restarted.");
		Config.StartBorderTimer();
	}
}
