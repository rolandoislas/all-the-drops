package com.rolandoislas.allthedrops.proxy;

import com.rolandoislas.allthedrops.data.Config;
import com.rolandoislas.allthedrops.event.EventHandlerCommon;
import com.rolandoislas.allthedrops.registry.ModItems;
import com.rolandoislas.allthedrops.registry.Recipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Rolando on 2/20/2017.
 */
public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		Config.setConfigFile(event.getSuggestedConfigurationFile());
		Config.load();
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
		ModItems.register();
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {
		Recipes.register();
	}
}
