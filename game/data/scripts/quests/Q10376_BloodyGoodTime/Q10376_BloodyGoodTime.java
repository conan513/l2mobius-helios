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
package quests.Q10376_BloodyGoodTime;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassLevel;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.NpcSay;

import quests.Q10375_SuccubusDisciples.Q10375_SuccubusDisciples;

/**
 * Bloody Good Time (10376)
 * @URL https://l2wiki.com/Bloody_Good_Time
 * @author Gigi
 */
public class Q10376_BloodyGoodTime extends Quest
{
	// NPCs
	private static final int ZENYA = 32140;
	private static final int CASCA = 32139;
	private static final int AGNES = 31588;
	private static final int ANDREI = 31292;
	private static final int MOB_BLOODY_VEIN = 27481;
	// Misc
	private static final int MIN_LEVEL = 80;
	// Reward
	private static final int EXP_REWARD = 121297500;
	private static final int SP_REWARD = 29111;
	// Items
	private static final ItemHolder REWARD_MAGIC_RUNE_CLIP = new ItemHolder(32700, 1);
	// Location
	private static final Location RETURN_LOC = new Location(178648, -84903, -7216);
	
	public Q10376_BloodyGoodTime()
	{
		super(10376);
		addStartNpc(ZENYA);
		addTalkId(ZENYA, CASCA, AGNES, ANDREI);
		addFirstTalkId(CASCA);
		addKillId(MOB_BLOODY_VEIN);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10375_SuccubusDisciples.class.getSimpleName(), "restriction.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = null;
		switch (event)
		{
			case "32139-05.html":
			case "32139-07.html":
			case "32139-08.html":
			case "32139-09.html":
			case "31588-02.html":
			case "32140-02.htm":
			case "32140-07.html":
			case "31292-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32140-06.html":
			{
				qs.startQuest();
				htmltext = "32140-06.html";
				break;
			}
			case "32139-02.html":
			{
				if (qs.isCond(1))
				{
					qs.setCond(2, true);
					htmltext = event;
					break;
				}
			}
			case "return":
			{
				if (player.isInCombat())
				{
					player.sendPacket(new ExShowScreenMessage("You cannot teleport when you in combat status.", 5000));
				}
				else
				{
					qs.setCond(2);
					player.teleToLocation(RETURN_LOC, 0);
					player.setInstance(null);
				}
			}
			case "32139-03.html":
			{
				htmltext = event;
				break;
			}
			case "31588-03.html":
			{
				qs.setCond(6, true);
				break;
			}
			case "31292-03.html":
			{
				addExpAndSp(player, EXP_REWARD, SP_REWARD);
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.WELL_DONE_I_WAS_RIGHT_TO_ENTRUST_THIS_TO_YOU));
				giveItems(player, REWARD_MAGIC_RUNE_CLIP);
				htmltext = event;
				qs.exitQuest(false, true);
			}
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		switch (npc.getId())
		{
			case ZENYA:
			{
				switch (qs.getState())
				{
					case State.CREATED:
					{
						if (((player.getClassId().level() != ClassLevel.AWAKEN.ordinal()) && (player.getRace() != Race.ERTHEIA)))
						{
							return "32140-01.htm";
						}
						else if ((player.getRace() == Race.ERTHEIA) && (player.getLevel() >= MIN_LEVEL))
						{
							return "32140-01.htm";
						}
						else
						{
							return "32140-03.html";
						}
					}
					case State.STARTED:
					{
						return "32140-07.html";
					}
					case State.COMPLETED:
					{
						return "32140-05.htm";
					}
				}
				break;
			}
			case CASCA:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(1))
					{
						return "32139-01.html";
					}
					else if (qs.isCond(2))
					{
						return "32139-03.html";
					}
					else if (qs.isCond(3))
					{
						return "32139-04.html";
					}
					else if (qs.isCond(4))
					{
						return "32139-06.html";
					}
				}
				break;
			}
			case AGNES:
			{
				if (qs.isStarted())
				{
					if (qs.isCond(5))
					{
						htmltext = "31588-01.html";
					}
					else if (qs.isCond(6))
					{
						htmltext = "31588-03.html";
					}
				}
				break;
			}
			case ANDREI:
			{
				if (qs.isStarted() && qs.isCond(6))
				{
					htmltext = "31292-01.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "32139.html";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isCond(3))
		{
			st.setCond(4, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
}