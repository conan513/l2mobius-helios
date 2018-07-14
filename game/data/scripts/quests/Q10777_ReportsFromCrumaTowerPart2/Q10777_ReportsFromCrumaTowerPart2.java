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
package quests.Q10777_ReportsFromCrumaTowerPart2;

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

import quests.Q10776_TheWrathOfTheGiants.Q10776_TheWrathOfTheGiants;

/**
 * Reports from Cruma Tower, Part 2 (10777)
 * @author malyelfik
 */
public final class Q10777_ReportsFromCrumaTowerPart2 extends Quest
{
	// NPCs
	private static final int BELKADHI = 30485;
	private static final int MAGIC_OWL = 33991;
	// Items
	private static final int ENCHANT_ARMOR_C = 952;
	// Location
	private static final Location OWL_LOC = new Location(17666, 108589, -9072);
	// Skill
	private static final SkillHolder TELEPORT = new SkillHolder(2588, 1);
	// Misc
	private static final int MIN_LEVEL = 49;
	
	public Q10777_ReportsFromCrumaTowerPart2()
	{
		super(10777);
		addStartNpc(BELKADHI);
		addTalkId(BELKADHI, MAGIC_OWL);
		
		addCondRace(Race.ERTHEIA, "30485-00.htm");
		addCondMinLevel(MIN_LEVEL, "30485-00.htm");
		addCondCompletedQuest(Q10776_TheWrathOfTheGiants.class.getSimpleName(), "30485-00.htm");
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
			case "30485-02.htm":
			case "30485-03.htm":
			case "30485-04.htm":
			case "30485-05.htm":
			case "33991-02.html":
			{
				break;
			}
			case "30485-06.htm":
			{
				qs.startQuest();
				break;
			}
			case "summon":
			{
				if (qs.isCond(1) && !L2World.getInstance().getVisibleObjects(player, L2Npc.class, 700).stream().anyMatch(n -> n.getId() == MAGIC_OWL))
				{
					final L2Npc owl = addSpawn(MAGIC_OWL, OWL_LOC);
					getTimers().addTimer("DESPAWN_OWL", 20000, owl, null);
				}
				htmltext = null;
				break;
			}
			case "despawn":
			{
				if (qs.isCond(1))
				{
					getTimers().cancelTimer("DESPAWN_OWL", npc, null);
					qs.setCond(2, true);
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.TO_QUEEN_NAVARI_OF_FAERON);
					npc.doCast(TELEPORT.getSkill());
					getTimers().addTimer("DESPAWN_OWL", 4000, npc, null);
				}
				htmltext = null;
				break;
			}
			case "30485-09.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_C, 2);
					giveStoryQuestReward(player, 4);
					addExpAndSp(player, 151263, 36);
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
		
		if (npc.getId() == BELKADHI)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "30485-01.htm";
					break;
				}
				case State.STARTED:
				{
					htmltext = qs.isCond(1) ? "30485-07.html" : "30485-08.html";
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
		if ((npc != null) && (npc.getId() == MAGIC_OWL) && event.equals("DESPAWN_OWL"))
		{
			npc.deleteMe();
		}
		else
		{
			super.onTimerEvent(event, params, npc, player);
		}
	}
}
