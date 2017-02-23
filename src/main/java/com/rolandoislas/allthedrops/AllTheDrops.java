package com.rolandoislas.allthedrops;

import com.rolandoislas.allthedrops.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = AllTheDrops.MODID, version = AllTheDrops.VERSION, name = AllTheDrops.NAME,
        guiFactory = "com.rolandoislas.allthedrops.gui.GuiFactory")
public class AllTheDrops {
    public static final String MODID = "allthedrops";
    public static final String VERSION = "1.0";
    public static final String NAME = "All the Drops";
    @Mod.Instance(MODID)
    public static AllTheDrops instance;
    @SidedProxy(clientSide = "com.rolandoislas.allthedrops.proxy.ClientProxy",
            serverSide = "com.rolandoislas.allthedrops.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
