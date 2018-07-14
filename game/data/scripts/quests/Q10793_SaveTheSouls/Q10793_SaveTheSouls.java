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
package quests.Q10793_SaveTheSouls;

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
 * Save the Souls (10793)
 * @author malyelfik
 */
public final class Q10793_SaveTheSouls extends Quest
{
	// NPC
	private static final int HATUBA = 33849;
	// Monsters
	private static final int[] MONSTERS =
	{
		18119, // Corpse of Deadman
		21547, // Corrupted Knight
		21548, // Resurrected Knight
		21549, // Corrupted Royal Guard
		21551, // Resurrected Royal Guard
		21553, // Trampled Man
		21555, // Slaughter Executioner
		21557, // Bone Snatcher
		21559, // Bone Maker
		21560, // Bone Shaper
		21561, // Sacrificed Man
	};
	// Items
	private static final int ENCHANT_ARMOR_A = 26351;
	// Misc
	private static final int MIN_LEVEL = 65;
	private static final int MAX_LEVEL = 70;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10793_SaveTheSouls()
	{
		super(10793);
		addStartNpc(HATUBA);
		addTalkId(HATUBA);
		addKillId(MONSTERS);
		
		addCondRace(Race.ERTHEIA, "33849-00.htm");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33849-01.htm");
		addCondStart(L2PcInstance::isMageClass, "33849-01.htm");
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
			case "33849-03.htm":
			case "33849-04.htm":
			{
				break;
			}
			case "33849-05.htm":
			{
				qs.startQuest();
				break;
			}
			case "33849-08.html":
			{
				if (qs.isCond(2))
				{
					giveItems(player, ENCHANT_ARMOR_A, 3);
					giveStoryQuestReward(player, 3);
					if (player.getLevel() >= MIN_LEVEL)
					{
						addExpAndSp(player, 942690, 226);
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
				htmltext = "33849-02.htm";
				break;
			}
			case State.STARTED:
			{
				htmltext = (qs.isCond(1)) ? "33849-06.html" : "33849-07.html";
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
			int count = qs.getInt(KILL_COUNT_VAR);
			qs.set(KILL_COUNT_VAR, ++count);
			if (count >= 50)
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
				holder.add(new NpcLogListHolder(NpcStringId.KILL_THE_UNDEAD, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
}
