

import java.util.ArrayList;
import java.util.List;

public class HolidayInfo {
    public HolidayInfo(String info) {
        String regex = "[，,]";
        String[] data = info.split(regex);
        name = data[0];
        start_time = data[1];
        end_time = data[2];
        dur_time = Integer.valueOf(data[3]);
        List<Integer> timeList = new ArrayList<>();
        ParseDatetime(data[1], timeList);
        startMonth = timeList.get(0);
        startDay = timeList.get(1);
        List<Integer> timeList1 = new ArrayList<>();
        ParseDatetime(data[2], timeList1);
        endMonth = timeList1.get(0);
        endDay = timeList1.get(1);
    }

    private void ParseDatetime(String time, List<Integer> datalist) {
        if (datalist == null)
            datalist = new ArrayList<>();

        String regex = "[月日]";
        String[] data = time.split(regex);
        int month = 0;
        int day = 0;
        if (data.length > 0)
            month = Integer.valueOf(data[0]);

        if (data.length > 1)
            day = Integer.valueOf(data[1]);

        datalist.add(month);
        datalist.add(day);
    }

    private void setStartMonthandDay(String start_time) {
        List<Integer> datalist = new ArrayList<Integer>();
        ParseDatetime(start_time, datalist);
        startMonth = datalist.get(0);
        startDay = (datalist.get(1));
    }

    private void setEndMonthandDay(String end_time) {
        List<Integer> datalist = new ArrayList<Integer>();
        ParseDatetime(end_time, datalist);
        endMonth = (datalist.get(0));
        endDay = (datalist.get(1));
    }

    public int getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    public String getStart_time() {
        return start_time;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public String getEnd_time() {
        return end_time;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public int getDur_time() {
        return dur_time;
    }

    public HolidayInfo(int year, String name, String start_time, String end_time, int dur_time) {
        this.year = year;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.dur_time = dur_time;
        setStartMonthandDay(start_time);
        setEndMonthandDay(end_time);
    }

    int year;
    String name;
    String start_time;
    int startMonth;
    int startDay;
    String end_time;
    int endMonth;
    int endDay;
    int dur_time;

}
