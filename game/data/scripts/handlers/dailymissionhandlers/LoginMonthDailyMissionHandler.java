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
package handlers.dailymissionhandlers;

import com.l2jmobius.gameserver.enums.DailyMissionStatus;
import com.l2jmobius.gameserver.handler.AbstractDailyMissionHandler;
import com.l2jmobius.gameserver.model.DailyMissionDataHolder;
import com.l2jmobius.gameserver.model.DailyMissionPlayerEntry;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.Containers;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jmobius.gameserver.model.events.listeners.ConsumerEventListener;

/**
 * @author Iris, Mobius
 */
public class LoginMonthDailyMissionHandler extends AbstractDailyMissionHandler
{
	public LoginMonthDailyMissionHandler(DailyMissionDataHolder holder)
	{
		super(holder);
	}
	
	@Override
	public boolean isAvailable(L2PcInstance player)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(player.getObjectId(), false);
		if ((entry != null) && (entry.getStatus() == DailyMissionStatus.AVAILABLE))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void init()
	{
		Containers.Global().addListener(new ConsumerEventListener(this, EventType.ON_PLAYER_LOGIN, (OnPlayerLogin event) -> onPlayerLogin(event), this));
	}
	
	@Override
	public void reset()
	{
		// Monthly rewards do not reset daily.
	}
	
	private void onPlayerLogin(OnPlayerLogin event)
	{
		final DailyMissionPlayerEntry entry = getPlayerEntry(event.getActiveChar().getObjectId(), true);
		final long lastCompleted = entry.getLastCompleted();
		if (lastCompleted == 0) // Initial entry.
		{
			entry.setLastCompleted(System.currentTimeMillis());
		}
		else if ((System.currentTimeMillis() - lastCompleted) > 2506000000L) // 2506000000L (29 day) delay.
		{
			entry.setProgress(1);
			entry.setStatus(DailyMissionStatus.AVAILABLE);
		}
		else
		{
			entry.setProgress(0);
			entry.setStatus(DailyMissionStatus.NOT_AVAILABLE);
		}
		storePlayerEntry(entry);
	}
}
