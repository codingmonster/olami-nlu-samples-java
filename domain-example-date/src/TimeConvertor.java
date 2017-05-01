
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class TimeConvertor {

	/**
	 * 该方法提供两个时区之间时间的转换
	 * @param time 原始时区的时间
	 * @param origin 原始地点
	 * @param destination 目的地点
	 * @param bValidPlace 表示原始地点或者目的地点是否是已知时区
	 * @return 返回转换之后的时间
	 * <br>
	 * 例如：当需要将北京八点转换为纽约时间的话，time为八点，origin为北京，destination为纽约。
	 */
	public static Calendar TimeZoneConvertor(Calendar time, String origin, String destination,
			MutableBoolean bValidPlace) {
		if (bValidPlace == null)
			bValidPlace = new MutableBoolean();
		bValidPlace.setValue(true);
		String oriTimeZoneId = getTimeZoneId(origin);
		String desTimeZoneId = getTimeZoneId(destination);

		if (oriTimeZoneId == null || desTimeZoneId == null) {
			bValidPlace.setValue(false);
			return time;
		}
		Calendar ori = Calendar.getInstance(TimeZone.getTimeZone(oriTimeZoneId));

		ori.set(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH),
				time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.SECOND));
		ori.set(Calendar.MILLISECOND, time.get(Calendar.MILLISECOND));
		ori.getTimeInMillis();

		ori.setTimeZone(TimeZone.getTimeZone(desTimeZoneId));
		ori.getTimeInMillis();
		
		return ori;

	}

	public long getUTCTime(String destination) {
		Calendar c = Calendar.getInstance();
		int orizoneOffset = c.get(Calendar.ZONE_OFFSET);
		int oridstOffset = c.get(Calendar.DST_OFFSET);
		TimeZone tz = TimeZone.getTimeZone(getTimeZoneId(destination));
		int deszoneOffset = tz.getOffset(0);
		int desdstOffset = tz.getDSTSavings();
		c.add(Calendar.MILLISECOND, (deszoneOffset + desdstOffset) - (orizoneOffset + oridstOffset));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * 公历日期转换为农历日期
	 * @param time 需要转换为农历的公历时间
	 * @param bLeapMonth 对应的农历月是否为闰月
	 * @return 转换之后的农历日期
	 */
	public static Calendar getLunarDate(Calendar time, MutableBoolean bLeapMonth) {

		Calendar lunaDate = Calendar.getInstance();
		MutableInt LunarYear = new MutableInt();
		MutableInt LunarMonth = new MutableInt();
		MutableInt LunarDay = new MutableInt();
		DateUtil.GregorianToLunarDate(time.get(Calendar.YEAR), time.get(Calendar.MONTH),
				time.get(Calendar.DAY_OF_MONTH), LunarYear, LunarMonth, LunarDay, bLeapMonth);
		lunaDate.set(Calendar.YEAR, LunarYear.intValue());
		lunaDate.set(Calendar.MONTH, LunarMonth.intValue());
		lunaDate.set(Calendar.DAY_OF_MONTH, LunarDay.intValue());
		return lunaDate;
	}

	/**
	 * 查询指定时间对应的节日
	 * @param time 指定的时间
	 * @param festivalList 返回的节日列表
	 * @return 如果指定时间是节日，则返回true。
	 * <br><br>
	 * 复活节不能用固定格式来表示，所以需要特殊处理。
	 * <br>
	 * 清明是农历二十四节气之一，但是一般意义上也是节日。所以也需要特殊处理。
	 */
	public static boolean getFestival(Calendar time, List<String> festivalList) {
		String answer = "";
		int month = time.get(Calendar.MONTH) + 1;
		int day = time.get(Calendar.DAY_OF_MONTH);

		for (int i = 0; i < DateUtil.festival_mday.length; i++) {
			if ((DateUtil.festival_mday[i].month == month) && (DateUtil.festival_mday[i].day == day)) {
				answer = (DateUtil.festival_mday[i].festivalPrefix + DateUtil.festival_mday[i].festivalName
						+ DateUtil.festival_mday[i].festivalPostfix);
				festivalList.add(answer);
			}
		}

		int nWeekIndex = ((time.get(Calendar.DAY_OF_MONTH) + 6) / 7);
		month = time.get(Calendar.MONTH) + 1;
		Calendar tmLastDay = (Calendar) time.clone();
		tmLastDay.set(Calendar.DAY_OF_MONTH, 1);
		tmLastDay.add(Calendar.MONTH, 1);
		tmLastDay.add(Calendar.DAY_OF_MONTH, -1);

		int nLastWeekIndex = (tmLastDay.get(Calendar.DAY_OF_MONTH) - time.get(Calendar.DAY_OF_MONTH)) / 7 + 1;
		for (int i = 0; i < DateUtil.festival_wday.length; i++) {
			if ((DateUtil.festival_wday[i].month == month)
					&& ((DateUtil.festival_wday[i].day == DateUtil.WEEKDAY(nWeekIndex, time.get(Calendar.DAY_OF_WEEK)))
							|| (DateUtil.festival_wday[i].day == DateUtil.WEEKDAY(DateUtil.LASTWEEK(nLastWeekIndex),
									time.get(Calendar.DAY_OF_WEEK))))) {
				answer = (DateUtil.festival_wday[i].festivalPrefix + DateUtil.festival_wday[i].festivalName
						+ DateUtil.festival_wday[i].festivalPostfix);
				festivalList.add(answer);
			}
		}

		int nDayCount = 0;
		int nLastLunarDays = 0;
		MutableBoolean bLeapMonth = new MutableBoolean();
		Calendar lunarDate = getLunarDate(time, bLeapMonth);
		int nLunarMonth = lunarDate.get(Calendar.MONTH);
		month = lunarDate.get(Calendar.MONTH) + 1;
		day = lunarDate.get(Calendar.DAY_OF_MONTH);
		LunarData LunarCalendar = new LunarData(DateUtil.LunarCalendarTable[lunarDate.get(Calendar.YEAR) - 1900 - 1]);
		if (LunarCalendar.LeapMon() != 0) {
			if ((bLeapMonth.booleanValue() && (nLunarMonth == (LunarCalendar.LeapMon() - 1)))
					|| (nLunarMonth > (LunarCalendar.LeapMon() - 1))) {
				nLunarMonth++;
			}
		}
		if ((LunarCalendar.LargeMon() & (1 << (12 - nLunarMonth))) != 0) {
			nDayCount = 30;
		} else {
			nDayCount = 29;
		}
		nLastLunarDays = (nDayCount - lunarDate.get(Calendar.DAY_OF_MONTH) + 1);
		for (int i = 0; i < DateUtil.festival_lunarmday.length; i++) {
			if ((DateUtil.festival_lunarmday[i].month == month) && ((DateUtil.festival_lunarmday[i].day == day)
					|| (DateUtil.festival_lunarmday[i].day == DateUtil.LASTDAY(nLastLunarDays)))) {
				answer = (DateUtil.festival_lunarmday[i].festivalPrefix + DateUtil.festival_lunarmday[i].festivalName
						+ DateUtil.festival_lunarmday[i].festivalPostfix);
				festivalList.add(answer);
			}
		}

		// 复活节
		MutableInt nEasterYear = new MutableInt(time.get(Calendar.YEAR));
		MutableInt nEasterMonth = new MutableInt(0);
		MutableInt nEasterDay = new MutableInt(0);
		GetEasterDay(nEasterYear, nEasterMonth, nEasterDay);
		if ((time.get(Calendar.YEAR) == nEasterYear.intValue())
				&& (time.get(Calendar.MONTH) == (nEasterMonth.intValue() - 1))
				&& (time.get(Calendar.DAY_OF_MONTH) == nEasterDay.intValue())) {
			answer = "复活节";
			festivalList.add(answer);
		}

		// 清明节
		answer = GetQingming(time);
		if (answer != null && !answer.isEmpty())
			festivalList.add(answer);

		if (festivalList == null || festivalList.isEmpty())
			return false;
		return true;
	}

	/**
	 * 查询指定时间对应的节气
	 * @param time 指定的时间
	 * @param isExactJieQi 指定的日期刚好是节气当天的话，返回true。
	 * @return 节气名
	 */
	public static String GetJieQi(Calendar time, MutableBoolean isExactJieQi) {
		int nJieQi1Index = time.get(Calendar.MONTH) * 2;
		int nJieQi2Index = time.get(Calendar.MONTH) * 2 + 1;
		MutableInt nJieQi1Month = new MutableInt(time.get(Calendar.MONTH));
		MutableInt nJieQi2Month = new MutableInt(time.get(Calendar.MONTH));
		MutableInt nJieQi1Day = new MutableInt(time.get(Calendar.DAY_OF_MONTH));
		MutableInt nJieQi2Day = new MutableInt(time.get(Calendar.DAY_OF_MONTH));
		String wsJieQi;
		MutableInt year = new MutableInt(time.get(Calendar.YEAR));

		GetJieDate(year, nJieQi1Index, nJieQi1Month, nJieQi1Day);
		GetJieDate(year, nJieQi2Index, nJieQi2Month, nJieQi2Day);

		if (time.get(Calendar.DAY_OF_MONTH) < nJieQi1Day.intValue()) {
			wsJieQi = DateUtil.JieQiNames[(nJieQi1Index + 23) % 24];
		} else if (time.get(Calendar.DAY_OF_MONTH) < nJieQi2Day.intValue()) {
			wsJieQi = DateUtil.JieQiNames[nJieQi1Index];
		} else {
			wsJieQi = DateUtil.JieQiNames[nJieQi2Index];
		}

		if ((time.get(Calendar.DAY_OF_MONTH) == nJieQi1Day.intValue())
				|| (time.get(Calendar.DAY_OF_MONTH) == nJieQi2Day.intValue())) {
			isExactJieQi.setValue(true);
		} else {
			isExactJieQi.setValue(false);
		}

		return (wsJieQi);
	}

	/**
	 * 查询指定时间是否为农历闰年
	 * @param time 指定的时间
	 * @return 是农历闰年是返回true
	 */
	public static boolean IsLunarLeapYear(Calendar time) {
		if ((time.get(Calendar.YEAR) - 1901 < 0) || (time.get(Calendar.YEAR) - 1901 > 200)) {
			return (false);
		}
		LunarData LunarCalendar = new LunarData(DateUtil.LunarCalendarTable[time.get(Calendar.YEAR) - 1901]);

		if (LunarCalendar.LeapMon() != 0) {
			return (true);
		} else {
			return (false);
		}
	}

	/**
	 * 查询指定时间是否为公历闰年
	 * @param time 指定的时间
	 * @return 是公历闰年是返回true
	 */
	public static boolean IsGregorianLeapYear(Calendar time) {
		boolean ret = false;
		int year = time.get(Calendar.YEAR);
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
			ret = true;

		return ret;
	}

	/**
	 * 根据当前节气名称获取下一个节气
	 * @param wsCurrent 节气名称
	 * @return 下一个节气名
	 */
	public static String GetNextJieQi(String wsCurrent) {
		int i = 0;
		boolean foundCurrent = false;
		for (i = 0; i < DateUtil.JieQiNames.length; i++) {
			if (DateUtil.JieQiNames[i] == wsCurrent) {
				foundCurrent = true;
				break;
			}
		}
		if (foundCurrent) {
			return (DateUtil.JieQiNames[(i + 1) % 24]);
		} else {
			return ("");
		}
	}

	/**
	 * 根据当前节气名称获取上一个节气
	 * @param wsCurrent 节气名称
	 * @return 上一个节气名
	 */
	public static String GetPrevJieQi(String wsCurrent) {
		int i = 0;
		boolean foundCurrent = false;
		for (i = 0; i < DateUtil.JieQiNames.length; i++) {
			if (DateUtil.JieQiNames[i] == wsCurrent) {
				foundCurrent = true;
				break;
			}
		}
		if (foundCurrent) {
			return (DateUtil.JieQiNames[(i - 1 + 24) % 24]);
		} else {
			return ("");
		}
	}

	/**
	 * 根据名称查询对应时区
	 * @param value 名称
	 * @return 名称对应的时区
	 * <br><br>
	 * 目前只支持部分国内外城市的数据
	 */
	private static String getTimeZoneId(String value) {
		return TimezoneInfo.timezonemap.get(value);
	}

	public static boolean GetEasterDay(MutableInt year, MutableInt month, MutableInt day) {
		int N = year.intValue() - 1900;
		int A = N % 19;
		int Q = N / 4;
		int B = (7 * A + 1) / 19;
		int M = (11 * A + 4 - B) % 29;
		int W = (N + Q + 31 - M) % 7;
		int result = 25 - M - W;

		if (result == 0) {
			month.setValue(3);
			day.setValue(31);
		} else if (result > 0) {
			month.setValue(4);
			day.setValue(result);
		} else {
			month.setValue(3);
			day.setValue(31 + result);
		}

		return (true);
	}

	private static String GetQingming(Calendar time) {
		MutableBoolean isExactJieQi = new MutableBoolean(false);
		String jieqiName = GetJieQi(time, isExactJieQi);
		if (isExactJieQi.isTrue() && jieqiName.equals("清明"))
			return jieqiName + "节";
		return null;
	}

	private static boolean GetJieDate(MutableInt miYear, int jieqiindex, MutableInt miMonth, MutableInt miDay) {
		int year = miYear.intValue();
		int month = miMonth.intValue();
		int day = miDay.intValue();
		final double x_1900_1_6_2_5 = 693966.08680556;
		final int termInfo[] = { 0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693,
				263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758 };

		int _days = (int) (x_1900_1_6_2_5 + 365.2422 * (year - 1900) + termInfo[jieqiindex] / (60. * 24));
		final int mdays[] = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };
		int diff;
		int days;

		days = (int) (100 * (_days - _days / (3652425L / (3652425L - 3652400L))));
		year = days / 36524;
		days %= 36524;
		month = 1 + days / 3044; /* [1..12] */
		day = 1 + (days % 3044) / 100; /* [1..31] */

		diff = year * 365 + year / 4 - year / 100 + year / 400 + mdays[month - 1] + day - _days;
		if (month <= 2 && ((year & 3) == 0) && ((year % 100) != 0 || year % 400 == 0)) {
			diff--;
		}

		if (diff > 0 && diff >= day) /* ~0.5% */
		{
			if (month == 1) {
				--year;
				month = 12;
				day = 31 - (diff - day);
			} else {
				day = mdays[month - 1] - (diff - day);
				if (--month == 2) {
					if (((year & 3) == 0) && ((year % 100) != 0 || year % 400 == 0)) {
						day++;
					}
				}
			}
		} else {
			if ((day -= diff) > mdays[month]) /* ~1.6% */
			{
				if (month == 2) {
					if (((year & 3) == 0) && ((year % 100) != 0 || year % 400 == 0)) {
						if (day != 29) {
							month = 3;
							day -= 29;
						}
					} else {
						month = 3;
						day -= 28;
					}
				} else {
					day -= mdays[month];
					if (month++ == 12) {
						++year;
						month = 1;
					}
				}
			}
		}

		// 以上的计算有些误差，对于2000~2030年做了些Patch
		for (int i = 0; i < DateUtil.JieQiPatchTable.length; i++) {
			if ((year == DateUtil.JieQiPatchTable[i].year) && (jieqiindex == DateUtil.JieQiPatchTable[i].index)) {
				day = DateUtil.JieQiPatchTable[i].day;
				break;
			}
		}

		miYear.setValue(year);
		miMonth.setValue(month);
		miDay.setValue(day);

		return (true);
	}
}
