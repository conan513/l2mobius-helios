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
package quests.Q10742_AFurryFriend;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.instancemanager.WalkingManager;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * A Furry Friend (10742)
 * @author Sdw
 */
public final class Q10742_AFurryFriend extends Quest
{
	// NPCs
	private static final int LEIRA = 33952;
	private static final int RICKY = 19552;
	private static final int KIKU_S_CAVE = 33995;
	// Monster
	private static final int KIKU = 23453;
	// Location
	private static final Location RICKY_SPAWN = new Location(-78119, 237377, -3536);
	// Misc
	private static final int MIN_LEVEL = 11;
	private static final int MAX_LEVEL = 20;
	
	public Q10742_AFurryFriend()
	{
		super(10742);
		addStartNpc(LEIRA);
		addTalkId(LEIRA, KIKU_S_CAVE);
		addFirstTalkId(KIKU_S_CAVE);
		addRouteFinishedId(RICKY);
		
		addCondRace(Race.ERTHEIA, "");
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "33952-00.htm");
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
			case "33952-02.htm":
			case "33952-05.html":
			{
				htmltext = event;
				break;
			}
			case "33952-03.htm":
			{
				qs.startQuest();
				qs.set("cave", getRandom(3));
				showOnScreenMsg(player, NpcStringId.FOLLOW_RICKY, ExShowScreenMessage.TOP_CENTER, 10000);
				final L2Npc ricky = addSpawn(RICKY, RICKY_SPAWN, false, 180000);
				ricky.setSummoner(player);
				ricky.setTitle(player.getName());
				htmltext = event;
				break;
			}
			case "spawn_ricky":
			{
				if (qs.isStarted() && qs.isCond(1))
				{
					final int caveId = npc.getParameters().getInt("caveId");
					if (qs.getInt("cave") != caveId) // Wrong cave
					{
						addAttackPlayerDesire(addSpawn(KIKU, player.getLocation(), true, 120000), player);
						showOnScreenMsg(player, NpcStringId.RICKY_IS_NOT_HERE_NTRY_SEARCHING_ANOTHER_KIKU_S_CAVE, ExShowScreenMessage.TOP_CENTER, 8000);
						htmltext = "33995-02.html";
					}
					else if (!L2World.getInstance().getVisibleObjectsInRange(player, L2Npc.class, 500).stream().anyMatch(n -> (n.getId() == RICKY) && (n.getSummoner() == player)))
					{
						showOnScreenMsg(player, NpcStringId.TAKE_RICKY_TO_LEIRA_IN_UNDER_2_MINUTES, ExShowScreenMessage.MIDDLE_CENTER, 5000);
						player.sendPacket(new ExSendUIEvent(player, false, false, 120, 0, NpcStringId.REMAINING_TIME));
						
						// Spawn Ricky
						final L2Npc ricky = addSpawn(RICKY, player, true, 120000);
						ricky.setSummoner(player);
						ricky.setTitle(player.getName());
						ricky.setRunning();
						ricky.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
						startQuestTimer("CHECK_RICKY_DISTANCE", 2500, ricky, player);
					}
					else // Already have Ricky
					{
						htmltext = "33995-03.html";
					}
				}
				break;
			}
			case "CHECK_RICKY_DISTANCE":
			{
				if (player == null)
				{
					startQuestTimer("DESPAWN_RICKY", 2000, npc, null);
				}
				else if ((npc != null) && !npc.isDecayed())
				{
					// Follow was breaking sometimes, making sure it doesn't happen.
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
					
					// Check Ricky position
					final double distanceToRicky = player.calculateDistance2D(npc);
					if (distanceToRicky > 350)
					{
						showOnScreenMsg(player, NpcStringId.YOU_ARE_FAR_FROM_RICKY, ExShowScreenMessage.TOP_CENTER, 5000);
						if (distanceToRicky > 650)
						{
							player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.REMAINING_TIME));
							startQuestTimer("DESPAWN_RICKY", 1000, npc, player);
						}
						else
						{
							startQuestTimer("CHECK_RICKY_DISTANCE", 2500, npc, player);
						}
					}
					else
					{
						final L2Npc leira = L2World.getInstance().getVisibleObjectsInRange(npc, L2Npc.class, 300).stream().filter(n -> (n.getId() == LEIRA)).findAny().orElse(null);
						if (leira != null)
						{
							qs.setCond(2, true);
							player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.REMAINING_TIME));
							showOnScreenMsg(player, NpcStringId.RICKY_HAS_FOUND_LEIRA, ExShowScreenMessage.MIDDLE_CENTER, 10000);
							startQuestTimer("DESPAWN_RICKY", 1000, npc, player);
						}
						else
						{
							startQuestTimer("CHECK_RICKY_DISTANCE", 2500, npc, player);
						}
					}
				}
				break;
			}
			case "DESPAWN_RICKY":
			{
				if ((npc != null) && !npc.isDecayed())
				{
					npc.deleteMe();
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
		
		if (npc.getId() == LEIRA)
		{
			switch (qs.getState())
			{
				case State.CREATED:
				{
					htmltext = "33952-01.htm";
					break;
				}
				case State.STARTED:
				{
					if (qs.isCond(1))
					{
						htmltext = "33952-06.html";
					}
					else if (qs.isCond(2))
					{
						giveAdena(player, 2500, true);
						addExpAndSp(player, 52516, 5);
						qs.exitQuest(false, true);
						htmltext = "33952-04.html";
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = getAlreadyCompletedMsg(player);
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		return ((qs != null) && qs.isCond(1)) ? "33995-01.html" : "33995.html";
	}
	
	@Override
	public void onRouteFinished(L2Npc npc)
	{
		WalkingManager.getInstance().cancelMoving(npc);
		ThreadPool.schedule(() -> npc.deleteMe(), 1000);
	}
}
