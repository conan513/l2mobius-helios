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

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Trainning Soldier AI.
 * @author Mobius
 */
public final class TrainningSoldier extends AbstractNpcAI
{
	// NPCs
	private static final int SOLDIER = 33201; // Trainning Soldier
	private static final int DUMMY = 33023; // Trainning Dummy
	
	private TrainningSoldier()
	{
		addSpawnId(SOLDIER);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((npc != null) && !npc.isDead())
		{
			if (!npc.isInCombat())
			{
				for (L2Npc nearby : L2World.getInstance().getVisibleObjectsInRange(npc, L2Npc.class, 150))
				{
					if ((nearby != null) && (nearby.getId() == DUMMY))
					{
						addAttackDesire(npc, nearby);
						break;
					}
				}
			}
			startQuestTimer("START_ATTACK", 10000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.setRandomAnimation(false);
		startQuestTimer("START_ATTACK", 5000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new TrainningSoldier();
	}
}