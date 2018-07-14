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
package quests.Q10360_CertificationOfFate;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.data.xml.impl.MultisellData;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.base.ClassId;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerPressTutorialMark;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.network.serverpackets.ExShowUsm;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowQuestionMark;
import com.l2jmobius.gameserver.util.Util;

import quests.Q10331_StartOfFate.Q10331_StartOfFate;

/**
 * Certification of Fate (10360)
 * @author St3eT
 */
public final class Q10360_CertificationOfFate extends Quest
{
	// NPCs
	private static final int RAYMOND = 30289;
	private static final int RAINS = 30288;
	private static final int TOBIAS = 30297;
	private static final int DRIKUS = 30505;
	private static final int MENDIO = 30504;
	private static final int GERSHWIN = 32196;
	private static final int ESRANDELL = 30158;
	private static final int ELLENIA = 30155;
	private static final int RENFAD = 33524;
	private static final int JOEL = 33516;
	private static final int SCHUAZEN = 33517;
	private static final int SELON = 33518;
	private static final int REGENERATED_KANILOV = 27459;
	private static final int REGENERATED_POSLOF = 27460;
	private static final int SAKUM = 27453;
	// Items
	private static final int SHINE_STONE = 17587;
	private static final int MAJOR_HEALING_POTION = 1061;
	private static final int SOULSHOT = 1464;
	private static final int SPIRITSHOT = 3949;
	private static final int PAULINA_EQUIPMENT_SET = 46850;
	// Locations
	private static final Location WASTELANDS_TELEPORT = new Location(-24795, 188754, -3960);
	// Misc
	private static final int MIN_LEVEL = 38;
	
	public Q10360_CertificationOfFate()
	{
		super(10360);
		addStartNpc(RAYMOND, RAINS, TOBIAS, DRIKUS, MENDIO, GERSHWIN, ESRANDELL, ELLENIA);
		addTalkId(RAYMOND, RAINS, TOBIAS, DRIKUS, MENDIO, GERSHWIN, ESRANDELL, ELLENIA, RENFAD, JOEL, SCHUAZEN, SELON);
		addFirstTalkId(JOEL, SCHUAZEN);
		addKillId(REGENERATED_KANILOV, REGENERATED_POSLOF, SAKUM);
		registerQuestItems(SHINE_STONE);
		addCondNotRace(Race.ERTHEIA, "");
		addCondMinLevel(MIN_LEVEL, "");
		addCondCompletedQuest(Q10331_StartOfFate.class.getSimpleName(), "restriction.html");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "30155-03.htm":
			case "30158-03.htm":
			case "32196-03.htm":
			case "30504-03.htm":
			case "30505-03.htm":
			case "30297-03.htm":
			case "30288-03.htm":
			case "30289-03.htm":
			case "33524-02.htm":
			case "33516-02.htm":
			case "33517-02.htm":
			case "33518-03.htm":
			case "33518-04.htm":
			case "33518-05.htm":
			{
				htmltext = event;
				break;
			}
			case "30155-04.htm":
			case "30158-04.htm":
			case "32196-04.htm":
			case "30504-04.htm":
			case "30505-04.htm":
			case "30297-04.htm":
			case "30288-04.htm":
			case "30289-04.htm":
			{
				if (!player.isSubClassActive())
				{
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "teleport":
			{
				player.teleToLocation(WASTELANDS_TELEPORT);
				break;
			}
			case "33524-03.htm":
			{
				if (st.isCond(1))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					st.setCond(2);
					htmltext = event;
				}
				break;
			}
			case "33516-03.htm":
			{
				if (st.isCond(3))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					st.setCond(4);
					htmltext = event;
				}
				break;
			}
			case "33517-03.htm":
			{
				if (st.isCond(5))
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					st.setCond(6);
					player.sendPacket(ExShowUsm.SECOND_TRANSFER_QUEST);
					htmltext = event;
				}
				break;
			}
			case "33518-06.htm":
			{
				switch (player.getRace())
				{
					case HUMAN:
					{
						st.setCond(player.isMageClass() ? 9 : 8);
						htmltext = player.isMageClass() ? "33518-06.htm" : "33518-07.htm";
						break;
					}
					case DARK_ELF:
					{
						st.setCond(12);
						htmltext = "33518-08.htm";
						break;
					}
					case ORC:
					{
						st.setCond(13);
						htmltext = "33518-09.htm";
						break;
					}
					case DWARF:
					{
						st.setCond(14);
						htmltext = "33518-10.htm";
						break;
					}
					case KAMAEL:
					{
						st.setCond(15);
						htmltext = "33518-11.htm";
						break;
					}
					case ELF:
					{
						st.setCond(player.isMageClass() ? 11 : 10);
						htmltext = player.isMageClass() ? "33518-12.htm" : "33518-13.htm";
						break;
					}
				}
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				break;
			}
			default:
			{
				if (event.startsWith("class_preview_"))
				{
					htmltext = event;
				}
				else if (event.startsWith("classChange;"))
				{
					if (player.isSubClassActive())
					{
						return null;
					}
					
					final ClassId newClassId = ClassId.getClassId(Integer.parseInt(event.replace("classChange;", "")));
					final ClassId currentClassId = player.getClassId();
					
					if (currentClassId.childOf(newClassId) || (st.getCond() < 8))
					{
						Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to cheat the 2nd class transfer!", Config.DEFAULT_PUNISH);
						return null;
					}
					player.setBaseClass(newClassId);
					player.setClassId(newClassId.getId());
					player.store(false);
					player.broadcastUserInfo();
					player.sendSkillList();
					giveAdena(player, 461880, true);
					giveItems(player, MAJOR_HEALING_POTION, 50);
					giveItems(player, SOULSHOT, 3000);
					giveItems(player, SPIRITSHOT, 3000);
					giveItems(player, PAULINA_EQUIPMENT_SET, 1);
					addExpAndSp(player, 2700000, 648);
					MultisellData.getInstance().separateAndSend(718, player, npc, false);
					st.exitQuest(false, true);
					htmltext = "transfer_complete_" + player.getClassId().toString().toLowerCase() + ".html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState st = getQuestState(player, true);
		
		if ((st == null) || st.isCompleted())
		{
			npc.showChatWindow(player);
			return super.onFirstTalk(npc, player);
		}
		
		if ((npc.getId() == JOEL) && (st.getCond() < 3))
		{
			htmltext = "33516.html";
		}
		else if ((npc.getId() == SCHUAZEN) && (st.getCond() < 5))
		{
			htmltext = "33517.html";
		}
		
		if (htmltext == null)
		{
			npc.showChatWindow(player);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		
		if ((player.getRace() == Race.ERTHEIA) || (player.getLevel() < MIN_LEVEL))
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				switch (npc.getId())
				{
					case RAYMOND:
					case ESRANDELL:
					case RAINS:
					case ELLENIA:
					case TOBIAS:
					case DRIKUS:
					case MENDIO:
					case GERSHWIN:
					{
						htmltext = isRightMaster(npc, player) ? npc.getId() + "-02.htm" : npc.getId() + "-01.html";
						break;
					}
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case RAYMOND:
					case ESRANDELL:
					case RAINS:
					case ELLENIA:
					case TOBIAS:
					case DRIKUS:
					case MENDIO:
					case GERSHWIN:
					{
						if (st.isCond(1) && isRightMaster(npc, player))
						{
							htmltext = npc.getId() + "-05.htm";
						}
						else if ((st.getCond() >= 8) && (st.getCond() <= 15))
						{
							htmltext = isRightMaster(npc, player) ? ("class_select_" + player.getClassId().toString().toLowerCase() + ".html") : (npc.getId() + "-06.html");
						}
						break;
					}
					case RENFAD:
					{
						if (st.isCond(1))
						{
							htmltext = "33524-01.htm";
						}
						else if (st.isCond(2))
						{
							htmltext = "33524-04.htm";
						}
						break;
					}
					case JOEL:
					{
						if (st.isCond(3))
						{
							htmltext = "33516-01.htm";
						}
						else if (st.isCond(4))
						{
							htmltext = "33516-04.htm";
						}
						break;
					}
					case SCHUAZEN:
					{
						if (st.isCond(5))
						{
							htmltext = "33517-01.htm";
						}
						else if (st.isCond(6))
						{
							htmltext = "33517-04.htm";
						}
						break;
					}
					case SELON:
					{
						if (st.isCond(6))
						{
							htmltext = "33518-01.htm";
						}
						else if (st.isCond(7))
						{
							htmltext = "33518-02.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				switch (npc.getId())
				{
					case RAYMOND:
					case ESRANDELL:
					case RAINS:
					case ELLENIA:
					case TOBIAS:
					case DRIKUS:
					case MENDIO:
					case GERSHWIN:
					{
						htmltext = npc.getId() + "-07.html";
						break;
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
		final QuestState st = getQuestState(killer, false);
		
		if ((st != null) && st.isStarted())
		{
			switch (npc.getId())
			{
				case REGENERATED_KANILOV:
				{
					if (st.isCond(2))
					{
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						st.setCond(0);
						st.setCond(3);
					}
					break;
				}
				case REGENERATED_POSLOF:
				{
					if (st.isCond(4))
					{
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						st.setCond(5);
					}
					break;
				}
				case SAKUM:
				{
					if (st.isCond(6))
					{
						giveItems(killer, SHINE_STONE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_MIDDLE);
						st.setCond(7);
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private boolean isRightMaster(L2Npc npc, L2PcInstance player)
	{
		switch (npc.getId())
		{
			case RAYMOND:
			case ESRANDELL:
			{
				if ((npc.getRace() == player.getRace()) && player.isMageClass())
				{
					return true;
				}
				break;
			}
			case RAINS:
			case ELLENIA:
			{
				if ((npc.getRace() == player.getRace()) && !player.isMageClass())
				{
					return true;
				}
				break;
			}
			case TOBIAS:
			case DRIKUS:
			case MENDIO:
			case GERSHWIN:
			{
				if (npc.getRace() == player.getRace())
				{
					return true;
				}
				break;
			}
		}
		return false;
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PRESS_TUTORIAL_MARK)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerPressTutorialMark(OnPlayerPressTutorialMark event)
	{
		if (event.getMarkId() == getId())
		{
			final L2PcInstance player = event.getActiveChar();
			String fileName = "";
			switch (player.getRace())
			{
				case DARK_ELF:
				case DWARF:
				case KAMAEL:
				case ORC:
				{
					fileName = "popup-" + player.getRace().toString().toLowerCase() + ".htm";
					break;
				}
				case ELF:
				case HUMAN:
				{
					fileName = "popup-" + player.getRace().toString().toLowerCase() + "-" + (player.isMageClass() ? "m" : "f") + ".htm";
					break;
				}
			}
			player.sendPacket(new TutorialShowHtml(getHtm(player, fileName)));
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLevelChanged(OnPlayerLevelChanged event)
	{
		final L2PcInstance player = event.getActiveChar();
		final int oldLevel = event.getOldLevel();
		final int newLevel = event.getNewLevel();
		
		if ((oldLevel < newLevel) && (newLevel == MIN_LEVEL) && (player.getRace() != Race.ERTHEIA) && (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerLogin(OnPlayerLogin event)
	{
		final L2PcInstance player = event.getActiveChar();
		
		if ((player.getLevel() >= MIN_LEVEL) && (player.getRace() != Race.ERTHEIA) && (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)))
		{
			final QuestState st = getQuestState(player, true);
			if (st.isCreated())
			{
				player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			}
		}
	}
}