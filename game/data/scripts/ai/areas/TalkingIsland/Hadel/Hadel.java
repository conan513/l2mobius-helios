/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.areas.TalkingIsland.Hadel;

import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Hadel AI.
 * @author St3eT
 */
public final class Hadel extends AbstractNpcAI
{
	// NPC
	private static final int HADEL = 33344;
	// Locations
	private static final Location GIANTS = new Location(-114562, 227307, -2864);
	private static final Location HARNAK = new Location(-114700, 147909, -7720);
	
	private Hadel()
	{
		addStartNpc(HADEL);
		addTalkId(HADEL);
		addFirstTalkId(HADEL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "33344.html":
			case "33344-01.html":
			{
				htmltext = event;
				break;
			}
			case "teleportToGiants":
			{
				player.teleToLocation(GIANTS);
				break;
			}
			case "teleportToHarnak":
			{
				if ((!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP)) || (player.getLevel() < 85))
				{
					htmltext = "33344-noClass.html";
					break;
				}
				player.teleToLocation(HARNAK);
				break;
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Hadel();
	}
}
