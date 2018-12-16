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
package handlers.effecthandlers;

import java.util.HashMap;
import java.util.Map;

import com.l2jmobius.gameserver.ai.CtrlEvent;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.geoengine.GeoEngine;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassId;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.effects.L2EffectType;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.stats.Formulas;
import com.l2jmobius.gameserver.network.serverpackets.ExAlterSkillRequest;
import com.l2jmobius.gameserver.network.serverpackets.FlyToLocation;
import com.l2jmobius.gameserver.network.serverpackets.FlyToLocation.FlyType;
import com.l2jmobius.gameserver.network.serverpackets.ValidateLocation;
import com.l2jmobius.gameserver.util.Util;

/**
 * Check if this effect is not counted as being stunned.
 * @author UnAfraid, Mobius
 */
public final class KnockBack extends AbstractEffect
{
	private final int _distance;
	private final int _speed;
	private final int _delay;
	private final int _animationSpeed;
	private final boolean _knockDown;
	private final FlyType _type;
	
	// skill data
	private static final Map<ClassId, Integer> _chainKnockSkills = new HashMap<>(36);
	{
		_chainKnockSkills.put(ClassId.SIGEL_PHOENIX_KNIGHT, 10250); // Heavy Hit
		_chainKnockSkills.put(ClassId.SIGEL_HELL_KNIGHT, 10250); // Heavy Hit
		_chainKnockSkills.put(ClassId.SIGEL_EVA_TEMPLAR, 10250); // Heavy Hit
		_chainKnockSkills.put(ClassId.SIGEL_SHILLIEN_TEMPLAR, 10250); // Heavy Hit
		_chainKnockSkills.put(ClassId.TYRR_DUELIST, 10500); // Heavy Hit
		_chainKnockSkills.put(ClassId.TYRR_DREADNOUGHT, 10500); // Heavy Hit
		_chainKnockSkills.put(ClassId.TYRR_TITAN, 10500); // Heavy Hit
		_chainKnockSkills.put(ClassId.TYRR_GRAND_KHAVATARI, 10500); // Heavy Hit
		_chainKnockSkills.put(ClassId.TYRR_MAESTRO, 10500); // Heavy Hit
		_chainKnockSkills.put(ClassId.TYRR_DOOMBRINGER, 10500); // Heavy Hit
		_chainKnockSkills.put(ClassId.OTHELL_ADVENTURER, 10750); // Heavy Hit
		_chainKnockSkills.put(ClassId.OTHELL_WIND_RIDER, 10750); // Heavy Hit
		_chainKnockSkills.put(ClassId.OTHELL_GHOST_HUNTER, 10750); // Heavy Hit
		_chainKnockSkills.put(ClassId.OTHELL_FORTUNE_SEEKER, 10750); // Heavy Hit
		_chainKnockSkills.put(ClassId.YUL_SAGITTARIUS, 11000); // Heavy Hit
		_chainKnockSkills.put(ClassId.YUL_MOONLIGHT_SENTINEL, 11000); // Heavy Hit
		_chainKnockSkills.put(ClassId.YUL_GHOST_SENTINEL, 11000); // Heavy Hit
		_chainKnockSkills.put(ClassId.YUL_TRICKSTER, 11000); // Heavy Hit
		_chainKnockSkills.put(ClassId.FEOH_ARCHMAGE, 11250); // Heavy Hit
		_chainKnockSkills.put(ClassId.FEOH_SOULTAKER, 11250); // Heavy Hit
		_chainKnockSkills.put(ClassId.FEOH_MYSTIC_MUSE, 11250); // Heavy Hit
		_chainKnockSkills.put(ClassId.FEOH_STORM_SCREAMER, 11250); // Heavy Hit
		_chainKnockSkills.put(ClassId.FEOH_SOUL_HOUND, 11250); // Heavy Hit
		_chainKnockSkills.put(ClassId.ISS_HIEROPHANT, 11750); // Heavy Hit
		_chainKnockSkills.put(ClassId.ISS_SWORD_MUSE, 11750); // Heavy Hit
		_chainKnockSkills.put(ClassId.ISS_SPECTRAL_DANCER, 11750); // Heavy Hit
		_chainKnockSkills.put(ClassId.ISS_DOMINATOR, 11750); // Heavy Hit
		_chainKnockSkills.put(ClassId.ISS_DOOMCRYER, 11750); // Heavy Hit
		_chainKnockSkills.put(ClassId.WYNN_ARCANA_LORD, 11500); // Heavy Hit
		_chainKnockSkills.put(ClassId.WYNN_ELEMENTAL_MASTER, 11500); // Heavy Hit
		_chainKnockSkills.put(ClassId.WYNN_SPECTRAL_MASTER, 11500); // Heavy Hit
		_chainKnockSkills.put(ClassId.AEORE_CARDINAL, 12000); // Heavy Hit
		_chainKnockSkills.put(ClassId.AEORE_EVA_SAINT, 12000); // Heavy Hit
		_chainKnockSkills.put(ClassId.AEORE_SHILLIEN_SAINT, 12000); // Heavy Hit
	}
	
	public KnockBack(StatsSet params)
	{
		_distance = params.getInt("distance", 50);
		_speed = params.getInt("speed", 0);
		_delay = params.getInt("delay", 0);
		_animationSpeed = params.getInt("animationSpeed", 0);
		_knockDown = params.getBoolean("knockDown", false);
		_type = params.getEnum("type", FlyType.class, _knockDown ? FlyType.PUSH_DOWN_HORIZONTAL : FlyType.PUSH_HORIZONTAL);
	}
	
	@Override
	public boolean calcSuccess(L2Character effector, L2Character effected, Skill skill)
	{
		return _knockDown || Formulas.calcProbability(100, effector, effected, skill);
	}
	
	@Override
	public boolean isInstant()
	{
		return !_knockDown;
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.KNOCK;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (!_knockDown)
		{
			knockBack(effector, effected);
		}
	}
	
	@Override
	public void continuousInstant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		if (_knockDown)
		{
			knockBack(effector, effected);
		}
	}
	
	@Override
	public void onExit(L2Character effector, L2Character effected, Skill skill)
	{
		if (!effected.isPlayer())
		{
			effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
		}
	}
	
	private void knockBack(L2Character effector, L2Character effected)
	{
		final double radians = Math.toRadians(Util.calculateAngleFrom(effector, effected));
		final int x = (int) (effected.getX() + (_distance * Math.cos(radians)));
		final int y = (int) (effected.getY() + (_distance * Math.sin(radians)));
		final int z = effected.getZ();
		final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(effected.getX(), effected.getY(), effected.getZ(), x, y, z, effected.getInstanceWorld());
		
		effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		effected.broadcastPacket(new FlyToLocation(effected, loc, _type, _speed, _delay, _animationSpeed));
		if (_knockDown)
		{
			effected.setHeading(Util.calculateHeadingFrom(effected, effector));
		}
		effected.setXYZ(loc);
		effected.broadcastPacket(new ValidateLocation(effected));
		effected.revalidateZone(true);
		
		for (L2PcInstance nearbyPlayer : L2World.getInstance().getVisibleObjectsInRange(effected, L2PcInstance.class, 1200))
		{
			if (nearbyPlayer.getRace() == Race.ERTHEIA)
			{
				continue;
			}
			if ((nearbyPlayer.getTarget() == effected) && nearbyPlayer.isInCategory(CategoryType.SIXTH_CLASS_GROUP) && !nearbyPlayer.isAlterSkillActive())
			{
				final int chainSkill = _chainKnockSkills.get(nearbyPlayer.getClassId());
				if (nearbyPlayer.getSkillRemainingReuseTime(chainSkill) == -1)
				{
					nearbyPlayer.sendPacket(new ExAlterSkillRequest(nearbyPlayer, chainSkill, chainSkill, 3));
				}
			}
		}
	}
}
