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
package ai.areas.IsleOfSouls;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Hills of Gold AI.
 * @author Mobius
 */
public final class HillsOfGold extends AbstractNpcAI
{
	// NPCs
	private static final int GOLEM_OF_REPAIRS = 19309;
	private static final int EXCAVATOR_GOLEM = 19312;
	private static final int DRILL_GOLEM = 19310;
	private static final int SPICULA_1 = 23246;
	private static final int SPICULA_2 = 23247;
	private static final int YIN_FRAGMENT = 19308;
	private static final int SPICULA_ELITE_GUARD = 23303;
	private static final int[] GOLEMS =
	{
		23255,
		23257,
		23259,
		23261,
		23263,
		23264,
		23266,
		23267,
	};
	
	public HillsOfGold()
	{
		addAttackId(YIN_FRAGMENT);
		addSpawnId(SPICULA_1, SPICULA_2);
		addSpawnId(GOLEMS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((npc != null) && !npc.isDead())
		{
			L2World.getInstance().forEachVisibleObjectInRange(npc, L2MonsterInstance.class, npc.getAggroRange(), nearby ->
			{
				if (npc.isInCombat())
				{
					return;
				}
				if ((nearby.getId() == GOLEM_OF_REPAIRS) || (nearby.getId() == EXCAVATOR_GOLEM) || (nearby.getId() == DRILL_GOLEM))
				{
					addAttackDesire(npc, nearby);
					return;
				}
			});
			startQuestTimer("SPICULA_AGGRO" + npc.getObjectId(), 10000, npc, null);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final L2Npc mob1 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob1, attacker);
		final L2Npc mob2 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob2, attacker);
		final L2Npc mob3 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob3, attacker);
		final L2Npc mob4 = addSpawn(SPICULA_ELITE_GUARD, npc.getX(), npc.getY(), npc.getZ(), attacker.getHeading() + 32500, true, npc.getSpawn().getRespawnDelay());
		addAttackDesire(mob4, attacker);
		npc.deleteMe();
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if ((npc.getId() == SPICULA_1) || (npc.getId() == SPICULA_2))
		{
			startQuestTimer("SPICULA_AGGRO" + npc.getObjectId(), 5000, npc, null);
		}
		else
		{
			npc.setDisplayEffect(1);
		}
		return super.onSpawn(npc);
	}
	
	public static void main(String[] args)
	{
		new HillsOfGold();
	}
}