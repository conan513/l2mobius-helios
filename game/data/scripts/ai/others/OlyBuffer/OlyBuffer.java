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
package ai.others.OlyBuffer;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.skills.SkillCaster;

import ai.AbstractNpcAI;

/**
 * Olympiad Buffer AI.
 * @author St3eT
 */
public final class OlyBuffer extends AbstractNpcAI
{
	// NPC
	private static final int OLYMPIAD_BUFFER = 36402;
	// Skills
	private static final SkillHolder KNIGHT = new SkillHolder(14744, 1); // Olympiad - Knight's Harmony
	private static final SkillHolder WARRIOR = new SkillHolder(14745, 1); // Olympiad - Warrior's Harmony
	private static final SkillHolder WIZARD = new SkillHolder(14746, 1); // Olympiad - Wizard's Harmony
	private static final SkillHolder[] BUFFS =
	{
		new SkillHolder(14738, 1), // Olympiad - Horn Melody
		new SkillHolder(14739, 1), // Olympiad - Drum Melody
		new SkillHolder(14740, 1), // Olympiad - Pipe Organ Melody
		new SkillHolder(14741, 1), // Olympiad - Guitar Melody
		new SkillHolder(14742, 1), // Olympiad - Harp Melody
		new SkillHolder(14743, 1), // Olympiad - Lute Melody
	};
	
	private OlyBuffer()
	{
		addStartNpc(OLYMPIAD_BUFFER);
		addFirstTalkId(OLYMPIAD_BUFFER);
		addTalkId(OLYMPIAD_BUFFER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		if (npc.isScriptValue(0))
		{
			htmltext = "olympiad_master001.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		
		switch (event)
		{
			case "guardian":
			{
				htmltext = applyBuffs(npc, player, KNIGHT.getSkill());
				break;
			}
			case "berserker":
			{
				htmltext = applyBuffs(npc, player, WARRIOR.getSkill());
				break;
			}
			case "magician":
			{
				htmltext = applyBuffs(npc, player, WIZARD.getSkill());
				break;
			}
		}
		npc.setScriptValue(1);
		htmltext = "olympiad_master003.htm";
		getTimers().addTimer("DELETE_ME", 5000, evnt -> npc.deleteMe());
		
		return htmltext;
	}
	
	private String applyBuffs(L2Npc npc, L2PcInstance player, Skill skill)
	{
		for (SkillHolder holder : BUFFS)
		{
			SkillCaster.triggerCast(npc, player, holder.getSkill());
		}
		SkillCaster.triggerCast(npc, player, skill);
		return null;
	}
	
	public static void main(String[] args)
	{
		new OlyBuffer();
	}
}