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
package quests.Q10334_ReportingTheStatusOfTheWindmillHill;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10333_DisappearedSakum.Q10333_DisappearedSakum;

/**
 * Reporting The Status Of The Windmill Hill (10334)
 * @author St3eT
 */
public final class Q10334_ReportingTheStatusOfTheWindmillHill extends Quest
{
	// NPCs
	private static final int SCHUNAIN = 33508;
	private static final int BATHIS = 30332;
	// Misc
	private static final int MIN_LEVEL = 22;
	private static final int MAX_LEVEL = 40;
	
	public Q10334_ReportingTheStatusOfTheWindmillHill()
	{
		super(10334);
		addStartNpc(SCHUNAIN);
		addTalkId(SCHUNAIN, BATHIS);
		addCondNotRace(Race.ERTHEIA, "33508-07.html");
		addCondCompletedQuest(Q10333_DisappearedSakum.class.getSimpleName(), "33508-06.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33508-06.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33508-02.htm":
			case "30332-02.html":
			{
				htmltext = event;
				break;
			}
			case "33508-03.html":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				htmltext = event;
				break;
			}
			case "30332-03.html":
			{
				if (qs.isCond(1))
				{
					giveAdena(player, 850, true);
					addExpAndSp(player, 200000, 48);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == SCHUNAIN)
				{
					htmltext = "33508-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				htmltext = npc.getId() == SCHUNAIN ? "33508-04.html" : "30332-01.html";
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == SCHUNAIN ? "33508-05.html" : "30332-04.html";
				break;
			}
		}
		return htmltext;
	}
}