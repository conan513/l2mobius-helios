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
package quests.Q10336_DividedSakumKanilov;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10335_RequestToFindSakum.Q10335_RequestToFindSakum;

/**
 * Divided Sakum, Kanilov (10336)
 * @author St3eT
 */
public final class Q10336_DividedSakumKanilov extends Quest
{
	// NPCs
	private static final int ZENATH = 33509;
	private static final int ADVENTURE_GUILDSMAN = 31795;
	private static final int KANILOV = 27451;
	// Items
	private static final int SAKUM_SKETCH = 17584;
	private static final int EWD = 955; // Scroll: Enchant Weapon (D-grade)
	// Misc
	private static final int MIN_LEVEL = 27;
	private static final int MAX_LEVEL = 40;
	
	public Q10336_DividedSakumKanilov()
	{
		super(10336);
		addStartNpc(ZENATH);
		addTalkId(ZENATH, ADVENTURE_GUILDSMAN);
		addKillId(KANILOV);
		registerQuestItems(SAKUM_SKETCH);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33509-08.html");
		addCondCompletedQuest(Q10335_RequestToFindSakum.class.getSimpleName(), "33509-08.html");
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
			case "33509-02.htm":
			case "31795-05.html":
			{
				htmltext = event;
				break;
			}
			case "33509-03.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "31795-06.html":
			{
				if (st.isCond(3))
				{
					giveAdena(player, 1000, true);
					giveItems(player, EWD, 3);
					addExpAndSp(player, 500000, 120);
					st.exitQuest(false, true);
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
				htmltext = npc.getId() == ZENATH ? "33509-01.htm" : "31795-02.html";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = npc.getId() == ZENATH ? "33509-04.html" : "31795-01.html";
						break;
					}
					case 2:
					{
						if (npc.getId() == ZENATH)
						{
							st.setCond(3);
							giveItems(player, SAKUM_SKETCH, 1);
							htmltext = "33509-05.html";
						}
						else
						{
							htmltext = "31795-03.html";
						}
						break;
					}
					case 3:
					{
						htmltext = npc.getId() == ZENATH ? "33509-06.html" : "31795-04.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == ZENATH ? "33509-07.html" : "31795-07.html";
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
			st.set("killed_" + KANILOV, 1);
			st.setCond(2);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(KANILOV, false, st.getInt("killed_" + KANILOV)));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}