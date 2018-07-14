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
package ai.areas.HellboundIsland.QuarryRebel;

import com.l2jmobius.gameserver.ai.CtrlEvent;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.skills.Skill;

import ai.AbstractNpcAI;

/**
 * Desert Quarry summoner's AI
 * @URL https://l2wiki.com/Desert_Quarry
 * @author Bonux, Gigi
 * @date 2017-11-08 - [16:38:56]
 */
public class QuarryRebel extends AbstractNpcAI
{
	// Monsters
	private static final int FIRE_SLAVE_BRIDGET = 19503;
	private static final int FLOX_GOLEM = 19506;
	private static final int EDAN = 19509;
	private static final int DISCIPLINED_DEATHMOZ = 19504;
	private static final int MAGICAL_DEATHMOZ = 19505;
	private static final int DISCIPLINED_FLOXIS = 19507;
	private static final int MAGICAL_FLOXIS = 19508;
	private static final int DISCIPLINED_BELIKA = 19510;
	private static final int MAGICAL_BELIKA = 19511;
	private static final int DISCIPLINED_TANYA = 19513;
	private static final int MAGICAL_SCARLETT = 19514;
	private static final int BERSERK_TANYA = 23379;
	private static final int BERSERK_SCARLETT = 23380;
	// Other
	private static final double GROUP_4_SPAWN_CHANCE = 25; // TODO need check this parameters
	
	private boolean _lastMagicAttack = false;
	
	private QuarryRebel()
	{
		addKillId(FIRE_SLAVE_BRIDGET, FLOX_GOLEM, EDAN);
		addAttackId(FIRE_SLAVE_BRIDGET, FLOX_GOLEM, EDAN);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("SPAWN"))
		{
			switch (npc.getId())
			{
				case FIRE_SLAVE_BRIDGET:
				{
					spawnNextMob(_lastMagicAttack ? MAGICAL_DEATHMOZ : DISCIPLINED_DEATHMOZ, player, npc.getLocation());
					npc.deleteMe();
					break;
				}
				case FLOX_GOLEM:
				{
					spawnNextMob(_lastMagicAttack ? MAGICAL_FLOXIS : DISCIPLINED_FLOXIS, player, npc.getLocation());
					npc.deleteMe();
					break;
				}
				case EDAN:
				{
					spawnNextMob(_lastMagicAttack ? MAGICAL_BELIKA : DISCIPLINED_BELIKA, player, npc.getLocation());
					npc.deleteMe();
					break;
				}
				case DISCIPLINED_DEATHMOZ:
				case DISCIPLINED_FLOXIS:
				case DISCIPLINED_BELIKA:
				{
					spawnNextMob(DISCIPLINED_TANYA, player, npc.getLocation());
					npc.deleteMe();
					break;
				}
				case MAGICAL_DEATHMOZ:
				case MAGICAL_FLOXIS:
				case MAGICAL_BELIKA:
				{
					spawnNextMob(MAGICAL_SCARLETT, player, npc.getLocation());
					npc.deleteMe();
					break;
				}
				case DISCIPLINED_TANYA:
				{
					if (getRandom(100) < GROUP_4_SPAWN_CHANCE)
					{
						spawnNextMob(BERSERK_TANYA, player, npc.getLocation());
						npc.deleteMe();
					}
					break;
				}
				case MAGICAL_SCARLETT:
				{
					if (getRandom(100) < GROUP_4_SPAWN_CHANCE)
					{
						spawnNextMob(BERSERK_SCARLETT, player, npc.getLocation());
						npc.deleteMe();
					}
					break;
				}
			}
		}
		return event;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		if ((skill != null) && skill.isBad())
		{
			_lastMagicAttack = true;
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		startQuestTimer("SPAWN", 500, npc, killer);
		return super.onKill(npc, killer, isSummon);
	}
	
	private static void spawnNextMob(int npcId, L2Character killer, Location loc)
	{
		final L2Npc npc = addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), killer.getHeading() + 32500, false, 300000);
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, 1000);
	}
	
	public static void main(String[] args)
	{
		new QuarryRebel();
	}
}
