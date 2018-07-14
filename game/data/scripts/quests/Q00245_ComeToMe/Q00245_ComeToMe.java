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
package quests.Q00245_ComeToMe;

import java.util.Collection;

import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.gameserver.enums.QuestSound;
import com.l2jmobius.gameserver.enums.QuestType;
import com.l2jmobius.gameserver.instancemanager.MentorManager;
import com.l2jmobius.gameserver.model.L2Mentee;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.util.Util;

/**
 * Come To Me (245)
 * @URL https://l2wiki.com/Come_to_Me
 * @author Gigi
 * @date 2017-08-18 - [13:01:14]
 */
public class Q00245_ComeToMe extends Quest
{
	// NPC
	private static final int FERRIS = 30847;
	// Monsters
	private static final int[] BLAZING_MOBS_1 = new int[]
	{
		21110, // Swamp Predator
		21111 // Lava Wyrm
	};
	private static final int[] BLAZING_MOBS_2 = new int[]
	{
		21112, // Hames Orc Foot Soldier
		21113, // Hames Orc Sniper
		21115, // Hames Orc Shaman
		21116 // Hames Orc Prefect
	};
	// Items
	private static final int FLAME_ASHES = 30322;
	private static final int CRYSTALS_OF_EXPERIENCE = 30323;
	private static final int CRYSTAL_A = 1461;
	private static final int MENTOR_RING = 30383;
	private static final int ACADEMY_DYE_STR = 47205;
	private static final int ACADEMY_DYE_WIT = 47210;
	// Misc
	private static final int MIN_LEVEL = 70;
	private static final int MAX_LEVEL = 75;
	
	public Q00245_ComeToMe()
	{
		super(245);
		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		addKillId(BLAZING_MOBS_1);
		addKillId(BLAZING_MOBS_2);
		addFirstTalkId(FERRIS);
		registerQuestItems(FLAME_ASHES, CRYSTALS_OF_EXPERIENCE);
		addCondLevel(MIN_LEVEL, MAX_LEVEL, "30847-02.htm");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (player.isMentor() && event.equals("30847-13.html"))
		{
			L2PcInstance mentee = getCurrentMentee(player);
			if (mentee != null)
			{
				if (player.destroyItemByItemId("quest_245", CRYSTAL_A, 100, npc, true))
				{
					mentee.getQuestState(getName()).setCond(3, true);
					return event;
				}
				return "30847-14.html";
			}
			return "30847-12.html";
		}
		
		QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return event;
		}
		else if (event.equals("30847-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("30847-07.htm"))
		{
			st.set("talk", "1");
			takeItems(player, FLAME_ASHES, -1);
			playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		if (npc.getId() == FERRIS)
		{
			switch (st.getState())
			{
				case State.CREATED:
				{
					if (player.isMentee() && player.isAcademyMember())
					{
						htmltext = "30847-01.htm";
					}
					else
					{
						htmltext = "30847-02.htm";
					}
					break;
				}
				case State.STARTED:
				{
					switch (st.getCond())
					{
						case 1:
						{
							htmltext = "30847-05.html";
							break;
						}
						case 2:
						{
							if (!st.isSet("talk"))
							{
								htmltext = "30847-06.html";
							}
							else if (player.isMentee())
							{
								L2PcInstance mentor = MentorManager.getInstance().getMentor(player.getObjectId()).getPlayerInstance();
								if ((mentor != null) && mentor.isOnline() && Util.checkIfInRange(200, npc, mentor, true))
								{
									htmltext = "30847-10.html";
								}
								else
								{
									htmltext = "30847-08.html";
								}
							}
							else
							{
								htmltext = "30847-09.html";
							}
							break;
						}
						case 3:
						{
							st.setCond(4, true);
							htmltext = "30847-17.html";
							break;
						}
						case 4:
						{
							htmltext = "30847-18.html";
							break;
						}
						case 5:
						{
							if (player.getLevel() >= MIN_LEVEL)
							{
								if (player.isAcademyMember())
								{
									player.getClan().addReputationScore(500, true);
								}
								addExpAndSp(player, 2_018_733, 484);
								giveItems(player, MENTOR_RING, 1);
								giveItems(player, getRandom(ACADEMY_DYE_STR, ACADEMY_DYE_WIT), 10);
								htmltext = "30847-19.html";
							}
							else
							{
								htmltext = getNoQuestLevelRewardMsg(player);
							}
							st.exitQuest(QuestType.ONE_TIME, true);
							break;
						}
					}
					break;
				}
				case State.COMPLETED:
				{
					htmltext = "30847-03.htm";
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final QuestState st = getQuestState(killer, true);
		
		if ((npc == null) || (st == null))
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		if (st.getCond() == 1)
		{
			if (CommonUtil.contains(BLAZING_MOBS_1, npc.getId()) && (getRandom(100) < 50))
			{
				giveItems(killer, FLAME_ASHES, 1);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				if (getQuestItemsCount(killer, FLAME_ASHES) >= 15)
				{
					st.setCond(2, true);
				}
			}
		}
		else if (st.getCond() == 4)
		{
			if (CommonUtil.contains(BLAZING_MOBS_2, npc.getId()))
			{
				if (killer.isMentee())
				{
					L2PcInstance mentor = MentorManager.getInstance().getMentor(killer.getObjectId()).getPlayerInstance();
					if ((mentor != null) && Util.checkIfInRange(500, killer, mentor, false))
					{
						giveItems(killer, CRYSTALS_OF_EXPERIENCE, 1);
						playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
						if (getQuestItemsCount(killer, CRYSTALS_OF_EXPERIENCE) >= 12)
						{
							st.setCond(5, true);
						}
					}
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.isMentor() && (npc.getId() == FERRIS))
		{
			final L2PcInstance mentee = getCurrentMentee(player);
			if (mentee != null)
			{
				return "30847-11.html";
			}
		}
		npc.showChatWindow(player);
		return null;
	}
	
	private L2PcInstance getCurrentMentee(L2PcInstance mentor)
	{
		L2PcInstance mentee = null;
		final Collection<L2Mentee> mentees = MentorManager.getInstance().getMentees(mentor.getObjectId());
		for (L2Mentee pl : mentees)
		{
			if (pl.isOnline() && Util.checkIfInRange(400, mentor, pl.getPlayerInstance(), false))
			{
				final QuestState st = getQuestState(pl.getPlayerInstance(), true);
				if ((st != null) && (st.getCond() == 2))
				{
					mentee = pl.getPlayerInstance();
				}
			}
		}
		return mentee;
	}
}
