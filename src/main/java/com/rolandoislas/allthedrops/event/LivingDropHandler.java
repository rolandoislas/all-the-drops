package com.rolandoislas.allthedrops.event;

import com.google.common.collect.Lists;
import com.rolandoislas.allthedrops.data.Config;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Rolando on 2/21/2017.
 */
public class LivingDropHandler {
	public static void livingEntityDropEvent(LivingDropsEvent event) {
		// Respect the mob loot gamerule
		if (!event.getEntityLiving().world.getGameRules().getBoolean("doMobLoot"))
			return;
		// Ignore if death was not caused by a player
		if (Config.requirePlayerKill && !(event.getSource().getSourceOfDamage() instanceof EntityPlayer))
			return;
		// All the Drops!
		EntityLiving entity = (EntityLiving) event.getEntityLiving();
		if (Config.guaranteeDrops) {
			// Try to get loot table from field
			ResourceLocation resourcelocation = ReflectionHelper.getPrivateValue(EntityLiving.class, entity,
					"deathLootTable");
			// Try to get loot table from method call
			if (resourcelocation == null)
				try {
					Method getLootTable = entity.getClass().getDeclaredMethod("getLootTable");
					getLootTable.setAccessible(true);
					resourcelocation = (ResourceLocation) getLootTable.invoke(entity);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			// Populate drops
			if (resourcelocation != null) {
				event.getDrops().clear();
				// Add drops
				event.getDrops().addAll(getAllDrops(event, resourcelocation));
				// Add Equipment
				for (ItemStack itemStack : entity.getEquipmentAndArmor())
					event.getDrops().add(new EntityItem(entity.world, entity.getPosition().getX(),
							entity.getPosition().getY(), entity.getPosition().getZ(),
							itemStack));
			}
		}
		ArrayList<ItemStack> equipment = Lists.newArrayList(entity.getEquipmentAndArmor());
		// Multiply drops
		for (EntityItem entityItem : event.getDrops())
			if (!equipment.contains(entityItem.getEntityItem()))
				entityItem.getEntityItem().setCount(entityItem.getEntityItem().getCount() * Config.dropMultiplier);
	}

	private static List<EntityItem> getAllDrops(LivingDropsEvent event, LootEntryTable lootEntryTable) {
		ResourceLocation table = ReflectionHelper.getPrivateValue(LootEntryTable.class, lootEntryTable,
				"table");
		return getAllDrops(event, table);
	}

	private static List<EntityItem> getAllDrops(LivingDropsEvent event, ResourceLocation resourceLocation) {
		List<EntityItem> drops = new ArrayList<EntityItem>();
		EntityLivingBase entity = event.getEntityLiving();
		LootTable lootTable = event.getEntityLiving().world.getLootTableManager()
				.getLootTableFromLocation(resourceLocation);
		List<LootPool> pools = ReflectionHelper.getPrivateValue(LootTable.class, lootTable, "pools");
		LootContext lootcontext = new LootContext.Builder((WorldServer)entity.world).withLootedEntity(entity)
				.withDamageSource(event.getSource()).build();
		// Add all loot from loot tables
		for (LootPool pool : pools) {
			List<LootEntry> lootEntries = ReflectionHelper.getPrivateValue(LootPool.class, pool,
					"lootEntries");
			for (LootEntry entry : lootEntries) {
				if (!(entry instanceof LootEntryItem)) {
					if (entry instanceof LootEntryTable)
						drops.addAll(getAllDrops(event, (LootEntryTable)entry));
					continue;
				}
				Item item = ReflectionHelper.getPrivateValue(LootEntryItem.class, (LootEntryItem)entry,
						"item");
				LootFunction[] functions = ReflectionHelper.getPrivateValue(LootEntryItem.class,
						(LootEntryItem)entry, "functions");
				ItemStack itemstack = new ItemStack(item);
				// Apply loot functuions (damage, enchant, etc)
				Random rand = new Random();
				for (LootFunction function : functions) {
					if (LootConditionManager.testAllConditions(function.getConditions(), rand, lootcontext))
						if (function instanceof SetCount) {
							SetCount setCountFunction = (SetCount) function;
							RandomValueRange countRange = ReflectionHelper.getPrivateValue(SetCount.class,
									setCountFunction, "countRange");
							RandomValueRange maxCountRange = new RandomValueRange(countRange.getMax());
							ReflectionHelper.setPrivateValue(SetCount.class, setCountFunction,
									maxCountRange, "countRange");
							itemstack = setCountFunction.apply(itemstack, rand, lootcontext);
							ReflectionHelper.setPrivateValue(SetCount.class, setCountFunction,
									countRange, "countRange");
						}
						else
							itemstack = function.apply(itemstack, rand, lootcontext);
				}
				EntityItem entityItem = new EntityItem(entity.world, entity.getPosition().getX(),
						entity.getPosition().getY(), entity.getPosition().getZ(), itemstack);
				entityItem.setPickupDelay(10);
				drops.add(entityItem);
			}
		}
		return drops;
	}
}
