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
package quests.Q10704_BottleOfOctavisSoul;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10354_ResurrectedOwnerOfHall.Q10354_ResurrectedOwnerOfHall;

/**
 * Bottle of Octavis' Soul (10704)
 * @URL http://l2on.net/en/?c=quests&id=10704&game=1
 * @author Gigi
 */
public final class Q10704_BottleOfOctavisSoul extends Quest
{
	// NPCs
	private static final int LYDIA = 32892;
	// Item
	private static final int OCTAVIS_SOUL_BOTTLE = 34884;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10704_BottleOfOctavisSoul()
	{
		super(10704);
		addStartNpc(LYDIA);
		addTalkId(LYDIA);
		addCondMinLevel(MIN_LEVEL, "32892-00.html");
		addCondCompletedQuest(Q10354_ResurrectedOwnerOfHall.class.getSimpleName(), "32892-00.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		final QuestState qs1 = player.getQuestState(Q10354_ResurrectedOwnerOfHall.class.getSimpleName());
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "32892-02.html":
			case "32892-03.html":
			case "32892-04.html":
			{
				htmltext = event;
				break;
			}
			case "32892-05.html":
			{
				qs.startQuest();
				break;
			}
			case "32892-06.html":
			{
				if (qs.isCond(1) && (getQuestItemsCount(player, OCTAVIS_SOUL_BOTTLE) >= 1))
				{
					takeItems(player, OCTAVIS_SOUL_BOTTLE, 1);
					qs1.setState(State.CREATED);
					qs1.setMemoState(1);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				else
				{
					htmltext = getNoQuestMsg(player);
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
				if (getQuestItemsCount(player, OCTAVIS_SOUL_BOTTLE) >= 1)
				{
					htmltext = "32892-01.html";
				}
				else
				{
					htmltext = getNoQuestMsg(player);
				}
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32892-05.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getNoQuestMsg(player);
				break;
			}
		}
		return htmltext;
	}
}