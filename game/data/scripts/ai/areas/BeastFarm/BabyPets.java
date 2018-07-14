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
package ai.areas.BeastFarm;

import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.L2Summon;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.skills.SkillCaster;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import ai.AbstractNpcAI;

/**
 * Baby Pets AI.
 * @author St3eT
 */
public final class BabyPets extends AbstractNpcAI
{
	// NPCs
	private static final int[] BABY_PETS =
	{
		12780, // Baby Buffalo
		12781, // Baby Kookaburra
		12782, // Baby Cougar
	};
	// Skills
	private static final int HEAL_1 = 4717; // Heal Trick
	private static final int HEAL_2 = 4718; // Greater Heal Trick
	
	private BabyPets()
	{
		addSummonSpawnId(BABY_PETS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("HEAL") && (player != null))
		{
			final L2Summon summon = player.getPet();
			
			if (summon != null)
			{
				if (getRandom(100) <= 25)
				{
					castHeal(summon, new SkillHolder(HEAL_1, getHealLv(summon)), 80);
				}
				
				if (getRandom(100) <= 75)
				{
					castHeal(summon, new SkillHolder(HEAL_2, getHealLv(summon)), 15);
				}
			}
			else
			{
				cancelQuestTimer("HEAL", null, player);
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL)
	public void OnPlayerLogout(OnPlayerLogout event)
	{
		cancelQuestTimer("HEAL", null, event.getActiveChar());
	}
	
	@Override
	public void onSummonSpawn(L2Summon summon)
	{
		startQuestTimer("HEAL", 5000, null, summon.getOwner(), true);
	}
	
	private int getHealLv(L2Summon summon)
	{
		final int summonLv = summon.getLevel();
		return CommonUtil.constrain(summonLv < 70 ? (summonLv / 10) : (7 + ((summonLv - 70) / 5)), 1, 12);
	}
	
	private void castHeal(L2Summon summon, SkillHolder skill, int maxHpPer)
	{
		final boolean previousFollowStatus = summon.getFollowStatus();
		final L2PcInstance owner = summon.getOwner();
		
		if (!owner.isDead() && (((owner.getCurrentHp() / owner.getMaxHp()) * 100) < maxHpPer) && !summon.isHungry() && SkillCaster.checkUseConditions(summon, skill.getSkill()))
		{
			summon.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill.getSkill(), owner);
			summon.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_USES_S1).addSkillName(skill.getSkill()));
			
			if (previousFollowStatus != summon.getFollowStatus())
			{
				summon.setFollowStatus(previousFollowStatus);
			}
		}
	}
	
	public static void main(String[] args)
	{
		new BabyPets();
	}
}