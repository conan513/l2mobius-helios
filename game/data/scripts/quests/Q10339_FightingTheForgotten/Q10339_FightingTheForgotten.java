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
package quests.Q10339_FightingTheForgotten;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * Fighting the Forgotten (10339)
 * @author St3eT
 */
public final class Q10339_FightingTheForgotten extends Quest
{
	// NPCs
	private static final int THEODORE = 32975;
	private static final int HADEL = 33344;
	private static final int[] MONSTERS =
	{
		22935, // Weiss Khan
		22936, // Weiss Ele
		22937, // Bamonti
		22931, // Krakia Bathus
		22934, // Rakzan
		22933, // Krakia Lotus
		23349, // Noctum
		22938, // Seknus
		22932, // Krakia Carcass
	};
	// Location
	private static final Location TELEPORT_LOC = new Location(-114857, 147877, -7720);
	// Misc
	private static final int MIN_LV = 85;
	
	public Q10339_FightingTheForgotten()
	{
		super(10339);
		addStartNpc(THEODORE);
		addTalkId(THEODORE, HADEL);
		addKillId(MONSTERS);
		addCondMinLevel(MIN_LV, "32975-06.html");
		addCondInCategory(CategoryType.SIXTH_CLASS_GROUP, "32975-06.html");
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
			case "32975-02.htm":
			case "32975-03.htm":
			case "32975-04.htm":
			{
				htmltext = event;
				break;
			}
			case "32975-05.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "TELEPORT":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					player.teleToLocation(TELEPORT_LOC);
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == THEODORE)
				{
					htmltext = "32975-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == THEODORE)
				{
					if (st.isCond(1))
					{
						htmltext = "32975-05.html";
					}
				}
				else if (npc.getId() == HADEL)
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "33344-01.html";
							break;
						}
						case 2:
						{
							htmltext = "33344-02.html";
							break;
						}
						case 3:
						{
							st.exitQuest(false, true);
							giveAdena(player, 528_210, true);
							addExpAndSp(player, 238_423_500, 57_221);
							htmltext = "33344-03.html";
							break;
						}
					}
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
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(2))
		{
			final int monsterCount = st.getMemoState() + 1;
			if (monsterCount >= 12)
			{
				st.setCond(3, true);
				st.setMemoState(0);
			}
			else
			{
				st.setMemoState(monsterCount);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if (st != null)
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(1);
			npcLogList.add(new NpcLogListHolder(NpcStringId.ELIMINATING_THE_ANCIENT_GHOSTS, st.getMemoState()));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}