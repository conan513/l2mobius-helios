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
package ai.bosses.Ramona;

import java.util.ArrayList;

import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.enums.Movie;
import com.l2jmobius.gameserver.instancemanager.MapRegionManager;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.TeleportWhereType;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.QuestTimer;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.zone.type.L2NoSummonFriendZone;

import ai.AbstractNpcAI;

/**
 * Ramona RB
 * @author Gigi
 * @date 2017-04-09 - [10:22:38]
 */
public class Ramona extends AbstractNpcAI
{
	// NPC
	private static final int ROOM_CONTROL = 19642;
	private static final int INVISIBLE = 19643;
	private static final int RAMONA = 19648;
	private static final int RAMONA_1 = 26141;
	private static final int RAMONA_2 = 26142;
	private static final int RAMONA_3 = 26143;
	private static final int[] MINION_LIST =
	{
		26144, // Dancer of the Queen
		26145, // Commander of the Queen
		26146, // Shooter of the Queen
		26147 // Wizard of the Queen
	};
	// skill
	private static final Skill HYPER_MEGA_PLASMA_SHOT = SkillData.getInstance().getSkill(16641, 1);
	private static final Skill HYPER_MEGA_PLASMA_BRUST = SkillData.getInstance().getSkill(16642, 1);
	private static final Skill HIPER_MEGA_TELEKINESS = SkillData.getInstance().getSkill(16643, 1);
	private static final Skill RIDE_THE_LIGHTING = SkillData.getInstance().getSkill(16644, 1);
	private static final Skill RIDE_THE_LIGHTING_MEGA_BRUST = SkillData.getInstance().getSkill(16645, 1);
	private static final Skill ULTRA_MEGA_TELEKINESS = SkillData.getInstance().getSkill(16647, 1);
	private static final Skill[] RAMONA1_SKILLS =
	{
		HYPER_MEGA_PLASMA_BRUST,
		HYPER_MEGA_PLASMA_SHOT,
		RIDE_THE_LIGHTING
	};
	private static final Skill[] RAMONA2_SKILLS =
	{
		HYPER_MEGA_PLASMA_BRUST,
		HYPER_MEGA_PLASMA_SHOT,
		RIDE_THE_LIGHTING,
		RIDE_THE_LIGHTING_MEGA_BRUST
	};
	private static final Skill[] RAMONA3_SKILLS =
	{
		HYPER_MEGA_PLASMA_BRUST,
		HYPER_MEGA_PLASMA_SHOT,
		RIDE_THE_LIGHTING,
		RIDE_THE_LIGHTING_MEGA_BRUST,
		HIPER_MEGA_TELEKINESS,
		ULTRA_MEGA_TELEKINESS
	};
	// Other
	private static final int ROOM_CONTROL_DOOR = 22230711;
	private static final L2NoSummonFriendZone ZONE = ZoneManager.getInstance().getZoneById(210108, L2NoSummonFriendZone.class);
	private static final int MIN_PLAYER_COUNT = 14;
	// Vars
	private static L2DoorInstance _door;
	private static ArrayList<L2Npc> _minions = new ArrayList<>();
	private static int _bossStage;
	private static long _lastAction;
	private static L2Npc _invisible;
	private static L2Npc _ramona1;
	private static L2Npc _ramona2;
	private static L2Npc _ramona3;
	
	private Ramona()
	{
		addStartNpc(ROOM_CONTROL);
		addKillId(ROOM_CONTROL, RAMONA_3);
		addSeeCreatureId(INVISIBLE);
		addAttackId(RAMONA_1, RAMONA_2, RAMONA_3);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "SPAWN_MS":
			{
				addSpawn(ROOM_CONTROL, 78023, 172262, -10604, 16383, false, 600000, false);
				addSpawn(RAMONA, 78012, 169922, -10467, 16383, false, 600000, false);
				_lastAction = System.currentTimeMillis();
				break;
			}
			case "SPAWN_RAMONA_1":
			{
				_bossStage = 1;
				L2World.getInstance().forEachVisibleObjectInRange(npc, L2Npc.class, 3000, ramona ->
				{
					if (ramona.getId() == RAMONA)
					{
						ramona.deleteMe();
					}
				});
				_ramona1 = addSpawn(RAMONA_1, 78023, 172262, -10604, 16383, false, 1200000, true);
				startQuestTimer("CHECK_ACTIVITY_TASK", 60000, null, null, true);
				startQuestTimer("RAMONA1_SKILL", 6000, _ramona1, null);
				break;
			}
			case "RAMONA1_SKILL":
			{
				if ((_bossStage == 1) && _ramona1.isInCombat())
				{
					Skill randomAttackSkill = RAMONA1_SKILLS[Rnd.get(RAMONA1_SKILLS.length)];
					if (getRandom(100) > 20)
					{
						_ramona1.doCast(randomAttackSkill);
					}
				}
				break;
			}
			case "SPAWN_RAMONA_MINIONS":
			{
				_bossStage = 2;
				for (int i = 0; i < 7; i++)
				{
					final L2Npc minion = addSpawn(MINION_LIST[Rnd.get(MINION_LIST.length)], npc.getX() + getRandom(-200, 200), npc.getY() + getRandom(-200, 200), npc.getZ(), npc.getHeading(), false, 600000);
					minion.setRunning();
					((L2Attackable) minion).setIsRaidMinion(true);
					addAttackPlayerDesire(minion, player);
					_minions.add(minion);
				}
				startQuestTimer("RAMONA2_SKILL", 6000, _ramona2, null);
				break;
			}
			case "RAMONA2_SKILL":
			{
				if ((_bossStage == 2) && _ramona2.isInCombat())
				{
					Skill randomAttackSkill = RAMONA2_SKILLS[Rnd.get(RAMONA2_SKILLS.length)];
					if (getRandom(100) > 20)
					{
						_ramona2.doCast(randomAttackSkill);
					}
				}
				break;
			}
			case "SPAWN_RAMONA_MINIONS_1":
			{
				_bossStage = 3;
				for (int i = 0; i < 7; i++)
				{
					final L2Npc minion = addSpawn(MINION_LIST[Rnd.get(MINION_LIST.length)], npc.getX() + getRandom(-200, 200), npc.getY() + getRandom(-200, 200), npc.getZ(), npc.getHeading(), false, 600000);
					minion.setRunning();
					((L2Attackable) minion).setIsRaidMinion(true);
					addAttackPlayerDesire(minion, player);
					_minions.add(minion);
				}
				startQuestTimer("RAMONA3_SKILL", 6000, _ramona3, null);
				break;
			}
			case "RAMONA3_SKILL":
			{
				if ((_bossStage == 3) && _ramona3.isInCombat())
				{
					Skill randomAttackSkill = RAMONA3_SKILLS[Rnd.get(RAMONA3_SKILLS.length)];
					if (getRandom(100) > 20)
					{
						_ramona3.doCast(randomAttackSkill);
					}
				}
				break;
			}
			case "CHECK_ACTIVITY_TASK":
			{
				if ((_lastAction + 900000) < System.currentTimeMillis())
				{
					// GrandBossManager.getInstance().setBossStatus(RAMONA, ALIVE);
					for (L2Character charInside : ZONE.getCharactersInside())
					{
						if (charInside != null)
						{
							if (charInside.isNpc())
							{
								charInside.deleteMe();
							}
							else if (charInside.isPlayer())
							{
								charInside.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(charInside, TeleportWhereType.TOWN));
							}
						}
					}
					startQuestTimer("END_RAMONA", 2000, null, null);
				}
				else
				{
					startQuestTimer("CHECK_ACTIVITY_TASK", 60000, null, null);
				}
				break;
			}
			case "CANCEL_TIMERS":
			{
				QuestTimer activityTimer = getQuestTimer("CHECK_ACTIVITY_TASK", null, null);
				if (activityTimer != null)
				{
					activityTimer.cancel();
				}
				break;
			}
			case "END_RAMONA":
			{
				_bossStage = 0;
				ZONE.oustAllPlayers();
				if (_ramona1 != null)
				{
					_ramona1.deleteMe();
				}
				if (_ramona2 != null)
				{
					_ramona2.deleteMe();
				}
				if (_ramona3 != null)
				{
					_ramona3.deleteMe();
				}
				if (!_minions.isEmpty())
				{
					for (L2Npc minion : _minions)
					{
						if (minion == null)
						{
							continue;
						}
						minion.deleteMe();
					}
				}
				_minions.clear();
				_invisible.setScriptValue(0);
				_door.setTargetable(true);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		double currentHp = (npc.getCurrentHp() / npc.getMaxHp()) * 100;
		switch (npc.getId())
		{
			case RAMONA_1:
			{
				_lastAction = System.currentTimeMillis();
				if (currentHp < 75)
				{
					playMovie(ZONE.getPlayersInside(), Movie.SC_RAMONA_TRANS_A);
					_ramona2 = addSpawn(RAMONA_2, 78023, 172262, -10604, 16383, false, 1200000, false);
					_ramona2.setCurrentHp(_ramona1.getCurrentHp());
					_ramona1.deleteMe();
					startQuestTimer("SPAWN_RAMONA_MINIONS", 6000, _ramona2, null);
				}
				break;
			}
			case RAMONA_2:
			{
				_lastAction = System.currentTimeMillis();
				if (currentHp < 50)
				{
					playMovie(ZONE.getPlayersInside(), Movie.SC_RAMONA_TRANS_B);
					_ramona3 = addSpawn(RAMONA_3, 78023, 172262, -10604, 16383, false, 1200000, false);
					_ramona3.setCurrentHp(_ramona2.getCurrentHp());
					_ramona2.deleteMe();
					startQuestTimer("SPAWN_RAMONA_MINIONS_1", 6000, _ramona3, null);
				}
				break;
			}
			case RAMONA_3:
			{
				_lastAction = System.currentTimeMillis();
				break;
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ROOM_CONTROL:
			{
				L2World.getInstance().forEachVisibleObjectInRange(npc, L2DoorInstance.class, 8000, Door ->
				{
					if (Door.getId() == ROOM_CONTROL_DOOR)
					{
						Door.closeMe();
						Door.setTargetable(false);
						_door = Door;
					}
				});
				startQuestTimer("SPAWN_RAMONA_1", 5000, npc, null);
				break;
			}
			case RAMONA_3:
			{
				notifyEvent("CANCEL_TIMERS", null, null);
				startQuestTimer("END_RAMONA", 300000, null, null);
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (creature.isPlayer() && npc.isScriptValue(0) && (ZONE.getCharactersInside().size() >= MIN_PLAYER_COUNT))
		{
			startQuestTimer("SPAWN_MS", 10000, npc, null);
			npc.setScriptValue(1);
			_invisible = npc;
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Ramona();
	}
}