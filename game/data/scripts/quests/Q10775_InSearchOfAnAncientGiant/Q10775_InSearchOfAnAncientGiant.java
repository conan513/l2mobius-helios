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
package quests.Q10775_InSearchOfAnAncientGiant;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * In Search of an Ancient Giant (10775)
 * @author malyelfik
 */
public final class Q10775_InSearchOfAnAncientGiant extends Quest
{
	// NPCs
	private static final int BELKADHI = 30485;
	private static final int ROMBEL = 30487;
	// Monsters
	private static final int[] MONSTERS =
	{
		20221, // Perum
		20753, // Dark Lord
		20754, // Dark Knight
		21040, // Soldier of Darkness
		21037, // Ossiud
		21038, // Liangma
		23153, // Achelando
		23154, // Styrindo
		23155, // Ashende
	};
	// Items
	private static final int ENERGY_OF_REGENERATION = 39715;
	private static final int ENCHANT_ARMOR_C = 952;
	// Misc
	private static final int MIN_LEVEL = 46;
	
	public Q10775_InSearchOfAnAncientGiant()
	{
		super(10775);
		addStartNpc(ROMBEL);
		addTalkId(ROMBEL, BELKADHI);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "30487-00.htm");
		addCondMinLevel(MIN_LEVEL, "30487-00.htm");
		registerQuestItems(ENERGY_OF_REGENERATION);
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
			case "30487-02.htm":
			case "30487-03.htm":
			case "30487-04.htm":
			case "30485-02.html":
			case "30485-03.html":
			{
				break;
			}
			case "30487-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "30485-04.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_C, 9);
					giveStoryQuestReward(player, 46);
					addExpAndSp(player, 4443600, 1066);
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
		
		if (npc.getId() == ROMBEL)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30487-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "30487-06.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if (qs.isStarted() && qs.isCond(2))
		{
			htmltext = "30485-01.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			giveItems(killer, ENERGY_OF_REGENERATION, 1);
			if (getQuestItemsCount(killer, ENERGY_OF_REGENERATION) >= 20)
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}