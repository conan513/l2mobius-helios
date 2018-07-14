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
package quests.Q10541_TrainLikeTheRealThing;

import java.util.HashSet;
import java.util.Set;

import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExQuestNpcLogList;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;

import quests.Q10321_QualificationsOfTheSeeker.Q10321_QualificationsOfTheSeeker;

/**
 * Train Like the Real Thing (10541)
 * @URL https://l2wiki.com/Train_Like_the_Real_Thing
 * @author Gigi
 */
public final class Q10541_TrainLikeTheRealThing extends Quest
{
	// NPCs
	private static final int SHANNON = 32974;
	private static final int ADVENTURERS_GUIDE = 32981;
	private static final int SCARECROW = 27457;
	// Misc
	private static final int MAX_LEVEL = 20;
	private static final String KILL_COUNT_VAR = "KillCount";
	// Buffs
	private static final SkillHolder WARRIOR = new SkillHolder(15649, 1); // Warrior's Harmony (Adventurer)
	private static final SkillHolder WIZARD = new SkillHolder(15650, 1); // Wizard's Harmony (Adventurer)
	private static final SkillHolder[] GROUP_BUFFS =
	{
		new SkillHolder(15642, 1), // Horn Melody (Adventurer)
		new SkillHolder(15645, 1), // Guitar Melody (Adventurer)
		new SkillHolder(15643, 1), // Drum Melody (Adventurer)
		new SkillHolder(15646, 1), // Harp Melody (Adventurer)
		new SkillHolder(15647, 1), // Lute Melody (Adventurer)
		new SkillHolder(15644, 1), // Pipe Organ Melody (Adventurer)
		new SkillHolder(15651, 1), // Prevailing Sonata (Adventurer)
		new SkillHolder(15652, 1), // Daring Sonata (Adventurer)
		new SkillHolder(15653, 1), // Refreshing Sonata (Adventurer)
	};
	
	public Q10541_TrainLikeTheRealThing()
	{
		super(10541);
		addStartNpc(SHANNON);
		addTalkId(SHANNON, ADVENTURERS_GUIDE);
		addKillId(SCARECROW);
		addCondNotRace(Race.ERTHEIA, "noRace.html");
		addCondMaxLevel(MAX_LEVEL, "noLevel.html");
		addCondCompletedQuest(Q10321_QualificationsOfTheSeeker.class.getSimpleName(), "noLevel.html");
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
			case "32974-02.htm":
			case "32974-03.htm":
			case "32981-02.html":
			{
				htmltext = event;
				break;
			}
			case "32981-03.html":
			{
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_002_Guide_01.htm", TutorialShowHtml.LARGE_WINDOW));
				htmltext = event;
				break;
			}
			case "32974-04.htm":
			{
				qs.startQuest();
				showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 8000);
				htmltext = event;
				break;
			}
			case "buff":
			{
				if (player.isMageClass())
				{
					applyBuffs(npc, player, WIZARD.getSkill());
				}
				else
				{
					applyBuffs(npc, player, WARRIOR.getSkill());
				}
				showOnScreenMsg(player, NpcStringId.ATTACK_THE_TRAINING_DUMMY, ExShowScreenMessage.TOP_CENTER, 8000);
				qs.setCond(4, true);
				htmltext = "32981-04.html";
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
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == SHANNON)
				{
					htmltext = "32974-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case SHANNON:
					{
						switch (qs.getCond())
						{
							case 1:
							{
								htmltext = "32974-05.html";
								break;
							}
							case 2:
							{
								qs.setCond(3, true);
								qs.unset("KillCount");
								showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_ADVENTURERS_GUIDE_FOR_TRAINING, ExShowScreenMessage.TOP_CENTER, 5000);
								htmltext = "32974-06.html";
								break;
							}
							case 3:
							{
								htmltext = "32974-07.html";
								showOnScreenMsg(player, NpcStringId.SPEAK_WITH_THE_ADVENTURERS_GUIDE_FOR_TRAINING, ExShowScreenMessage.TOP_CENTER, 5000);
								break;
							}
							case 5:
							{
								addExpAndSp(player, 2550, 7);
								qs.exitQuest(false, true);
								htmltext = "32974-08.html";
								break;
							}
						}
						break;
					}
					case ADVENTURERS_GUIDE:
					{
						if (qs.isCond(3))
						{
							htmltext = "32981-01.html";
						}
						else if (qs.isCond(4))
						{
							htmltext = "32981-04.html";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			if (qs.isCond(1))
			{
				int killCount = qs.getInt(KILL_COUNT_VAR);
				qs.set(KILL_COUNT_VAR, ++killCount);
				if (killCount >= 4)
				{
					qs.setCond(2, true);
				}
				else
				{
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
			else if (qs.isCond(4))
			{
				int kills = qs.getInt(Integer.toString(SCARECROW));
				if (kills < 4)
				{
					kills++;
					qs.set(Integer.toString(SCARECROW), kills);
					playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				final ExQuestNpcLogList log = new ExQuestNpcLogList(getId());
				log.addNpc(SCARECROW, qs.getInt(Integer.toString(SCARECROW)));
				qs.getPlayer().sendPacket(log);
				
				if (qs.getInt(Integer.toString(SCARECROW)) >= 4)
				{
					qs.setCond(5, true);
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR);
			if (killCount > 0)
			{
				final Set<NpcLogListHolder> holder = new HashSet<>();
				holder.add(new NpcLogListHolder(NpcStringId.DEFEATING_THE_SCARECROW, killCount));
				return holder;
			}
		}
		return super.getNpcLogList(player);
	}
	
	private String applyBuffs(L2Npc npc, L2PcInstance player, Skill skill)
	{
		for (SkillHolder holder : GROUP_BUFFS)
		{
			holder.getSkill().applyEffects(npc, player);
		}
		skill.applyEffects(npc, player);
		return null;
	}
}