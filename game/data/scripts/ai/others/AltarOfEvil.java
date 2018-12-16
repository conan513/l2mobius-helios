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
package ai.others;

import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import ai.AbstractNpcAI;

/**
 * Altar of Evil AI.
 * @author St3eT
 */
public final class AltarOfEvil extends AbstractNpcAI
{
	// NPCs
	private static final int RIFTER = 23179; // Dimensional Rifter
	// Skill
	private static final SkillHolder SKILL = new SkillHolder(14643, 1); // Summon
	
	public AltarOfEvil()
	{
		addAttackId(RIFTER);
		addNpcHateId(RIFTER);
		addSpellFinishedId(RIFTER);
	}
	
	@Override
	public boolean onNpcHate(L2Attackable mob, L2PcInstance player, boolean isSummon)
	{
		teleportPlayer(mob, player);
		return super.onNpcHate(mob, player, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		teleportPlayer(npc, attacker);
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill)
	{
		if (skill.getId() == SKILL.getSkillId())
		{
			showOnScreenMsg(player, NpcStringId.DIMENSIONAL_RIFTER_SUMMONED_YOU, ExShowScreenMessage.TOP_CENTER, 5000);
			player.teleToLocation(npc);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	private void teleportPlayer(L2Npc npc, L2PcInstance player)
	{
		if (npc.isScriptValue(0) && (npc.calculateDistance3D(player) > 200))
		{
			addSkillCastDesire(npc, player, SKILL, 23);
			npc.setScriptValue(1);
		}
	}
	
	public static void main(String[] args)
	{
		new AltarOfEvil();
	}
}