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

import java.util.List;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Ye Sagira Guards AI.
 * @author Mobius
 */
public final class YeSagiraGuards extends AbstractNpcAI
{
	// NPCs
	private static final int GUARDS[] =
	{
		19152,
		19153
	};
	
	private YeSagiraGuards()
	{
		addSpawnId(GUARDS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((npc != null) && !npc.isDead())
		{
			if (!npc.isInCombat())
			{
				final List<L2MonsterInstance> nearbyMonsters = L2World.getInstance().getVisibleObjectsInRange(npc, L2MonsterInstance.class, 1000);
				if (!nearbyMonsters.isEmpty())
				{
					final L2MonsterInstance monster = nearbyMonsters.get(getRandom(nearbyMonsters.size()));
					if ((monster != null) && !monster.isDead() && !monster.isInCombat())
					{
						npc.reduceCurrentHp(1, monster, null); // TODO: Find better way for attack
					}
				}
			}
			startQuestTimer("GUARD_AGGRO", 10000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.setIsInvul(true);
		startQuestTimer("GUARD_AGGRO", 5000, npc, null);
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new YeSagiraGuards();
	}
}
