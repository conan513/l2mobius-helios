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
package quests.Q10375_SuccubusDisciples;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassLevel;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExQuestNpcLogList;
import com.l2jmobius.gameserver.network.serverpackets.NpcSay;
import com.l2jmobius.gameserver.util.Util;

import quests.Q10374_ThatPlaceSuccubus.Q10374_ThatPlaceSuccubus;

/**
 * Succubus Discipless (10375)
 * @URL https://l2wiki.com/Succubus_Disciples
 * @author Gigi
 */
public class Q10375_SuccubusDisciples extends Quest
{
	// NPCs
	private static final int ZENYA = 32140;
	// Items
	private static final ItemHolder ADENA = new ItemHolder(57, 498700);
	// Reward
	private static final int EXP_REWARD = 24782300;
	private static final int SP_REWARD = 5947;
	// Misc
	private static final int MIN_LEVEL = 80;
	// Monsters
	private static final int SUCCUBUS_OF_DEATH = 23191;
	private static final int SUCCUBUS_OF_DARKNESS = 23192;
	private static final int SUCCUBUS_OF_LUNACY = 23197;
	private static final int SUCCUBUS_OF_SILENCE = 23198;
	
	public Q10375_SuccubusDisciples()
	{
		super(10375);
		addStartNpc(ZENYA);
		addTalkId(ZENYA);
		addKillId(SUCCUBUS_OF_DEATH, SUCCUBUS_OF_DARKNESS, SUCCUBUS_OF_SILENCE, SUCCUBUS_OF_LUNACY);
		addCondMinLevel(MIN_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10374_ThatPlaceSuccubus.class.getSimpleName(), "restriction.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		String htmltext = null;
		switch (event)
		{
			case "32140-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32140-06.html":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "32140-04.html":
			{
				qs.setCond(0);
				qs.setCond(3, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
		}
		
		if ((npc.getId() == ZENYA) && (player.getClassId().level() == ClassLevel.THIRD.ordinal()))
		{
			return "noClass.html";
		}
		
		switch (npc.getId())
		{
			case ZENYA:
			{
				if (qs.isCreated())
				{
					htmltext = "32140-01.htm";
				}
				else if (qs.isStarted())
				{
					switch (qs.getCond())
					{
						case 1:
						{
							htmltext = "32140-07.html";
							break;
						}
						case 2:
						{
							htmltext = "32140-03.html";
							break;
						}
						case 4:
						{
							if (qs.isCond(4))
							{
								giveItems(player, ADENA);
								addExpAndSp(player, EXP_REWARD, SP_REWARD);
								npc.broadcastPacket(new NpcSay(npc.getObjectId(), ChatType.NPC_GENERAL, npc.getId(), NpcStringId.YOU_ARE_TRULY_AMAZING_FOR_DEFEATING_THE_SUCCUBUS_DISCIPLES));
								playSound(player, QuestSound.ITEMSOUND_QUEST_FINISH);
								qs.exitQuest(false, true);
								htmltext = "32140-05.html";
							}
							break;
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, qs.getPlayer(), false))
		{
			switch (npc.getId())
			{
				case SUCCUBUS_OF_DEATH:
				{
					int kills = qs.getInt(Integer.toString(SUCCUBUS_OF_DEATH));
					if (kills < 5)
					{
						kills++;
						qs.set(Integer.toString(SUCCUBUS_OF_DEATH), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_OF_DARKNESS:
				{
					int kills = qs.getInt(Integer.toString(SUCCUBUS_OF_DARKNESS));
					if (kills < 5)
					{
						kills++;
						qs.set(Integer.toString(SUCCUBUS_OF_DARKNESS), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(SUCCUBUS_OF_DEATH, qs.getInt(Integer.toString(SUCCUBUS_OF_DEATH)));
			log.addNpc(SUCCUBUS_OF_DARKNESS, qs.getInt(Integer.toString(SUCCUBUS_OF_DARKNESS)));
			qs.getPlayer().sendPacket(log);
			
			if ((qs.getInt(Integer.toString(SUCCUBUS_OF_DEATH)) >= 5) && (qs.getInt(Integer.toString(SUCCUBUS_OF_DARKNESS)) >= 5))
			{
				qs.setCond(2);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
			}
		}
		
		else if ((qs != null) && qs.isCond(3) && Util.checkIfInRange(1500, npc, qs.getPlayer(), false))
		{
			switch (npc.getId())
			{
				case SUCCUBUS_OF_SILENCE:
				{
					int kills = qs.getInt(Integer.toString(SUCCUBUS_OF_SILENCE));
					if (kills < 5)
					{
						kills++;
						qs.set(Integer.toString(SUCCUBUS_OF_SILENCE), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
				case SUCCUBUS_OF_LUNACY:
				{
					int kills = qs.getInt(Integer.toString(SUCCUBUS_OF_LUNACY));
					if (kills < 5)
					{
						kills++;
						qs.set(Integer.toString(SUCCUBUS_OF_LUNACY), kills);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
			final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
			log.addNpc(SUCCUBUS_OF_SILENCE, qs.getInt(Integer.toString(SUCCUBUS_OF_SILENCE)));
			log.addNpc(SUCCUBUS_OF_LUNACY, qs.getInt(Integer.toString(SUCCUBUS_OF_LUNACY)));
			qs.getPlayer().sendPacket(log);
			
			if ((qs.getInt(Integer.toString(SUCCUBUS_OF_SILENCE)) >= 5) && (qs.getInt(Integer.toString(SUCCUBUS_OF_LUNACY)) >= 5))
			{
				qs.setCond(4);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}