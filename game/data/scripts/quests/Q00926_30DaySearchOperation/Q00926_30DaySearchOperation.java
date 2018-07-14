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
package quests.Q00926_30DaySearchOperation;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Exploring the Dimension - 30-day Search Operation (926)
 * @URL https://l2wiki.com/Exploring_the_Dimension_-_30-day_Search_Operation
 * @Custom-Made based on quest 928
 * @author Mobius
 */
public class Q00926_30DaySearchOperation extends Quest
{
	// NPC
	private static final int BELOA = 34227;
	// Monsters
	private static final int WANDERING_OF_DIMENSION = 23755;
	private static final int LOST_SOUL_DIMENSION = 23757;
	private static final int ROAMING_VENGEANCE = 23759;
	// Items
	private static final int SPIRIT_FRAGMENTS = 46785;
	private static final int BELOAS_SUPPLY_ITEMS = 47043;
	private static final int REMNANT_OF_THE_RIFT = 46787;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q00926_30DaySearchOperation()
	{
		super(926);
		addStartNpc(BELOA);
		addTalkId(BELOA);
		addKillId(WANDERING_OF_DIMENSION, LOST_SOUL_DIMENSION, ROAMING_VENGEANCE);
		registerQuestItems(SPIRIT_FRAGMENTS);
		addCondMinLevel(MIN_LEVEL, "34227-00.html");
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
			case "34227-02.htm":
			case "34227-03.htm":
			{
				htmltext = event;
				break;
			}
			case "34227-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34227-07.html":
			{
				if (player.getLevel() >= MIN_LEVEL)
				{
					if (getQuestItemsCount(player, REMNANT_OF_THE_RIFT) < 29)
					{
						giveItems(player, REMNANT_OF_THE_RIFT, 1);
						giveItems(player, BELOAS_SUPPLY_ITEMS, 1);
						addExpAndSp(player, 1507592779L, 3618222);
						qs.exitQuest(QuestType.DAILY, true);
						htmltext = event;
						break;
					}
					addExpAndSp(player, 1507592779L, 3618222);
					giveItems(player, REMNANT_OF_THE_RIFT, 1);
					giveItems(player, BELOAS_SUPPLY_ITEMS, 1);
					qs.exitQuest(QuestType.ONE_TIME, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestLevelRewardMsg(player);
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
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				htmltext = "34227-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "34227-05.html" : "34227-06.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && (qs.isCond(1)))
		{
			if (giveItemRandomly(killer, npc, SPIRIT_FRAGMENTS, 1, 100, 1.0, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}