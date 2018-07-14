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
package quests;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.HtmlActionScope;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.instancemanager.CastleManager;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerBypass;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.PlaySound;
import com.l2jmobius.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowQuestionMark;
import com.l2jmobius.gameserver.taskmanager.AttackStanceTaskManager;

/**
 * Abstract class for quests "Letters from the Queen" and "Kekropus' Letter"
 * @author malyelfik
 */
public abstract class LetterQuest extends Quest
{
	private int _startSOE;
	private Location _startTeleport;
	private NpcStringId _startMessage;
	private String _startQuestSound;
	
	public LetterQuest(int questId)
	{
		super(questId);
	}
	
	/**
	 * Sets additional conditions that must be met to show question mark or start quest.
	 * @param player player trying start quest
	 * @return {@code true} when additional conditions are met, otherwise {@code false}
	 */
	public boolean canShowTutorialMark(L2PcInstance player)
	{
		return true;
	}
	
	/**
	 * Sets start quest sound.<br>
	 * This sound will be played when player clicks on the tutorial question mark.
	 * @param sound name of sound
	 */
	public final void setStartQuestSound(String sound)
	{
		_startQuestSound = sound;
	}
	
	/**
	 * Sets quest level restrictions.
	 * @param min minimum player's level to start quest
	 * @param max maximum player's level to start quest
	 */
	public final void setLevel(int min, int max)
	{
		addCondLevel(min, max, "");
	}
	
	/**
	 * Sets start location of quest.<br>
	 * When player starts quest, he will receive teleport scroll with id {@code itemId}.<br>
	 * When tutorial window is displayed, he can also teleport to location {@code loc} using HTML bypass.
	 * @param itemId id of item which player gets on quest start
	 * @param loc place where player will be teleported
	 */
	public final void setStartLocation(int itemId, Location loc)
	{
		_startSOE = itemId;
		_startTeleport = loc;
	}
	
	/**
	 * Sets if quest is only for Ertheia characters or not.
	 * @param val {@code true} means {@code Race.ERTHEIA}, {@code false} means other
	 */
	public final void setIsErtheiaQuest(boolean val)
	{
		if (val)
		{
			addCondRace(Race.ERTHEIA, "");
			_startMessage = NpcStringId.QUEEN_NAVARI_HAS_SENT_A_LETTER_NCLICK_THE_QUESTION_MARK_ICON_TO_READ;
		}
		else
		{
			addCondNotRace(Race.ERTHEIA, "");
			_startMessage = NpcStringId.KEKROPUS_LETTER_HAS_ARRIVED_NCLICK_THE_QUESTION_MARK_ICON_TO_READ;
		}
	}
	
	public final void setStartMessage(NpcStringId msg)
	{
		_startMessage = msg;
	}
	
	/**
	 * Gets teleport command associated with current quest.
	 * @return command in form Q<i>questId</i>_teleport (<i>questId</i> is replaced with original quest id)
	 */
	private String getTeleportCommand()
	{
		return "Q" + getId() + "_teleport";
	}
	
	@Override
	public boolean canStartQuest(L2PcInstance player)
	{
		return canShowTutorialMark(player) && super.canStartQuest(player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final L2PcInstance player = event.getActiveChar();
		if ((event.getMarkId() == getId()) && canStartQuest(player))
		{
			final String html = getHtm(player, "popup.html").replace("%teleport%", getTeleportCommand());
			final QuestState st = getQuestState(player, true);
			st.startQuest();
			
			player.sendPacket(new PlaySound(3, _startQuestSound, 0, 0, 0, 0, 0));
			player.sendPacket(new TutorialShowHtml(html));
			giveItems(player, _startSOE, 1);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_BYPASS)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerBypass(OnPlayerBypass event)
	{
		final L2PcInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if (event.getCommand().equals(getTeleportCommand()))
		{
			if ((st != null) && st.isCond(1) && hasQuestItems(player, _startSOE))
			{
				if (CastleManager.getInstance().getCastles().stream().anyMatch(c -> c.getSiege().isInProgress()))
				{
					showOnScreenMsg(player, NpcStringId.YOU_MAY_NOT_TELEPORT_IN_MIDDLE_OF_A_SIEGE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isInParty())
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_IN_PARTY_STATUS, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isInInstance())
				{
					showOnScreenMsg(player, NpcStringId.YOU_MAY_NOT_TELEPORT_WHILE_USING_INSTANCE_ZONE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(player))
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_IN_COMBAT, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isTransformed())
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_WHILE_IN_A_TRANSFORMED_STATE, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else if (player.isDead())
				{
					showOnScreenMsg(player, NpcStringId.YOU_CANNOT_TELEPORT_WHILE_YOU_ARE_DEAD, ExShowScreenMessage.TOP_CENTER, 5000);
				}
				else
				{
					player.teleToLocation(_startTeleport);
					takeItems(player, _startSOE, -1);
				}
			}
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			player.clearHtmlActions(HtmlActionScope.TUTORIAL_HTML);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final L2PcInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if ((st == null) && (event.getOldLevel() < event.getNewLevel()) && canStartQuest(player))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
			showOnScreenMsg(player, _startMessage, ExShowScreenMessage.TOP_CENTER, 10000);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final L2PcInstance player = event.getActiveChar();
		final QuestState st = getQuestState(player, false);
		
		if ((st == null) && canStartQuest(player))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
			showOnScreenMsg(player, _startMessage, ExShowScreenMessage.TOP_CENTER, 10000);
		}
	}
	
	@Override
	public void onQuestAborted(L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		
		st.startQuest();
		player.sendPacket(SystemMessageId.THIS_QUEST_CANNOT_BE_DELETED);
	}
}