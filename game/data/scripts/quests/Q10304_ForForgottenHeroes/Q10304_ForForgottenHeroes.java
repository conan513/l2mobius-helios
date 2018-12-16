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
package quests.Q10304_ForForgottenHeroes;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExQuestNpcLogList;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowQuestionMark;

import quests.Q10302_UnsettlingShadowAndRumors.Q10302_UnsettlingShadowAndRumors;

/**
 * For the Forgotten Heroes (10304)
 * @URL https://l2wiki.com/For_the_Forgotten_Heroes
 * @author Gigi
 */
public class Q10304_ForForgottenHeroes extends Quest
{
	// NPC
	private static final int ISHAEL = 32894;
	// Monsters
	private static final int YUI = 25837;
	private static final int KINEN = 25840;
	private static final int KONYAR = 25845;
	private static final int LESYINDA = 25841;
	private static final int MAKSHU = 25838;
	private static final int HORNAFI = 25839;
	private static final int YONTYMAK = 25846;
	private static final int RON = 25825;
	// Items
	private static final int EWR = 17526; // Scroll: Enchant Weapon (R-grade)
	private static final int EAR = 17527; // Scroll: Enchant Armor (R-grade)
	private static final int COKES = 36563; // Synthetic Cokes
	private static final int POUCH = 34861; // Ingredient and Hardener Pouch (R-grade)
	private static final int OLD_ROLL_OF_PAPER = 34033;
	// Misc
	private static final int MIN_LEVEL = 90;
	
	public Q10304_ForForgottenHeroes()
	{
		super(10304);
		addItemTalkId(OLD_ROLL_OF_PAPER);
		addTalkId(ISHAEL);
		addKillId(YUI, KINEN, KONYAR, LESYINDA, MAKSHU, HORNAFI, YONTYMAK, RON);
		addCondCompletedQuest(Q10302_UnsettlingShadowAndRumors.class.getSimpleName(), "32894-02.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return getNoQuestMsg(player);
		}
		switch (event)
		{
			case "32894-02.htm":
			case "32894-03.htm":
			case "32894-08.html":
			{
				htmltext = event;
				break;
			}
			case "32894-06.htm":
			{
				qs.setCond(2);
				htmltext = event;
				break;
			}
			case "reward1":
			{
				giveItems(player, COKES, 68);
				addExpAndSp(player, 15197798, 3647);
				giveAdena(player, 47085998, true);
				qs.exitQuest(false, true);
				htmltext = "32894-09.html";
				break;
			}
			case "reward2":
			{
				giveItems(player, EWR, 1);
				giveItems(player, EAR, 1);
				addExpAndSp(player, 15197798, 3647);
				giveAdena(player, 47085998, true);
				qs.exitQuest(false, true);
				htmltext = "32894-09.html";
				break;
			}
			case "reward3":
			{
				giveItems(player, POUCH, 4);
				addExpAndSp(player, 15197798, 3647);
				giveAdena(player, 47085998, true);
				qs.exitQuest(false, true);
				htmltext = "32894-09.html";
				break;
			}
		}
		if (event.equalsIgnoreCase("condition"))
		{
			if ((player.getParty() == null))
			{
				return "32894-05.html";
			}
			else if ((player.getParty() != null) && (player.getParty().getLeader() != player))
			{
				return "32894-05.html";
			}
			else if ((player.getParty() != null) && (player.getParty().getLeader() == player) && (player.getParty().getMemberCount() < 7))
			{
				return "32894-05.html";
			}
			return "32894-04.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCond(1))
		{
			htmltext = "32894-01.htm";
		}
		else if ((qs.getCond() > 1) && (qs.getCond() < 9))
		{
			htmltext = "32894-03.htm";
		}
		else if (qs.isCond(9))
		{
			htmltext = "32894-07.html";
		}
		else if (qs.isCompleted())
		{
			htmltext = "Complete.html";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		executeForEachPlayer(player, npc, isSummon, true, false);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs != null)
		{
			int cond = qs.getCond();
			int npcId = npc.getId();
			if (player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))
			{
				if ((npcId == YUI) && (cond == 2))
				{
					qs.setCond(3, true);
				}
				else if ((npcId == KINEN) && (cond == 3))
				{
					qs.setCond(4, true);
				}
				else if ((npcId == KONYAR) && (cond == 4))
				{
					qs.setCond(5, true);
				}
				else if ((npcId == LESYINDA) && (cond == 5))
				{
					qs.setCond(6, true);
				}
				else if ((npcId == MAKSHU) && (cond == 6))
				{
					int kills = qs.getInt(Integer.toString(MAKSHU));
					if (kills < 1)
					{
						kills++;
						qs.set(Integer.toString(MAKSHU), kills);
					}
				}
				else if ((npcId == HORNAFI) && (cond == 6))
				{
					int kills = qs.getInt(Integer.toString(HORNAFI));
					if (kills < 1)
					{
						kills++;
						qs.set(Integer.toString(HORNAFI), kills);
					}
				}
				else if ((npcId == YONTYMAK) && (cond == 7))
				{
					qs.setCond(8);
				}
				else if ((npcId == RON) && (cond == 8))
				{
					qs.setCond(8);
				}
			}
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(MAKSHU, qs.getInt(Integer.toString(MAKSHU)));
			log.addNpc(HORNAFI, qs.getInt(Integer.toString(HORNAFI)));
			qs.getPlayer().sendPacket(log);
			if ((qs.getInt(Integer.toString(MAKSHU)) >= 1) && (qs.getInt(Integer.toString(HORNAFI)) >= 1))
			{
				qs.setCond(7, true);
			}
		}
	}
	
	@Override
	public String onItemTalk(L2ItemInstance item, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		final QuestState qs1 = player.getQuestState(Q10302_UnsettlingShadowAndRumors.class.getSimpleName());
		boolean startQuest = false;
		switch (qs.getState())
		{
			case State.CREATED:
			{
				startQuest = true;
				break;
			}
		}
		if (startQuest)
		{
			if ((player.getLevel() >= MIN_LEVEL) && qs1.isCompleted())
			{
				qs.startQuest();
				takeItems(player, OLD_ROLL_OF_PAPER, -1);
				htmltext = "";
			}
			else
			{
				htmltext = "32894-00.htm";
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final L2PcInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) && (event.getOldLevel() < event.getNewLevel()) && canStartQuest(player) && (player.getLevel() >= MIN_LEVEL))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final L2PcInstance player = event.getActiveChar();
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) && canStartQuest(player) && (player.getLevel() >= MIN_LEVEL))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		final L2PcInstance player = event.getActiveChar();
		if ((event.getMarkId() == getId()) && canStartQuest(player) && (player.getLevel() >= MIN_LEVEL))
		{
			showOnScreenMsg(player, NpcStringId.YOU_CAN_USE_THE_OLD_ROLL_OF_PAPER_TO_BEGIN_THE_QUEST, ExShowScreenMessage.TOP_CENTER, 8000);
			if (!hasQuestItems(player, OLD_ROLL_OF_PAPER))
			{
				giveItems(player, OLD_ROLL_OF_PAPER, 1);
			}
		}
	}
}