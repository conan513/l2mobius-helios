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
package quests.Q10444_TheOriginOfMonsters;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.util.Util;

import quests.Q10443_TheAnnihilatedPlains2.Q10443_TheAnnihilatedPlains2;

/**
 * The Origin of Monsters (10444)
 * @URL https://l2wiki.com/The_Origin_of_Monsters
 * @author Gigi
 */
public final class Q10444_TheOriginOfMonsters extends Quest
{
	// NPCs
	private static final int PARAJAN = 33842;
	private static final int QUINCY = 33838;
	private static final int KROGEL = 25927;
	// Items
	private static final int BLOODY_ETERNEL_ENHANCEMENT_STONE = 35569;
	private static final int ELMORES_SUPPORT_BOX = 37020;
	private static final int CHUNK_OF_A_CROPSE = 36679;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10444_TheOriginOfMonsters()
	{
		super(10444);
		addStartNpc(PARAJAN);
		addTalkId(PARAJAN, QUINCY);
		addKillId(KROGEL);
		registerQuestItems(CHUNK_OF_A_CROPSE);
		addCondMinLevel(MIN_LEVEL, "33842-00.htm");
		addCondCompletedQuest(Q10443_TheAnnihilatedPlains2.class.getSimpleName(), "33842-00.htm");
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
			case "33842-02.htm":
			case "33842-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33842-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33838-02.html":
			{
				if (qs.isCond(2))
				{
					qs.exitQuest(false, true);
					giveItems(player, BLOODY_ETERNEL_ENHANCEMENT_STONE, 1);
					giveItems(player, ELMORES_SUPPORT_BOX, 1);
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
				if (npc.getId() == PARAJAN)
				{
					htmltext = "33842-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case PARAJAN:
					{
						if (qs.isCond(1))
						{
							htmltext = "33842-05.html";
						}
						else if (qs.isCond(2))
						{
							htmltext = "33842-06.html";
						}
						break;
					}
					case QUINCY:
					{
						if (qs.isStarted() && qs.isCond(2))
						{
							htmltext = "33838-01.html";
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
			giveItems(player, CHUNK_OF_A_CROPSE, 1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			if (getQuestItemsCount(player, CHUNK_OF_A_CROPSE) >= 2)
			{
				qs.setCond(2, true);
			}
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		executeForEachPlayer(killer, npc, isSummon, true, false);
		return super.onKill(npc, killer, isSummon);
	}
}