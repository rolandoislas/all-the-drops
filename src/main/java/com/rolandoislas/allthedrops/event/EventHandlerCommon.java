package com.rolandoislas.allthedrops.event;

import com.rolandoislas.allthedrops.data.Config;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Rolando on 2/20/2017.
 */
public class EventHandlerCommon {
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void livingEntityDropEvent(LivingDropsEvent event) {
		LivingDropHandler.livingEntityDropEvent(event);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void harvestBlockDropEvent(BlockEvent.HarvestDropsEvent event) {
		HarvestDropHandler.harvestBlockDropEvent(event);
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		Config.configChanged(event);
	}
}
