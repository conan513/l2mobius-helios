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
package quests.Q10453_StoppingTheWindDragon;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;

/**
 * @author hlwrave
 */
public class Q10453_StoppingTheWindDragon extends Quest
{
	// NPC
	private static final int JENNA = 33872;
	// Monsters
	private static final int LINDVIOR = 29240;
	// Items
	private static final int LINDVIOR_SLAYERS_HELMET = 37497;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10453_StoppingTheWindDragon()
	{
		super(10453);
		addStartNpc(JENNA);
		addTalkId(JENNA);
		addKillId(LINDVIOR);
		addCondMinLevel(MIN_LEVEL, "adens_wizard_jenna_q10453_0.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "adens_wizard_jenna_q10453_2.html":
			{
				qs.startQuest();
				break;
			}
			case "adens_wizard_jenna_q10453_5.html":
			{
				addExpAndSp(player, 2147483500, 37047780);
				giveItems(player, LINDVIOR_SLAYERS_HELMET, 1);
				qs.exitQuest(QuestType.ONE_TIME, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCreated())
		{
			htmltext = "adens_wizard_jenna_q10453_1.htm";
		}
		else if (qs.isStarted())
		{
			if (qs.isCond(1))
			{
				htmltext = "adens_wizard_jenna_q10453_3.html";
			}
			else if (qs.isCond(2))
			{
				htmltext = "adens_wizard_jenna_q10453_4.html";
			}
			
		}
		else if (qs.isCompleted())
		{
			htmltext = "adens_wizard_jenna_q10453_6.html";
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			qs.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
}