/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.ItemListSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"auto drop", "AutoEject", "auto-eject", "auto eject",
	"InventoryCleaner", "inventory cleaner", "InvCleaner", "inv cleaner"})
public final class AutoDropHack extends Hack implements UpdateListener
{
	private final SliderSetting delay = new SliderSetting("Delay", "Masscrafter delay. ", 5, 0, 20, 1, ValueDisplay.INTEGER);
	private ItemListSetting items = new ItemListSetting("Items",
		"Unwanted items that will be dropped.", "minecraft:allium",
		"minecraft:azure_bluet", "minecraft:blue_orchid",
		"minecraft:cornflower", "minecraft:dandelion", "minecraft:lilac",
		"minecraft:lily_of_the_valley", "minecraft:orange_tulip",
		"minecraft:oxeye_daisy", "minecraft:peony", "minecraft:pink_tulip",
		"minecraft:poisonous_potato", "minecraft:poppy", "minecraft:red_tulip",
		"minecraft:rose_bush", "minecraft:rotten_flesh", "minecraft:sunflower",
		"minecraft:wheat_seeds", "minecraft:white_tulip");

	private int timer;
	public AutoDropHack()
	{
		super("AutoDrop");
		setCategory(Category.ITEMS);
		addSetting(delay);
		addSetting(items);
	}
	
	@Override
	protected void onEnable()
	{
		timer = 0;
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		timer = 0;
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check screen
		if(MC.currentScreen instanceof HandledScreen
			&& !(MC.currentScreen instanceof InventoryScreen))
			return;

		// wait for timer
		if(timer > 0)
		{
			timer--;
			return;
		}
		timer = delay.getValueI();

		for(int slot = 9; slot < 45; slot++)
		{
			int adjustedSlot = slot;
			ItemStack stack = MC.player.getInventory().getStack(adjustedSlot);
			Item item = stack.getItem();
			String itemName = Registries.ITEM.getId(item).toString();
			
			if(stack.isEmpty())
				continue;
			
			if(!items.getItemNames().contains(itemName))
				continue;

			if(adjustedSlot >= 36)
				adjustedSlot -= 36;
			
			IMC.getInteractionManager().windowClick_THROW(slot);
		}
	}
}
