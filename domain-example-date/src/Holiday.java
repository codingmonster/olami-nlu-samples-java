
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Holiday {
	private static Map<Integer, List<HolidayInfo>> holiday = new HashMap<>();

	static {
		List<String> infoList = new ArrayList<>();
		infoList.add("年份：2017");
		infoList.add("元旦，1月1日，1月2日，3，");
		infoList.add("春节，1月27日，2月2日，7，");
		infoList.add("清明节，4月2日，4月4日，3，");
		infoList.add("国际劳动节，4月29日，5月1日，3，");
		infoList.add("端午节，5月28日，5月30日，3，");
		infoList.add("国庆节，10月1日，10月7日，7，");
		infoList.add("中秋节，10月8日，10月8日，1，");

		LoadHolidayInfo(infoList);
	}

	private static void LoadHolidayInfo(List<String> infoList) {
		if (infoList == null || infoList.isEmpty())
			return;

		int year = 0;
		List<HolidayInfo> holidayInfo = new ArrayList<>();
		for (String record : infoList) {
			if (record.startsWith("#") || record.length() <= 3)
				continue;

			if (record.startsWith("年份")) {
				if (holidayInfo != null && !holidayInfo.isEmpty() && year != 0)
					holiday.put(year, holidayInfo);

				year = 0;
				if (holidayInfo != null && !holidayInfo.isEmpty())
					holidayInfo.clear();
				// holidayInfo = new ArrayList<>();

				String syear = record.substring(3);
				if (syear == null || syear.isEmpty()) {
					continue;
				}

				try {
					year = Integer.parseInt(syear);
				} catch (Exception e) {
					continue;
				}
			} else
				holidayInfo.add(new HolidayInfo(record));
		}
		if (holidayInfo != null && !holidayInfo.isEmpty() && year != 0)
			holiday.put(year, holidayInfo);
	}
	
    public static List<HolidayInfo> getValue(Integer key) {
        if (key == null) {
            return null;
        }
        return holiday.get(key);
    }
    
}
