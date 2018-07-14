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
package quests.Q10776_TheWrathOfTheGiants;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;

import quests.Q10775_InSearchOfAnAncientGiant.Q10775_InSearchOfAnAncientGiant;

/**
 * The Wrath of the Giants (10776)
 * @author malyelfik
 */
public final class Q10776_TheWrathOfTheGiants extends Quest
{
	// NPCs
	private static final int BELKADHI = 30485;
	private static final int DESTROYED_DEVICE = 32366;
	private static final int NARSIDES = 33992;
	// Monsters
	private static final int ENRAGED_GIANT_NARSIDES = 27534;
	private static final int GIANT_MINION = 27535;
	// Items
	private static final int REGENERATION_DEVICE_CORE = 39716;
	private static final int ENCHANT_ARMOR_C = 952;
	// Location
	private static final Location NARSIDES_LOC = new Location(16422, 113281, -9064);
	private static final Location ENRAGED_NARSIDES_LOC = new Location(16422, 113281, -9064);
	private static final Location[] MINIONS_LOC =
	{
		new Location(16313, 113301, -9064),
		new Location(16515, 113298, -9064)
	};
	// Misc
	private static final int MIN_LEVEL = 48;
	
	public Q10776_TheWrathOfTheGiants()
	{
		super(10776);
		addStartNpc(BELKADHI);
		addTalkId(BELKADHI, DESTROYED_DEVICE, NARSIDES);
		addKillId(ENRAGED_GIANT_NARSIDES);
		
		addCondRace(Race.ERTHEIA, "30485-00.htm");
		addCondMinLevel(MIN_LEVEL, "30485-00.htm");
		addCondCompletedQuest(Q10775_InSearchOfAnAncientGiant.class.getSimpleName(), "30485-00.htm");
		registerQuestItems(REGENERATION_DEVICE_CORE);
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
			case "33992-02.html":
			case "33992-03.html":
			case "30485-07.html":
			{
				break;
			}
			case "30485-03.htm":
			{
				qs.startQuest();
				giveItems(player, REGENERATION_DEVICE_CORE, 1);
				break;
			}
			case "spawn_narsides":
			{
				if (npc.isScriptValue(0))
				{
					qs.setCond(2, true);
					npc.setScriptValue(player.getObjectId());
					npc.setTitle(player.getName());
					npc.broadcastInfo();
					
					final L2Npc narsides = addSpawn(NARSIDES, NARSIDES_LOC);
					narsides.setSummoner(npc);
					narsides.setScriptValue(player.getObjectId());
					getTimers().addTimer("DESPAWN_NARSIDES", 62000, narsides, null);
					htmltext = null;
				}
				else
				{
					htmltext = "32366-02.html";
				}
				break;
			}
			case "33992-04.html":
			{
				if (qs.isCond(2) || qs.isCond(3))
				{
					qs.setCond(3, true);
				}
				break;
			}
			case "spawn_mob":
			{
				if (!npc.isScriptValue(0) && getTimers().hasTimer("DESPAWN_NARSIDES", npc, null))
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.CURSED_ERTHEIA_I_WILL_KILL_YOU_ALL);
					getTimers().cancelTimer("DESPAWN_NARSIDES", npc, null);
					getTimers().addTimer("SPAWN_MINIONS", 4000, npc, player);
				}
				htmltext = null;
				break;
			}
			case "30485-08.html":
			{
				if (qs.isCond(4))
				{
					giveItems(player, ENCHANT_ARMOR_C, 4);
					giveStoryQuestReward(player, 20);
					addExpAndSp(player, 4838400, 1161);
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
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "30485-04.html";
							break;
						}
						case 2:
						case 3:
						{
							htmltext = "30485-05.html";
							break;
						}
						case 4:
						{
							htmltext = "30485-06.html";
							break;
						}
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
		else if (qs.isStarted())
		{
			if (npc.getId() == DESTROYED_DEVICE)
			{
				switch (qs.getCond())
				{
					case 1:
					case 2:
					case 3:
					{
						htmltext = "32366-01.html";
						break;
					}
				}
			}
			else if (npc.isScriptValue(player.getObjectId()) && (qs.isCond(2) || qs.isCond(3)))
			{
				htmltext = "33992-01.html";
			}
		}
		return htmltext;
		
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(3))
		{
			qs.setCond(4, true);
			
			final L2Npc device = (L2Npc) npc.getSummoner();
			device.setTitle("");
			device.setScriptValue(0);
			device.broadcastInfo();
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "DESPAWN_NARSIDES":
			{
				if ((npc != null) && (npc.getId() == NARSIDES))
				{
					final L2Npc summoner = (L2Npc) npc.getSummoner();
					summoner.setTitle("");
					summoner.setScriptValue(0);
					summoner.broadcastInfo();
					npc.deleteMe();
				}
				break;
			}
			case "SPAWN_MINIONS":
			{
				if ((npc != null) && (player != null))
				{
					for (Location loc : MINIONS_LOC)
					{
						final L2Npc mob = addSpawn(GIANT_MINION, loc, false, 120000);
						addAttackPlayerDesire(mob, player);
					}
					getTimers().addTimer("SPAWN_MASTER", 2000, npc, player);
				}
				break;
			}
			case "SPAWN_MASTER":
			{
				if (npc != null)
				{
					final L2Npc master = addSpawn(ENRAGED_GIANT_NARSIDES, ENRAGED_NARSIDES_LOC);
					master.setSummoner(npc.getSummoner());
					addAttackPlayerDesire(master, player);
					npc.deleteMe();
					getTimers().addTimer("MASTER_DESPAWN", 120000, master, null);
				}
				break;
			}
			case "MASTER_DESPAWN":
			{
				if (npc != null)
				{
					final L2Npc device = (L2Npc) npc.getSummoner();
					device.setTitle("");
					device.setScriptValue(0);
					device.broadcastInfo();
					npc.deleteMe();
				}
				break;
			}
			default:
			{
				super.onTimerEvent(event, params, npc, player);
			}
		}
	}
}