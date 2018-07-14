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
package quests.Q10414_KekropusLetterWithCourage;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Kekropus' Letter: With Courage (10414)
 * @author St3eT
 */
public final class Q10414_KekropusLetterWithCourage extends LetterQuest
{
	// NPCs
	private static final int ANDREI = 31292;
	private static final int JANITT = 33851;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_WALL_OF_ARGOS = 37032; // Scroll of Escape: Wall of Argos
	private static final int SOE_TOWN_OF_GODDARD = 37119; // Scroll of Escape: Town of Goddard
	private static final int EAA = 730; // Scroll: Enchant Armor (A-grade)
	// Location
	private static final Location TELEPORT_LOC = new Location(147491, -56633, -2776);
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	
	public Q10414_KekropusLetterWithCourage()
	{
		super(10414);
		addTalkId(ANDREI, JANITT);
		addSeeCreatureId(INVISIBLE_NPC);
		
		setIsErtheiaQuest(false);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartQuestSound("Npcdialog1.kekrops_quest_8");
		setStartLocation(SOE_TOWN_OF_GODDARD, TELEPORT_LOC);
		registerQuestItems(SOE_TOWN_OF_GODDARD, SOE_WALL_OF_ARGOS);
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
			case "31292-02.html":
			{
				htmltext = event;
				break;
			}
			case "31292-03.html":
			{
				if (st.isCond(1))
				{
					takeItems(player, SOE_TOWN_OF_GODDARD, -1);
					giveItems(player, SOE_WALL_OF_ARGOS, 1);
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "33851-02.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAA, 5);
					giveStoryQuestReward(player, 123);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 1_088_640, 261);
					}
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_76, ExShowScreenMessage.TOP_CENTER, 6000);
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
			if ((npc.getId() == ANDREI) && st.isCond(1))
			{
				htmltext = "31292-01.html";
			}
			else if (st.isCond(2))
			{
				htmltext = npc.getId() == ANDREI ? "31292-04.html" : "33851-01.html";
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
				showOnScreenMsg(player, NpcStringId.WALL_OF_ARGOS_IS_A_GOOD_HUNTING_ZONE_FOR_LV_70_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	@Override
	public boolean canShowTutorialMark(L2PcInstance player)
	{
		return !player.isMageClass();
	}
}