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
package quests.Q10772_ReportsFromCrumaTowerPart1;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

import quests.Q10771_VolatilePower.Q10771_VolatilePower;

/**
 * Reports from Cruma Tower, Part 1 (10772)
 * @author malyelfik
 */
public final class Q10772_ReportsFromCrumaTowerPart1 extends Quest
{
	// NPCs
	private static final int JANSSEN = 30484;
	private static final int MAGIC_OWL = 33991;
	// Items
	private static final int ENCHANT_ARMOR_C = 952;
	// Location
	private static final Location OWL_LOC = new Location(17698, 115064, -11736);
	// Skill
	private static final SkillHolder OWL_TELEPORT = new SkillHolder(2588, 1);
	// Misc
	private static final int MIN_LEVEL = 45;
	
	public Q10772_ReportsFromCrumaTowerPart1()
	{
		super(10772);
		addStartNpc(JANSSEN);
		addTalkId(JANSSEN, MAGIC_OWL);
		
		addCondRace(Race.ERTHEIA, "30484-00.htm");
		addCondMinLevel(MIN_LEVEL, "30484-00.htm");
		addCondCompletedQuest(Q10771_VolatilePower.class.getSimpleName(), "30484-00.htm");
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
			case "30484-02.htm":
			case "30484-03.htm":
			case "30484-04.htm":
			case "30484-05.htm":
			case "33991-02.html":
			{
				break;
			}
			case "30484-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "spawn_owl":
			{
				if (qs.isCond(1) && !L2World.getInstance().getVisibleObjects(player, L2Npc.class, 700).stream().anyMatch(n -> n.getId() == MAGIC_OWL))
				{
					addSpawn(MAGIC_OWL, OWL_LOC, true, 20000);
				}
				htmltext = null;
				break;
			}
			case "despawn_owl":
			{
				if (qs.isCond(1) && (npc != null))
				{
					getTimers().addTimer("DESPAWN_OWL", 4000, npc, null);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TO_QUEEN_NAVARI_OF_FAERON);
					npc.doCast(OWL_TELEPORT.getSkill());
					qs.setCond(2, true);
				}
				htmltext = null;
				break;
			}
			case "30484-09.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_C, 2);
					giveStoryQuestReward(player, 4);
					addExpAndSp(player, 127575, 30);
					qs.exitQuest(false, true);
				}
				break;
			}
			default:
			{
				htmltext = null;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == JANSSEN)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30484-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = (qs.isCond(1)) ? "30484-07.html" : "30484-08.html";
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		else if (qs.isStarted() && qs.isCond(1))
		{
			htmltext = "33991-01.html";
		}
		return htmltext;
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("DESPAWN_OWL") && (npc != null) && (npc.getId() == MAGIC_OWL))
		{
			npc.deleteMe();
		}
		else
		{
			super.onTimerEvent(event, params, npc, player);
		}
	}
}
