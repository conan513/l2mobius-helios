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
package quests.Q10320_LetsGoToTheCentralSquare;

import com.l2jmobius.gameserver.enums.Movie;
import com.l2jmobius.gameserver.enums.Race;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerCreate;
import com.l2jmobius.gameserver.model.quest.Quest;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.quest.State;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * Let's Go To The Central Square (10320)
 * @author ivantotov, Gladicek
 */
public final class Q10320_LetsGoToTheCentralSquare extends Quest
{
	// NPCs
	private static final int PANTHEON = 32972;
	private static final int THEODORE = 32975;
	// Zone
	private static final int TALKING_ISLAND_PRESENTATION_MOVIE_ZONE = 200034;
	// Misc
	private static final int MAX_LEVEL = 20;
	private static final String MOVIE_VAR = "TI_presentation_movie";
	
	public Q10320_LetsGoToTheCentralSquare()
	{
		super(10320);
		addStartNpc(PANTHEON);
		addTalkId(PANTHEON, THEODORE);
		addEnterZoneId(TALKING_ISLAND_PRESENTATION_MOVIE_ZONE);
		addCondMaxLevel(MAX_LEVEL, "32972-01a.html");
		addCondNotRace(Race.ERTHEIA, "32972-01b.html");
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
			case "32972-03.html":
			{
				qs.startQuest();
				qs.setCond(2); // arrow hack
				qs.setCond(1);
				player.sendPacket(new TutorialShowHtml(npc.getObjectId(), "..\\L2Text\\QT_001_Radar_01.htm", TutorialShowHtml.LARGE_WINDOW));
				htmltext = event;
				break;
			}
			case "32972-02.htm":
			{
				htmltext = event;
				break;
			}
			case "32975-02.html":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 300, 6);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = null;
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = npc.getId() == PANTHEON ? "32972-01.htm" : "32975-04.html";
				break;
			}
			case State.STARTED:
			{
				htmltext = npc.getId() == PANTHEON ? "32972-04.html" : "32975-01.html";
				break;
			}
			case State.COMPLETED:
			{
				htmltext = npc.getId() == PANTHEON ? "32972-05.html" : "32975-03.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character.isPlayer())
		{
			final L2PcInstance player = character.getActingPlayer();
			
			if (player.getVariables().getBoolean(MOVIE_VAR, false))
			{
				if (player.getLevel() <= MAX_LEVEL)
				{
					final QuestState qs = getQuestState(player, false);
					playMovie(player, ((qs != null) && qs.isStarted()) ? Movie.SI_ILLUSION_02_QUE : Movie.SI_ILLUSION_01_QUE);
				}
				player.getVariables().remove(MOVIE_VAR);
			}
		}
		return super.onEnterZone(character, zone);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CREATE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void OnPlayerCreate(OnPlayerCreate event)
	{
		final L2PcInstance player = event.getActiveChar();
		if (player.getRace() != Race.ERTHEIA)
		{
			player.getVariables().set(MOVIE_VAR, true);
		}
	}
}