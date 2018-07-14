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
package quests.Q10405_KartiasSeed;

import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Kartia's Seed (10405)
 * @author St3eT
 */
public final class Q10405_KartiasSeed extends Quest
{
	// NPCs
	private static final int SHUVANN = 33867;
	private static final int[] MONSTERS =
	{
		21001, // Archer of Destruction
		21003, // Graveyard Lich
		21004, // Dismal Pole
		21005, // Graveyard Predator
		21002, // Doom Scout
		21006, // Doom Servant
		21007, // Doom Guard
		21008, // Doom Archer
		21009, // Doom Trooper
		21010, // Doom Warrior
		20674, // Doom Knight
		20974, // Spiteful Soul Leader
		20975, // Spiteful Soul Wizard
		20976, // Spiteful Soul Warrior
	};
	// Items
	private static final int KARTIA_SEED = 36714; // Kartia's Mutated Seed
	private static final int EAA = 730; // Scroll: Enchant Armor (A-grade)
	// Misc
	private static final int MIN_LEVEL = 61;
	private static final int MAX_LEVEL = 65;
	
	public Q10405_KartiasSeed()
	{
		super(10405);
		addStartNpc(SHUVANN);
		addTalkId(SHUVANN);
		addKillId(MONSTERS);
		registerQuestItems(KARTIA_SEED);
		addCondNotRace(Race.ERTHEIA, "33867-09.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33867-08.htm");
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
			case "33867-02.htm":
			case "33867-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33867-04.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33867-07.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAA, 5);
					giveStoryQuestReward(player, 57);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 6_251_174, 1_500);
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
				htmltext = "33867-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = st.isCond(1) ? "33867-05.html" : "33867-06.html";
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
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(1))
		{
			if (giveItemRandomly(killer, KARTIA_SEED, 1, 100, 1, true))
			{
				st.setCond(2);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}