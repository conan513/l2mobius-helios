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
package events;

import java.util.HashMap;
import java.util.Map;

import com.l2jmobius.gameserver.instancemanager.EventShrineManager;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;

/**
 * @author hlwrave, Mobius
 * @Add in event config.xml enableShrines="true" after event name to enable them.
 */
public final class EventShrines extends Quest
{
	private static final Map<Integer, Integer> ZONE_TRIGGERS = new HashMap<>();
	static
	{
		ZONE_TRIGGERS.put(11030, 23206292); // Hunter
		ZONE_TRIGGERS.put(11031, 24186292); // Aden
		ZONE_TRIGGERS.put(11032, 24166292); // Goddard
		ZONE_TRIGGERS.put(11035, 22136292); // Shuttgard
		ZONE_TRIGGERS.put(11028, 20226292); // Dion
		ZONE_TRIGGERS.put(11029, 22196292); // Oren
		ZONE_TRIGGERS.put(11020, 22226292); // Giran
		ZONE_TRIGGERS.put(11027, 19216292); // Gludio
		ZONE_TRIGGERS.put(11034, 23246292); // Heine
		ZONE_TRIGGERS.put(11025, 17226292); // Gluddin
		ZONE_TRIGGERS.put(11033, 21166292); // Rune
		ZONE_TRIGGERS.put(11042, 17256292); // Faeron
		ZONE_TRIGGERS.put(11043, 26206292); // Arcan
		ZONE_TRIGGERS.put(11022, 16256292); // Talking Island
	}
	
	public EventShrines()
	{
		super(-1);
		addEnterZoneId(ZONE_TRIGGERS.keySet());
	}
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character.isPlayer() && EventShrineManager.getInstance().areShrinesEnabled())
		{
			character.sendPacket(new OnEventTrigger(ZONE_TRIGGERS.get(zone.getId()), true));
		}
		return super.onEnterZone(character, zone);
	}
	
	public static void main(String[] args)
	{
		new EventShrines();
	}
}