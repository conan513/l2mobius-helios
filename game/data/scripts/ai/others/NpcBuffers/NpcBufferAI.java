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
package ai.others.NpcBuffers;

import java.util.logging.Logger;

import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.skills.BuffInfo;
import com.l2jmobius.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
class NpcBufferAI implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(NpcBufferAI.class.getName());
	private final L2Npc _npc;
	private final NpcBufferSkillData _skillData;
	
	protected NpcBufferAI(L2Npc npc, NpcBufferSkillData skill)
	{
		_npc = npc;
		_skillData = skill;
	}
	
	private Skill getSkill(L2PcInstance player)
	{
		if (_skillData.getScaleToLevel() < 1)
		{
			return _skillData.getSkill();
		}
		
		final BuffInfo currentBuff = player.getEffectList().getBuffInfoBySkillId(_skillData.getSkill().getId());
		if (currentBuff != null)
		{
			int level = currentBuff.getSkill().getLevel();
			if (_skillData.getScaleToLevel() > level)
			{
				level++;
			}
			
			final Skill skill = SkillData.getInstance().getSkill(_skillData.getSkill().getId(), level);
			if (skill == null)
			{
				LOGGER.warning("Requested non existing skill level: " + level + " for id: " + _skillData.getSkill().getId());
			}
			return skill;
		}
		
		return _skillData.getSkill();
	}
	
	@Override
	public void run()
	{
		if ((_npc == null) || !_npc.isSpawned() || _npc.isDecayed() || _npc.isDead() || (_skillData == null) || (_skillData.getSkill() == null))
		{
			return;
		}
		
		if ((_npc.getSummoner() == null) || !_npc.getSummoner().isPlayer())
		{
			return;
		}
		
		final L2PcInstance player = _npc.getSummoner().getActingPlayer();
		
		final Skill skill = getSkill(player);
		if (skill == null)
		{
			return;
		}
		
		_npc.doCast(skill);
		
		ThreadPool.schedule(this, skill.getReuseDelay());
	}
}