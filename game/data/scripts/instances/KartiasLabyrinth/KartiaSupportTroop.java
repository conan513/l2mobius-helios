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
package instances.KartiasLabyrinth;

import java.util.List;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.geoengine.GeoEngine;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Kartia Support Troop AI.
 * @author St3eT
 */
public final class KartiaSupportTroop extends AbstractNpcAI
{
	// NPCs
	private static final int[] SUPPORT_TROOPS =
	{
		33642, // Support Troop (Kartia 85)
		33644, // Support Troop (Kartia 90)
		33646, // Support Troop (Kartia 95)
	};
	
	private KartiaSupportTroop()
	{
		addSpawnId(SUPPORT_TROOPS);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("NPC_SAY") && !npc.isDead())
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DEFEAT_ALL_THE_MONSTERS);
			getTimers().addTimer("NPC_SAY", 20000, npc, null);
		}
		else if (event.equals("CHECK_TARGET"))
		{
			if (!npc.isInCombat() || !npc.isAttackingNow() || (npc.getTarget() == null))
			{
				final List<L2MonsterInstance> monsterList = L2World.getInstance().getVisibleObjects(npc, L2MonsterInstance.class);
				if (!monsterList.isEmpty())
				{
					final L2MonsterInstance monster = monsterList.get(getRandom(monsterList.size()));
					
					if (monster.isTargetable() && GeoEngine.getInstance().canSeeTarget(npc, monster))
					{
						addAttackDesire(npc, monster);
					}
				}
			}
		}
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getInstanceWorld() != null)
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.VANGUARD_OF_ADEN_WE_HAVE_RETURNED);
			getTimers().addTimer("NPC_SAY", 20000, npc, null);
			getTimers().addRepeatingTimer("CHECK_TARGET", 1000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new KartiaSupportTroop();
	}
}