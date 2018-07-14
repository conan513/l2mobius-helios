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
package quests.Q10786_ResidentProblemSolver;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

/**
 * Resident Problem Solver (10786)
 * @author malyelfik
 */
public final class Q10786_ResidentProblemSolver extends Quest
{
	// NPC
	private static final int SHUVANN = 33867;
	// Monsters
	private static final int[] MONSTERS =
	{
		21001, // Archer of Destruction
		21002, // Doom Scout
		21003, // Graveyard Lich
		21004, // Dismal Pole
		21005, // Graveyard Predator
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
	private static final int ENCHANT_ARMOR_A = 26351;
	// Misc
	private static final int MIN_LEVEL = 61;
	private static final int MAX_LEVEL = 65;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10786_ResidentProblemSolver()
	{
		super(10786);
		addStartNpc(SHUVANN);
		addTalkId(SHUVANN);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33867-00.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33867-01.htm");
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
			case "33867-03.htm":
			case "33867-04.htm":
			{
				break;
			}
			case "33867-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "33867-08.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_A, 5);
					giveStoryQuestReward(player, 57);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 6251174, 1500);
					}
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33867-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33867-06.html" : "33867-07.html";
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
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			int killCount = qs.getInt(KILL_COUNT_VAR);
			qs.set(KILL_COUNT_VAR, ++killCount);
			if (killCount >= 50)
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.KILL_MONSTERS_IN_THE_FIELDS_OF_MASSACRE, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}