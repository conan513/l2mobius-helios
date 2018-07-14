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
package quests.Q10459_ASickAmbition;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.util.Util;

import quests.Q10455_ElikiasLetter.Q10455_ElikiasLetter;

/**
 * A Sick Ambition (10459)
 * @URL https://l2wiki.com/A_Sick_Ambition
 * @author Gigi
 */
public class Q10459_ASickAmbition extends Quest
{
	// NPCs
	private static final int LEONA_BLACKBIRD = 31595;
	private static final int LEONA_BLACKBIRD_2 = 33899;
	// Boss
	private static final int DARION = 25603;
	private static final int BELETH = 29118;
	// Misc
	private static final int MIN_LEVEL = 99;
	private static final int SP_RUNE_PACK = 37903;
	
	public Q10459_ASickAmbition()
	{
		super(10459);
		addStartNpc(LEONA_BLACKBIRD);
		addTalkId(LEONA_BLACKBIRD, LEONA_BLACKBIRD_2);
		addKillId(DARION, BELETH);
		addCondMinLevel(MIN_LEVEL, "31595-00.htm");
		addCondCompletedQuest(Q10455_ElikiasLetter.class.getSimpleName(), "31595-00.htm");
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
			case "31595-02.htm":
			case "31595-03.htm":
			{
				htmltext = event;
				break;
			}
			case "31595-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33899-02.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, SP_RUNE_PACK, 1);
					addExpAndSp(player, 555716700, 2133952);
					qs.exitQuest(false, true);
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
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "31595-01.htm";
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LEONA_BLACKBIRD:
					{
						htmltext = "31595-05.html";
						break;
					}
					case LEONA_BLACKBIRD_2:
					{
						if (qs.isCond(1))
						{
							htmltext = "33899-03.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "33899-01.html";
						}
						break;
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
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, player, false))
		{
			qs.setCond(2, true);
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
}