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
package quests.Q10316_UndecayingMemoryOfThePast;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10315_ToThePrisonOfDarkness.Q10315_ToThePrisonOfDarkness;

/**
 * Undecaying Memory of the Past (10316)
 * @URL https://l2wiki.com/Undecaying_Memory_of_the_Past
 * @author Gigi
 */
public final class Q10316_UndecayingMemoryOfThePast extends Quest
{
	// NPCs
	private static final int OPERA = 32946;
	private static final int SPEZION = 25779;
	// Misc
	private static final int MIN_LEVEL = 90;
	// Item's
	private static final int EAR = 17527;
	private static final int CORRODED_GIANTS_WARSMITH_HOLDER = 19305;
	private static final int CORRODED_GIANTS_REORINS_MOLD = 19306;
	private static final int CORRODED_GIANTS_ARCSMITH_ANVIL = 19307;
	private static final int CORRODED_GIANTS_WARSMITH_MOLD = 19308;
	private static final int HARDENER_POUCHES = 34861;
	
	public Q10316_UndecayingMemoryOfThePast()
	{
		super(10316);
		addStartNpc(OPERA);
		addTalkId(OPERA);
		addKillId(SPEZION);
		addCondMinLevel(MIN_LEVEL, "32946-00.htm");
		addCondCompletedQuest(Q10315_ToThePrisonOfDarkness.class.getSimpleName(), "32946-00a.html");
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
			case "32946-02.htm":
			case "32946-03.htm":
			case "32946-04.htm":
			case "32946-08.html":
			{
				htmltext = event;
				break;
			}
			case "32946-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "material":
			{
				giveItems(player, CORRODED_GIANTS_WARSMITH_HOLDER, 1);
				giveItems(player, CORRODED_GIANTS_REORINS_MOLD, 1);
				giveItems(player, CORRODED_GIANTS_ARCSMITH_ANVIL, 1);
				giveItems(player, CORRODED_GIANTS_WARSMITH_MOLD, 1);
				addExpAndSp(player, 54093924, 12982);
				qs.exitQuest(false, true);
				htmltext = "32946-09.html";
				break;
			}
			case "enchant":
			{
				giveItems(player, EAR, 2);
				addExpAndSp(player, 54093924, 12982);
				qs.exitQuest(false, true);
				htmltext = "32946-09.html";
				break;
			}
			case "pouch":
			{
				giveItems(player, HARDENER_POUCHES, 2);
				addExpAndSp(player, 54093924, 12982);
				qs.exitQuest(false, true);
				htmltext = "32946-09.html";
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
				htmltext = "32946-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "32946-06.html";
				}
				else if (qs.isCond(2))
				{
					htmltext = "32946-07.html";
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