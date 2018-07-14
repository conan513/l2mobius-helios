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
package quests.Q10387_SoullessOne;

import com.l2jmobius.gameserver.enums.Movie;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10386_MysteriousJourney.Q10386_MysteriousJourney;

/**
 * Soulless One (10387)
 * @author St3eT
 */
public final class Q10387_SoullessOne extends Quest
{
	// NPCs
	private static final int HASED = 33780;
	private static final int VERNA = 33796;
	private static final int SPICULA_ZERO = 25901;
	// Items
	private static final int EWR = 17526; // Scroll: Enchant Weapon (R-grade)
	private static final int EAR = 17527; // Scroll: Enchant Armor (R-grade)
	private static final int COKES = 36563; // Synthetic Cokes
	private static final int POUCH = 34861; // Ingredient and Hardener Pouch (R-grade)
	// Misc
	private static final int MIN_LEVEL = 93;
	
	public Q10387_SoullessOne()
	{
		super(10387);
		addStartNpc(HASED);
		addTalkId(HASED, VERNA);
		addKillId(SPICULA_ZERO);
		addCondMinLevel(MIN_LEVEL, "33796-12.htm");
		addCondCompletedQuest(Q10386_MysteriousJourney.class.getSimpleName(), "33796-12.htm");
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
			case "33796-02.html":
			case "33796-03.html":
			case "33796-04.html":
			case "33796-08.html":
			case "33796-09.html":
			{
				htmltext = event;
				break;
			}
			case "33780-02.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33796-05.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					playMovie(player, Movie.SC_METUCELLAR_OPENING); // TODO: Need be created instance and run movie in instance
					htmltext = event;
				}
				break;
			}
			case "reward1":
			{
				if (st.isCond(3))
				{
					giveItems(player, COKES, 68);
					giveItems(player, EAR, 5);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 817_330_500, 196_159);
					}
					st.exitQuest(false, true);
					htmltext = "33796-10.html";
				}
				break;
			}
			case "reward2":
			{
				if (st.isCond(3))
				{
					giveItems(player, EWR, 1);
					giveItems(player, EAR, 6);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 817_330_500, 196_159);
					}
					st.exitQuest(false, true);
					htmltext = "33796-10.html";
				}
				break;
			}
			case "reward3":
			{
				if (st.isCond(3))
				{
					giveItems(player, POUCH, 1);
					giveItems(player, EAR, 5);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 817_330_500, 196_159);
					}
					st.exitQuest(false, true);
					htmltext = "33796-10.html";
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
				if (npc.getId() == HASED)
				{
					htmltext = "33780-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == HASED)
				{
					if (st.isCond(1))
					{
						htmltext = "33780-03.html";
					}
				}
				else
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "33796-01.htm";
							break;
						}
						case 2:
						{
							htmltext = "33796-06.html";
							break;
						}
						case 3:
						{
							htmltext = "33796-07.html";
							break;
						}
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == VERNA ? "33796-11.htm" : "33780-04.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(2))
		{
			st.setCond(0);
			st.setCond(3, true);
		}
	}
}