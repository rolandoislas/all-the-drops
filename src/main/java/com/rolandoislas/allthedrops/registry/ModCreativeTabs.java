package com.rolandoislas.allthedrops.registry;

import com.rolandoislas.allthedrops.AllTheDrops;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Rolando on 2/23/2017.
 */
public class ModCreativeTabs {
	public static final CreativeTabs MAIN = new CreativeTabs(CreativeTabs.getNextID(), AllTheDrops.MODID) {
		public Item getTabIconItem() {
			return ModItems.DEV_TOOL;
		}
	};
}
