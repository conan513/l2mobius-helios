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
package quests.Q00775_RetrievingTheChaosFragment;

import com.l2jmobius.gameserver.enums.Faction;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

import quests.Q10455_ElikiasLetter.Q10455_ElikiasLetter;

/**
 * Retrieving the Fragment of Chaos (775)
 * @URL https://l2wiki.com/Retrieving_the_Fragment_of_Chaos
 * @author Gigi
 */
public class Q00775_RetrievingTheChaosFragment extends Quest
{
	// NPCs
	private static final int LEONA_BLACKBIRD = 31595;
	// Monsters
	private static final int[] MONSTERS =
	{
		23388, // Kandiloth
		23387, // Kanzaroth
		23385, // Lunatikan
		23384, // Smaug
		23386, // Jabberwok
		23395, // Garion
		23397, // Desert Wendigo
		23399, // Bend Beetle
		23398, // Koraza
		23395, // Garion
		23396, // Garion Neti
		23357, // Disorder Warrior
		23356, // Klien Soldier
		23361, // Mutated Fly
		23358, // Blow Archer
		23355, // Armor Beast
		23360, // Bizuard
		23354, // Dacey Hannibal
		23357, // Disorder Warrior
		23363, // Amos Officer
		23364, // Amos Master
		23362, // Amos Soldier
		23365, // Ailith Hunter
	};
	// Misc
	private static final int MIN_LEVEL = 99;
	// Item
	private static final int CHAOS_FRAGMENT = 37766;
	private static final int BASIC_SUPPLY_BOX = 47172;
	private static final int INTERMEDIATE_SUPPLY_BOX = 47173;
	private static final int ADVANCED_SUPPLY_BOX = 47174;
	
	public Q00775_RetrievingTheChaosFragment()
	{
		super(775);
		addStartNpc(LEONA_BLACKBIRD);
		addTalkId(LEONA_BLACKBIRD);
		addKillId(MONSTERS);
		registerQuestItems(CHAOS_FRAGMENT);
		addCondMinLevel(MIN_LEVEL, "31595-00.htm");
		addCondCompletedQuest(Q10455_ElikiasLetter.class.getSimpleName(), "31595-00.htm");
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
			case "31595-05.html":
			{
				htmltext = event;
				break;
			}
			case "31595-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "31595-03.html":
			{
				if (qs.isCond(2))
				{
					final int factionLevel = player.getFactionLevel(Faction.BLACKBIRD_CLAN);
					if (factionLevel == 0)
					{
						addFactionPoints(player, Faction.BLACKBIRD_CLAN, 100);
						giveItems(player, BASIC_SUPPLY_BOX, 1);
						addExpAndSp(player, 4522369500L, 10853640);
					}
					else if (factionLevel == 1)
					{
						addFactionPoints(player, Faction.BLACKBIRD_CLAN, 200);
						giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
						addExpAndSp(player, 9044739000L, 21707280);
					}
					else if (factionLevel > 1)
					{
						addFactionPoints(player, Faction.BLACKBIRD_CLAN, 300);
						giveItems(player, ADVANCED_SUPPLY_BOX, 1);
						addExpAndSp(player, 13567108500L, 32560920);
					}
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
		
		if (npc.getId() == LEONA_BLACKBIRD)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "31595-08.html";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = "31595-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "31595-07.html";
					}
					else if (qs.isCond(2))
					{
						if (getQuestItemsCount(player, CHAOS_FRAGMENT) < 200)
						{
							htmltext = "31595-02.html";
						}
						else
						{
							htmltext = "31595-09.html";
						}
					}
					break;
				}
			}
		}
		else if (qs.isCompleted() && !qs.isNowAvailable())
		{
			htmltext = "31595-08.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && qs.isCond(1))
		{
			final int factionLevel = killer.getFactionLevel(Faction.BLACKBIRD_CLAN);
			if (factionLevel == 0)
			{
				giveItems(killer, CHAOS_FRAGMENT, 1, true);
				if (getQuestItemsCount(killer, CHAOS_FRAGMENT) >= 300)
				{
					qs.setCond(2, true);
				}
			}
			else if (factionLevel == 1)
			{
				giveItems(killer, CHAOS_FRAGMENT, 1, true);
				if (getQuestItemsCount(killer, CHAOS_FRAGMENT) >= 600)
				{
					qs.setCond(2, true);
				}
			}
			else if (factionLevel > 1)
			{
				giveItems(killer, CHAOS_FRAGMENT, 1, true);
				if (getQuestItemsCount(killer, CHAOS_FRAGMENT) >= 900)
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}