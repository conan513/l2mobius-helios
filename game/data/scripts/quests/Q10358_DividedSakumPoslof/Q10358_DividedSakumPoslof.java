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
package quests.Q10358_DividedSakumPoslof;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10337_SakumsImpact.Q10337_SakumsImpact;

/**
 * Divided Sakum, Poslof (10358)
 * @author St3eT
 */
public final class Q10358_DividedSakumPoslof extends Quest
{
	// NPCs
	private static final int LEF = 33510;
	private static final int ADVENTURER_GUIDE = 31795;
	private static final int ZOMBIE_WARRIOR = 20458;
	private static final int VEELEAN = 20402; // Veelan Bugbear Warrior
	private static final int POSLOF = 27452;
	// Items
	private static final int SAKUM_SKETCH = 17585;
	// Misc
	private static final int MIN_LEVEL = 33;
	private static final int MAX_LEVEL = 40;
	
	public Q10358_DividedSakumPoslof()
	{
		super(10358);
		addStartNpc(LEF);
		addTalkId(LEF, ADVENTURER_GUIDE);
		addKillId(ZOMBIE_WARRIOR, VEELEAN, POSLOF);
		registerQuestItems(SAKUM_SKETCH);
		addCondCompletedQuest(Q10337_SakumsImpact.class.getSimpleName(), "33510-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33510-09.html");
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
			case "33510-02.htm":
			case "31795-04.html":
			{
				htmltext = event;
				break;
			}
			case "33510-03.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "31795-05.html":
			{
				if (st.isCond(4))
				{
					giveAdena(player, 1050, true);
					addExpAndSp(player, 750000, 180);
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
				htmltext = npc.getId() == LEF ? "33510-01.htm" : "31795-02.html";
				break;
			}
			case State.STARTED:
			{
				switch (st.getCond())
				{
					case 1:
					{
						htmltext = npc.getId() == LEF ? "33510-04.html" : "31795-01.html";
						break;
					}
					case 2:
					{
						if (npc.getId() == LEF)
						{
							st.setCond(3);
							giveItems(player, SAKUM_SKETCH, 1);
							htmltext = "33510-05.html";
						}
						else if (npc.getId() == ADVENTURER_GUIDE)
						{
							htmltext = "31795-01.html";
						}
						break;
					}
					case 3:
					{
						htmltext = npc.getId() == LEF ? "33510-06.html" : "31795-01.html";
						break;
					}
					case 4:
					{
						htmltext = npc.getId() == LEF ? "33510-07.html" : "31795-03.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == LEF ? "33510-08.html" : "31795-06.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted())
		{
			if (st.isCond(1))
			{
				int killedZombies = st.getInt("killed_" + ZOMBIE_WARRIOR);
				int killedVeelans = st.getInt("killed_" + VEELEAN);
				
				if (npc.getId() == ZOMBIE_WARRIOR)
				{
					if (killedZombies < 20)
					{
						killedZombies++;
						st.set("killed_" + ZOMBIE_WARRIOR, killedZombies);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				else if (killedVeelans < 23)
				{
					killedVeelans++;
					st.set("killed_" + VEELEAN, killedVeelans);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				
				if ((killedZombies == 20) && (killedVeelans == 23))
				{
					st.setCond(2, true);
				}
			}
			else if (st.isCond(3))
			{
				st.set("killed_" + POSLOF, 1);
				st.setCond(4);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if ((st != null) && st.isStarted())
		{
			if (st.isCond(1))
			{
				final Set<NpcLogListHolder> npcLogList = new HashSet<>(2);
				npcLogList.add(new NpcLogListHolder(ZOMBIE_WARRIOR, false, st.getInt("killed_" + ZOMBIE_WARRIOR)));
				npcLogList.add(new NpcLogListHolder(VEELEAN, false, st.getInt("killed_" + VEELEAN)));
				return npcLogList;
			}
			else if (st.isCond(3))
			{
				final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
				npcLogList.add(new NpcLogListHolder(POSLOF, false, st.getInt("killed_" + POSLOF)));
				return npcLogList;
			}
		}
		return super.getNpcLogList(activeChar);
	}
}