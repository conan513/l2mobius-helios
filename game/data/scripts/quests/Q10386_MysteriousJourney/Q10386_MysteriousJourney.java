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
package quests.Q10386_MysteriousJourney;

import com.l2jmobius.gameserver.enums.Movie;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Mysterious Journey (10386)
 * @author St3eT
 */
public final class Q10386_MysteriousJourney extends Quest
{
	// NPCs
	private static final int TAPOY = 30499;
	private static final int HESED = 33780;
	private static final int SEAL_WATCHMAN = 33797;
	private static final int VERNA = 33796;
	// Items
	private static final int VACCINE_BOX = 36073; // Vaccine Box
	private static final int EWR = 17526; // Scroll: Enchant Weapon (R-grade)
	// Misc
	private static final int MIN_LEVEL = 93;
	
	public Q10386_MysteriousJourney()
	{
		super(10386);
		addStartNpc(TAPOY);
		addTalkId(TAPOY, HESED, SEAL_WATCHMAN, VERNA);
		registerQuestItems(VACCINE_BOX);
		addCondMinLevel(MIN_LEVEL, "30499-05.htm");
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
			case "30499-02.htm":
			case "33780-02.html":
			case "33780-03.html":
			case "33797-02.html":
			case "33796-02.html":
			{
				htmltext = event;
				break;
			}
			case "30499-03.htm":
			{
				st.startQuest();
				giveItems(player, VACCINE_BOX, 1);
				playMovie(player, Movie.SC_SOULISLAND_QUEST);
				htmltext = event;
				break;
			}
			case "33780-04.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33797-03.html":
			{
				if (st.isCond(2))
				{
					st.setCond(0);
					st.setCond(3, true);
					takeItems(player, VACCINE_BOX, 1);
					htmltext = event;
				}
				break;
			}
			case "33796-03.html":
			{
				if (st.isCond(3))
				{
					st.setCond(4, true);
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
				if (npc.getId() == TAPOY)
				{
					htmltext = "30499-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case TAPOY:
					{
						if (st.isCond(1))
						{
							htmltext = "30499-04.html";
						}
						break;
					}
					case HESED:
					{
						switch (st.getCond())
						{
							case 1:
							{
								htmltext = "33780-01.html";
								break;
							}
							case 2:
							{
								htmltext = "33780-05.html";
								break;
							}
							case 4:
							{
								giveAdena(player, 58_707, true);
								giveItems(player, EWR, 1);
								if (player.getLevel() >= MIN_LEVEL)
								{
									addExpAndSp(player, 27_244_350, 6_538);
								}
								st.exitQuest(false, true);
								htmltext = "33780-06.html";
								break;
							}
						}
						break;
					}
					case SEAL_WATCHMAN:
					{
						if (st.isCond(2))
						{
							htmltext = "33797-01.html";
						}
						else if (st.isCond(3))
						{
							htmltext = "33797-03.html";
						}
						break;
					}
					case VERNA:
					{
						if (st.isCond(3))
						{
							htmltext = "33796-01.html";
						}
						else if (st.isCond(4))
						{
							htmltext = "33796-04.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == TAPOY)
				{
					htmltext = "30499-06.html";
				}
				else if (npc.getId() == HESED)
				{
					htmltext = "33780-07.html";
				}
				break;
			}
		}
		return htmltext;
	}
}