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
package quests.Q10781_IngredientsToEnforcements;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10780_AWeakenedBarrier.Q10780_AWeakenedBarrier;

/**
 * Ingredients to Enforcements (10781)
 * @author malyelfik
 */
public final class Q10781_IngredientsToEnforcements extends Quest
{
	// NPC
	private static final int BACON = 33846;
	// Monsters
	private static final int[] MONSTERS =
	{
		23309, // Corpse Looter Stakato
		23310, // Lesser Laikel
	};
	// Items
	private static final int WIND_SPIRIT_FRAGMENT = 39721;
	private static final int ENCHANT_ARMOR_B = 948;
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 58;
	
	public Q10781_IngredientsToEnforcements()
	{
		super(10781);
		addStartNpc(BACON);
		addTalkId(BACON);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33846-00.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33846-01.htm");
		addCondCompletedQuest(Q10780_AWeakenedBarrier.class.getSimpleName(), "33846-01.htm");
		registerQuestItems(WIND_SPIRIT_FRAGMENT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "33846-03.htm":
			case "33846-04.htm":
			{
				break;
			}
			case "33846-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "33846-08.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_B, 5);
					giveStoryQuestReward(player, 37);
					addExpAndSp(player, 19688585, 914);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
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
				htmltext = "33846-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33846-06.html" : "33846-07.html";
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
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			final long itemCount = getQuestItemsCount(killer, WIND_SPIRIT_FRAGMENT);
			if (itemCount < 80)
			{
				giveItems(killer, WIND_SPIRIT_FRAGMENT, 1);
				if (getQuestItemsCount(killer, WIND_SPIRIT_FRAGMENT) >= 80)
				{
					qs.setCond(2, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}