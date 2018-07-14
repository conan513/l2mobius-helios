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
package instances.CastleDungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.entity.Castle;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.model.instancezone.InstanceTemplate;
import com.l2jmobius.gameserver.model.quest.QuestState;

import instances.AbstractInstance;
import quests.Q00512_BladeUnderFoot.Q00512_BladeUnderFoot;

/**
 * <b>Castle dungeon</b> instance for quest <b>BladeUnderFoot (512)</b>
 * @author Mobius
 */
public final class CastleDungeon extends AbstractInstance
{
	// NPCs
	private static final Map<Integer, Integer> NPCS = new HashMap<>();
	static
	{
		NPCS.put(36403, 13); // Gludio
		NPCS.put(36404, 14); // Dion
		NPCS.put(36405, 15); // Giran
		NPCS.put(36406, 16); // Oren
		NPCS.put(36407, 17); // Aden
		NPCS.put(36408, 18); // Innadril
		NPCS.put(36409, 19); // Goddard
		NPCS.put(36410, 20); // Rune
		NPCS.put(36411, 21); // Schuttgart
	}
	// Monsters
	private static final int[] RAIDS1 =
	{
		25546,
		25549,
		25552
	};
	private static final int[] RAIDS2 =
	{
		25553,
		25554,
		25557,
		25560
	};
	private static final int[] RAIDS3 =
	{
		25563,
		25566,
		25569
	};
	// Item
	private static final int MARK = 9798;
	// Locations
	private static final Location SPAWN_LOC = new Location(12230, -49139, -3013);
	// Misc
	private static final int MARK_COUNT = 2520;
	private static final long REENTER = 24 * 3600000; // 24 hours
	private static final Map<Integer, Long> REENETER_HOLDER = new ConcurrentHashMap<>();
	
	public CastleDungeon()
	{
		super(NPCS.values().stream().mapToInt(Integer::valueOf).toArray());
		// NPCs
		addStartNpc(NPCS.keySet());
		addTalkId(NPCS.keySet());
		// Monsters
		addKillId(RAIDS1);
		addKillId(RAIDS2);
		addKillId(RAIDS3);
		// Instance
		addInstanceCreatedId(NPCS.values());
		addInstanceDestroyId(NPCS.values());
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final int npcId = npc.getId();
		if (NPCS.containsKey(npcId))
		{
			enterInstance(player, npc, NPCS.get(npcId));
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			if (CommonUtil.contains(RAIDS3, npc.getId()))
			{
				// Get players with active quest
				final List<L2PcInstance> members = new ArrayList<>();
				for (L2PcInstance member : world.getPlayers())
				{
					final QuestState qs = member.getQuestState(Q00512_BladeUnderFoot.class.getSimpleName());
					if ((qs != null) && qs.isCond(1))
					{
						members.add(member);
					}
				}
				
				// Distribute marks between them
				if (!members.isEmpty())
				{
					final long itemCount = MARK_COUNT / members.size();
					for (L2PcInstance member : members)
					{
						giveItems(member, MARK, itemCount);
						playSound(member, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
				world.finishInstance();
			}
			else
			{
				world.incStatus();
				spawnRaid(world);
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, L2PcInstance player)
	{
		// Put re-enter for instance
		REENETER_HOLDER.put(instance.getTemplateId(), System.currentTimeMillis() + REENTER);
		// Schedule spawn of first raid
		spawnRaid(instance);
	}
	
	@Override
	protected boolean validateConditions(List<L2PcInstance> group, L2Npc npc, InstanceTemplate template)
	{
		final L2PcInstance groupLeader = group.get(0);
		final Castle castle = npc.getCastle();
		if (castle == null)
		{
			showHtmlFile(groupLeader, "noProperPledge.html");
			return false;
		}
		else if (REENETER_HOLDER.containsKey(template.getId()))
		{
			final long time = REENETER_HOLDER.get(template.getId());
			if (time > System.currentTimeMillis())
			{
				showHtmlFile(groupLeader, "enterRestricted.html");
				return false;
			}
			REENETER_HOLDER.remove(template.getId());
		}
		return true;
	}
	
	@Override
	public void onInstanceDestroy(Instance instance)
	{
		// Stop running spawn task
		final ScheduledFuture<?> task = instance.getParameters().getObject("spawnTask", ScheduledFuture.class);
		if ((task != null) && !task.isDone())
		{
			task.cancel(true);
		}
		instance.setParameter("spawnTask", null);
	}
	
	/**
	 * Spawn raid boss according to instance status.
	 * @param instance instance world where instance should be spawned
	 */
	private void spawnRaid(Instance instance)
	{
		final ScheduledFuture<?> spawnTask = ThreadPool.schedule(() ->
		{
			// Get template id of raid
			final int npcId;
			switch (instance.getStatus())
			{
				case 0:
				{
					npcId = RAIDS1[getRandom(RAIDS1.length)];
					break;
				}
				case 1:
				{
					npcId = RAIDS2[getRandom(RAIDS2.length)];
					break;
				}
				default:
				{
					npcId = RAIDS3[getRandom(RAIDS3.length)];
				}
			}
			
			// Spawn raid
			addSpawn(npcId, SPAWN_LOC, false, 0, false, instance.getId());
			
			// Unset spawn task reference
			instance.setParameter("spawnTask", null);
		}, 2 * 60 * 1000); // 2 minutes
		
		// Save timer to instance world
		instance.setParameter("spawnTask", spawnTask);
	}
	
	public static void main(String[] args)
	{
		new CastleDungeon();
	}
}