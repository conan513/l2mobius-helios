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
package quests.Q10301_ShadowOfTerrorBlackishRedFog;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.model.L2Object;
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
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowQuestionMark;

/**
 * Shadow of Terror: Blackish Red Fog (10301)
 * @author St3eT, Gigi
 */
public final class Q10301_ShadowOfTerrorBlackishRedFog extends Quest
{
	// NPCs
	private static final int LADA = 33100;
	private static final int SLASKI = 32893;
	private static final int LARGE_VERDANT_WILDS = 33489;
	private static final int WHISP = 27456;
	// Items
	private static final int LADA_LETTER = 17725; // Lada's Letter
	private static final int GLIMMER_CRYSTAL = 17604; // Glimmer Crystal
	private static final int SPIRIT_ITEM = 17588; // Calsuled Whisp
	private static final int FAIRY = 17380; // Agathion - Fairy
	// Skills
	private static final int WHISP_SKILL = 12001;
	// Misc
	private static final int MIN_LEVEL = 88;
	
	public Q10301_ShadowOfTerrorBlackishRedFog()
	{
		super(10301);
		addItemTalkId(LADA_LETTER);
		addTalkId(LADA, SLASKI);
		addSkillSeeId(LARGE_VERDANT_WILDS);
		addAttackId(WHISP);
		addCondMinLevel(MIN_LEVEL, "33100-08.htm");
		registerQuestItems(SPIRIT_ITEM, GLIMMER_CRYSTAL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		switch (event)
		{
			case "33100-02.htm":
			case "33100-03.htm":
			case "32893-02.html":
			case "32893-03.html":
			case "32893-04.html":
			case "32893-05.html":
			{
				htmltext = event;
				break;
			}
			case "33100-04.htm":
			{
				qs.setCond(2, true);
				htmltext = event;
				giveItems(player, GLIMMER_CRYSTAL, 10);
				break;
			}
			case "giveCrystals":
			{
				if (getQuestItemsCount(player, GLIMMER_CRYSTAL) < 1)
				{
					giveItems(player, GLIMMER_CRYSTAL, 5);
					htmltext = "33100-06.html";
					break;
				}
				htmltext = "33100-06a.html";
				break;
			}
			default:
			{
				if (event.startsWith("giveReward_") && qs.isCond(3) && (player.getLevel() >= MIN_LEVEL) && (getQuestItemsCount(player, SPIRIT_ITEM) >= 1))
				{
					final int itemId = Integer.parseInt(event.replace("giveReward_", ""));
					qs.exitQuest(false, true);
					giveAdena(player, 1_863_420, false);
					giveItems(player, itemId, 15);
					giveItems(player, FAIRY, 1);
					addExpAndSp(player, 26_920_620, 6_460);
					htmltext = "32893-06.html";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onItemTalk(L2ItemInstance item, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState qs = getQuestState(player, true);
		
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
			if (player.getLevel() >= MIN_LEVEL)
			{
				qs.startQuest();
				takeItems(player, LADA_LETTER, -1);
				htmltext = "start.htm";
			}
			else
			{
				htmltext = "33100-08.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.STARTED:
			{
				if (npc.getId() == LADA)
				{
					if (qs.isCond(1))
					{
						htmltext = "33100-01.htm";
					}
					else if (qs.isCond(2))
					{
						htmltext = "33100-05.html";
					}
					else if (qs.isCond(3))
					{
						htmltext = "33100-07.html";
					}
				}
				else if (npc.getId() == SLASKI)
				{
					if (qs.isCond(3))
					{
						htmltext = "32893-01.html";
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				if (npc.getId() == SLASKI)
				{
					htmltext = "32893-07.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isSummon)
	{
		final QuestState qs = getQuestState(caster, false);
		if ((qs != null) && qs.isCond(2) && (skill.getId() == WHISP_SKILL))
		{
			for (int i1 = 0; i1 < 3; i1++)
			{
				final L2Npc whisp = addSpawn(WHISP, caster.getX() + getRandom(-20, 20), caster.getY() + getRandom(-20, 20), caster.getZ(), 0, true, 30000, false);
				whisp.setTitle(caster.getName());
				whisp.broadcastInfo();
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		final QuestState qs = getQuestState(attacker, false);
		if ((qs != null) && qs.isCond(2))
		{
			if (getRandom(1000) < 300)
			{
				showOnScreenMsg(attacker, NpcStringId.YOU_VE_CAPTURED_A_WISP_SUCCESSFULLY, ExShowScreenMessage.TOP_CENTER, 10000);
				giveItems(attacker, SPIRIT_ITEM, 1);
				takeItems(attacker, GLIMMER_CRYSTAL, -1);
				qs.setCond(1);
				qs.setCond(3, true);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
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
		final QuestState qs = getQuestState(player, false);
		
		if ((qs == null) && (event.getOldLevel() < event.getNewLevel()) && canStartQuest(player))
		{
			player.sendPacket(new TutorialShowQuestionMark(getId(), 1));
			playSound(player, QuestSound.ITEMSOUND_QUEST_TUTORIAL);
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
		final QuestState qs = getQuestState(player, false);
		
		if ((qs == null) && canStartQuest(player) && !hasQuestItems(player, LADA_LETTER))
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
		if ((event.getMarkId() == getId()) && canStartQuest(player))
		{
			final String html = getHtm(player, "popup.html");
			player.sendPacket(new TutorialShowHtml(html));
			if (!hasQuestItems(player, LADA_LETTER))
			{
				giveItems(player, LADA_LETTER, 1);
			}
		}
	}
}