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
package quests.Q10452_IsItEdible;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;

/**
 * Is it Edible? (10452)
 * @URL https://l2wiki.com/Is_it_Edible%3F
 * @author Gigi
 */
public final class Q10452_IsItEdible extends Quest
{
	// NPCs
	private static final int SALLY = 32743;
	// Monster's
	private static final int FANTASY_MUSHROM = 18864;
	private static final int STICKY_MUSHROMS = 18865;
	private static final int VITALIITY_PLANT = 18868;
	// items
	private static final int FANTASY_MUSHROMS_SPORE = 36688;
	private static final int STICKY_MUSHROMS_SPORE = 36689;
	private static final int VITALIITY_LEAF_POUCH = 36690;
	// Misc
	private static final int MIN_LEVEL = 81;
	
	public Q10452_IsItEdible()
	{
		super(10452);
		addStartNpc(SALLY);
		addTalkId(SALLY);
		addKillId(FANTASY_MUSHROM, STICKY_MUSHROMS, VITALIITY_PLANT);
		registerQuestItems(FANTASY_MUSHROMS_SPORE, STICKY_MUSHROMS_SPORE, VITALIITY_LEAF_POUCH);
		addCondMinLevel(MIN_LEVEL, "32743-08.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		switch (event)
		{
			case "32743-02.htm":
			case "32743-09.html":
			case "32743-10.html":
			case "32743-11.html":
			{
				htmltext = event;
				break;
			}
			case "32743-03.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32743-07.html":
			{
				giveAdena(player, 299940, true);
				addExpAndSp(player, 14120400, 3388);
				qs.exitQuest(false, true);
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
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCreated())
		{
			htmltext = "32743-01.htm";
		}
		else if (qs.isCond(1))
		{
			htmltext = "32743-04.html";
		}
		else if (qs.isCond(2))
		{
			takeItems(player, FANTASY_MUSHROMS_SPORE, -1);
			takeItems(player, STICKY_MUSHROMS_SPORE, -1);
			takeItems(player, VITALIITY_LEAF_POUCH, -1);
			htmltext = "32743-05.html";
			qs.setCond(3);
		}
		else if (qs.isCond(3))
		{
			htmltext = "32743-06.html";
		}
		else
		{
			htmltext = "Complete.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		switch (npc.getId())
		{
			case FANTASY_MUSHROM:
			{
				if (qs.isCond(1) && !hasQuestItems(killer, FANTASY_MUSHROMS_SPORE))
				{
					giveItems(killer, FANTASY_MUSHROMS_SPORE, 1);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
			}
			case STICKY_MUSHROMS:
			{
				if (qs.isCond(1) && !hasQuestItems(killer, STICKY_MUSHROMS_SPORE))
				{
					giveItems(killer, STICKY_MUSHROMS_SPORE, 1);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
			}
			case VITALIITY_PLANT:
			{
				if (qs.isCond(1) && !hasQuestItems(killer, VITALIITY_LEAF_POUCH))
				{
					giveItems(killer, VITALIITY_LEAF_POUCH, 1);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					break;
				}
			}
		}
		if ((getQuestItemsCount(killer, FANTASY_MUSHROMS_SPORE) >= 1) && (getQuestItemsCount(killer, STICKY_MUSHROMS_SPORE) >= 1) && (getQuestItemsCount(killer, VITALIITY_LEAF_POUCH) >= 1))
		{
			qs.setCond(2, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}