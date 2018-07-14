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
package quests.Q10708_StrengthenTheBarrier;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10399_TheAlphabetOfTheGiants.Q10399_TheAlphabetOfTheGiants;

/**
 * Strengthen the Barrier (10708)
 * @author St3eT
 */
public final class Q10708_StrengthenTheBarrier extends Quest
{
	// NPCs
	private static final int BACON = 33846;
	private static final int BARRIER_ENFORCER = 33960;
	private static final int LAIKEL = 27519;
	private static final int SEALBREAKER = 27523;
	// Items
	private static final int KEY = 39509; // Barrier Enforcer Key
	private static final int EWB = 947; // Scroll: Enchant Armor (B-grade)
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 57;
	
	public Q10708_StrengthenTheBarrier()
	{
		super(10708);
		addStartNpc(BACON);
		addTalkId(BACON, BARRIER_ENFORCER);
		registerQuestItems(KEY);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33846-07.htm");
		addCondCompletedQuest(Q10399_TheAlphabetOfTheGiants.class.getSimpleName(), "33846-07.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		
		if (event.equals("activateKey"))
		{
			if ((st != null) && st.isCond(1))
			{
				addAttackPlayerDesire(addSpawn(LAIKEL, npc, true, 120000), player);
				addAttackPlayerDesire(addSpawn(LAIKEL, npc, true, 120000), player);
				addAttackPlayerDesire(addSpawn(LAIKEL, npc, true, 120000), player);
				addAttackPlayerDesire(addSpawn(SEALBREAKER, npc, true, 120000), player);
				addAttackPlayerDesire(addSpawn(SEALBREAKER, npc, true, 120000), player);
				takeItems(player, KEY, -1);
				st.setCond(2, true);
			}
			else
			{
				return "33960-01.html";
			}
		}
		
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "33846-02.htm":
			{
				htmltext = event;
				break;
			}
			case "33846-03.htm":
			{
				st.startQuest();
				giveItems(player, KEY, 1);
				htmltext = event;
				break;
			}
			case "33846-06.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EWB, 1);
					giveStoryQuestReward(player, 17);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 20881876, 152);
					}
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
				if (npc.getId() == BACON)
				{
					htmltext = "33846-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (npc.getId() == BACON)
				{
					if (st.isCond(1))
					{
						htmltext = "33846-04.html";
					}
					else if (st.isCond(2))
					{
						htmltext = "33846-05.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == BACON)
				{
					htmltext = getAlreadyCompletedMsg(player);
				}
				break;
			}
		}
		return htmltext;
	}
}