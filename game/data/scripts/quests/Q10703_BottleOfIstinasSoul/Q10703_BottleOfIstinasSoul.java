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
package quests.Q10703_BottleOfIstinasSoul;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q00150_ExtremeChallengePrimalMotherResurrected.Q00150_ExtremeChallengePrimalMotherResurrected;

/**
 * Bottle of Istina's Soul (10703)
 * @URL http://l2on.net/en/?c=quests&id=10703&game=1
 * @author Gigi
 */
public final class Q10703_BottleOfIstinasSoul extends Quest
{
	// NPCs
	private static final int RUMIESE = 33293;
	// Item
	private static final int ISTINAS_SOUL_BOTTLE = 34883;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q10703_BottleOfIstinasSoul()
	{
		super(10703);
		addStartNpc(RUMIESE);
		addTalkId(RUMIESE);
		addCondMinLevel(MIN_LEVEL, "33293-00.html");
		addCondCompletedQuest(Q00150_ExtremeChallengePrimalMotherResurrected.class.getSimpleName(), "33293-00.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		final QuestState qs1 = player.getQuestState(Q00150_ExtremeChallengePrimalMotherResurrected.class.getSimpleName());
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "33293-02.html":
			case "33293-03.html":
			case "33293-04.html":
			{
				htmltext = event;
				break;
			}
			case "33293-05.html":
			{
				qs.startQuest();
				break;
			}
			case "33293-06.html":
			{
				if (qs.isCond(1) && (getQuestItemsCount(player, ISTINAS_SOUL_BOTTLE) >= 1))
				{
					takeItems(player, ISTINAS_SOUL_BOTTLE, 1);
					qs1.setState(State.CREATED);
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
				if (getQuestItemsCount(player, ISTINAS_SOUL_BOTTLE) >= 1)
				{
					htmltext = "33293-01.html";
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
					htmltext = "33293-05.html";
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