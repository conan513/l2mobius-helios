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
package quests.Q10335_RequestToFindSakum;

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

/**
 * Request To Find Sakum (10335)
 * @author St3eT
 */
public final class Q10335_RequestToFindSakum extends Quest
{
	// NPCs
	private static final int BATHIS = 30332;
	private static final int KALLESIN = 33177;
	private static final int ZENATH = 33509;
	private static final int SKELETON_TRACKER = 20035;
	private static final int SKELETON_BOWMAN = 20051;
	private static final int RUIN_SPARTOI = 20054;
	private static final int RUIN_ZOMBIE = 20026;
	private static final int RUIN_ZOMBIE_LEADER = 20029;
	// Misc
	private static final int MIN_LEVEL = 23;
	private static final int MAX_LEVEL = 40;
	
	public Q10335_RequestToFindSakum()
	{
		super(10335);
		addStartNpc(BATHIS);
		addTalkId(BATHIS, KALLESIN, ZENATH);
		addKillId(SKELETON_TRACKER, SKELETON_BOWMAN, RUIN_SPARTOI, RUIN_ZOMBIE, RUIN_ZOMBIE_LEADER);
		addCondNotRace(Race.ERTHEIA, "30332-08.html");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30332-07.html");
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
			case "30332-02.htm":
			case "33509-03.html":
			{
				htmltext = event;
				break;
			}
			case "30332-03.html":
			{
				st.startQuest();
				htmltext = event;
				break;
			}
			case "33177-02.html":
			{
				if (st.isCond(1))
				{
					st.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "33509-04.html":
			{
				if (st.isCond(3))
				{
					giveAdena(player, 900, true);
					addExpAndSp(player, 350000, 84);
					st.exitQuest(false, true);
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
		final QuestState st = getQuestState(player, true);
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == BATHIS)
				{
					htmltext = "30332-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case BATHIS:
					{
						htmltext = st.isCond(1) ? "30332-04.html" : "30332-05.html";
						break;
					}
					case KALLESIN:
					{
						switch (st.getCond())
						{
							case 1:
							{
								htmltext = "33177-01.html";
								break;
							}
							case 2:
							{
								htmltext = "33177-03.html";
								break;
							}
							case 3:
							{
								htmltext = "33177-04.html";
								break;
							}
						}
						break;
					}
					case ZENATH:
					{
						switch (st.getCond())
						{
							case 1:
							case 2:
							{
								htmltext = "33509-01.html";
								break;
							}
							case 3:
							{
								htmltext = "33509-02.html";
								break;
							}
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case BATHIS:
					{
						htmltext = "30332-06.html";
						break;
					}
					case KALLESIN:
					{
						htmltext = "33177-05.html";
						break;
					}
					case ZENATH:
					{
						htmltext = "33509-05.html";
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted() && st.isCond(2))
		{
			int killedTracker = st.getInt("killed_" + SKELETON_TRACKER);
			int killedBowman = st.getInt("killed_" + SKELETON_BOWMAN);
			int killedRuinSpartois = st.getInt("killed_" + RUIN_SPARTOI);
			int killedZombie = st.getInt("killed_" + RUIN_ZOMBIE);
			
			switch (npc.getId())
			{
				case SKELETON_TRACKER:
				{
					if (killedTracker < 10)
					{
						killedTracker++;
						st.set("killed_" + SKELETON_TRACKER, killedTracker);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SKELETON_BOWMAN:
				{
					if (killedBowman < 10)
					{
						killedBowman++;
						st.set("killed_" + SKELETON_BOWMAN, killedBowman);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case RUIN_SPARTOI:
				{
					if (killedRuinSpartois < 15)
					{
						killedRuinSpartois++;
						st.set("killed_" + RUIN_SPARTOI, killedRuinSpartois);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case RUIN_ZOMBIE:
				case RUIN_ZOMBIE_LEADER:
				{
					if (killedZombie < 15)
					{
						killedZombie++;
						st.set("killed_" + RUIN_ZOMBIE, killedZombie);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			if ((killedTracker == 10) && (killedBowman == 10) && (killedRuinSpartois == 15) && (killedZombie == 15))
			{
				st.setCond(3, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance activeChar)
	{
		final QuestState st = getQuestState(activeChar, false);
		if ((st != null) && st.isStarted() && st.isCond(2))
		{
			final Set<NpcLogListHolder> npcLogList = new HashSet<>(4);
			npcLogList.add(new NpcLogListHolder(SKELETON_TRACKER, false, st.getInt("killed_" + SKELETON_TRACKER)));
			npcLogList.add(new NpcLogListHolder(SKELETON_BOWMAN, false, st.getInt("killed_" + SKELETON_BOWMAN)));
			npcLogList.add(new NpcLogListHolder(RUIN_SPARTOI, false, st.getInt("killed_" + RUIN_SPARTOI)));
			npcLogList.add(new NpcLogListHolder(RUIN_ZOMBIE, false, st.getInt("killed_" + RUIN_ZOMBIE)));
			return npcLogList;
		}
		return super.getNpcLogList(activeChar);
	}
}