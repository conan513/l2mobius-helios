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
package quests.Q10372_PurgatoryVolvere;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassLevel;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10371_GraspThyPower.Q10371_GraspThyPower;

/**
 * Purgatory Volvere (10372)
 * @URL https://l2wiki.com/Purgatory_Volvere
 * @author Gigi
 */
public class Q10372_PurgatoryVolvere extends Quest
{
	// NPCs
	private static final int GERKENSHTEIN = 33648;
	private static final int ANDREI = 31292;
	// Monster's
	private static final int BLOODY_SUCCUBUS = 23185;
	// Items
	private static final int SUCCUBUS_ESENCE = 34766;
	private static final int GERKENSHTEINS_REPORT = 34767;
	// Reward
	private static final int EXP_REWARD = 23009000;
	private static final int SP_REWARD = 5522;
	// Misc
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 81;
	
	public Q10372_PurgatoryVolvere()
	{
		super(10372);
		addStartNpc(GERKENSHTEIN);
		addTalkId(GERKENSHTEIN, ANDREI);
		addKillId(BLOODY_SUCCUBUS);
		registerQuestItems(SUCCUBUS_ESENCE, GERKENSHTEINS_REPORT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
		addCondCompletedQuest(Q10371_GraspThyPower.class.getSimpleName(), "restriction.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "33648-02.htm":
			case "33648-03.htm":
			case "31292-02.html":
			case "31292-03.html":
			{
				htmltext = event;
				break;
			}
			case "33648-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33648-07.html":
			{
				takeItems(player, SUCCUBUS_ESENCE, -1);
				giveItems(player, GERKENSHTEINS_REPORT, 1);
				qs.setCond(0);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
			default:
			{
				if (event.startsWith("giveReward_") && qs.isCond(3))
				{
					final int itemId = Integer.parseInt(event.replace("giveReward_", ""));
					addExpAndSp(player, EXP_REWARD, SP_REWARD);
					takeItems(player, GERKENSHTEINS_REPORT, -1);
					giveItems(player, itemId, 15);
					qs.exitQuest(false, true);
					htmltext = "31292-04.html";
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
		
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				htmltext = "complete.htm";
				break;
			}
			case State.CREATED:
			{
				if (npc.getId() == GERKENSHTEIN)
				{
					htmltext = ((player.getClassId().level() == ClassLevel.FOURTH.ordinal()) ? "33648-01.htm" : "complete.htm");
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case GERKENSHTEIN:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "33648-05.html";
								break;
							}
							case 2:
							{
								htmltext = "33648-06.html";
								break;
							}
							case 3:
							{
								htmltext = "33648-07.html";
								break;
							}
						}
						break;
					}
					case ANDREI:
					{
						if ((qs.isCond(3)) && (getQuestItemsCount(player, GERKENSHTEINS_REPORT) > 0))
						{
							htmltext = "31292-01.html";
						}
						break;
					}
				}
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
			if (giveItemRandomly(killer, npc, SUCCUBUS_ESENCE, 1, 10, 0.2, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}