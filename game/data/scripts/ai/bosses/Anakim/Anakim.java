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
package ai.bosses.Anakim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.l2jmobius.Config;
import com.l2jmobius.commons.util.CommonUtil;
import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.ai.CtrlIntention;
import com.l2jmobius.gameserver.data.xml.impl.SkillData;
import com.l2jmobius.gameserver.instancemanager.GrandBossManager;
import com.l2jmobius.gameserver.instancemanager.MapRegionManager;
import com.l2jmobius.gameserver.instancemanager.ZoneManager;
import com.l2jmobius.gameserver.model.L2Object;
import com.l2jmobius.gameserver.model.L2Party;
import com.l2jmobius.gameserver.model.L2Spawn;
import com.l2jmobius.gameserver.model.Location;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.TeleportWhereType;
import com.l2jmobius.gameserver.model.actor.L2Attackable;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SpawnHolder;
import com.l2jmobius.gameserver.model.quest.QuestTimer;
import com.l2jmobius.gameserver.model.skills.AbnormalType;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

import ai.AbstractNpcAI;

/**
 * Anakim AI<br>
 * @author LasTravel<br>
 * @URL http://boards.lineage2.com/showpost.php?p=3386784&postcount=6<br>
 * @video http://www.youtube.com/watch?v=LecymFTJQzQ
 */
public class Anakim extends AbstractNpcAI
{
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int FIGHTING = 2;
	private static final int DEAD = 3;
	// NPCs
	private static final int ANAKIM = 25286;
	private static final int REMNANT = 19490;
	private static final int ENTER_CUBIC = 31101;
	private static final int EXIST_CUBIC = 31109;
	private static final int ANAKIM_CUBIC = 31111;
	//@formatter:off
    private static final int[] ANAKIM_MINIONS = {25287, 25288, 25289};
    private static final int[] NECRO_MOBS = {21199, 21200, 21201, 21202, 21203, 21204, 21205, 21206, 21207};
    //@formatter:on	
	private static final int[] ALL_MOBS =
	{
		ANAKIM,
		ANAKIM_MINIONS[0],
		ANAKIM_MINIONS[1],
		ANAKIM_MINIONS[2],
		NECRO_MOBS[0],
		NECRO_MOBS[1],
		NECRO_MOBS[2],
		NECRO_MOBS[3],
		NECRO_MOBS[4],
		NECRO_MOBS[5],
		NECRO_MOBS[6],
		NECRO_MOBS[7],
		NECRO_MOBS[8],
		REMNANT
	};
	// Skill
	private static final Skill REMANT_TELE = SkillData.getInstance().getSkill(23303, 1);
	// Spawns
	private static final List<SpawnHolder> SPAWNS = new ArrayList<>();
	static
	{
		SPAWNS.add(new SpawnHolder(21206, 173077, -16317, -4906, 4056, false));
		SPAWNS.add(new SpawnHolder(21202, 173082, -16047, -4906, 14971, false));
		SPAWNS.add(new SpawnHolder(21206, 174578, -17866, -4906, 25990, false));
		SPAWNS.add(new SpawnHolder(21207, 175080, -17489, -4904, 13139, false));
		SPAWNS.add(new SpawnHolder(21205, 175172, -14020, -4904, 8666, false));
		SPAWNS.add(new SpawnHolder(21199, 175176, -14809, -4904, 10473, false));
		SPAWNS.add(new SpawnHolder(21201, 175510, -14982, -4906, 11447, false));
		SPAWNS.add(new SpawnHolder(21207, 176139, -17080, -4906, 60699, false));
		SPAWNS.add(new SpawnHolder(21200, 176466, -17481, -4904, 63292, false));
		SPAWNS.add(new SpawnHolder(21201, 176864, -14996, -4904, 53988, false));
		SPAWNS.add(new SpawnHolder(21203, 176887, -14742, -4906, 31818, false));
		SPAWNS.add(new SpawnHolder(21200, 177261, -17739, -4904, 17424, false));
		SPAWNS.add(new SpawnHolder(21206, 177451, -12992, -4925, 1420, false));
		SPAWNS.add(new SpawnHolder(21206, 179151, -13687, -4906, 48500, false));
		SPAWNS.add(new SpawnHolder(21206, 179385, -12830, -4904, 41930, false));
		SPAWNS.add(new SpawnHolder(21199, 178338, -17401, -4904, 15803, false));
		SPAWNS.add(new SpawnHolder(21199, 178515, -12993, -4925, 1155, false));
		SPAWNS.add(new SpawnHolder(21206, 178766, -15805, -4904, 1183, false));
		SPAWNS.add(new SpawnHolder(21205, 178776, -15535, -4927, 13472, false));
		SPAWNS.add(new SpawnHolder(21207, 180176, -15794, -4906, 38823, false));
		SPAWNS.add(new SpawnHolder(21200, 180557, -16149, -4906, 40308, false));
		SPAWNS.add(new SpawnHolder(21205, 180636, -16493, -4927, 50561, false));
		SPAWNS.add(new SpawnHolder(21202, 180750, -13175, -4906, 44641, false));
		SPAWNS.add(new SpawnHolder(21201, 181019, -12961, -4904, 1972, false));
		SPAWNS.add(new SpawnHolder(21204, 182219, -14352, -4904, 33181, false));
		SPAWNS.add(new SpawnHolder(21199, 182923, -14598, -4906, 24425, false));
		SPAWNS.add(new SpawnHolder(21206, 182940, -12808, -4904, 12476, false));
		SPAWNS.add(new SpawnHolder(21201, 182952, -17418, -4904, 15163, false));
		SPAWNS.add(new SpawnHolder(21203, 172455, -9219, -4906, 64914, false));
		SPAWNS.add(new SpawnHolder(21205, 172468, -7295, -4904, 1132, false));
		SPAWNS.add(new SpawnHolder(21201, 172508, -10953, -4904, 61515, false));
		SPAWNS.add(new SpawnHolder(21207, 172794, -9531, -4927, 1997, false));
		SPAWNS.add(new SpawnHolder(21200, 172880, -11898, -4925, 65021, false));
		SPAWNS.add(new SpawnHolder(21204, 173598, -11065, -4927, 571, false));
		SPAWNS.add(new SpawnHolder(21199, 173928, -7388, -4927, 34341, false));
		SPAWNS.add(new SpawnHolder(21199, 174259, -7974, -4906, 7003, false));
		SPAWNS.add(new SpawnHolder(21207, 175278, -11072, -4927, 17977, false));
		SPAWNS.add(new SpawnHolder(21207, 175288, -10136, -4906, 47983, false));
		SPAWNS.add(new SpawnHolder(21200, 175519, -9575, -4906, 58165, false));
		SPAWNS.add(new SpawnHolder(21200, 175527, -12001, -4906, 59818, false));
		SPAWNS.add(new SpawnHolder(21206, 176524, -9907, -4906, 5094, false));
		SPAWNS.add(new SpawnHolder(21204, 177097, -11914, -4904, 32360, false));
		SPAWNS.add(new SpawnHolder(21204, 177279, -7486, -4904, 47036, false));
		SPAWNS.add(new SpawnHolder(21203, 178337, -11691, -4904, 16100, false));
		SPAWNS.add(new SpawnHolder(21203, 178357, -7493, -4904, 50527, false));
		SPAWNS.add(new SpawnHolder(21207, 178451, -10181, -4906, 54905, false));
		SPAWNS.add(new SpawnHolder(21200, 179213, -9903, -4906, 6134, false));
		SPAWNS.add(new SpawnHolder(21200, 180086, -9917, -4906, 33891, false));
		SPAWNS.add(new SpawnHolder(21207, 180422, -7251, -4904, 56765, false));
		SPAWNS.add(new SpawnHolder(21201, 180472, -10304, -4904, 49629, false));
		SPAWNS.add(new SpawnHolder(21205, 180577, -11232, -4906, 48645, false));
		SPAWNS.add(new SpawnHolder(21200, 181269, -7388, -4904, 426, false));
		SPAWNS.add(new SpawnHolder(21199, 182519, -8817, -4906, 49404, false));
		SPAWNS.add(new SpawnHolder(21200, 182612, -8674, -4904, 44491, false));
		SPAWNS.add(new SpawnHolder(21206, 183165, -7264, -4904, 28242, false));
		SPAWNS.add(new SpawnHolder(21201, 172469, -12995, -4904, 819, false));
		SPAWNS.add(new SpawnHolder(21204, 172765, -16043, -4906, 48239, false));
		SPAWNS.add(new SpawnHolder(21203, 173490, -16291, -4906, 514, false));
		SPAWNS.add(new SpawnHolder(21204, 174545, -17625, -4904, 41172, false));
		SPAWNS.add(new SpawnHolder(21199, 174907, -17434, -4906, 10827, false));
		SPAWNS.add(new SpawnHolder(21207, 174982, -14693, -4906, 27145, false));
		SPAWNS.add(new SpawnHolder(21204, 175010, -13720, -4906, 18841, false));
		SPAWNS.add(new SpawnHolder(21203, 175316, -15239, -4906, 20459, false));
		SPAWNS.add(new SpawnHolder(21203, 176275, -16947, -4904, 19252, false));
		SPAWNS.add(new SpawnHolder(21204, 176443, -17690, -4906, 65438, false));
		SPAWNS.add(new SpawnHolder(21199, 176677, -15076, -4906, 47622, false));
		SPAWNS.add(new SpawnHolder(21199, 177168, -14718, -4927, 20070, false));
		SPAWNS.add(new SpawnHolder(21207, 177265, -12826, -4904, 19490, false));
		SPAWNS.add(new SpawnHolder(21200, 177424, -17617, -4904, 3700, false));
		SPAWNS.add(new SpawnHolder(21206, 179467, -13725, -4906, 43672, false));
		SPAWNS.add(new SpawnHolder(21203, 179526, -13134, -4927, 52220, false));
		SPAWNS.add(new SpawnHolder(21200, 178349, -12839, -4904, 18461, false));
		SPAWNS.add(new SpawnHolder(21199, 178428, -15548, -4906, 55285, false));
		SPAWNS.add(new SpawnHolder(21204, 178546, -17597, -4904, 2584, false));
		SPAWNS.add(new SpawnHolder(21204, 179256, -15820, -4906, 64089, false));
		SPAWNS.add(new SpawnHolder(21200, 180340, -15506, -4906, 51622, false));
		SPAWNS.add(new SpawnHolder(21207, 180444, -15827, -4904, 51038, false));
		SPAWNS.add(new SpawnHolder(21202, 180555, -13151, -4906, 48007, false));
		SPAWNS.add(new SpawnHolder(21202, 180789, -16337, -4906, 8540, false));
		SPAWNS.add(new SpawnHolder(21202, 180794, -13066, -4904, 62828, false));
		SPAWNS.add(new SpawnHolder(21203, 181987, -14470, -4925, 36453, false));
		SPAWNS.add(new SpawnHolder(21203, 182752, -17591, -4904, 31555, false));
		SPAWNS.add(new SpawnHolder(21199, 183052, -14243, -4927, 17937, false));
		SPAWNS.add(new SpawnHolder(21206, 183145, -12992, -4904, 35585, false));
		SPAWNS.add(new SpawnHolder(21199, 172505, -11880, -4904, 7971, false));
		SPAWNS.add(new SpawnHolder(21202, 172514, -10004, -4906, 7764, false));
		SPAWNS.add(new SpawnHolder(21200, 172730, -9035, -4925, 20022, false));
		SPAWNS.add(new SpawnHolder(21199, 172839, -7282, -4904, 407, false));
		SPAWNS.add(new SpawnHolder(21199, 172890, -11128, -4906, 36753, false));
		SPAWNS.add(new SpawnHolder(21203, 173200, -10885, -4904, 64025, false));
		SPAWNS.add(new SpawnHolder(21200, 173969, -8136, -4906, 51517, false));
		SPAWNS.add(new SpawnHolder(21202, 174223, -7296, -4906, 40856, false));
		SPAWNS.add(new SpawnHolder(21206, 174964, -9859, -4904, 61063, false));
		SPAWNS.add(new SpawnHolder(21201, 175234, -11487, -4906, 32278, false));
		SPAWNS.add(new SpawnHolder(21204, 175508, -11435, -4906, 10075, false));
		SPAWNS.add(new SpawnHolder(21204, 175872, -9856, -4925, 5782, false));
		SPAWNS.add(new SpawnHolder(21207, 176006, -9475, -4906, 18443, false));
		SPAWNS.add(new SpawnHolder(21204, 177259, -7101, -4904, 51015, false));
		SPAWNS.add(new SpawnHolder(21205, 177451, -11916, -4904, 762, false));
		SPAWNS.add(new SpawnHolder(21205, 178343, -12110, -4904, 49406, false));
		SPAWNS.add(new SpawnHolder(21206, 178346, -7111, -4904, 53343, false));
		SPAWNS.add(new SpawnHolder(21201, 178670, -9862, -4927, 9673, false));
		SPAWNS.add(new SpawnHolder(21201, 178962, -10192, -4906, 63558, false));
		SPAWNS.add(new SpawnHolder(21207, 180354, -10754, -4927, 16000, false));
		SPAWNS.add(new SpawnHolder(21201, 180589, -9772, -4906, 55342, false));
		SPAWNS.add(new SpawnHolder(21201, 180681, -7470, -4906, 51819, false));
		SPAWNS.add(new SpawnHolder(21204, 180830, -10655, -4906, 6352, false));
		SPAWNS.add(new SpawnHolder(21206, 181038, -7145, -4906, 52771, false));
		SPAWNS.add(new SpawnHolder(21203, 182094, -8779, -4904, 39237, false));
		SPAWNS.add(new SpawnHolder(21205, 182783, -7300, -4904, 32768, false));
		SPAWNS.add(new SpawnHolder(21204, 183072, -8569, -4906, 17776, false));
		SPAWNS.add(new SpawnHolder(21204, 172550, -16371, -4906, 50062, false));
		SPAWNS.add(new SpawnHolder(21205, 172655, -12687, -4904, 16450, false));
		SPAWNS.add(new SpawnHolder(21207, 173513, -16089, -4904, 842, false));
		SPAWNS.add(new SpawnHolder(21204, 174277, -17521, -4904, 30613, false));
		SPAWNS.add(new SpawnHolder(21206, 174716, -17481, -4904, 17821, false));
		SPAWNS.add(new SpawnHolder(21204, 174756, -14268, -4906, 31489, false));
		SPAWNS.add(new SpawnHolder(21200, 174949, -13934, -4906, 10044, false));
		SPAWNS.add(new SpawnHolder(21202, 175243, -14276, -4904, 54713, false));
		SPAWNS.add(new SpawnHolder(21202, 176327, -14884, -4904, 36197, false));
		SPAWNS.add(new SpawnHolder(21205, 176378, -17184, -4904, 61540, false));
		SPAWNS.add(new SpawnHolder(21203, 176414, -16655, -4906, 20877, false));
		SPAWNS.add(new SpawnHolder(21201, 176576, -14876, -4904, 53805, false));
		SPAWNS.add(new SpawnHolder(21201, 177039, -17604, -4904, 31523, false));
		SPAWNS.add(new SpawnHolder(21206, 177046, -12988, -4904, 34343, false));
		SPAWNS.add(new SpawnHolder(21203, 177361, -14704, -4906, 19318, false));
		SPAWNS.add(new SpawnHolder(21205, 179145, -13076, -4906, 35562, false));
		SPAWNS.add(new SpawnHolder(21203, 179328, -13363, -4904, 45077, false));
		SPAWNS.add(new SpawnHolder(21202, 178136, -17606, -4904, 33699, false));
		SPAWNS.add(new SpawnHolder(21203, 178202, -12994, -4904, 33656, false));
		SPAWNS.add(new SpawnHolder(21205, 178253, -15890, -4906, 50087, false));
		SPAWNS.add(new SpawnHolder(21206, 179258, -15629, -4904, 1139, false));
		SPAWNS.add(new SpawnHolder(21201, 180197, -13096, -4906, 64516, false));
		SPAWNS.add(new SpawnHolder(21202, 180358, -16895, -4906, 45409, false));
		SPAWNS.add(new SpawnHolder(21201, 180400, -16511, -4904, 9156, false));
		SPAWNS.add(new SpawnHolder(21199, 180472, -15369, -4925, 51432, false));
		SPAWNS.add(new SpawnHolder(21199, 181355, -12900, -4904, 3690, false));
		SPAWNS.add(new SpawnHolder(21199, 182353, -14555, -4906, 46420, false));
		SPAWNS.add(new SpawnHolder(21205, 182906, -14226, -4906, 20032, false));
		SPAWNS.add(new SpawnHolder(21202, 182957, -13295, -4904, 48756, false));
		SPAWNS.add(new SpawnHolder(21206, 182959, -17755, -4904, 19094, false));
		SPAWNS.add(new SpawnHolder(21199, 172522, -8958, -4906, 17302, false));
		SPAWNS.add(new SpawnHolder(21207, 172655, -7499, -4904, 49907, false));
		SPAWNS.add(new SpawnHolder(21203, 172656, -11131, -4906, 53634, false));
		SPAWNS.add(new SpawnHolder(21206, 172657, -11668, -4904, 19014, false));
		SPAWNS.add(new SpawnHolder(21199, 172826, -9732, -4906, 2140, false));
		SPAWNS.add(new SpawnHolder(21203, 173554, -10838, -4927, 1396, false));
		SPAWNS.add(new SpawnHolder(21207, 173927, -7208, -4906, 31031, false));
		SPAWNS.add(new SpawnHolder(21204, 174175, -8170, -4927, 49895, false));
		SPAWNS.add(new SpawnHolder(21201, 175140, -9993, -4906, 57469, false));
		SPAWNS.add(new SpawnHolder(21205, 175250, -11779, -4906, 6938, false));
		SPAWNS.add(new SpawnHolder(21201, 175456, -11034, -4904, 14019, false));
		SPAWNS.add(new SpawnHolder(21204, 175790, -9431, -4906, 17010, false));
		SPAWNS.add(new SpawnHolder(21207, 176470, -9685, -4906, 62544, false));
		SPAWNS.add(new SpawnHolder(21201, 177259, -12172, -4904, 49445, false));
		SPAWNS.add(new SpawnHolder(21202, 177470, -7299, -4904, 64668, false));
		SPAWNS.add(new SpawnHolder(21205, 178150, -11911, -4904, 34327, false));
		SPAWNS.add(new SpawnHolder(21207, 178267, -10241, -4906, 47671, false));
		SPAWNS.add(new SpawnHolder(21202, 178539, -7297, -4925, 64795, false));
		SPAWNS.add(new SpawnHolder(21205, 179213, -10144, -4906, 4281, false));
		SPAWNS.add(new SpawnHolder(21201, 180110, -10119, -4906, 32768, false));
		SPAWNS.add(new SpawnHolder(21200, 180454, -9977, -4904, 40697, false));
		SPAWNS.add(new SpawnHolder(21200, 180588, -11006, -4906, 41368, false));
		SPAWNS.add(new SpawnHolder(21207, 180818, -7210, -4904, 37030, false));
		SPAWNS.add(new SpawnHolder(21200, 181265, -7212, -4904, 484, false));
		SPAWNS.add(new SpawnHolder(21201, 182345, -8886, -4906, 49823, false));
		SPAWNS.add(new SpawnHolder(21199, 182847, -8519, -4906, 13647, false));
		SPAWNS.add(new SpawnHolder(21201, 182962, -7497, -4904, 49589, false));
		SPAWNS.add(new SpawnHolder(21207, 172731, -16335, -4906, 51647, false));
		SPAWNS.add(new SpawnHolder(21201, 172803, -12986, -4925, 1432, false));
		SPAWNS.add(new SpawnHolder(21199, 173324, -15973, -4906, 27090, false));
		SPAWNS.add(new SpawnHolder(21199, 174302, -17745, -4906, 37147, false));
		SPAWNS.add(new SpawnHolder(21205, 174793, -14519, -4906, 24542, false));
		SPAWNS.add(new SpawnHolder(21204, 174884, -17717, -4906, 3409, false));
		SPAWNS.add(new SpawnHolder(21202, 175225, -13800, -4904, 13904, false));
		SPAWNS.add(new SpawnHolder(21200, 175566, -14732, -4906, 55543, false));
		SPAWNS.add(new SpawnHolder(21206, 176232, -16745, -4904, 21221, false));
		SPAWNS.add(new SpawnHolder(21201, 176257, -17436, -4904, 15723, false));
		SPAWNS.add(new SpawnHolder(21207, 176625, -14737, -4906, 32062, false));
		SPAWNS.add(new SpawnHolder(21205, 177188, -14954, -4904, 28884, false));
		SPAWNS.add(new SpawnHolder(21201, 177254, -13177, -4904, 50898, false));
		SPAWNS.add(new SpawnHolder(21199, 177267, -17394, -4925, 21017, false));
		SPAWNS.add(new SpawnHolder(21207, 179173, -12908, -4906, 32768, false));
		SPAWNS.add(new SpawnHolder(21201, 179274, -13854, -4925, 55304, false));
		SPAWNS.add(new SpawnHolder(21204, 178174, -7303, -4904, 27814, false));
		SPAWNS.add(new SpawnHolder(21207, 178542, -11903, -4904, 64644, false));
		SPAWNS.add(new SpawnHolder(21205, 178715, -10145, -4906, 5002, false));
		SPAWNS.add(new SpawnHolder(21204, 179011, -9845, -4906, 23256, false));
		SPAWNS.add(new SpawnHolder(21201, 180287, -9677, -4906, 49376, false));
		SPAWNS.add(new SpawnHolder(21200, 180320, -11223, -4906, 45409, false));
		SPAWNS.add(new SpawnHolder(21206, 180503, -7440, -4906, 53375, false));
		SPAWNS.add(new SpawnHolder(21206, 180763, -10369, -4906, 64705, false));
		SPAWNS.add(new SpawnHolder(21205, 181039, -7438, -4906, 44443, false));
		SPAWNS.add(new SpawnHolder(21201, 182135, -8603, -4904, 32768, false));
		SPAWNS.add(new SpawnHolder(21205, 182867, -8842, -4906, 21450, false));
		SPAWNS.add(new SpawnHolder(21205, 182961, -7120, -4904, 27395, false));
		SPAWNS.add(new SpawnHolder(21199, 172441, -9616, -4906, 64703, false));
		SPAWNS.add(new SpawnHolder(21207, 172652, -7100, -4904, 52107, false));
		SPAWNS.add(new SpawnHolder(21203, 172660, -12111, -4904, 51353, false));
		SPAWNS.add(new SpawnHolder(21206, 172785, -10820, -4906, 4057, false));
		SPAWNS.add(new SpawnHolder(21200, 172822, -9266, -4906, 14418, false));
		SPAWNS.add(new SpawnHolder(21199, 173280, -11141, -4906, 35853, false));
		SPAWNS.add(new SpawnHolder(21202, 173886, -7770, -4906, 44964, false));
		SPAWNS.add(new SpawnHolder(21205, 174232, -7718, -4927, 1781, false));
		SPAWNS.add(new SpawnHolder(21203, 175176, -9639, -4906, 56329, false));
		SPAWNS.add(new SpawnHolder(21204, 175213, -11259, -4906, 42485, false));
		SPAWNS.add(new SpawnHolder(21199, 175513, -10104, -4906, 47655, false));
		SPAWNS.add(new SpawnHolder(21207, 175526, -11796, -4907, 64496, false));
		SPAWNS.add(new SpawnHolder(21202, 176249, -10035, -4906, 36039, false));
		SPAWNS.add(new SpawnHolder(21204, 177094, -7290, -4904, 31782, false));
		SPAWNS.add(new SpawnHolder(21202, 177253, -11727, -4904, 14326, false));
		SPAWNS.add(new SpawnHolder(21205, 178347, -17851, -4904, 17424, false));
		SPAWNS.add(new SpawnHolder(21203, 178347, -13141, -4904, 52914, false));
		SPAWNS.add(new SpawnHolder(21199, 178441, -15842, -4906, 52668, false));
		SPAWNS.add(new SpawnHolder(21202, 179015, -15501, -4906, 24709, false));
		SPAWNS.add(new SpawnHolder(21203, 180177, -15611, -4906, 31898, false));
		SPAWNS.add(new SpawnHolder(21204, 180361, -12895, -4904, 65335, false));
		SPAWNS.add(new SpawnHolder(21205, 180585, -16856, -4927, 46695, false));
		SPAWNS.add(new SpawnHolder(21200, 180743, -16075, -4906, 60699, false));
		SPAWNS.add(new SpawnHolder(21200, 181298, -13086, -4904, 63978, false));
		SPAWNS.add(new SpawnHolder(21202, 182522, -14565, -4906, 50463, false));
		SPAWNS.add(new SpawnHolder(21206, 182730, -14374, -4904, 42720, false));
		SPAWNS.add(new SpawnHolder(21200, 182787, -13004, -4904, 27242, false));
		SPAWNS.add(new SpawnHolder(21206, 183164, -17602, -4904, 32912, false));
	}
	// Misc
	private static final Location ENTER_LOC = new Location(172420, -17602, -4906);
	private static final Location ENTER_ANAKIM_LOC = new Location(184569, -12134, -5499);
	private static final L2ZoneType BOSS_ZONE = ZoneManager.getInstance().getZoneById(12003);
	private static final L2ZoneType PRE_ANAKIM_ZONE = ZoneManager.getInstance().getZoneById(12004);
	// Vars
	private static List<L2Npc> _spawns = new ArrayList<>();
	private static List<L2Npc> _remnants = new ArrayList<>();
	private static long _lastAction;
	private static L2Npc _anakimBoss;
	
	public Anakim()
	{
		addTalkId(ENTER_CUBIC, EXIST_CUBIC, ANAKIM_CUBIC);
		addStartNpc(ENTER_CUBIC, EXIST_CUBIC, ANAKIM_CUBIC);
		addFirstTalkId(ENTER_CUBIC, EXIST_CUBIC, ANAKIM_CUBIC);
		addSpellFinishedId(REMNANT);
		addAttackId(ALL_MOBS);
		addKillId(ALL_MOBS);
		addSkillSeeId(ALL_MOBS);
		
		// Unlock
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(ANAKIM);
		final int status = GrandBossManager.getInstance().getBossStatus(ANAKIM);
		if (status == DEAD)
		{
			final long time = info.getLong("respawn_time") - System.currentTimeMillis();
			if (time > 0)
			{
				startQuestTimer("unlock_anakim", time, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(ANAKIM, ALIVE);
			}
		}
		else if (status != ALIVE)
		{
			GrandBossManager.getInstance().setBossStatus(ANAKIM, ALIVE);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "unlock_anakim":
			{
				GrandBossManager.getInstance().setBossStatus(ANAKIM, ALIVE);
				break;
			}
			case "check_activity_task":
			{
				if ((_lastAction + 900000) < System.currentTimeMillis())
				{
					GrandBossManager.getInstance().setBossStatus(ANAKIM, ALIVE);
					for (L2Character charInside : BOSS_ZONE.getCharactersInside())
					{
						if (charInside != null)
						{
							if (charInside.isNpc())
							{
								charInside.deleteMe();
							}
							else if (charInside.isPlayer())
							{
								charInside.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(charInside, TeleportWhereType.TOWN));
							}
						}
					}
					startQuestTimer("end_anakim", 2000, null, null);
				}
				else
				{
					startQuestTimer("check_activity_task", 60000, null, null);
				}
				break;
			}
			case "spawn_remant":
			{
				L2Npc randomSpawn = null;
				if (npc == null)
				{
					for (int i = 0; i < 2; i++)
					{
						randomSpawn = _spawns.get(Rnd.get(_spawns.size()));
						if (randomSpawn != null)
						{
							L2Npc remnant = addSpawn(REMNANT, randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ(), randomSpawn.getHeading(), true, 0, false, 0);
							_remnants.add(remnant);
						}
					}
				}
				else
				{
					randomSpawn = _spawns.get(Rnd.get(_spawns.size()));
					if (randomSpawn != null)
					{
						npc.teleToLocation(randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ());
						_spawns.add(npc);
					}
				}
				break;
			}
			case "cancel_timers":
			{
				QuestTimer activityTimer = getQuestTimer("check_activity_task", null, null);
				if (activityTimer != null)
				{
					activityTimer.cancel();
				}
				
				QuestTimer forceEnd = getQuestTimer("end_anakim", null, null);
				if (forceEnd != null)
				{
					forceEnd.cancel();
				}
				break;
			}
			case "end_anakim":
			{
				notifyEvent("cancel_timers", null, null);
				if (_anakimBoss != null)
				{
					_anakimBoss.deleteMe();
				}
				BOSS_ZONE.oustAllPlayers();
				PRE_ANAKIM_ZONE.oustAllPlayers();
				for (L2Npc spawn : _spawns)
				{
					if (spawn != null)
					{
						spawn.deleteMe();
					}
				}
				_spawns.clear();
				for (L2Npc remnant : _remnants)
				{
					if (remnant == null)
					{
						continue;
					}
					remnant.deleteMe();
				}
				if (GrandBossManager.getInstance().getBossStatus(ANAKIM) != DEAD)
				{
					GrandBossManager.getInstance().setBossStatus(ANAKIM, ALIVE);
				}
				break;
			}
			case "exist":
			{
				player.teleToLocation(TeleportWhereType.TOWN);
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		if ((npc.getId() == ENTER_CUBIC) || (npc.getId() == ANAKIM_CUBIC))
		{
			final int _anakimStatus = GrandBossManager.getInstance().getBossStatus(ANAKIM);
			if ((npc.getId() == ENTER_CUBIC) && (_anakimStatus > ALIVE))
			{
				return "31101-01.html";
			}
			if (!player.isInParty())
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "31101-02.html"));
				packet.replace("%min%", Integer.toString(Config.ANAKIM_MIN_PLAYERS));
				player.sendPacket(packet);
				return null;
			}
			final L2Party party = player.getParty();
			final boolean isInCC = party.isInCommandChannel();
			final List<L2PcInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
			final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
			if (!isPartyLeader)
			{
				return "31101-03.html";
			}
			
			if ((members.size() < Config.ANAKIM_MIN_PLAYERS) || (members.size() > Config.ANAKIM_MAX_PLAYERS))
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "31101-02.html"));
				packet.replace("%min%", Integer.toString(Config.ANAKIM_MIN_PLAYERS));
				player.sendPacket(packet);
				return null;
			}
			
			for (L2PcInstance member : members)
			{
				if ((member.getLevel() < Config.ANAKIM_MIN_PLAYER_LVL) || (member.getLevel() > Config.ANAKIM_MAX_PLAYER_LVL))
				{
					final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
					packet.setHtml(getHtm(player, "31101-04.html"));
					packet.replace("%minlvl%", Integer.toString(Config.ANAKIM_MIN_PLAYER_LVL));
					packet.replace("%maxlvl%", Integer.toString(Config.ANAKIM_MAX_PLAYER_LVL));
					player.sendPacket(packet);
					return null;
				}
			}
			
			for (L2PcInstance member : members)
			{
				if (member.isInsideRadius3D(npc, 1000) && (npc.getId() == ENTER_CUBIC))
				{
					member.teleToLocation(ENTER_LOC, true);
				}
				else if (member.isInsideRadius3D(npc, 1000) && (npc.getId() == ANAKIM_CUBIC))
				{
					member.teleToLocation(ENTER_ANAKIM_LOC, true);
				}
			}
			
			if ((_anakimStatus == ALIVE) && (npc.getId() == ENTER_CUBIC))
			{
				GrandBossManager.getInstance().setBossStatus(ANAKIM, WAITING);
				_spawns.clear();
				for (SpawnHolder spawn : SPAWNS)
				{
					_spawns.add(addSpawn(spawn.getNpcId(), spawn.getLocation()));
				}
				_remnants.clear();
				notifyEvent("spawn_remant", null, null);
				_lastAction = System.currentTimeMillis();
				startQuestTimer("check_activity_task", 60000, null, null, true);
			}
			else if ((_anakimStatus == WAITING) && (npc.getId() == ANAKIM_CUBIC))
			{
				GrandBossManager.getInstance().setBossStatus(ANAKIM, FIGHTING);
				// Spawn the rb
				_anakimBoss = addSpawn(ANAKIM, 185080, -12613, -5499, 16550, false, 0);
				GrandBossManager.getInstance().addBoss((L2GrandBossInstance) _anakimBoss);
				startQuestTimer("end_anakim", 60 * 60000, null, null); // 1h
				if (!_remnants.isEmpty())
				{
					return "You must kill all minions before you can engage in a fight with Anakim.";
				}
			}
		}
		return super.onTalk(npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return npc.getId() + ".html";
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		_lastAction = System.currentTimeMillis();
		if (npc.isMinion() || npc.isRaid())// Anakim and minions
		{
			// Anti BUGGERS
			if (!BOSS_ZONE.isInsideZone(attacker)) // Character attacking out of zone
			{
				attacker.doDie(null);
			}
			if (!BOSS_ZONE.isInsideZone(npc)) // Npc moved out of the zone
			{
				L2Spawn spawn = npc.getSpawn();
				if (spawn != null)
				{
					npc.teleToLocation(spawn.getX(), spawn.getY(), spawn.getZ());
				}
			}
		}
		if (npc.getId() == REMNANT)
		{
			if (npc.getCurrentHp() < (npc.getMaxHp() * 0.30))
			{
				if (!npc.isCastingNow() && (Rnd.get(100) > 95))
				{
					npc.doCast(REMANT_TELE);
				}
			}
		}
		
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getId() == ANAKIM)
		{
			notifyEvent("cancel_timers", null, null);
			addSpawn(EXIST_CUBIC, 185082, -12606, -5499, 6133, false, 900000); // 15min
			
			GrandBossManager.getInstance().setBossStatus(ANAKIM, DEAD);
			final long respawnTime = getRespawnTime();
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(ANAKIM);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(ANAKIM, info);
			
			startQuestTimer("unlock_anakim", respawnTime, null, null);
			startQuestTimer("end_anakim", 900000, null, null);
		}
		else if (npc.getId() == REMNANT)
		{
			_remnants.remove(npc);
			if (_remnants.isEmpty())
			{
				addSpawn(ANAKIM_CUBIC, 183225, -11911, -4897, 32768, false, 60 * 60000, false, 0);
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill)
	{
		if ((npc.getId() == REMNANT) && PRE_ANAKIM_ZONE.isInsideZone(npc))
		{
			if (skill == REMANT_TELE)
			{
				notifyEvent("spawn_remant", npc, null);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, L2Object[] targets, boolean isPet)
	{
		if (CommonUtil.contains(ANAKIM_MINIONS, npc.getId()) && Rnd.nextBoolean())
		{
			if (skill.getAbnormalType() == AbnormalType.HP_RECOVER)
			{
				if (!npc.isCastingNow() && (npc.getTarget() != npc) && (npc.getTarget() != caster) && (npc.getTarget() != _anakimBoss)) // Don't call minions if are healing Anakim
				{
					((L2Attackable) npc).clearAggroList();
					npc.setTarget(caster);
					((L2Attackable) npc).addDamageHate(caster, 500, 99999);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, caster);
				}
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	private int getRespawnTime()
	{
		return (int) calcReuseFromDays(0, 21, Calendar.TUESDAY, 0, 16, Calendar.SATURDAY);
	}
	
	private long calcReuseFromDays(int day1Minute, int day1Hour, int day1Day, int day2Minute, int day2Hour, int day2Day)
	{
		Calendar now = Calendar.getInstance();
		Calendar day1 = (Calendar) now.clone();
		day1.set(Calendar.MINUTE, day1Minute);
		day1.set(Calendar.HOUR_OF_DAY, day1Hour);
		day1.set(Calendar.DAY_OF_WEEK, day1Day);
		
		Calendar day2 = (Calendar) day1.clone();
		day2.set(Calendar.MINUTE, day2Minute);
		day2.set(Calendar.HOUR_OF_DAY, day2Hour);
		day2.set(Calendar.DAY_OF_WEEK, day2Day);
		
		if (now.after(day1))
		{
			day1.add(Calendar.WEEK_OF_MONTH, 1);
		}
		if (now.after(day2))
		{
			day2.add(Calendar.WEEK_OF_MONTH, 1);
		}
		
		Calendar reenter = day1;
		if (day2.before(day1))
		{
			reenter = day2;
		}
		return reenter.getTimeInMillis() - System.currentTimeMillis();
	}
	
	public static void main(String[] args)
	{
		new Anakim();
	}
}
