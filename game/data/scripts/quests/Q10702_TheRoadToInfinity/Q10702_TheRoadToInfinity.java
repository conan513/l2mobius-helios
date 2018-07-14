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
package quests.Q10702_TheRoadToInfinity;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * The Road to Infinity (10702)
 * @author Gladicek
 */
public final class Q10702_TheRoadToInfinity extends Quest
{
	// NPCs
	private static final int KEUCEREUS = 32548;
	private static final int TEPIOS = 32603;
	// Item
	private static final int KEUCEREUS_INTRODUCTION_SOI = 38578;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10702_TheRoadToInfinity()
	{
		super(10702);
		addStartNpc(KEUCEREUS);
		addTalkId(KEUCEREUS, TEPIOS);
		addCondMinLevel(MIN_LEVEL, "32548-06.htm");
		registerQuestItems(KEUCEREUS_INTRODUCTION_SOI);
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
			case "32548-02.htm":
			case "32548-03.htm":
			{
				htmltext = event;
				break;
			}
			case "32548-04.html":
			{
				qs.startQuest();
				giveItems(player, KEUCEREUS_INTRODUCTION_SOI, 1);
				break;
			}
			case "32603-02.html":
			{
				if (qs.isCond(1))
				{
					giveAdena(player, 18243, true);
					addExpAndSp(player, 8_528_625, 2046);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == KEUCEREUS)
				{
					htmltext = "32548-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				htmltext = npc.getId() == KEUCEREUS ? "32548-05.html" : "32603-01.html";
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == KEUCEREUS)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}