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
package ai.areas.GardenOfSpirits;

import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * Isabella Raid Boss AI
 * @URL https://www.youtube.com/watch?v=3M73b6Kre6Y
 * @author Gigi
 */
public final class Isabella extends AbstractNpcAI
{
	// NPC
	private static final int ISABELLA = 26131;
	// Minions
	private static final int CROA = 26132;
	private static final int AMIS = 26133;
	
	public Isabella()
	{
		addAttackId(ISABELLA);
		addKillId(ISABELLA);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("SPAWN"))
		{
			final L2Npc minion1 = addSpawn(CROA, -51104, 83436, -5120, 61567, false, 300000, false);
			addAttackPlayerDesire(minion1, player);
			final L2Npc minion2 = addSpawn(AMIS, -50307, 83662, -5120, 45183, false, 300000, false);
			addAttackPlayerDesire(minion2, player);
			final L2Npc minion3 = addSpawn(CROA, -50259, 82825, -5120, 23684, false, 300000, false);
			addAttackPlayerDesire(minion3, player);
			final L2Npc minion4 = addSpawn(AMIS, -50183, 82901, -5120, 28180, false, 300000, false);
			addAttackPlayerDesire(minion4, player);
			final L2Npc minion5 = addSpawn(CROA, -50387, 83732, -5112, 45183, false, 300000, false);
			addAttackPlayerDesire(minion5, player);
			final L2Npc minion6 = addSpawn(AMIS, -51157, 83298, -5112, 64987, true, 300000, false);
			addAttackPlayerDesire(minion6, player);
			return null;
		}
		return event;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (!npc.isDead())
		{
			if ((npc.getCurrentHpPercent() <= 80) && npc.isScriptValue(0))
			{
				startQuestTimer("SPAWN", 500, npc, attacker);
				npc.setScriptValue(1);
			}
			else if ((npc.getCurrentHpPercent() <= 50) && npc.isScriptValue(1))
			{
				startQuestTimer("SPAWN", 500, npc, attacker);
				npc.setScriptValue(2);
			}
			else if ((npc.getCurrentHpPercent() <= 10) && npc.isScriptValue(2))
			{
				startQuestTimer("SPAWN", 500, npc, attacker);
				npc.setScriptValue(3);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		L2World.getInstance().forEachVisibleObjectInRange(npc, L2Npc.class, 1500, minion ->
		{
			if ((minion.getId() == CROA) || (minion.getId() == AMIS))
			{
				minion.deleteMe();
			}
		});
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Isabella();
	}
}