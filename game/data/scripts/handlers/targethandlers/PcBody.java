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
package handlers.targethandlers;

import com.l2jmobius.gameserver.geoengine.GeoEngine;
import com.l2jmobius.gameserver.handler.ITargetTypeHandler;
import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Playable;
import com.l2jmobius.gameserver.model.effects.L2EffectType;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.skills.targets.TargetType;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.network.SystemMessageId;

/**
 * Target dead player or pet.
 * @author Nik
 */
public class PcBody implements ITargetTypeHandler
{
	
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.PC_BODY;
	}
	
	@Override
	public L2Object getTarget(L2Character activeChar, L2Object selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage)
	{
		if (selectedTarget == null)
		{
			return null;
		}
		
		if (!selectedTarget.isCharacter())
		{
			return null;
		}
		
		if (!selectedTarget.isPlayer() && !selectedTarget.isPet())
		{
			if (sendMessage)
			{
				activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
			}
			
			return null;
		}
		
		final L2Playable target = (L2Playable) selectedTarget;
		
		if (target.isDead())
		{
			if (skill.hasEffectType(L2EffectType.RESURRECTION))
			{
				if (activeChar.isResurrectionBlocked() || target.isResurrectionBlocked())
				{
					if (sendMessage)
					{
						activeChar.sendPacket(SystemMessageId.REJECT_RESURRECTION); // Reject resurrection
						target.sendPacket(SystemMessageId.REJECT_RESURRECTION); // Reject resurrection
					}
					
					return null;
				}
				
				// check target is not in a active siege zone
				if (target.isPlayer() && target.isInsideZone(ZoneId.SIEGE) && !target.getActingPlayer().isInSiege())
				{
					if (sendMessage)
					{
						activeChar.sendPacket(SystemMessageId.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
					}
					
					return null;
				}
			}
			
			// Check for cast range if character cannot move. TODO: char will start follow until within castrange, but if his moving is blocked by geodata, this msg will be sent.
			if (dontMove)
			{
				if (activeChar.calculateDistance2D(target) > skill.getCastRange())
				{
					if (sendMessage)
					{
						activeChar.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED);
					}
					
					return null;
				}
			}
			
			// Geodata check when character is within range.
			if (!GeoEngine.getInstance().canSeeTarget(activeChar, target))
			{
				if (sendMessage)
				{
					activeChar.sendPacket(SystemMessageId.CANNOT_SEE_TARGET);
				}
				
				return null;
			}
			
			return target;
		}
		
		// If target is not dead or not player/pet it will not even bother to walk within range, unlike Enemy target type.
		if (sendMessage)
		{
			activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
		}
		
		return null;
	}
}
