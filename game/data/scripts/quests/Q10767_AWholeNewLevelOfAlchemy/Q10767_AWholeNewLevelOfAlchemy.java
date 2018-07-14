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
package quests.Q10767_AWholeNewLevelOfAlchemy;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.Id;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.item.OnItemCreate;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * A Whole New Level of Alchemy (10767)
 * @URL https://l2wiki.com/A_Whole_New_Level_of_Alchemy
 * @author Gigi
 */
public class Q10767_AWholeNewLevelOfAlchemy extends Quest
{
	// NPC
	private static final int VERUTI = 33977;
	// Items
	private static final int SUPERIOR_WINDY_HEALING_POTION = 39469;
	private static final int SUPERIOR_WINDY_QUIK_HEALING_POTION = 39474;
	private static final int HIGH_GRADE_LOVE_POTION = 39479;
	// Reward
	private static final int EXP_REWARD = 14819175;
	private static final int SP_REWARD = 3556;
	private static final int ALCHEMIC_TOME_POTION = 39482;
	// Misc
	private static final int MIN_LEVEL = 97;
	
	public Q10767_AWholeNewLevelOfAlchemy()
	{
		super(10767);
		addStartNpc(VERUTI);
		addTalkId(VERUTI);
		registerQuestItems(SUPERIOR_WINDY_HEALING_POTION, SUPERIOR_WINDY_QUIK_HEALING_POTION, HIGH_GRADE_LOVE_POTION);
		addCondMinLevel(MIN_LEVEL, "33977-00.htm");
		addCondRace(Race.ERTHEIA, "noErtheia.html");
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
			case "33977-02.htm":
			case "33977-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33977-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33977-07.html":
			{
				if (qs.isCond(2))
				{
					takeItems(player, SUPERIOR_WINDY_HEALING_POTION, 1000);
					takeItems(player, SUPERIOR_WINDY_QUIK_HEALING_POTION, 1000);
					takeItems(player, HIGH_GRADE_LOVE_POTION, 1000);
					giveItems(player, ALCHEMIC_TOME_POTION, 3);
					addExpAndSp(player, EXP_REWARD, SP_REWARD);
					qs.exitQuest(false, true);
				}
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33977-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "33977-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "33977-06.html";
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
	
	@RegisterEvent(EventType.ON_ITEM_CREATE)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(SUPERIOR_WINDY_HEALING_POTION)
	@Id(SUPERIOR_WINDY_QUIK_HEALING_POTION)
	@Id(HIGH_GRADE_LOVE_POTION)
	public void onItemCreate(OnItemCreate event)
	{
		final L2PcInstance player = event.getActiveChar().getActingPlayer();
		if (player != null)
		{
			final QuestState qs = getQuestState(player, false);
			if ((qs != null) && (qs.isCond(1)) && (getQuestItemsCount(qs.getPlayer(), SUPERIOR_WINDY_HEALING_POTION) >= 1000) && (getQuestItemsCount(qs.getPlayer(), SUPERIOR_WINDY_QUIK_HEALING_POTION) >= 1000) && (getQuestItemsCount(qs.getPlayer(), HIGH_GRADE_LOVE_POTION) >= 1000))
			{
				qs.setCond(2, true);
			}
		}
	}
}