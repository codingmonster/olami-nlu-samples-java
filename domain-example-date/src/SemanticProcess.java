import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SemanticProcess {

    public static final int YEAR_INDEX = 0x1;
    public static final int MONTH_INDEX= 0x2;
    public static final int DAY_INDEX = 0x4;
    public static final int HOUR_INDEX = 0x8;
    public static final int MINUTE_INDEX = 0x10;
    public static final int SECOND_INDEX = 0x20;
    public static final int WEEKDAY_INDEX = 0x40;
    public static final int WEEKINDEX_INDEX = 0x80;
    
	/**
	 * 根据Semantic，输出最终结果
	 * @param semantic	语义分析结果
	 * @return	返回最终结果
	 */
	public static JSONObject process (JSONObject semantic) {
		Semantic s = parseSemantic(semantic);

		JSONObject result = new JSONObject();
		try {
			if (0 != s.status) {
				result.put("answer", s.answer);
			} else {
				// handle slots & modifier
				int index = GetOutputField(s.modifiers);
				String answer = "";
				Slot time = s.slots.get("time");
				String timeType = "";
				if (time != null) {
					timeType = time.datetime.optString("type", "");
				}
				if (s.modifiers.contains("query_time") || s.modifiers.contains("query_gregorian")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						String dst = "";
						String src = "";
						if (s.slots.get("place_dst") != null || s.slots.get("place_src") != null) {
							dst = (s.slots.get("place_dst") != null) ? s.slots.get("place_dst").value : "";
							src = (s.slots.get("place_src") != null) ? s.slots.get("place_src").value : "";
							c = TimeConvertor.TimeZoneConvertor(c, src, dst, null);
						}
						if (0 == index) {
							if (s.modifiers.contains("query_time")) {
								index = HOUR_INDEX | MINUTE_INDEX;
							} else if (s.modifiers.contains("query_gregorian")) {
								index = MONTH_INDEX | DAY_INDEX;
							}
						}
						answer = src + ((time == null) ? "现在" : time.value) + "是" + dst + GetOutputAnswer(c, index);
					}
				} else if (s.modifiers.contains("query_lunar")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						answer = time.value + "是";
						
						Calendar c = GetInputTime(time);
						MutableBoolean isLeapMonth = new MutableBoolean(false);
				        Calendar lunarc = TimeConvertor.getLunarDate(c, isLeapMonth);
				        
				        // 如果index为0，默认输出月日
				        if (0 == index) {
				        	index = MONTH_INDEX | DAY_INDEX;
				        }
				        
				        if ((index & YEAR_INDEX) != 0) {
				            answer += GetLunarNianFen(lunarc.get(Calendar.YEAR) - 1900);
				        }
				        if ((index & MONTH_INDEX) != 0) {
				            String LunarMonthName[] = { "正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月",
				                    "十一月", "腊月" };
				            if (isLeapMonth.isTrue()) {
				                answer += "闰";
				            }
				            answer += LunarMonthName[lunarc.get(Calendar.MONTH)];
				        }
				        if ((index & DAY_INDEX) != 0) {
				            int day = lunarc.get(Calendar.DAY_OF_MONTH);
				            if (day > 10) {
				            	answer += day;
				            } else {
				            	answer += "初" + day;
				            }
				        }
					}
				} else if (s.modifiers.contains("query_festival")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						String festival = genFestivalString(c);
						if (festival.isEmpty()) {
							answer = time.value + "不是节日";
						} else {
							answer = time.value + "是" + festival;
						}
					}
				} else if (s.modifiers.contains("query_duration_festival")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						Calendar end_time = GetInputEndTime(time);
						String festival = genDurationFestival(c, end_time);
						if (festival.isEmpty()) {
							answer = time.value + "没有节日";
						} else {
							answer = time.value + "有以下节日：" + festival;
						}
					}
				} else if (s.modifiers.contains("query_jieqi")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						MutableBoolean isExactJieQi = new MutableBoolean(false);
						String curJieqi = TimeConvertor.GetJieQi(c, isExactJieQi);
						if (s.modifiers.contains("prev")) {
							answer = "上一个节气是" + TimeConvertor.GetPrevJieQi(curJieqi);
						} else if (s.modifiers.contains("next")) {
							answer = "下一个节气是" + TimeConvertor.GetNextJieQi(curJieqi);
						} else if (!curJieqi.isEmpty() && !isExactJieQi.isTrue()) {
							answer = time.value + "属于" + curJieqi;
						} else {
							answer = time.value + "是" + curJieqi;
						}
					}
				} else if (s.modifiers.contains("query_duration_jieqi")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						Calendar end_time = GetInputEndTime(time);
						String jieqi = "";
						MutableBoolean isExactJieQi = new MutableBoolean(false);
						while (c.before(end_time)) {
			                isExactJieQi.setValue(false);
			                String today = TimeConvertor.GetJieQi(c, isExactJieQi);
			                if (!today.isEmpty() && isExactJieQi.isTrue()) {
			                    if (!jieqi.isEmpty()) 
			                    	jieqi += "，";
			                    jieqi += today;
			                }
			                c.add(Calendar.DAY_OF_MONTH, 1);// 每次加一天
			            }
						if (jieqi.isEmpty()) {
							answer = time.value + "没有节气";
						} else {
							answer = time.value + "有以下节气：" + jieqi;
						}
					}
				} else if (s.modifiers.contains("query_holiday")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						Calendar end_time = GetInputEndTime(time);

				        int year = c.get(Calendar.YEAR);

				        List<HolidayInfo> holidayList = Holiday.getValue(year);

				        if (holidayList == null || holidayList.isEmpty()) {
				            answer = c.get(Calendar.YEAR) + "年的数据我暂时没有。";
				        } else {
				            List<HolidayInfo> holidaylist = new ArrayList<>();
					        String fesitevalName = genDurationFestival(c, end_time);
					        for (HolidayInfo info : holidayList) {
					            if (info.getName() == null || info.getName().isEmpty()) {
					                continue;
					            }
					            
					            if (fesitevalName.contains(info.getName())) {
					                holidaylist.add(info);
					            }
					        }

					        if (holidaylist.isEmpty()) {
					        	answer = time.value + "没有国定节假日";
					        } else {
					        	for (HolidayInfo holiday : holidaylist) {
					                if (!answer.isEmpty())
					                    answer += "，";
					                answer += holiday.getName() + "(" + holiday.getStart_time() + "-"
					                        + holiday.getEnd_time() + ")";
					            }
					            answer = time.value + "的放假安排如下：" + answer;
					        }
				        }
					}
				} else if (s.modifiers.contains("query_rizi")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						String rizi = "";
						rizi = genFestivalString(c);
						if (!rizi.isEmpty()) {
							answer = time.value + "是" + rizi;
						} else {
							MutableBoolean isExactJieQi = new MutableBoolean(false);
					        String jieqi = TimeConvertor.GetJieQi(c, isExactJieQi);
					        if (isExactJieQi.isTrue()) {
					        	answer = time.value + "是" + jieqi;
					        }
						}
						if (answer.isEmpty()) 
							answer = time.value + "不是什么特别的日子。";
					}
				} else if (s.modifiers.contains("query_week")) {
					long duration = 0;
					if ("time_recommend".equalsIgnoreCase(timeType)) {
						Calendar c = GetInputTime(time);
						Calendar end_time = GetInputEndTime(time);
						duration = (end_time.getTimeInMillis() - c.getTimeInMillis()) / 1000;
					} else if ("time_semantic".equalsIgnoreCase(timeType)) {
						JSONObject data = time.datetime.optJSONObject("data");
						String sub_type = data.optString("sub_type", "");
						if ("duration".equalsIgnoreCase(sub_type)) {
							JSONObject time_struct = data.optJSONObject("time_struct");
							int year = time_struct.optInt("Year", 0);
							int month = time_struct.optInt("Month", 0);
							int day = time_struct.optInt("Day", 0);
							int hour = time_struct.optInt("Hour", 0);
							int minute = time_struct.optInt("Minute", 0);
							int second = time_struct.optInt("Second", 0);
							int week = time_struct.optInt("Week", 0);
							duration = (((year * 365 + month * 30 + day + week * 7) * 24 + hour) * 60 + minute) * 60 + second;
						} else if ("repeat".equalsIgnoreCase(sub_type)) {
							answer = "输入时间有误！";
						} else {
							answer = "时间解析有误！";
						}
					} else {
						answer = "输入时间有误！";
					}
					if (answer.isEmpty() && 0 != duration) {
						long days = duration / (3600*24);
						long day = days % 7;// 得到余下几天
		                long week = days / 7;// 得到多少周
						if (day > 0)
							answer = time.value + "有" + week + "周多" + day + "天";
						else
							answer = time.value + "有" + week + "周";
					}
				} else if (s.modifiers.contains("query_past_duration")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						answer = time.value + "过去了";
						if (c.after(Calendar.getInstance())) {
							answer = time.value + "好像还没有到，还有";
						}
						answer += CalDuration(c, index);
					}
				} else if (s.modifiers.contains("query_future_duration")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						answer = "离" + time.value + "还有";
						if (c.before(Calendar.getInstance())) {
							answer = time.value + "好像已经过去了";
						}
						answer += CalDuration(c, index);
					}
				} else if (s.modifiers.contains("query_duration")) {
					long duration = 0;
					Calendar c = null;
					if ("time_recommend".equalsIgnoreCase(timeType)) {
						c = GetInputTime(time);
						Calendar end_time = GetInputEndTime(time);
						duration = (end_time.getTimeInMillis() - c.getTimeInMillis()) / 1000;
					} else if ("time_semantic".equalsIgnoreCase(timeType)) {
						JSONObject data = time.datetime.optJSONObject("data");
						String sub_type = data.optString("sub_type", "");
						if ("duration".equalsIgnoreCase(sub_type)) {
							JSONObject time_struct = data.optJSONObject("time_struct");
							int year = time_struct.optInt("Year", 0);
							int month = time_struct.optInt("Month", 0);
							int day = time_struct.optInt("Day", 0);
							int hour = time_struct.optInt("Hour", 0);
							int minute = time_struct.optInt("Minute", 0);
							int second = time_struct.optInt("Second", 0);
							int week = time_struct.optInt("Week", 0);
							duration = (((year * 365 + month * 30 + day + week * 7) * 24 + hour) * 60 + minute) * 60 + second;
						} else if ("repeat".equalsIgnoreCase(sub_type)) {
							answer = "输入时间有误！";
						} else {
							answer = "时间解析有误！";
						}
					} else {
						answer = "输入时间有误！";
					}
					if ((index & DAY_INDEX) != 0) {
			            answer = time.value + "有" + duration / (3600*24) + "天";
			        }
					if (s.modifiers.contains("week")) {
						long days = duration / (3600*24);
						long day = days % 7;// 得到余下几天
		                long week = days / 7;// 得到多少周
						if (day > 0)
							answer = time.value + "有" + week + "周多" + day + "天";
						else
							answer = time.value + "有" + week + "周";
			        }
			        if (s.modifiers.contains("weekend")) {
			        	if ("time_recommend".equalsIgnoreCase(timeType)) {
							long allDayCount = (long) (duration / (60 * 60 * 24));
							long lDayCount = allDayCount;
							long lWorkDayCount = (lDayCount / 7) * 5;
							lDayCount %= 7;

							while (lDayCount >= 0) {
								if ((((c.get(Calendar.DAY_OF_WEEK) + lDayCount - 1) % 7) != 0)
										&& (((c.get(Calendar.DAY_OF_WEEK) + lDayCount - 1) % 7) != 6)) {
									lWorkDayCount++;
								}
								lDayCount--;
							}
							long weekendCount = allDayCount + 1 - lWorkDayCount;
							if (weekendCount % 2 != 0)
								weekendCount = weekendCount / 2 + 1;
							else
								weekendCount /= 2;
							if (weekendCount > 0) {
					            answer = time.value + "有" + weekendCount + "个周末。";
					        } else {
					            answer = time.value + "没有周末。";
					        }
			        	} else if ("time_semantic".equalsIgnoreCase(timeType)) {
			        		answer = "输入时间必须有具体的起始点才能计算有多少个周末！";
			        	}
			        }
			        if (s.modifiers.contains("workday")) {
						if ("time_recommend".equalsIgnoreCase(timeType)) {
							long lDayCount = (long) (duration / (60 * 60 * 24));
							long lWorkDayCount = (lDayCount / 7) * 5;
							lDayCount %= 7;

							while (lDayCount >= 0) {
								if ((((c.get(Calendar.DAY_OF_WEEK) + lDayCount - 1) % 7) != 0)
										&& (((c.get(Calendar.DAY_OF_WEEK) + lDayCount - 1) % 7) != 6)) {
									lWorkDayCount++;
								}

								lDayCount--;
							}
							if (lWorkDayCount > 0) {
								answer = "不考虑假日调休，" + time.value + "有" + lWorkDayCount + "个工作日。";
							} else {
								answer = "不考虑假日调休，" + time.value + "没有工作日。";
							}
						} else if ("time_semantic".equalsIgnoreCase(timeType)) {
							answer = "输入时间必须有具体的起始点才能计算有多少个工作日！";
						}
			        }
				} else if (s.modifiers.contains("query_run_year")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
				        MutableBoolean isLeapMonth = new MutableBoolean(false);
						Calendar lunarc = TimeConvertor.getLunarDate(c, isLeapMonth);
						boolean islunarrun = TimeConvertor.IsLunarLeapYear(lunarc);
						boolean isGregorianrun = TimeConvertor.IsGregorianLeapYear(c);
						if (islunarrun && isGregorianrun)
							answer = time.value + "既是公历闰年又是农历闰年";
						else if (!islunarrun && isGregorianrun)
							answer = time.value + "是公历闰年但不是农历闰年";
						else if (islunarrun && !isGregorianrun)
							answer = time.value + "是农历闰年但不是公历闰年";
						else answer = time.value + "不是闰年";
					}
				} else if (s.modifiers.contains("query_run_month")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
				        MutableBoolean isLeapMonth = new MutableBoolean(false);
						Calendar lunarc = TimeConvertor.getLunarDate(c, isLeapMonth);
						if (isLeapMonth.isTrue())
			                answer = time.value + "是闰月";
			            else {
			                // 如果用户说的是“2012年农历4月”，这时候需要把月份加一，看下一个农历月是不是润4月
			                // AppTime是否能够获取到用户输入是不是农历时间？
			                int month = lunarc.get(Calendar.MONTH);
			                c.add(Calendar.MONTH, 1);
			                Calendar nextlunarc = TimeConvertor.getLunarDate(c, isLeapMonth);
			                if (isLeapMonth.isTrue() && (month == nextlunarc.get(Calendar.MONTH)))
			                    answer = time.value + "是闰月";
			            }
					}
				} else if (s.modifiers.contains("query_have_run_month")) {
					if (time != null && !"time_recommend".equalsIgnoreCase(timeType)) {
						answer = "输入时间有误！";
					} else {
						Calendar c = GetInputTime(time);
						Calendar end_time = GetInputEndTime(time);
						long durationdays = (end_time.getTimeInMillis() - c.getTimeInMillis()) / (1000 * 3600 * 24);
						String LeapMonth = "";
						while (c.before(end_time)) {
							MutableBoolean isLeapMonth = new MutableBoolean(false);
							Calendar lunarctime = TimeConvertor.getLunarDate(c, isLeapMonth);
							if (isLeapMonth.isTrue()) {
								// 如果是闰月
								LeapMonth = "闰农历" + (lunarctime.get(MONTH_INDEX) + 1) + "月";
								answer = LeapMonth;
								break;
							}
							if (durationdays > 31)
								c.add(Calendar.MONTH, 1);// 每次加一个月
							else
								c.add(Calendar.DAY_OF_MONTH, 1);// 每次加一天
						}
						if(answer.isEmpty()){
				            if(durationdays > 31)
				                answer = "没有闰月";
				            else
				                answer = "不是闰月";
				        }
				        else {
				            if(durationdays > 31)
				            	answer = time.value + "有" + answer;
				            else answer = time.value + "是" + answer;
				        }
					}
				}
				result.put("answer", answer);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String CalDuration(Calendar time, int index) {
		String duration = "";
		if (0 == index) {
			index = DAY_INDEX | HOUR_INDEX | MINUTE_INDEX;
		}
		Calendar now = Calendar.getInstance();
		double dblDiff = Math.abs((time.getTimeInMillis() - now.getTimeInMillis()) / 1000);
        long lcount = 0;

		if (((index & MINUTE_INDEX) != 0) || ((index & HOUR_INDEX) != 0)
				|| ((((index & MINUTE_INDEX) != 0) || ((index & HOUR_INDEX) != 0)) && ((index & DAY_INDEX) != 0))) {
			String value = "";
			lcount = (long) ((dblDiff / 60) + 0.5f);
			if (lcount > 0) {
				if ((lcount % 60) != 0) {
					value = (lcount % 60) + "分钟";
				}
				lcount /= 60;
				if (lcount > 0) {
					value = (lcount % 24) + "小时" + value;
				}
				lcount /= 24;
				if (lcount > 0) {
					value = (lcount) + "天" + value;
				}

				duration = value;
			} else {
				// isNow.setValue(true);
			}
        } else if (((index & DAY_INDEX) != 0)) {
            // 到查询之日的这个时候
        	time.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        	time.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            dblDiff = Math.abs((time.getTimeInMillis() - now.getTimeInMillis()) / 1000);

            lcount = (long) ((dblDiff / (60 * 60 * 24)) + 0.5f);
            if (lcount > 0) {
            	duration = lcount + "天";
            } else {
            	duration = "不到一天";
            }
        } else if (((index & WEEKINDEX_INDEX) != 0)) {
            // 到查询之日的这个时候
        	time.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        	time.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            dblDiff = Math.abs((time.getTimeInMillis() - now.getTimeInMillis()) / 1000);

            lcount = (long) ((dblDiff / (60 * 60 * 24 * 7)) + 0.5f);
            if (lcount > 0) {
            	duration = lcount + "周";
                if (lcount >= ((dblDiff / (60 * 60 * 24 * 7)) + 1.0f / 14)) // 1.0f/14是半天
                {
                	duration += "不到一点";
                } else if (lcount <= ((dblDiff / (60 * 60 * 24 * 7)) - 1.0f / 14)) // 1.0f/14是半天
                {
                	duration += "多一点";
                }
            } else {
            	duration += "不到一周";
            }
        } else if (((index & MONTH_INDEX) != 0)) {
            // 到查询之日的这个时候
        	time.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        	time.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            dblDiff = Math.abs((time.getTimeInMillis() - now.getTimeInMillis()) / 1000);

            lcount = (long) ((dblDiff / (60 * 60 * 24 * 30.5f)) + 0.5f);
            if (lcount > 0) {
            	duration = lcount + "个月";
                if (lcount >= ((dblDiff / (60 * 60 * 24 * 30.5f)) + 1.0f / 30.5)) // 1.0f/30.5是1天
                {
                	duration += "不到一点";
                } else if (lcount <= ((dblDiff / (60 * 60 * 24 * 30.5f)) - 1.0f / 30.5)) // 1.0f/30.5是1天
                {
                	duration += "多一点";
                }
            } else {
            	duration += "不到一个月";
            }
        } else if (((index & YEAR_INDEX) != 0)) {
            // 到查询之日的这个时候
        	time.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
        	time.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            dblDiff = Math.abs((time.getTimeInMillis() - now.getTimeInMillis()) / 1000);

            lcount = Math.abs(time.get(Calendar.YEAR)
                    - now.get(Calendar.YEAR));

            if ((long) ((dblDiff / (60 * 60 * 24)) + 0.5f) > (365 * lcount)) {
            	duration = "超过";
            } else {
            	duration = "不到";
            }

            if (lcount > 0) {
            	duration += lcount + "年";
            } else {
            	duration += "不到一年";
            }
        }
		return duration;
	}

	private static String GetOutputAnswer(Calendar c, int index) {
		String answer = "";
		if ((index & YEAR_INDEX) != 0) {
			answer += c.get(Calendar.YEAR) + "年";
		}
		if ((index & MONTH_INDEX) != 0) {
			int month = c.get(Calendar.MONTH) + 1;
			answer += month + "月";
		}
		if ((index & DAY_INDEX) != 0) {
			answer += c.get(Calendar.DAY_OF_MONTH) + "号";
		}
		if ((index & HOUR_INDEX) != 0) {
			answer += c.get(Calendar.HOUR_OF_DAY) + "点";
		}
		if ((index & MINUTE_INDEX) != 0) {
			answer += c.get(Calendar.MINUTE) + "分";
		}
		if ((index & SECOND_INDEX) != 0) {
			answer += c.get(Calendar.SECOND) + "秒";
		}
		if ((index & WEEKDAY_INDEX) != 0) {
            switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                answer += "星期天";
                break;
            case Calendar.MONDAY:
                answer += "星期一";
                break;
            case Calendar.TUESDAY:
                answer += "星期二";
                break;
            case Calendar.WEDNESDAY:
                answer += "星期三";
                break;
            case Calendar.THURSDAY:
                answer += "星期四";
                break;
            case Calendar.FRIDAY:
                answer += "星期五";
                break;
            case Calendar.SATURDAY:
                answer += "星期六";
                break;
            }
        }
		return answer;
	}

	private static Calendar GetInputTime(Slot time) {
		Calendar calendar = Calendar.getInstance();
		if (time != null) {
			long starttime = time.datetime.optJSONObject("data").optLong("start_time", 0);
			if (0 != starttime) {
				calendar.setTimeInMillis(starttime);
			}
		}
		return calendar;
	}

	private static Calendar GetInputEndTime(Slot time) {
		Calendar calendar = Calendar.getInstance();
		if (time != null) {
			long endtime = time.datetime.optJSONObject("data").optLong("end_time", 0);
			if (0 != endtime) {
				calendar.setTimeInMillis(endtime);
			}
		}
		return calendar;
	}
	
	private static int GetOutputField(List<String> modifiers) {
		int index = 0;
		if (modifiers.contains("year")) {
			index |= YEAR_INDEX;
		}
		if (modifiers.contains("month")) {
			index |= MONTH_INDEX;
		}
		if (modifiers.contains("day")) {
			index |= DAY_INDEX;
		}
		if (modifiers.contains("hour")) {
			index |= HOUR_INDEX;
		}
		if (modifiers.contains("minute")) {
			index |= MINUTE_INDEX;
		}
		if (modifiers.contains("second")) {
			index |= SECOND_INDEX;
		}
		if (modifiers.contains("weekday")) {
			index |= WEEKDAY_INDEX;
		}
		if (modifiers.contains("week")) {
			index |= WEEKINDEX_INDEX;
		}
		return index;
	}
	
    private static String GetLunarNianFen(int LunarYear) {
        String wsTianGan[] = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
        String wsDiZhi[] = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
        String wsAnimals[] = { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

        return (wsTianGan[(LunarYear + 6) % 10] + wsDiZhi[(LunarYear) % 12]
                + wsAnimals[(LunarYear) % 12] + "年");
    }
    
    private static String genFestivalString(Calendar time) {
        String name = "";
        List<String> festivalList = new ArrayList<String>();
        if (TimeConvertor.getFestival(time, festivalList)) {
            for (String festivalName : festivalList) {
                if (!name.isEmpty())
                    name += "，";
                name += festivalName;
            }
        }
        return name;
    }
    
	// 获取一段时间内的节日信息，遍历时间段内的每一天，例：8月份有哪些节日，需要从8月1号遍历到8月31号
	private static String genDurationFestival(Calendar start_time, Calendar end_time) {
		String answer = "";
		while (start_time.before(end_time)) {
			String today = genFestivalString(start_time);
			if (!answer.isEmpty() && !today.isEmpty())
				answer += "，";
			answer += today;
			start_time.add(Calendar.DAY_OF_MONTH, 1);// 每次加一天
		}
		return answer;
	}

	/**
	 * 解析语义分析结果
	 * @param semantic 语义分析结果
	 * @return 返回解析后的结果, 结构请参照{@link Semantic}类
	 */
	private static Semantic parseSemantic(JSONObject semantic) {
		Semantic s = new Semantic();
		if (semantic == null || !"ok".equalsIgnoreCase(semantic.optString("status", ""))) 
			return s;
		
		JSONObject data = semantic.optJSONObject("data");
		JSONArray s_list = data.optJSONArray("nli");
		JSONObject s_first = s_list.optJSONObject(0);
		
		JSONObject desc_obj = s_first.optJSONObject("desc_obj");
		s.status = desc_obj.optInt("status", -1);
		if (0 != s.status) {
			s.answer = desc_obj.optString("result", "some error occured");
			return s;
		}
		
		JSONObject intention = s_first.optJSONArray("semantic").optJSONObject(0);
		JSONArray modifier = intention.optJSONArray("modifier");
		for (int i = 0; i != modifier.length(); i++) {
			s.modifiers.add(modifier.optString(i, ""));
		}
		JSONArray slots = intention.optJSONArray("slots");
		for (int i = 0; i != slots.length(); i++) {
			JSONObject record = slots.optJSONObject(i);
			Slot slot = new Slot();
			slot.name = record.optString("name", "");
			slot.value = record.optString("value", "");
			slot.datetime = record.optJSONObject("datetime");
			s.slots.put(slot.name, slot);
		}
		
		return s;
	}
}

class Semantic {
	String app = "";
	String answer = "";
	Map<String, Slot> slots = new HashMap();
	List<String> modifiers = new ArrayList<>();
	// status -1表示未处理的错误；0表示正常输出；其他数值对应不同的系统错误。
	int status = -1;
}

class Slot {
	String name = "";
	String value = "";
	JSONObject datetime = new JSONObject();
}