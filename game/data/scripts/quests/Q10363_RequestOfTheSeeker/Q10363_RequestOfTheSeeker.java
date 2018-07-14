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
package quests.Q10363_RequestOfTheSeeker;

import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.Q10362_CertificationOfTheSeeker.Q10362_CertificationOfTheSeeker;

/**
 * Request of the Seeker (10363)
 * @URL https://l2wiki.com/Request_of_the_Seeker
 * @author Gladicek, Gigi
 */
public final class Q10363_RequestOfTheSeeker extends Quest
{
	// NPCs
	private static final int NAGEL = 33450;
	private static final int CELIN = 33451;
	private static final int[] MONSTERS =
	{
		22991, // Crawler
		22996 // Krapher
	};
	// Items
	private static final int WOODEN_HELMET = 43;
	private static final int HUSK_DISTRIBUTION_REPORT = 47606;
	// Misc
	private static final int MIN_LEVEL = 11;
	private static final int MAX_LEVEL = 20;
	
	public Q10363_RequestOfTheSeeker()
	{
		super(10363);
		addStartNpc(NAGEL);
		addTalkId(NAGEL, CELIN);
		addKillId(MONSTERS);
		registerQuestItems(HUSK_DISTRIBUTION_REPORT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33450-07.html");
		addCondCompletedQuest(Q10362_CertificationOfTheSeeker.class.getSimpleName(), "33450-07.html");
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
			case "33450-02.htm":
			case "33450-02a.htm":
			{
				htmltext = event;
				break;
			}
			case "33450-03.htm":
			{
				qs.startQuest();
				showOnScreenMsg(player, NpcStringId.USE_THE_YE_SAGIRA_TELEPORT_DEVICE_SHINING_WITH_A_RED_SHIMMER_TO_GO_TO_EXPLORATION_AREA_2, ExShowScreenMessage.TOP_CENTER, 10000);
				htmltext = event;
				break;
			}
			
			case "33451-02.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, WOODEN_HELMET, 1);
					addExpAndSp(player, 70000, 13);
					qs.exitQuest(false, true);
					htmltext = event;
					break;
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
				if (npc.getId() == NAGEL)
				{
					htmltext = "33450-01.htm";
					break;
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == NAGEL)
				{
					switch (qs.getCond())
					{
						case 1:
						case 2:
						{
							htmltext = "33450-04.html";
							break;
						}
					}
					break;
				}
				else if (npc.getId() == CELIN)
				{
					if (qs.isCond(2))
					{
						htmltext = "33451-01.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == CELIN)
				{
					htmltext = "33451-03.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (CommonUtil.contains(MONSTERS, npc.getId()))
		{
			if ((qs != null) && qs.isCond(1) && giveItemRandomly(killer, npc, HUSK_DISTRIBUTION_REPORT, 1, 15, 0.8, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}