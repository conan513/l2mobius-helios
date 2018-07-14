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
 * Boy and Girl AI.
 * @author St3eT
 */
public final class BoyAndGirl extends AbstractNpcAI
{
	// NPCs
	private static final int BOY = 33224;
	private static final int GIRL = 33217;
	// Items
	private static final int WEAPON = 15304;
	
	private BoyAndGirl()
	{
		addSpawnId(BOY, GIRL);
		addMoveFinishedId(BOY, GIRL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("NPC_CHANGEWEAP"))
		{
			if (npc.isScriptValue(1))
			{
				npc.setRHandId(0);
				npc.setScriptValue(0);
			}
			else
			{
				npc.setRHandId(WEAPON);
				npc.setScriptValue(1);
			}
			startQuestTimer("NPC_CHANGEWEAP", 15000 + (getRandom(5) * 1000), npc, null);
		}
		else if (event.equals("NPC_SHOUT"))
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, npc.getId() == BOY ? NpcStringId.WEEE : NpcStringId.BOYS_ARE_SO_ANNOYING);
			startQuestTimer("NPC_SHOUT", 10000 + (getRandom(5) * 1000), npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("NPC_CHANGEWEAP", 15000 + (getRandom(5) * 1000), npc, null);
		startQuestTimer("NPC_SHOUT", 10000 + (getRandom(5) * 1000), npc, null);
		npc.setRunning();
		final Location randomLoc = Util.getRandomPosition(npc.getSpawn().getLocation(), 200, 600);
		addMoveToDesire(npc, GeoEngine.getInstance().canMoveToTargetLoc(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), randomLoc.getX(), randomLoc.getY(), randomLoc.getZ(), npc.getInstanceWorld()), 23);
		return super.onSpawn(npc);
	}
	
	@Override
	public void onMoveFinished(L2Npc npc)
	{
		final Location randomLoc = Util.getRandomPosition(npc.getSpawn().getLocation(), 200, 600);
		addMoveToDesire(npc, GeoEngine.getInstance().canMoveToTargetLoc(npc.getLocation().getX(), npc.getLocation().getY(), npc.getLocation().getZ(), randomLoc.getX(), randomLoc.getY(), randomLoc.getZ(), npc.getInstanceWorld()), 23);
		super.onMoveFinished(npc);
	}
	
	public static void main(String[] args)
	{
		new BoyAndGirl();
	}
}