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
package instances.KartiasLabyrinth;

import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.FriendlyNpcInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.impl.character.OnCreatureAttacked;
import com.l2jmobius.gameserver.model.events.impl.character.OnCreatureDeath;
import com.l2jmobius.gameserver.model.events.impl.instance.OnInstanceStatusChange;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.model.skills.SkillCaster;
import com.l2jmobius.gameserver.network.NpcStringId;

import ai.AbstractNpcAI;

/**
 * Kartia Helper Barton AI. Tyrr Warrior
 * @author flanagak
 */
public final class KartiaHelperBarton extends AbstractNpcAI
{
	// NPCs
	private static final int[] KARTIA_BARTON =
	{
		33611, // Barton (Kartia 85)
		33622, // Barton (Kartia 90)
		33633, // Barton (Kartia 95)
	};
	private static final int[] KARTIA_ADOLPH =
	{
		33609, // Adolph (Kartia 85)
		33620, // Adolph (Kartia 90)
		33631, // Adolph (Kartia 95)
	};
	private static final int[] KARTIA_FRIENDS =
	{
		33617, // Elise (Kartia 85)
		33628, // Elise (Kartia 90)
		33639, // Elise (Kartia 95)
		33609, // Adolph (Kartia 85)
		33620, // Adolph (Kartia 90)
		33631, // Adolph (Kartia 95)
		33611, // Barton (Kartia 85)
		33622, // Barton (Kartia 90)
		33633, // Barton (Kartia 95)
		33615, // Eliyah (Kartia 85)
		33626, // Eliyah (Kartia 90)
		33637, // Eliyah (Kartia 95)
		33613, // Hayuk (Kartia 85)
		33624, // Hayuk (Kartia 90)
		33635, // Hayuk (Kartia 95)
		33618, // Eliyah's Guardian Spirit (Kartia 85)
		33629, // Eliyah's Guardian Spirit (Kartia 90)
		33640, // Eliyah's Guardian Spirit (Kartia 95)
	};
	// Misc
	private static final int[] KARTIA_SOLO_INSTANCES =
	{
		205, // Solo 85
		206, // Solo 90
		207, // Solo 95
	};
	
	private KartiaHelperBarton()
	{
		setCreatureKillId(this::onCreatureKill, KARTIA_BARTON);
		setCreatureAttackedId(this::onCreatureAttacked, KARTIA_BARTON);
		addSeeCreatureId(KARTIA_BARTON);
		setInstanceStatusChangeId(this::onInstanceStatusChange, KARTIA_SOLO_INSTANCES);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, L2Npc npc, L2PcInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		if ((instance != null) && event.equals("CHECK_ACTION"))
		{
			final FriendlyNpcInstance adolph = npc.getVariables().getObject("ADOLPH_OBJECT", FriendlyNpcInstance.class);
			if (adolph != null)
			{
				final double distance = npc.calculateDistance2D(adolph);
				if (distance > 300)
				{
					final Location loc = new Location(adolph.getX(), adolph.getY(), adolph.getZ() + 50);
					final Location randLoc = new Location(loc.getX() + getRandom(-100, 100), loc.getY() + getRandom(-100, 100), loc.getZ());
					if (distance > 600)
					{
						npc.teleToLocation(loc);
					}
					else
					{
						npc.setRunning();
					}
					addMoveToDesire(npc, randLoc, 23);
				}
				else if (!npc.isInCombat() || (npc.getTarget() == null))
				{
					final L2Character monster = (L2Character) adolph.getTarget();
					if ((monster != null) && adolph.isInCombat() && !CommonUtil.contains(KARTIA_FRIENDS, monster.getId()))
					{
						addAttackDesire(npc, monster);
					}
				}
			}
		}
		else if ((instance != null) && event.equals("USE_SKILL"))
		{
			if (npc.isInCombat() || npc.isAttackingNow() || (npc.getTarget() != null))
			{
				if ((npc.getCurrentMpPercent() > 25) && !CommonUtil.contains(KARTIA_FRIENDS, npc.getTargetId()))
				{
					useRandomSkill(npc);
				}
			}
		}
	}
	
	public void onInstanceStatusChange(OnInstanceStatusChange event)
	{
		final Instance instance = event.getWorld();
		final int status = event.getStatus();
		if (status == 1)
		{
			instance.getAliveNpcs(KARTIA_BARTON).forEach(barton -> getTimers().addRepeatingTimer("CHECK_ACTION", 3000, barton, null));
			instance.getAliveNpcs(KARTIA_BARTON).forEach(barton -> getTimers().addRepeatingTimer("USE_SKILL", 6000, barton, null));
		}
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (creature.isPlayer())
		{
			npc.getVariables().set("PLAYER_OBJECT", creature.getActingPlayer());
		}
		else if (CommonUtil.contains(KARTIA_ADOLPH, creature.getId()))
		{
			npc.getVariables().set("ADOLPH_OBJECT", creature);
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public void useRandomSkill(L2Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		final L2Npc target = (L2Npc) npc.getTarget();
		if ((instance != null) && !npc.isCastingNow() && (target != null) && (!CommonUtil.contains(KARTIA_FRIENDS, target.getId())))
		{
			final StatsSet instParams = instance.getTemplateParameters();
			final SkillHolder skill_01 = instParams.getSkillHolder("bartonInfinity");
			final SkillHolder skill_02 = instParams.getSkillHolder("bartonBerserker");
			final SkillHolder skill_03 = instParams.getSkillHolder("bartonHurricane");
			final SkillHolder skill_04 = instParams.getSkillHolder("bartonPowerBomber");
			final SkillHolder skill_05 = instParams.getSkillHolder("bartonSonicStar");
			final int numberOfActiveSkills = 5;
			final int randomSkill = getRandom(numberOfActiveSkills + 1);
			
			switch (randomSkill)
			{
				case 0:
				case 1:
				{
					if ((skill_01 != null) && SkillCaster.checkUseConditions(npc, skill_01.getSkill()))
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.I_WILL_SHOW_YOU_THE_JUSTICE_OF_ADEN);
						npc.doCast(skill_01.getSkill(), null, true, false);
					}
					break;
				}
				case 2:
				{
					if ((skill_02 != null) && SkillCaster.checkUseConditions(npc, skill_02.getSkill()))
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.DIE_3);
						npc.doCast(skill_02.getSkill(), null, true, false);
					}
					break;
				}
				case 3:
				{
					if ((skill_03 != null) && SkillCaster.checkUseConditions(npc, skill_03.getSkill()))
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.FOR_THE_GODDESS);
						npc.doCast(skill_03.getSkill(), null, true, false);
					}
					break;
				}
				case 4:
				{
					if ((skill_04 != null) && SkillCaster.checkUseConditions(npc, skill_04.getSkill()))
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_WILL_BE_DESTROYED);
						npc.doCast(skill_04.getSkill(), null, true, false);
					}
					break;
				}
				case 5:
				{
					if ((skill_05 != null) && SkillCaster.checkUseConditions(npc, skill_05.getSkill()))
					{
						npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOU_WILL_DIE);
						npc.doCast(skill_05.getSkill(), null, true, false);
					}
					break;
				}
			}
		}
	}
	
	public void onCreatureAttacked(OnCreatureAttacked event)
	{
		final L2Npc npc = (L2Npc) event.getTarget();
		if (npc != null)
		{
			final Instance instance = npc.getInstanceWorld();
			if ((instance != null) && !npc.isInCombat() && !event.getAttacker().isPlayable() && !CommonUtil.contains(KARTIA_FRIENDS, event.getAttacker().getId()))
			{
				npc.setTarget(event.getAttacker());
				addAttackDesire(npc, (L2Character) npc.getTarget());
			}
		}
	}
	
	public void onCreatureKill(OnCreatureDeath event)
	{
		final L2Npc npc = (L2Npc) event.getTarget();
		final Instance world = npc.getInstanceWorld();
		if (world != null)
		{
			getTimers().cancelTimersOf(npc);
			npc.doDie(event.getAttacker());
		}
	}
	
	public static void main(String[] args)
	{
		new KartiaHelperBarton();
	}
}