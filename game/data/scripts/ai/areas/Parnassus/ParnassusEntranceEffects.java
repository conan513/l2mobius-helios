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
package ai.areas.Parnassus;

import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;

import ai.AbstractNpcAI;

/**
 * @author Gigi
 */
public class ParnassusEntranceEffects extends AbstractNpcAI
{
	// NPCs
	private static final int PRISON_ENTRACE = 33523;
	private static final int CAVERNS_ENTRACE = 33522;
	// Misc
	private static final int PRISON_ENTRACE_TRIGGER_1 = 24230010;
	private static final int PRISON_ENTRACE_TRIGGER_2 = 24230012;
	private static final int CAVERNS_ENTRACE_TRIGGER_1 = 24230018;
	private static final int CAVERNS_ENTRACE_TRIGGER_2 = 24230020;
	
	private ParnassusEntranceEffects()
	{
		addSeeCreatureId(PRISON_ENTRACE);
		addSeeCreatureId(CAVERNS_ENTRACE);
	}
	
	@Override
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		if (creature != null)
		{
			if (npc.getId() == PRISON_ENTRACE)
			{
				creature.sendPacket(new OnEventTrigger(PRISON_ENTRACE_TRIGGER_1, true));
				creature.sendPacket(new OnEventTrigger(PRISON_ENTRACE_TRIGGER_2, true));
			}
			else
			{
				creature.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_1, true));
				creature.sendPacket(new OnEventTrigger(CAVERNS_ENTRACE_TRIGGER_2, true));
			}
		}
		return super.onSeeCreature(npc, creature, isSummon);
	}
	
	public static void main(String[] args)
	{
		new ParnassusEntranceEffects();
	}
}