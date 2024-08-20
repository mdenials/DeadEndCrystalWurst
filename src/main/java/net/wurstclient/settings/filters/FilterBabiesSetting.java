/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.settings.filters;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TadpoleEntity;

public final class FilterBabiesSetting extends EntityFilterCheckbox
{
	private static final String EXCEPTIONS_TEXT = "\n\nThis filter does not"
		+ " affect baby zombies and other hostile baby mobs.";
	
	public FilterBabiesSetting(String description, boolean checked)
	{
		super("Filter babies", description + EXCEPTIONS_TEXT, checked);
	}
	
	@Override
	public boolean test(Entity e)
	{
		// never filter out hostile mobs (including hoglins)
		if(e instanceof Monster)
			return true;
		
		// filter out passive entity babies
		if(e instanceof PassiveEntity pe && pe.isBaby())
			return false;
		
		// filter out tadpoles
		if(e instanceof TadpoleEntity)
			return false;
		
		return true;
	}
	
	public static FilterBabiesSetting genericCombat(boolean checked)
	{
		return new FilterBabiesSetting(
			"Won't attack baby pigs, baby villagers, etc.", checked);
	}

	public static FilterBabiesSetting genericVision(boolean checked)
	{
		return new FilterBabiesSetting(
			"description.wurst.setting.generic.filter_babies_vision", checked);
	}
}
