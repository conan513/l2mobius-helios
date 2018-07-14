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
package ai.areas.TalkingIsland;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.geoengine.GeoEngine;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.util.Util;

import ai.AbstractNpcAI;

/**
 * Devno AI.
 * @author St3eT
 */
public final class Devno extends AbstractNpcAI
{
	// NPC
	private static final int DEVNO = 33241;
	
	private Devno()
	{
		addSpawnId(DEVNO);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("NPC_MOVE"))
		{
			if (getRandomBoolean())
			{
				final Location randomLoc = Util.getRandomPosition(npc.getSpawn().getLocation(), 0, 500);
				addMoveToDesire(npc, GeoEngine.getInstance().canMoveToTargetLoc(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), randomLoc.getX(), randomLoc.getY(), randomLoc.getZ(), npc.getInstanceWorld()), 23);
			}
			startQuestTimer("NPC_MOVE", (10 + getRandom(5)) * 1000, npc, null);
		}
		else if (event.equals("NPC_SHOUT"))
		{
			switch (getRandom(4))
			{
				case 0:
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.CARRY_OUT_YOUR_QUESTS_FAITHFULLY_IN_TALKING_ISLAND_AND_YOU_LL_GET_TO_THE_1ST_CLASS_TRANSFER_IN_NO_TIME);
					break;
				}
				case 1:
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_SEE_THAT_ADVENTURERS_ARE_RETURNING_TO_TALKING_ISLAND_FOR_THE_AWAKENING);
					break;
				}
				case 2:
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_CAN_SEE_VARIOUS_STATISTICS_IN_THE_MUSEUM_STATS_IN_THE_MAIN_MENU);
					break;
				}
			}
			startQuestTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("NPC_MOVE", (10 + getRandom(5)) * 1000, npc, null);
		startQuestTimer("NPC_SHOUT", (10 + getRandom(5)) * 1000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new Devno();
	}
}