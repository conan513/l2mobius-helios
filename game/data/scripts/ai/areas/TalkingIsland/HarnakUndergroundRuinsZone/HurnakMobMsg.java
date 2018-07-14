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
package ai.areas.TalkingIsland.HarnakUndergroundRuinsZone;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Harnak Monsters AI
 * @author Gigi
 * @date 2017-03-11 - [13:50:02]
 */
public class HurnakMobMsg extends AbstractNpcAI
{
	// NPCs
	private static final int NOCTUM = 23349;
	private static final int KRAKIA = 22932;
	private static final int SEKNUS = 22938;
	
	// Attack messages
	private static final NpcStringId[] ON_ATTACK_MSG =
	{
		NpcStringId.I_NEED_HELP,
		NpcStringId.I_NEED_HEAL,
		NpcStringId.COME_AT_ME
	};
	private static final NpcStringId[] ON_FAILED_MSG =
	{
		NpcStringId.I_M_GOING_TO_BACK_OFF_FOR_A_BIT,
		NpcStringId.ONLY_DEATH_AWAITS_FOR_THE_WEAK
	};
	
	private HurnakMobMsg()
	{
		addAttackId(NOCTUM, KRAKIA);
		addKillId(SEKNUS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("ATTACK"))
		{
			npc.broadcastSay(ChatType.NPC_GENERAL, ON_ATTACK_MSG[getRandom(ON_ATTACK_MSG.length)]);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if ((npc != null) && !npc.isDead() && ((npc.getId() == NOCTUM) || (npc.getId() == KRAKIA)))
		{
			startQuestTimer("ATTACK", (getRandom(7) + 3) * 1000, npc, null);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance attacker, boolean isSummon)
	{
		npc.broadcastSay(ChatType.NPC_GENERAL, ON_FAILED_MSG[getRandom(ON_FAILED_MSG.length)]);
		return super.onKill(npc, attacker, isSummon);
	}
	
	public static void main(String[] args)
	{
		new HurnakMobMsg();
	}
}
