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
package quests.Q10797_CrossingFate;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.util.Util;

import quests.Q10796_TheEyeThatDefiedTheGods.Q10796_TheEyeThatDefiedTheGods;

/**
 * Crossing Fate (10797)
 * @URL https://l2wiki.com/Crossing_Fate
 * @author Gigi
 */
public class Q10797_CrossingFate extends Quest
{
	// NPCs
	private static final int EYE_OF_ARGOS = 31683;
	private static final int DAIMON_THE_WHITE_EYED = 27499;
	// Items
	private static final int EAA = 730;
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	
	public Q10797_CrossingFate()
	{
		super(10797);
		addStartNpc(EYE_OF_ARGOS);
		addTalkId(EYE_OF_ARGOS);
		addKillId(DAIMON_THE_WHITE_EYED);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
		addCondRace(Race.ERTHEIA, "noErtheya.html");
		addCondCompletedQuest(Q10796_TheEyeThatDefiedTheGods.class.getSimpleName(), "restriction.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = event;
		switch (event)
		{
			case "31683-02.htm":
			case "31683-03.htm":
			{
				break;
			}
			case "31683-04.htm":
			{
				qs.startQuest();
				break;
			}
			case "31683-07.html":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 2721600, 653);
					giveStoryQuestReward(player, 26);
					giveItems(player, EAA, 5);
					qs.exitQuest(false, true);
					break;
				}
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
			htmltext = "31683-01.htm";
		}
		else if (qs.isCond(1))
		{
			htmltext = "31683-05.html";
		}
		else if (qs.isCond(2))
		{
			htmltext = "31683-06.html";
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, qs.getPlayer(), false))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}