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
package quests.Q10443_TheAnnihilatedPlains2;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10442_TheAnnihilatedPlains1.Q10442_TheAnnihilatedPlains1;

/**
 * The Annihilated Plains - 2 (10443)
 * @URL https://l2wiki.com/The_Annihilated_Plains_-_2
 * @author Gigi
 */
public final class Q10443_TheAnnihilatedPlains2 extends Quest
{
	// NPCs
	private static final int TUSKA = 33839;
	private static final int REFUGEE_CORPSE = 33837;
	private static final int FALK = 33843;
	// Item
	private static final int REFUGEES_NEACKLES = 36678;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10443_TheAnnihilatedPlains2()
	{
		super(10443);
		addStartNpc(TUSKA);
		addTalkId(TUSKA, REFUGEE_CORPSE, FALK);
		addFirstTalkId(REFUGEE_CORPSE);
		registerQuestItems(REFUGEES_NEACKLES);
		addCondMinLevel(MIN_LEVEL, "33839-00.htm");
		addCondCompletedQuest(Q10442_TheAnnihilatedPlains1.class.getSimpleName(), "33839-00.htm");
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
			case "33839-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33839-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33843-02.htm":
			{
				if (qs.isCond(2))
				{
					addExpAndSp(player, 308731500, 74095);
					giveItems(player, 30357, 50);
					giveItems(player, 30358, 50);
					giveItems(player, 34609, 10000);
					giveItems(player, 34616, 10000);
					giveItems(player, 37018, 1);
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
				if (npc.getId() == TUSKA)
				{
					htmltext = "33839-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case TUSKA:
					{
						if (qs.isCond(1))
						{
							htmltext = "33839-04.html";
						}
						break;
					}
					case REFUGEE_CORPSE:
					{
						if (qs.isStarted() && qs.isCond(1))
						{
							giveItems(player, REFUGEES_NEACKLES, 1);
							qs.setCond(2, true);
							htmltext = "33837-01.html";
						}
						break;
					}
					case FALK:
					{
						if (qs.isStarted() && qs.isCond(2))
						{
							htmltext = "33843-01.html";
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
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "33837.html";
	}
}