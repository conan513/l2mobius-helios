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
package ai.areas.HuntersVillage.Merlot;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Merlot AI.
 * @author crystalgarden
 */
public final class Merlot extends AbstractNpcAI
{
	// NPC
	private static final int MERLOT = 34018;
	// Item
	private static final int ATELIA_CRYSTAL = 45610;
	private static final int DIMENSIONAL_COIN = 45941;
	// Misc
	private static final int MIN_LEVEL = 99;
	// Location
	private static final Location DIMENSIONAL_RAID = new Location(116503, 75392, -2712); // Merlot Position
	
	private Merlot()
	{
		addStartNpc(MERLOT);
		addTalkId(MERLOT);
		addFirstTalkId(MERLOT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "34018-2.htm":
			case "34018-3.htm":
			{
				htmltext = event;
				break;
			}
			case "give_coin":
			{
				if (hasQuestItems(player, ATELIA_CRYSTAL))
				{
					giveItems(player, DIMENSIONAL_COIN, 1);
					addExpAndSp(player, 0, 14000000);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				else
				{
					htmltext = "34018-5.htm";
				}
				break;
			}
			case "dimensional_raid": // Need TODO Dimensional Raid
			{
				if (player.getLevel() < MIN_LEVEL)
				{
					htmltext = "34018-1.htm";
				}
				else
				{
					player.teleToLocation(DIMENSIONAL_RAID);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "34018.htm";
	}
	
	public static void main(String[] args)
	{
		new Merlot();
	}
}