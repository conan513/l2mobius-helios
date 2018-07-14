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
package quests.Q10399_TheAlphabetOfTheGiants;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10398_ASuspiciousBadge.Q10398_ASuspiciousBadge;

/**
 * The Alphabet of the Giants (10399)
 * @author St3eT
 */
public final class Q10399_TheAlphabetOfTheGiants extends Quest
{
	// NPCs
	private static final int BACON = 33846;
	private static final int[] MONSTERS =
	{
		23309, // Corpse Looter Stakato
		23310, // Lesser Laikel
	};
	// Items
	private static final int TABLET = 36667; // Giant's Alphabet
	private static final int EAB = 948; // Scroll: Enchant Armor (B-grade)
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 58;
	
	public Q10399_TheAlphabetOfTheGiants()
	{
		super(10399);
		addStartNpc(BACON);
		addTalkId(BACON);
		addKillId(MONSTERS);
		registerQuestItems(TABLET);
		addCondNotRace(Race.ERTHEIA, "33846-08.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33846-07.htm");
		addCondCompletedQuest(Q10398_ASuspiciousBadge.class.getSimpleName(), "33846-07.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33846-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33846-03.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33846-06.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAB, 5);
					giveStoryQuestReward(player, 37);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 8779765, 914);
					}
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
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = "33846-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = "33846-04.html";
				}
				else if (st.isCond(2))
				{
					htmltext = "33846-05.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			if (giveItemRandomly(killer, npc, TABLET, 1, 50, 1, true))
			{
				st.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}