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
package quests.Q10450_ADarkAmbition;

import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * A Dark Ambition (10450)
 * @author St3eT
 */
public final class Q10450_ADarkAmbition extends Quest
{
	// NPCs
	private static final int MATHIAS = 31340;
	private static final int TUSKA = 33839;
	// Items
	private static final int ELIXIR_LIFE = 30357; // Elixir of Life (R-grade)
	private static final int ELIXIR_MANA = 30358; // Elixir of Mind (R-grade)
	private static final int SSR = 34609; // Mysterious Soulshot (R-grade) - Event
	private static final int BSSR = 34616; // Mysterious Blessed Spiritshot (R-grade) - Event
	private static final int SOE = 37019; // Scroll of Escape: Gainak
	// Misc
	private static final int MIN_LEVEL = 99;
	
	public Q10450_ADarkAmbition()
	{
		super(10450);
		addStartNpc(MATHIAS);
		addTalkId(MATHIAS, TUSKA);
		addCondMinLevel(MIN_LEVEL, "31340-07.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "31340-02.htm":
			case "31340-03.htm":
			case "33839-02.html":
			{
				htmltext = event;
				break;
			}
			case "31340-05.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33839-03.html":
			{
				if (st.isCond(2))
				{
					giveItems(player, ELIXIR_LIFE, 50);
					giveItems(player, ELIXIR_MANA, 50);
					giveItems(player, SSR, 10000);
					giveItems(player, BSSR, 10000);
					giveItems(player, SOE, 1);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 15_436_575, 3_704);
					}
					st.exitQuest(false, true);
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
				if (npc.getId() == MATHIAS)
				{
					htmltext = "31340-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				if (st.isCond(1))
				{
					htmltext = npc.getId() == MATHIAS ? "31340-06.html" : "33839-01.html";
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
}