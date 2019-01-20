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
package quests.Q10801_TheDimensionalWarpPart1;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * The Dimensional Warp, Part 1 (10801)
 * @URL https://l2wiki.com/The_Dimensional_Warp,_Part_1
 * @author Gigi
 * @date 2018-09-01 - [12:20:59]
 */
public class Q10801_TheDimensionalWarpPart1 extends Quest
{
	// NPC
	private static final int RESED = 33974;
	// Monsters
	private static final int DIMENSIONAL_BUGBEAR = 23465;
	// other
	private static final int MIN_LEVEL = 99;
	private static final int DIMENSIONAL_BRACELET_STAGE_1 = 39747;
	private static final int WARP_CRYSTAL = 39597;
	
	public Q10801_TheDimensionalWarpPart1()
	{
		super(10801);
		addStartNpc(RESED);
		addTalkId(RESED);
		addKillId(DIMENSIONAL_BUGBEAR);
		addCondMinLevel(MIN_LEVEL, "33974-00.htm");
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
			case "33974-02.htm":
			case "33974-03.htm":
			{
				htmltext = event;
				break;
			}
			case "33974-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "33974-07.html":
			{
				if (qs.isCond(2))
				{
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 44442855977L, 0);
						giveItems(player, DIMENSIONAL_BRACELET_STAGE_1, 1);
						giveItems(player, WARP_CRYSTAL, 300);
						qs.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "33974-01.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33974-05.html" : "33974-06.html";
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
		L2Party party = killer.getParty();
		if (party != null)
		{
			party.getMembers().forEach(p -> onKill(npc, p));
		}
		else
		{
			onKill(npc, killer);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void onKill(L2Npc npc, L2PcInstance killer)
	{
		final QuestState qs = getQuestState(killer, false);
		
		if ((qs != null) && qs.isCond(1) && (npc.calculateDistance3D(killer) <= Config.ALT_PARTY_RANGE))
		{
			int killedBugbear = qs.getInt("killed_" + DIMENSIONAL_BUGBEAR);
			if (killedBugbear < 100)
			{
				qs.set("killed_" + DIMENSIONAL_BUGBEAR, ++killedBugbear);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
			if (killedBugbear >= 100)
			{
				qs.setCond(2, true);
			}
			sendNpcLogList(killer);
		}
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(DIMENSIONAL_BUGBEAR, false, qs.getInt("killed_" + DIMENSIONAL_BUGBEAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
