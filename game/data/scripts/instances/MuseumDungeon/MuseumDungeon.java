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
package instances.MuseumDungeon;

import java.util.List;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.FriendlyNpcInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.Id;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.OnCreatureAttacked;
import com.l2jmobius.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import com.l2jmobius.gameserver.model.events.impl.character.OnCreatureDeath;
import com.l2jmobius.gameserver.model.events.returns.DamageReturn;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.instancezone.Instance;
import com.l2jmobius.gameserver.model.quest.QuestState;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

import instances.AbstractInstance;
import quests.Q10542_SearchingForNewPower.Q10542_SearchingForNewPower;

/**
 * Museum Dungeon Instance Zone.
 * @author Gladicek
 */
public final class MuseumDungeon extends AbstractInstance
{
	// NPCs
	private static final int SHANNON = 32974;
	private static final int TOYRON = 33004;
	private static final int DESK = 33126;
	private static final int THIEF = 23121;
	// Items
	private static final int THE_WAR_OF_GODS_AND_GIANTS = 17575;
	// Skills
	private static final SkillHolder SPOIL = new SkillHolder(254, 1);
	// Misc
	private static final int TEMPLATE_ID = 182;
	private static final double DAMAGE_BY_SKILL = 0.5d;
	
	private static final NpcStringId[] THIEF_SHOUT =
	{
		NpcStringId.YOU_LL_NEVER_LEAVE_WITH_THAT_BOOK,
		NpcStringId.FINALLY_I_THOUGHT_I_WAS_GOING_TO_DIE_WAITING
	};
	
	public MuseumDungeon()
	{
		super(TEMPLATE_ID);
		addStartNpc(SHANNON);
		addFirstTalkId(DESK);
		addTalkId(SHANNON, TOYRON);
		addAttackId(THIEF);
		addSkillSeeId(THIEF);
	}
	
	@Override
	protected void onEnter(L2PcInstance player, Instance instance, boolean firstEnter)
	{
		super.onEnter(player, instance, firstEnter);
		
		final L2Attackable toyron = (L2Attackable) instance.getNpc(TOYRON);
		if (firstEnter)
		{
			// Set desk status
			final List<L2Npc> desks = instance.getNpcs(DESK);
			final L2Npc desk = desks.get(getRandom(desks.size()));
			desk.getVariables().set("book", true);
			
			// Set Toyron
			toyron.setRunning();
			toyron.setCanReturnToSpawnPoint(false);
		}
		
		final QuestState qs = player.getQuestState(Q10542_SearchingForNewPower.class.getSimpleName());
		if (qs != null)
		{
			switch (qs.getCond())
			{
				case 1:
				{
					qs.setCond(2, true);
					break;
				}
				case 3:
				{
					showOnScreenMsg(player, NpcStringId.AMONG_THE_4_BOOKSHELVES_FIND_THE_ONE_CONTAINING_A_VOLUME_CALLED_THE_WAR_OF_GODS_AND_GIANTS, ExShowScreenMessage.TOP_CENTER, 4500);
					break;
				}
				case 4:
				{
					getTimers().addTimer("TOYRON_FOLLOW", 1500, toyron, player);
					toyron.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHEN_DID_THEY_GET_IN_HERE);
					
					if (instance.getNpcs(THIEF).isEmpty())
					{
						instance.spawnGroup("thiefs").forEach(npc -> npc.setRunning());
					}
				}
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("enter_instance"))
		{
			enterInstance(player, npc, TEMPLATE_ID);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		if (skill == null)
		{
			showOnScreenMsg(attacker, NpcStringId.USE_YOUR_SKILL_ATTACKS_AGAINST_THEM, ExShowScreenMessage.TOP_CENTER, 2500);
		}
		return onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isSummon)
	{
		if (npc.isScriptValue(0) && (skill == SPOIL.getSkill()) && (caster.getTarget() == npc) && (npc.calculateDistance2D(caster) < 200))
		{
			final L2Npc toyron = npc.getInstanceWorld().getNpc(TOYRON);
			((FriendlyNpcInstance) toyron).addDamageHate(npc, 0, 9999); // TODO: Find better way for attack
			npc.reduceCurrentHp(1, toyron, null);
			npc.setScriptValue(1);
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public void onTimerEvent(String event, StatsSet params, L2Npc npc, L2PcInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		final L2Attackable toyron = (L2Attackable) instance.getNpc(TOYRON);
		if (isInInstance(instance))
		{
			switch (event)
			{
				case "TOYRON_FOLLOW":
				{
					toyron.getAI().startFollow(player);
					toyron.setRunning();
					break;
				}
				case "SPAWN_THIEFS_STAGE_1":
				{
					instance.spawnGroup("thiefs").forEach(thief ->
					{
						thief.setRunning();
						addAttackPlayerDesire(thief, player);
						thief.broadcastSay(ChatType.NPC_GENERAL, THIEF_SHOUT[getRandom(2)]);
					});
					toyron.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.WHEN_DID_THEY_GET_IN_HERE);
					getTimers().addRepeatingTimer("TOYRON_MSG_1", 10000, toyron, player);
					getTimers().addRepeatingTimer("TOYRON_MSG_2", 12500, toyron, player);
					break;
				}
				case "TOYRON_MSG_1":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.YOUR_NORMAL_ATTACKS_AREN_T_WORKING);
					break;
				}
				case "TOYRON_MSG_2":
				{
					npc.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.LOOKS_LIKE_ONLY_SKILL_BASED_ATTACKS_DAMAGE_THEM);
					break;
				}
				case "THIEF_DIE":
				{
					toyron.broadcastSay(ChatType.NPC_GENERAL, NpcStringId.ENOUGH_OF_THIS_COME_AT_ME);
					break;
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final Instance instance = npc.getInstanceWorld();
		String htmltext = null;
		if (isInInstance(instance))
		{
			final L2Npc toyron = instance.getNpc(TOYRON);
			
			final QuestState qs = player.getQuestState(Q10542_SearchingForNewPower.class.getSimpleName());
			if ((qs == null) || qs.isCond(4) || qs.isCond(5))
			{
				htmltext = "33126.html";
			}
			else if (qs.isCond(3))
			{
				if (npc.getVariables().getBoolean("book", false) && !hasQuestItems(player, THE_WAR_OF_GODS_AND_GIANTS))
				{
					qs.setCond(4, true);
					giveItems(player, THE_WAR_OF_GODS_AND_GIANTS, 1);
					showOnScreenMsg(player, NpcStringId.WATCH_OUT_YOU_ARE_BEING_ATTACKED, ExShowScreenMessage.TOP_CENTER, 4500, true);
					getTimers().addTimer("SPAWN_THIEFS_STAGE_1", 1000, npc, player);
					getTimers().addTimer("TOYRON_FOLLOW", 1000, toyron, player);
					htmltext = "33126-01.html";
				}
				else
				{
					htmltext = "33126-02.html";
				}
			}
		}
		return htmltext;
	}
	
	@RegisterEvent(EventType.ON_CREATURE_ATTACKED)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(THIEF)
	public void onCreatureAttacked(OnCreatureAttacked event)
	{
		final L2Character creature = event.getAttacker();
		final L2Npc npc = (L2Npc) event.getTarget();
		final Instance instance = npc.getInstanceWorld();
		
		if (isInInstance(instance) && !creature.isPlayer() && npc.isScriptValue(1))
		{
			getTimers().addTimer("THIEF_DIE", 500, npc, null);
		}
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DAMAGE_RECEIVED)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(THIEF)
	public DamageReturn onCreatureDamageReceived(OnCreatureDamageReceived event)
	{
		final L2Character target = event.getTarget();
		if (target.isNpc() && event.getAttacker().isPlayer())
		{
			final L2PcInstance player = event.getAttacker().getActingPlayer();
			final Instance instance = player.getInstanceWorld();
			if (isInInstance(instance))
			{
				final QuestState qs = player.getQuestState(Q10542_SearchingForNewPower.class.getSimpleName());
				if ((qs != null) && qs.isCond(4))
				{
					if (event.getSkill() == null)
					{
						return new DamageReturn(true, true, true, 0);
					}
					
					final L2Npc toyron = instance.getNpc(TOYRON);
					((FriendlyNpcInstance) toyron).addDamageHate(target, 0, 9999); // TODO: Find better way for attack
					target.reduceCurrentHp(1, toyron, null);
					((L2Npc) target).setScriptValue(1);
					return new DamageReturn(false, true, false, target.getMaxHp() * DAMAGE_BY_SKILL);
				}
			}
		}
		return null;
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(THIEF)
	public void onCreatureKill(OnCreatureDeath event)
	{
		final L2Npc npc = (L2Npc) event.getTarget();
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance))
		{
			final L2Attackable toyron = (L2Attackable) instance.getNpc(TOYRON);
			
			final L2PcInstance player = instance.getFirstPlayer();
			final QuestState qs = player.getQuestState(Q10542_SearchingForNewPower.class.getSimpleName());
			if ((qs != null) && qs.isCond(4))
			{
				if (instance.getAliveNpcs(THIEF).isEmpty())
				{
					qs.setCond(5, true);
					showOnScreenMsg(player, NpcStringId.SPEAK_WITH_TOYRON_IN_ORDER_TO_RETURN_TO_SHANNON, ExShowScreenMessage.TOP_CENTER, 4500);
					getTimers().cancelTimer("TOYRON_MSG_1", toyron, player);
					getTimers().cancelTimer("TOYRON_MSG_2", toyron, player);
				}
				else
				{
					final int value = qs.getMemoStateEx(Q10542_SearchingForNewPower.KILL_COUNT_VAR) + 1;
					qs.setMemoStateEx(Q10542_SearchingForNewPower.KILL_COUNT_VAR, value);
					qs.getQuest().sendNpcLogList(player);
					getTimers().addTimer("TOYRON_FOLLOW", 1500, toyron, player);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new MuseumDungeon();
	}
}