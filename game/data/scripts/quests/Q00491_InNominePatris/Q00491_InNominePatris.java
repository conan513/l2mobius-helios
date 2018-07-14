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
package quests.Q00491_InNominePatris;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassLevel;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.NpcSay;

/**
 * In Nomine Patris (491)
 * @URL https://l2wiki.com/In_Nomine_Patris
 * @author Gigi
 */
public class Q00491_InNominePatris extends Quest
{
	// NPCs
	private static final int SIRIK = 33649;
	// Monster's
	private static final int[] MONSTERS =
	{
		23181, // Succubus Soldier
		23182, // Succubus Warrior
		23183, // Succubus Archer
		23184 // Succubus Shaman
	};
	// Items
	private static final int DIMENSIONAL_FRAGMENT = 34768;
	// Others
	private static final int MIN_LEVEL = 76;
	private static final int MAX_LEVEL = 81;
	// Reward
	private static final int EXP_REWARD = 184210;
	private static final int SP_REWARD = 45;
	
	public Q00491_InNominePatris()
	{
		super(491);
		addStartNpc(SIRIK);
		addTalkId(SIRIK);
		addKillId(MONSTERS);
		registerQuestItems(DIMENSIONAL_FRAGMENT);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "no_level.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "33649-02.htm":
			case "33649-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33649-04.htm":
			{
				qs.startQuest();
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, SIRIK, NpcStringId.HURRY_AND_DEFEAT_THOSE_MONSTERS));
				htmltext = event;
				break;
			}
			case "33649-06.html":
			{
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, SIRIK, NpcStringId.HMM_THANK_YOU_SO_THEN_MAYBE_I_WON_T_HAVE_TO_GET_INVOLVED));
				addExpAndSp(player, (EXP_REWARD * player.getLevel()), (SP_REWARD * player.getLevel()));
				qs.exitQuest(QuestType.DAILY, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == SIRIK)
		{
			switch (qs.getState())
			{
				case State.COMPLETED:
				{
					if (!qs.isNowAvailable())
					{
						htmltext = "complete.htm";
						break;
					}
					qs.setState(State.CREATED);
				}
				case State.CREATED:
				{
					htmltext = ((player.getClassId().level() == ClassLevel.FOURTH.ordinal()) ? "33649-01.htm" : "33649-00.html");
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33649-07.html";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33649-05.html";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && (qs.isCond(1)))
		{
			if (giveItemRandomly(killer, npc, DIMENSIONAL_FRAGMENT, 1, 50, 0.5, true))
			{
				qs.setCond(2, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}