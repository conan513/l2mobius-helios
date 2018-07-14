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
package quests.Q10393_KekropusLetterAClueCompleted;

import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import quests.LetterQuest;

/**
 * Kekropus' Letter: A Clue Completed (10393)
 * @author St3eT
 */
public final class Q10393_KekropusLetterAClueCompleted extends LetterQuest
{
	// NPCs
	private static final int FLUTER = 30677;
	private static final int KELIOS = 33862;
	private static final int INVISIBLE_NPC = 19543;
	// Items
	private static final int SOE_TOWN_OF_OREN = 37113; // Scroll of Escape: Town of Oren
	private static final int SOE_OUTLAW_FOREST = 37026; // Scroll of Escape: Outlaw Forest
	private static final int EAC = 952; // Scroll: Enchant Armor (C-grade)
	// Location
	private static final Location TELEPORT_LOC = new Location(83676, 55510, -1512);
	// Misc
	private static final int MIN_LEVEL = 46;
	private static final int MAX_LEVEL = 51;
	
	public Q10393_KekropusLetterAClueCompleted()
	{
		super(10393);
		addTalkId(FLUTER, KELIOS);
		addSeeCreatureId(INVISIBLE_NPC);
		
		setIsErtheiaQuest(false);
		setLevel(MIN_LEVEL, MAX_LEVEL);
		setStartQuestSound("Npcdialog1.kekrops_quest_2");
		setStartLocation(SOE_TOWN_OF_OREN, TELEPORT_LOC);
		registerQuestItems(SOE_TOWN_OF_OREN, SOE_OUTLAW_FOREST);
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
			case "30677-02.html":
			{
				htmltext = event;
				break;
			}
			case "30677-03.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2, true);
					giveItems(player, SOE_OUTLAW_FOREST, 1);
					htmltext = event;
				}
				break;
			}
			case "33862-02.html":
			{
				if (st.isCond(2))
				{
					st.exitQuest(false, true);
					giveItems(player, EAC, 4);
					giveStoryQuestReward(player, 15);
					addExpAndSp(player, 483840, 116);
					showOnScreenMsg(player, NpcStringId.GROW_STRONGER_HERE_UNTIL_YOU_RECEIVE_THE_NEXT_LETTER_FROM_KEKROPUS_AT_LV_52, ExShowScreenMessage.TOP_CENTER, 6000);
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
			if (st.isCond(1) && (npc.getId() == FLUTER))
			{
				htmltext = "30677-01.html";
			}
			else if (st.isCond(2))
			{
				htmltext = npc.getId() == FLUTER ? "30677-04.html" : "33862-01.html";
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
				showOnScreenMsg(player, NpcStringId.OUTLAW_FOREST_IS_A_GOOD_HUNTING_ZONE_FOR_LV_46_OR_ABOVE, ExShowScreenMessage.TOP_CENTER, 6000);
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
}