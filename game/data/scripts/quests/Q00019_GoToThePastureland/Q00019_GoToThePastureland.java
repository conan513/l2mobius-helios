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
package quests.Q00019_GoToThePastureland;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Go to the Pastureland (19)
 * @author malyelfik
 */
public final class Q00019_GoToThePastureland extends Quest
{
	// NPCs
	private static final int VLADIMIR = 31302;
	private static final int TUNATUN = 31537;
	// Items
	private static final int VEAL = 15532;
	private static final int YOUNG_WILD_BEAST_MEAT = 7547;
	// Misc
	private static final int MIN_LEVEL = 82;
	
	public Q00019_GoToThePastureland()
	{
		super(19);
		addStartNpc(VLADIMIR);
		addTalkId(VLADIMIR, TUNATUN);
		registerQuestItems(VEAL, YOUNG_WILD_BEAST_MEAT);
		addCondMinLevel(MIN_LEVEL, "31302-03.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (event.equalsIgnoreCase("31302-02.htm"))
		{
			st.startQuest();
			giveItems(player, VEAL, 1);
		}
		else if (event.equalsIgnoreCase("31537-02.htm"))
		{
			if (hasQuestItems(player, YOUNG_WILD_BEAST_MEAT))
			{
				giveAdena(player, 50000, true);
				addExpAndSp(player, 136766, 59); // TODO: Retail like SP value
				st.exitQuest(false, true);
				htmltext = event;
			}
			else if (hasQuestItems(player, VEAL))
			{
				giveAdena(player, 299928, true);
				addExpAndSp(player, 1_456_218, 349);
				st.exitQuest(false, true);
				htmltext = event;
			}
			else
			{
				htmltext = "31537-03.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if (npc.getId() == VLADIMIR)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = "31302-01.html";
					break;
				}
				case State.STARTED:
				{
					htmltext = "31302-04.html";
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if ((npc.getId() == TUNATUN) && (st.isCond(1)))
		{
			htmltext = "31537-01.html";
		}
		return htmltext;
	}
}
