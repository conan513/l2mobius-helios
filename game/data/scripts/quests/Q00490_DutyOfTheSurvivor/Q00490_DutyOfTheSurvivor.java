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
package quests.Q00490_DutyOfTheSurvivor;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;

/**
 * Duty of the Survivor (400)
 * @author St3eT
 */
public final class Q00490_DutyOfTheSurvivor extends Quest
{
	// NPCs
	private static final int VOLLODOS = 30137;
	private static final int[] EXTRACT_MONSTERS =
	{
		23162, // Corpse Devourer
		23163, // Corpse Absorber
		23164, // Corpse Shredder
		23165, // Plagueworm
		23166, // Contaminated Rotten Root
		23167, // Decayed Spore
	
	};
	private static final int[] BLOOD_MONSTERS =
	{
		23168, // Swamp Tracker
		23169, // Swamp Assassin
		23170, // Swamp Watcher
		23171, // Corpse Collector
		23172, // Delegate of Blood
		23173, // Blood Aide
	};
	// Items
	private static final int EXTRACT = 34059; // Putrefied Extract
	private static final int BLOOD = 34060; // Rotten Blood
	// Misc
	private static final int DROP_CHANCE = 65; // Guessed
	private static final int MIN_LEVEL = 85;
	
	public Q00490_DutyOfTheSurvivor()
	{
		super(490);
		addStartNpc(VOLLODOS);
		addTalkId(VOLLODOS);
		addKillId(EXTRACT_MONSTERS);
		addKillId(BLOOD_MONSTERS);
		registerQuestItems(EXTRACT, BLOOD);
		addCondMinLevel(MIN_LEVEL, "30137-09.htm");
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
			case "30137-02.htm":
			case "30137-03.htm":
			case "30137-04.htm":
			{
				htmltext = event;
				break;
			}
			case "30137-05.htm":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if (npc.getId() == VOLLODOS)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					htmltext = "30137-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (st.isCond(1))
					{
						htmltext = "30137-06.htm";
					}
					else
					{
						giveAdena(player, 505_062, true);
						st.exitQuest(QuestType.DAILY, true);
						if (player.getLevel() >= MIN_LEVEL)
						{
							addExpAndSp(player, 145_557_000, 34_933);
						}
						htmltext = "30137-07.htm";
					}
					break;
				}
				case State.COMPLETED:
				{
					if (st.isNowAvailable())
					{
						st.setState(State.CREATED);
						htmltext = "30137-01.htm";
					}
					else
					{
						htmltext = "30137-08.htm";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final L2PcInstance member = getRandomPartyMember(player, 1);
		if (member != null)
		{
			final QuestState st = getQuestState(member, false);
			if (st.isCond(1) && (getRandom(100) < DROP_CHANCE))
			{
				final int itemId = CommonUtil.contains(EXTRACT_MONSTERS, npc.getId()) ? EXTRACT : BLOOD;
				if (getQuestItemsCount(player, itemId) < 20)
				{
					giveItems(player, itemId, 1);
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				
				if ((getQuestItemsCount(player, EXTRACT) == 20) && (getQuestItemsCount(player, BLOOD) == 20))
				{
					st.setCond(2);
				}
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(2);
			npcLogList.add(new NpcLogListHolder(EXTRACT, false, (int) getQuestItemsCount(player, EXTRACT)));
			npcLogList.add(new NpcLogListHolder(BLOOD, false, (int) getQuestItemsCount(player, BLOOD)));
			return npcLogList;
		}
		return super.getNpcLogList(player);
	}
}