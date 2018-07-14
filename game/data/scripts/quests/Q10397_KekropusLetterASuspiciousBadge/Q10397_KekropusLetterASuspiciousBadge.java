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
package quests.Q10397_KekropusLetterASuspiciousBadge;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Kekropus' Letter: A Suspicious Badge (10397)
 * @author St3eT
 */
public final class Q10397_KekropusLetterASuspiciousBadge extends LetterQuest
{
	// NPCs
	private static final int MOUEN = 30196;
	private static final int ANDY = 33845;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_TOWN_OF_OREN = 37114; // Scroll of Escape: Town of Oren
	private static final int SOE_SEA_OF_SPORES = 37027; // Scroll of Escape: Sea of Spores
	private static final int EWB = 947; // Scroll: Enchant Weapon (B-grade)
	// Location
	private static final Location TELEPORT_LOC = new Location(81013, 56413, -1552);
	// Misc
	private static final int MIN_LEVEL = 52;
	private static final int MAX_LEVEL = 57;
	
	public Q10397_KekropusLetterASuspiciousBadge()
	{
		super(10397);
		addTalkId(MOUEN, ANDY);
		addSeeCreatureId(INVISIBLE_NPC);
		
		setIsErtheiaQuest(false);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartQuestSound("Npcdialog1.kekrops_quest_3");
		setStartLocation(SOE_TOWN_OF_OREN, TELEPORT_LOC);
		registerQuestItems(SOE_TOWN_OF_OREN, SOE_SEA_OF_SPORES);
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
			case "30196-02.html":
			{
				htmltext = event;
				break;
			}
			case "30196-03.html":
			{
				if (st.isCond(1))
				{
					giveItems(player, SOE_SEA_OF_SPORES, 1);
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33845-02.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EWB, 2);
					giveStoryQuestReward(player, 20);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 635_250, 152);
					}
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_58, ExShowScreenMessage.TOP_CENTER, 6000);
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
			if (st.isCond(1) && (npc.getId() == MOUEN))
			{
				htmltext = "30196-01.html";
			}
			else if (st.isCond(2))
			{
				htmltext = npc.getId() == MOUEN ? "30196-04.html" : "33845-01.html";
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
				showOnScreenMsg(player, NpcStringId.SEA_OF_SPORES_IS_A_GOOD_HUNTING_ZONE_FOR_LV_52_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}