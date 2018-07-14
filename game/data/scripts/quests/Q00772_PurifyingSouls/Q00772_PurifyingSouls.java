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
package quests.Q00772_PurifyingSouls;

import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Purifying Souls (772)
 * @URL https://l2wiki.com/Purifying_Souls
 * @author Gigi
 */
public class Q00772_PurifyingSouls extends Quest
{
	// NPC
	private static final int QUINCY = 33838;
	// Monsters
	private static final int[] MONSTERS =
	{
		23330, // Rubble
		23337, // Large Rubble
		23332, // One-armed Zombie
		23333, // Putrefied Zombie
		23334, // Shelop
		25927, // Krogel
		23335, // Poras
		23336 // Death Worm
	};
	// Items
	private static final int SOUL_OF_DARKNESS = 36680;
	private static final int FAINT_SOUL_OF_LIGHT = 36696;
	// Reward
	private static final int ELEXIR_OF_LIFE = 30357;
	private static final int ELEXIR_OF_MIND = 30358;
	private static final int ELEXIR_OF_BLESSING = 32316;
	private static final int SOE_LEND_OF_CHAOS = 37018;
	private static final int ELMORES_MYSTERIUS_BOX = 37021;
	private static final int ELMORES_NOBLE_BOX = 37022;
	private static final int ENERGY_OF_DESTRUCTION = 35562;
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q00772_PurifyingSouls()
	{
		super(772);
		addStartNpc(QUINCY);
		addTalkId(QUINCY);
		addKillId(MONSTERS);
		registerQuestItems(SOUL_OF_DARKNESS, FAINT_SOUL_OF_LIGHT);
		addCondMinLevel(MIN_LEVEL, "33838-0.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		
		switch (event)
		{
			case "33838-2.htm":
			case "33838-3.htm":
			case "33838-7.html":
			case "33838-8.html":
			{
				htmltext = event;
				break;
			}
			case "33838-4.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33838-9.html":
			{
				if (qs.isCond(2) && (getQuestItemsCount(player, SOUL_OF_DARKNESS) >= 50))
				{
					if ((getQuestItemsCount(player, FAINT_SOUL_OF_LIGHT) >= 1000) && (getQuestItemsCount(player, FAINT_SOUL_OF_LIGHT) <= 1999))
					{
						giveItems(player, ELMORES_MYSTERIUS_BOX, 1);
					}
					else if (getQuestItemsCount(player, FAINT_SOUL_OF_LIGHT) >= 2000)
					{
						giveItems(player, ELMORES_NOBLE_BOX, 1);
						giveItems(player, ENERGY_OF_DESTRUCTION, 1);
					}
					giveItems(player, ELEXIR_OF_LIFE, 5);
					giveItems(player, ELEXIR_OF_MIND, 5);
					giveItems(player, ELEXIR_OF_BLESSING, 5);
					giveItems(player, SOE_LEND_OF_CHAOS, 1);
					qs.exitQuest(QuestType.DAILY, true);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (npc.getId() == QUINCY)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "33838-10.htm";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "33838-1.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33838-5.html";
					}
					else if (qs.isStarted() && qs.isCond(2))
					{
						htmltext = "33838-6.html";
					}
					break;
				}
			}
		}
		else if (qs.isCompleted() && !qs.isNowAvailable())
		{
			htmltext = "33838-10.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		
		if ((qs != null) && qs.isCond(1) && (giveItemRandomly(killer, npc, SOUL_OF_DARKNESS, 1, 50, 0.1, true)))
		{
			qs.setCond(2, true);
		}
		if ((qs != null) && (qs.getCond() > 0) && (getRandom(100) < 40))
		{
			giveItems(killer, FAINT_SOUL_OF_LIGHT, 1);
		}
		return super.onKill(npc, killer, isSummon);
	}
}