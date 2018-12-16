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
package quests.Q10354_ResurrectedOwnerOfHall;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10351_OwnerOfHall.Q10351_OwnerOfHall;

/**
 * Resurrected Owner of Hall (10354)
 * @URL https://l2wiki.com/index.php?title=Resurrected_Owner_of_Hall&mobileaction=toggle_view_desktop
 * @author Gigi
 */
public final class Q10354_ResurrectedOwnerOfHall extends Quest
{
	// NPCs
	private static final int LYDIA = 32892;
	private static final int OCTAVIS = 29212; // Octavis extreme mode
	// Item
	private static final int OCTAVIS_SOUL_BOTTLE = 34884;
	// Misc
	private static final int MIN_LEVEL = 95;
	
	public Q10354_ResurrectedOwnerOfHall()
	{
		super(10354);
		addStartNpc(LYDIA);
		addTalkId(LYDIA);
		addKillId(OCTAVIS);
		addCondMinLevel(MIN_LEVEL, "32892-00.htm");
		addCondCompletedQuest(Q10351_OwnerOfHall.class.getSimpleName(), "32892-00a.htm");
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
			case "32892-02.htm":
			case "32892-03.htm":
			{
				htmltext = event;
				break;
			}
			case "32892-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32892-07.html":
			{
				if (qs.isCond(2))
				{
					giveAdena(player, 23655000, false);
					addExpAndSp(player, 897850000, 215484);
					giveItems(player, OCTAVIS_SOUL_BOTTLE, 1);
					qs.exitQuest(false, true);
					htmltext = getHtm(player, "32892-07.html").replace("%name%", player.getName());
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
				htmltext = "32892-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32892-05.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "32892-06.html";
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = "Complete.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
		{
			qs.setCond(2, true);
		}
	}
}