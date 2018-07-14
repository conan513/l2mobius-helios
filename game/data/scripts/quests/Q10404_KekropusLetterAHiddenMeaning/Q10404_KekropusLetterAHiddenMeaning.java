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
package quests.Q10404_KekropusLetterAHiddenMeaning;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Kekropus' Letter: A Hidden Meaning (10404)
 * @author St3eT
 */
public final class Q10404_KekropusLetterAHiddenMeaning extends LetterQuest
{
	// NPCs
	private static final int PATERSON = 33864;
	private static final int SHUVANN = 33867;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_TOWN_OF_ADEN = 37116; // Scroll of Escape: Town of Aden
	private static final int SOE_FIELDS_OF_MASSACRE = 37029; // Scroll of Escape: Fields of Massacre
	private static final int EWA = 729; // Scroll: Enchant Weapon (A-grade)
	// Location
	private static final Location TELEPORT_LOC = new Location(147619, 24681, -1984);
	// Misc
	private static final int MIN_LEVEL = 61;
	private static final int MAX_LEVEL = 64;
	
	public Q10404_KekropusLetterAHiddenMeaning()
	{
		super(10404);
		addTalkId(PATERSON, SHUVANN);
		addSeeCreatureId(INVISIBLE_NPC);
		
		setIsErtheiaQuest(false);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartLocation(SOE_TOWN_OF_ADEN, TELEPORT_LOC);
		setStartQuestSound("Npcdialog1.kekrops_quest_5");
		registerQuestItems(SOE_TOWN_OF_ADEN, SOE_FIELDS_OF_MASSACRE);
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
			case "33864-02.html":
			{
				htmltext = event;
				break;
			}
			case "33864-03.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					giveItems(player, SOE_FIELDS_OF_MASSACRE, 1);
					htmltext = event;
				}
				break;
			}
			case "33867-02.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EWA, 1);
					giveStoryQuestReward(player, 71);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 807_240, 193);
					}
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_65, ExShowScreenMessage.TOP_CENTER, 6000);
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
		final QuestState st = getQuestState(player, false);
		
		if (st == null)
		{
			return htmltext;
		}
		
		if (st.isStarted())
		{
			if ((npc.getId() == PATERSON) && st.isCond(1))
			{
				htmltext = "33864-01.html";
			}
			else if (st.isCond(2))
			{
				htmltext = npc.getId() == PATERSON ? "33864-04.html" : "33867-01.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			final L2PcInstance player = creature.getActingPlayer();
			final QuestState st = getQuestState(player, false);
			
			if ((st != null) && st.isCond(2))
			{
				showOnScreenMsg(player, NpcStringId.FIELDS_OF_MASSACRE_IS_A_GOOD_HUNTING_ZONE_FOR_LV_61_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}