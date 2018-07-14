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
package quests.Q00823_DisappearedRaceNewFairy;

import com.l2jmobius.gameserver.enums.Faction;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Disappeared Race, New Fairy (00823)
 * @URL https://l2wiki.com/Disappeared_Race,_New_Fairy
 * @author Gigi / Stayway rework rewards
 */
public class Q00823_DisappearedRaceNewFairy extends Quest
{
	// NPCs
	private static final int MIMYU = 30747;
	// Monsters
	private static final int[] MONSTERS =
	{
		23566, // Nymph Rose
		23567, // Nymph Rose
		23568, // Nymph Lily
		23569, // Nymph Lily
		23570, // Nymph Tulip
		23571, // Nymph Tulip
		23572, // Nymph Cosmos
		23573, // Nymph Cosmos
		23578 // Nymph Guardian
	};
	// Item's
	private static final int NYMPH_STAMEN = 46258;
	private static final int BASIC_SUPPLY_BOX = 47178;
	private static final int INTERMEDIATE_SUPPLY_BOX = 47179;
	private static final int ADVANCED_SUPPLY_BOX = 47180;
	// Misc
	private static final int MIN_LEVEL = 100;
	
	public Q00823_DisappearedRaceNewFairy()
	{
		super(823);
		addStartNpc(MIMYU);
		addTalkId(MIMYU);
		addKillId(MONSTERS);
		registerQuestItems(NYMPH_STAMEN);
		addCondMinLevel(MIN_LEVEL, "30747-00.htm");
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
			case "30747-02.htm":
			case "30747-03.htm":
			case "30747-04.htm":
			case "30747-09.html":
			{
				htmltext = event;
				break;
			}
			case "30747-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30747-10.html":
			{
				if (qs.isCond(2))
				{
					final int factionLevel = player.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS);
					if (factionLevel == 0)
					{
						addFactionPoints(player, Faction.MOTHER_TREE_GUARDIANS, 100);
						giveItems(player, BASIC_SUPPLY_BOX, 1);
						addExpAndSp(player, 5536944000L, 13288590);
					}
					else if (factionLevel == 1)
					{
						addFactionPoints(player, Faction.MOTHER_TREE_GUARDIANS, 200);
						giveItems(player, INTERMEDIATE_SUPPLY_BOX, 1);
						addExpAndSp(player, 11073888000L, 26577180);
					}
					else if (factionLevel > 1)
					{
						addFactionPoints(player, Faction.MOTHER_TREE_GUARDIANS, 300);
						giveItems(player, ADVANCED_SUPPLY_BOX, 1);
						addExpAndSp(player, 16610832000L, 39865770);
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
		
		switch (qs.getState())
		{
			case State.COMPLETED:
			{
				if (!qs.isNowAvailable())
				{
					htmltext = "30747-11.html";
					break;
				}
				qs.setState(State.CREATED);
			}
			case State.CREATED:
			{
				htmltext = "30747-01.htm";
				break;
			}
			case State.STARTED:
			{
				if (qs.isCond(1))
				{
					htmltext = "30747-06.html";
				}
				else if (qs.isCond(2) && (getQuestItemsCount(player, NYMPH_STAMEN) < 1800))
				{
					htmltext = "30747-07.html";
				}
				else
				{
					htmltext = "30747-08.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && qs.isCond(1))
		{
			final int factionLevel = killer.getFactionLevel(Faction.MOTHER_TREE_GUARDIANS);
			if (factionLevel == 0)
			{
				giveItems(killer, NYMPH_STAMEN, 1, true);
				if (getQuestItemsCount(killer, NYMPH_STAMEN) >= 300)
				{
					qs.setCond(2, true);
				}
			}
			else if (factionLevel == 1)
			{
				giveItems(killer, NYMPH_STAMEN, 1, true);
				if (getQuestItemsCount(killer, NYMPH_STAMEN) >= 600)
				{
					qs.setCond(2, true);
				}
			}
			else if (factionLevel > 1)
			{
				giveItems(killer, NYMPH_STAMEN, 1, true);
				if (getQuestItemsCount(killer, NYMPH_STAMEN) >= 900)
				{
					qs.setCond(2, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}