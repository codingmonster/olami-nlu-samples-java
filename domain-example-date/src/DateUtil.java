

import java.util.Calendar;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class DateUtil {

    public static int[] LunarCalendarTable = { 0x04AE53, 0x0A5748, 0x5526BD,
            0x0D2650, 0x0D9544, 0x46AAB9, 0x056A4D, 0x09AD42, 0x24AEB6,
            0x04AE4A,/* 1901-1910 */
            0x6A4DBE, 0x0A4D52, 0x0D2546, 0x5D52BA, 0x0B544E, 0x0D6A43,
            0x296D37, 0x095B4B, 0x749BC1, 0x049754,/* 1911-1920 */
            0x0A4B48, 0x5B25BC, 0x06A550, 0x06D445, 0x4ADAB8, 0x02B64D,
            0x095742, 0x2497B7, 0x04974A, 0x664B3E,/* 1921-1930 */
            0x0D4A51, 0x0EA546, 0x56D4BA, 0x05AD4E, 0x02B644, 0x393738,
            0x092E4B, 0x7C96BF, 0x0C9553, 0x0D4A48,/* 1931-1940 */
            0x6DA53B, 0x0B554F, 0x056A45, 0x4AADB9, 0x025D4D, 0x092D42,
            0x2C95B6, 0x0A954A, 0x7B4ABD, 0x06CA51,/* 1941-1950 */
            0x0B5546, 0x555ABB, 0x04DA4E, 0x0A5B43, 0x352BB8, 0x052B4C,
            0x8A953F, 0x0E9552, 0x06AA48, 0x6AD53C,/* 1951-1960 */
            0x0AB54F, 0x04B645, 0x4A5739, 0x0A574D, 0x052642, 0x3E9335,
            0x0D9549, 0x75AABE, 0x056A51, 0x096D46,/* 1961-1970 */
            0x54AEBB, 0x04AD4F, 0x0A4D43, 0x4D26B7, 0x0D254B, 0x8D52BF,
            0x0B5452, 0x0B6A47, 0x696D3C, 0x095B50,/* 1971-1980 */
            0x049B45, 0x4A4BB9, 0x0A4B4D, 0xAB25C2, 0x06A554, 0x06D449,
            0x6ADA3D, 0x0AB651, 0x093746, 0x5497BB,/* 1981-1990 */
            0x04974F, 0x064B44, 0x36A537, 0x0EA54A, 0x86B2BF, 0x05AC53,
            0x0AB647, 0x5936BC, 0x092E50, 0x0C9645,/* 1991-2000 */
            0x4D4AB8, 0x0D4A4C, 0x0DA541, 0x25AAB6, 0x056A49, 0x7AADBD,
            0x025D52, 0x092D47, 0x5C95BA, 0x0A954E,/* 2001-2010 */
            0x0B4A43, 0x4B5537, 0x0AD54A, 0x955ABF, 0x04BA53, 0x0A5B48,
            0x652BBC, 0x052B50, 0x0A9345, 0x474AB9,/* 2011-2020 */
            0x06AA4C, 0x0AD541, 0x24DAB6, 0x04B64A, 0x69573D, 0x0A4E51,
            0x0D2646, 0x5E933A, 0x0D534D, 0x05AA43,/* 2021-2030 */
            0x36B537, 0x096D4B, 0xB4AEBF, 0x04AD53, 0x0A4D48, 0x6D25BC,
            0x0D254F, 0x0D5244, 0x5DAA38, 0x0B5A4C,/* 2031-2040 */
            0x056D41, 0x24ADB6, 0x049B4A, 0x7A4BBE, 0x0A4B51, 0x0AA546,
            0x5B52BA, 0x06D24E, 0x0ADA42, 0x355B37,/* 2041-2050 */
            0x09374B, 0x8497C1, 0x049753, 0x064B48, 0x66A53C, 0x0EA54F,
            0x06B244, 0x4AB638, 0x0AAE4C, 0x092E42,/* 2051-2060 */
            0x3C9735, 0x0C9649, 0x7D4ABD, 0x0D4A51, 0x0DA545, 0x55AABA,
            0x056A4E, 0x0A6D43, 0x452EB7, 0x052D4B,/* 2061-2070 */
            0x8A95BF, 0x0A9553, 0x0B4A47, 0x6B553B, 0x0AD54F, 0x055A45,
            0x4A5D38, 0x0A5B4C, 0x052B42, 0x3A93B6,/* 2071-2080 */
            0x069349, 0x7729BD, 0x06AA51, 0x0AD546, 0x54DABA, 0x04B64E,
            0x0A5743, 0x452738, 0x0D264A, 0x8E933E,/* 2081-2090 */
            0x0D5252, 0x0DAA47, 0x66B53B, 0x056D4F, 0x04AE45, 0x4A4EB9,
            0x0A4D4C, 0x0D1541, 0x2D92B5 /* 2091-2099 */
    };

    public static FESTIVAL_DATA[] festival_mday = {
            new FESTIVAL_DATA(1, 1, "", "元旦", "", false),
            new FESTIVAL_DATA(1, 26, "国际", "海关", "日", true),
            new FESTIVAL_DATA(2, 2, "世界", "湿地", "日", true),
            new FESTIVAL_DATA(2, 7, "国际", "声援南非", "日", true),
            new FESTIVAL_DATA(2, 10, "国际", "气象", "节", true),
            new FESTIVAL_DATA(2, 14, "", "情人", "节", true),
            new FESTIVAL_DATA(2, 21, "国际", "母语", "日", true),
            new FESTIVAL_DATA(2, 24, "", "第三世界青年", "日", true),
            new FESTIVAL_DATA(3, 1, "国际", "海豹", "日", true),
            new FESTIVAL_DATA(3, 3, "全国", "爱耳", "日", true),
            new FESTIVAL_DATA(3, 5, "中国", "青年志愿者服务", "日", true),
            new FESTIVAL_DATA(3, 5, "", "雷锋", "纪念日", true),
            new FESTIVAL_DATA(3, 6, "世界", "青光眼", "日", true),
            new FESTIVAL_DATA(3, 8, "", "三八妇女", "节", false),
            new FESTIVAL_DATA(3, 12, "", "植树", "节", true),
            new FESTIVAL_DATA(3, 12, "", "孙中山逝世", "纪念日", true),
            new FESTIVAL_DATA(3, 14, "", "白色情人", "节", true),
            new FESTIVAL_DATA(3, 14, "国际", "警察", "日", true),
            new FESTIVAL_DATA(3, 15, "", "消费者权益", "日", true),
            new FESTIVAL_DATA(3, 17, "中国", "国医", "节", true),
            new FESTIVAL_DATA(3, 17, "国际", "航海", "日", true),
            new FESTIVAL_DATA(3, 21, "世界", "森林", "日", true),
            new FESTIVAL_DATA(3, 21, "世界", "儿歌", "日", true),
            new FESTIVAL_DATA(3, 21, "", "消除种族歧视国际", "日", true),
            new FESTIVAL_DATA(3, 22, "世界", "水", "日", true),
            new FESTIVAL_DATA(3, 23, "世界", "气象", "日", true),
            new FESTIVAL_DATA(3, 24, "世界", "防治结核病", "日", true),
            new FESTIVAL_DATA(3, 25, "全国", "中小学生安全教育", "日", true),
            new FESTIVAL_DATA(3, 30, "", "巴勒斯坦国土", "日", true),
            new FESTIVAL_DATA(4, 1, "", "愚人", "节", true),
            new FESTIVAL_DATA(4, 2, "国际", "儿童图书", "日", true),
            new FESTIVAL_DATA(4, 7, "世界", "卫生", "日", true),
            new FESTIVAL_DATA(4, 21, "全国", "企业家活动", "日", true),
            new FESTIVAL_DATA(4, 22, "世界", "地球", "日", true),
            new FESTIVAL_DATA(4, 22, "世界", "法律", "日", true),
            new FESTIVAL_DATA(4, 23, "世界", "图书和版权", "日", true),
            new FESTIVAL_DATA(4, 24, "世界", "青年反对殖民主义", "日", true),
            new FESTIVAL_DATA(4, 24, "", "亚非新闻工作者", "日", true),
            new FESTIVAL_DATA(4, 25, "全国", "儿童预防接种宣传", "日", true),
            new FESTIVAL_DATA(4, 26, "世界", "知识产权", "日", true),
            new FESTIVAL_DATA(4, 30, "全国", "交通安全反思", "日", true),
            new FESTIVAL_DATA(5, 1, "国际", "劳动", "节", false),
            new FESTIVAL_DATA(5, 3, "世界", "新闻自由", "日", true),
            new FESTIVAL_DATA(5, 4, "", "五四青年", "节", false),
            new FESTIVAL_DATA(5, 4, "", "五四运动", "纪念日", false),
            new FESTIVAL_DATA(5, 4, "", "科技传播", "日", true),
            new FESTIVAL_DATA(5, 4, "", "青年", "节", true),
            new FESTIVAL_DATA(5, 5, "", "碘缺乏病防治", "日", true),
            new FESTIVAL_DATA(5, 8, "世界", "红十字", "日", true),
            new FESTIVAL_DATA(5, 8, "世界", "微笑", "日", true),
            new FESTIVAL_DATA(5, 12, "国际", "护士", "节", true),
            new FESTIVAL_DATA(5, 15, "国际", "家庭", "日", true),
            new FESTIVAL_DATA(5, 15, "全国", "碘缺乏病防治", "日", true),
            new FESTIVAL_DATA(5, 17, "世界", "电信", "日", true),
            new FESTIVAL_DATA(5, 18, "国际", "博物馆", "日", true),
            new FESTIVAL_DATA(5, 20, "全国", "学生营养", "日", true),
            new FESTIVAL_DATA(5, 22, "", "生物多样性国际", "日", true),
            new FESTIVAL_DATA(5, 23, "国际", "牛奶", "日", true),
            new FESTIVAL_DATA(5, 30, "", "五卅反对帝国主义运动", "纪念日", false),
            new FESTIVAL_DATA(5, 31, "世界", "无烟", "日", true),
            new FESTIVAL_DATA(6, 1, "国际", "儿童", "节", true),
            new FESTIVAL_DATA(6, 5, "世界", "环境", "日", true),
            new FESTIVAL_DATA(6, 6, "全国", "爱眼", "日", true),
            new FESTIVAL_DATA(6, 6, "中国", "人口", "日", true),
            new FESTIVAL_DATA(6, 17, "", "防治荒漠化和干旱", "日", true),
            new FESTIVAL_DATA(6, 20, "世界", "难民", "日", true),
            new FESTIVAL_DATA(6, 22, "中国", "儿童慈善活动", "日", true),
            new FESTIVAL_DATA(6, 23, "国际", "奥林匹克", "日", true),
            new FESTIVAL_DATA(6, 23, "世界", "手球", "日", true),
            new FESTIVAL_DATA(6, 25, "全国", "土地", "日", true),
            new FESTIVAL_DATA(6, 26, "国际", "禁毒", "日", true),
            new FESTIVAL_DATA(6, 26, "国际", "宪章", "日", true),
            new FESTIVAL_DATA(6, 26, "", "禁止药物滥用和非法贩运", "日", true),
            new FESTIVAL_DATA(6, 30, "世界", "青年联欢", "节", true),
            new FESTIVAL_DATA(7, 1, "", "建党", "节", true),
            new FESTIVAL_DATA(7, 1, "中国", "共产党诞生", "日", true),
            new FESTIVAL_DATA(7, 1, "世界", "建筑", "日", true),
            new FESTIVAL_DATA(7, 1, "", "香港回归", "纪念日", true),
            new FESTIVAL_DATA(7, 2, "国际", "体育记者", "日", true),
            new FESTIVAL_DATA(7, 7, "中国", "人民抗日战争", "纪念日", true),
            new FESTIVAL_DATA(7, 11, "世界", "人口", "日", true),
            new FESTIVAL_DATA(7, 26, "世界", "语创立", "日", true),
            new FESTIVAL_DATA(7, 30, "", "非洲妇女", "日", true),
            new FESTIVAL_DATA(8, 1, "", "建军", "节", true),
            new FESTIVAL_DATA(8, 6, "国际", "电影", "节", true),
            new FESTIVAL_DATA(8, 8, "中国", "男子", "节", true),
            new FESTIVAL_DATA(8, 8, "", "爸爸", "节", true),
            new FESTIVAL_DATA(8, 13, "国际", "左撇子", "日", true),
            new FESTIVAL_DATA(8, 15, "", "日本正式宣布无条件投降", "日", true),
            new FESTIVAL_DATA(8, 26, "全国", "律师咨询", "日", true),
            new FESTIVAL_DATA(9, 3, "中国", "抗战胜利", "日", true),
            new FESTIVAL_DATA(9, 8, "国际", "扫盲", "日", true),
            new FESTIVAL_DATA(9, 8, "国际", "新闻工作者", "日", true),
            new FESTIVAL_DATA(9, 10, "中国", "教师", "节", true),
            new FESTIVAL_DATA(9, 14, "世界", "清洁地球", "日", true),
            new FESTIVAL_DATA(9, 16, "国际", "臭氧层保护", "日", true),
            new FESTIVAL_DATA(9, 18, "", "九一八事变", "纪念日", false),
            new FESTIVAL_DATA(9, 18, "中国", "国耻", "日", true),
            new FESTIVAL_DATA(9, 20, "国际", "爱牙", "日", true),
            new FESTIVAL_DATA(9, 21, "国际", "和平", "日", true),
            new FESTIVAL_DATA(9, 27, "世界", "旅游", "日", true),
            new FESTIVAL_DATA(10, 1, "", "国庆", "节", false),
            new FESTIVAL_DATA(10, 1, "国际", "音乐", "日", true),
            new FESTIVAL_DATA(10, 1, "国际", "老人", "节", true),
            new FESTIVAL_DATA(10, 2, "国际", "和平与民主自由斗争", "日", true),
            new FESTIVAL_DATA(10, 4, "世界", "动物", "日", true),
            new FESTIVAL_DATA(10, 8, "全国", "高血压", "日", true),
            new FESTIVAL_DATA(10, 8, "世界", "视觉", "日", true),
            new FESTIVAL_DATA(10, 9, "世界", "邮政", "日", true),
            new FESTIVAL_DATA(10, 9, "万国", "邮联", "日", true),
            new FESTIVAL_DATA(10, 10, "", "辛亥革命", "纪念日", true),
            new FESTIVAL_DATA(10, 10, "世界", "精神卫生", "日", true),
            new FESTIVAL_DATA(10, 13, "国际", "教师", "节", true),
            new FESTIVAL_DATA(10, 13, "世界", "保健", "日", true),
            new FESTIVAL_DATA(10, 14, "世界", "标准", "日", true),
            new FESTIVAL_DATA(10, 15, "国际", "盲人", "节", true),
            new FESTIVAL_DATA(10, 15, "", "白手杖", "节", true),
            new FESTIVAL_DATA(10, 16, "世界", "粮食", "日", true),
            new FESTIVAL_DATA(10, 17, "世界", "消除贫困", "日", true),
            new FESTIVAL_DATA(10, 22, "世界", "传统医药", "日", true),
            new FESTIVAL_DATA(10, 24, "", "联合国", "日", true),
            new FESTIVAL_DATA(10, 24, "世界", "发展信息", "日", true),
            new FESTIVAL_DATA(10, 28, "世界", "男性健康", "日", true),
            new FESTIVAL_DATA(10, 31, "", "万圣", "节", false),
            new FESTIVAL_DATA(10, 31, "世界", "勤俭", "日", true),
            new FESTIVAL_DATA(11, 7, "", "十月社会主义革命", "纪念日", true),
            new FESTIVAL_DATA(11, 8, "中国", "记者", "节", true),
            new FESTIVAL_DATA(11, 9, "", "消防", "节", true),
            new FESTIVAL_DATA(11, 10, "世界", "青年", "节", true),
            new FESTIVAL_DATA(11, 11, "", "光棍", "节", true),
            new FESTIVAL_DATA(11, 11, "国际", "科学与和平周", "", false),
            new FESTIVAL_DATA(11, 12, "", "孙中山诞辰", "纪念日", false),
            new FESTIVAL_DATA(11, 14, "世界", "糖尿病", "日", true),
            new FESTIVAL_DATA(11, 17, "国际", "大学生", "节", true),
            new FESTIVAL_DATA(11, 17, "世界", "学生", "节", true),
            new FESTIVAL_DATA(11, 21, "世界", "问候", "日", true),
            new FESTIVAL_DATA(11, 21, "世界", "电视", "日", true),
            new FESTIVAL_DATA(11, 29, "国际", "声援巴勒斯坦人民国际", "日", true),
            new FESTIVAL_DATA(12, 1, "世界", "艾滋病", "日", true),
            new FESTIVAL_DATA(12, 3, "世界", "残疾人", "日", true),
            new FESTIVAL_DATA(12, 4, "中国", "法制宣传", "日", true),
            new FESTIVAL_DATA(12, 5, "国际", "经济和社会发展志愿人员", "日", true),
            new FESTIVAL_DATA(12, 7, "国际", "民航", "日", true),
            new FESTIVAL_DATA(12, 8, "国际", "儿童电视", "日", true),
            new FESTIVAL_DATA(12, 9, "国际", "反腐败", "日", true),
            new FESTIVAL_DATA(12, 9, "世界", "足球", "日", true),
            new FESTIVAL_DATA(12, 9, "", "一二九运动", "纪念日", false),
            new FESTIVAL_DATA(12, 10, "世界", "人权", "日", true),
            new FESTIVAL_DATA(12, 11, "世界", "防治哮喘", "日", true),
            new FESTIVAL_DATA(12, 12, "", "西安事变", "纪念日", true),
            new FESTIVAL_DATA(12, 13, "", "南京大屠杀", "纪念日", true),
            new FESTIVAL_DATA(12, 15, "世界", "强化免疫", "日", true),
            new FESTIVAL_DATA(12, 20, "", "澳门回归", "纪念日", true),
            new FESTIVAL_DATA(12, 21, "国际", "篮球", "日", true),
            new FESTIVAL_DATA(12, 24, "", "平安夜", "", false),
            new FESTIVAL_DATA(12, 25, "", "圣诞", "节", false),
            new FESTIVAL_DATA(12, 26, "", "节礼", "节", false),
            new FESTIVAL_DATA(12, 29, "国际", "生物多样性", "日", true), };

    public static FESTIVAL_DATA festival_wday[] = {
            new FESTIVAL_DATA(1, WEEKDAY(1, Calendar.SUNDAY), "", "黑人", "日",
                    true),
            new FESTIVAL_DATA(2, WEEKDAY(LASTWEEK(1), Calendar.SUNDAY), "国际",
                    "麻风", "节", true),
            new FESTIVAL_DATA(4, WEEKDAY(LASTWEEK(1), Calendar.WEDNESDAY), "",
                    "秘书", "节", true),
            new FESTIVAL_DATA(5, WEEKDAY(2, Calendar.SUNDAY), "国际", "母亲", "节",
                    true),
            new FESTIVAL_DATA(5, WEEKDAY(3, Calendar.SUNDAY), "全国", "助残", "日",
                    true),
            new FESTIVAL_DATA(5, WEEKDAY(3, Calendar.TUESDAY), "国际", "牛奶", "日",
                    true),
            new FESTIVAL_DATA(6, WEEKDAY(3, Calendar.SUNDAY), "", "父亲", "节",
                    true),
            new FESTIVAL_DATA(7, WEEKDAY(1, Calendar.SATURDAY), "国际", "合作",
                    "节", true),
            new FESTIVAL_DATA(9, WEEKDAY(3, Calendar.SATURDAY), "", "全民国防教育",
                    "日", true),
            new FESTIVAL_DATA(9, WEEKDAY(4, Calendar.SUNDAY), "国际", "聋人", "节",
                    true),
            new FESTIVAL_DATA(9, WEEKDAY(4, Calendar.SUNDAY), "世界", "儿童", "日",
                    true),
            new FESTIVAL_DATA(9, WEEKDAY(LASTWEEK(1), Calendar.SUNDAY), "世界",
                    "海事", "日", true),
            new FESTIVAL_DATA(9, WEEKDAY(LASTWEEK(1), Calendar.SUNDAY), "世界",
                    "心脏", "日", true),
            new FESTIVAL_DATA(10, WEEKDAY(1, Calendar.MONDAY), "国际", "住房", "日",
                    true),
            new FESTIVAL_DATA(10, WEEKDAY(1, Calendar.WEDNESDAY), "国际",
                    "减轻自然灾害", "日", true),
            new FESTIVAL_DATA(10, WEEKDAY(2, Calendar.WEDNESDAY), "", "减灾",
                    "日", true),
            new FESTIVAL_DATA(11, WEEKDAY(4, Calendar.THURSDAY), "", "感恩", "节",
                    true),
            new FESTIVAL_DATA(12, WEEKDAY(2, Calendar.SUNDAY), "国际", "儿童电视广播",
                    "日", true), };

    public static FESTIVAL_DATA festival_lunarmday[] = {
            new FESTIVAL_DATA(1, 1, "", "春", "节", true),
            new FESTIVAL_DATA(1, 15, "", "元宵", "节", false),
            new FESTIVAL_DATA(2, 2, "", "龙抬头", "节", false),
            new FESTIVAL_DATA(2, 2, "", "头牙", "", false),
            new FESTIVAL_DATA(3, 23, "", "妈祖", "生辰", false),
            new FESTIVAL_DATA(5, 5, "", "端午", "节", false),
            new FESTIVAL_DATA(7, 7, "", "七夕中国情人", "节", false),
            new FESTIVAL_DATA(7, 7, "", "中国的情人", "节", false),
            new FESTIVAL_DATA(7, 15, "", "中元", "节", false),
            new FESTIVAL_DATA(8, 15, "", "中秋", "节", false),
            new FESTIVAL_DATA(9, 9, "", "重阳", "节", false),
            new FESTIVAL_DATA(12, 8, "", "腊八", "节", false),
            new FESTIVAL_DATA(12, 16, "", "尾牙", "", false),
            new FESTIVAL_DATA(12, 23, "", "北方小年", "", false),
            new FESTIVAL_DATA(12, 24, "", "南方小年", "", false),
            new FESTIVAL_DATA(12, 30, "", "大年三十", "", false),
            new FESTIVAL_DATA(12, LASTDAY(1), "", "除夕", "", false), };


    // 第x周, 星期y
    public static int WEEKDAY(int weekindex, int weekday) {
        return (((weekindex - 1) * 7) + weekday);
    }

    // 最后x周
    public static int LASTWEEK(int x) {
        return (100 + (x));
    }

    // 最后第x天
    public static int LASTDAY(int x) {
        return (100 + (x));
    }


    public static final String[] JieQiNames = { "小寒", "大寒", "立春", "雨水", "惊蛰",
            "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑",
            "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };
    

    public static final JieQiPatch[] JieQiPatchTable = {
            new JieQiPatch(1990, JieQiPatch.XiaZhi, 21),
            new JieQiPatch(1990, JieQiPatch.LiDong, 8),
            new JieQiPatch(1991, JieQiPatch.XiaoShu, 7),
            new JieQiPatch(1991, JieQiPatch.ChuShu, 23),
            new JieQiPatch(1997, JieQiPatch.MangZhong, 5),
            new JieQiPatch(2004, JieQiPatch.LiQiu, 8),
            new JieQiPatch(2006, JieQiPatch.LiXia, 5),
            new JieQiPatch(2006, JieQiPatch.LiQiu, 7),
            new JieQiPatch(2006, JieQiPatch.LiQiu, 7),
            new JieQiPatch(2007, JieQiPatch.XiaoXue, 23),
            new JieQiPatch(2008, JieQiPatch.DaHan, 21),
            new JieQiPatch(2008, JieQiPatch.QiuFen, 22),
            new JieQiPatch(2009, JieQiPatch.LiChun, 4),
            new JieQiPatch(2010, JieQiPatch.JinZhe, 6),
            new JieQiPatch(2010, JieQiPatch.LiQiu, 7),
            new JieQiPatch(2011, JieQiPatch.XiaoHan, 6),
            new JieQiPatch(2011, JieQiPatch.XiaoXue, 23),
            new JieQiPatch(2012, JieQiPatch.DaHan, 21),
            new JieQiPatch(2012, JieQiPatch.XiaoMan, 20),
            new JieQiPatch(2012, JieQiPatch.DaXue, 7),
            new JieQiPatch(2013, JieQiPatch.LiChun, 4),
            new JieQiPatch(2013, JieQiPatch.DaShu, 22),
            new JieQiPatch(2013, JieQiPatch.DongZhi, 22),
            new JieQiPatch(2014, JieQiPatch.JinZhe, 6),
            new JieQiPatch(2015, JieQiPatch.XiaoHan, 6),
            new JieQiPatch(2016, JieQiPatch.DaXue, 7),
            new JieQiPatch(2017, JieQiPatch.DaShu, 22),
            new JieQiPatch(2017, JieQiPatch.DongZhi, 22),
            new JieQiPatch(2018, JieQiPatch.YuShui, 19),
            new JieQiPatch(2018, JieQiPatch.ChunFen, 21),
            new JieQiPatch(2019, JieQiPatch.XiaZhi, 21),
            new JieQiPatch(2020, JieQiPatch.XiaoShu, 6),
            new JieQiPatch(2020, JieQiPatch.ChuShu, 22),
            new JieQiPatch(2020, JieQiPatch.DaXue, 7),
            new JieQiPatch(2022, JieQiPatch.YuShui, 19),
            new JieQiPatch(2022, JieQiPatch.BaiLu, 7),
            new JieQiPatch(2023, JieQiPatch.XiaZhi, 21),
            new JieQiPatch(2023, JieQiPatch.ShuangJiang, 24),
            new JieQiPatch(2023, JieQiPatch.LiDong, 8),
            new JieQiPatch(2024, JieQiPatch.ChuShu, 22), };


    
    public static boolean GregorianToLunarDate(int Year, int Month, int Day, MutableInt LunarYear, MutableInt LunarMonth, MutableInt LunarDay, MutableBoolean isLeapMonth)
    {
        Year -= 1900;
        
        Calendar tmGregorianSpFestival = Calendar.getInstance();
        Calendar tmGregorian = Calendar.getInstance();
        double dblDuration = 0;
        int nDaysAfterSpringFestival = 0;
        int nDayInMonth = 0;

        if( (Year > 199) || (Year < 1) )
        {
            return(false);
        }

        isLeapMonth.setValue(false);
        LunarData LunarCalendar = new LunarData(DateUtil.LunarCalendarTable[Year - 1]);

        tmGregorianSpFestival.clear();
        tmGregorianSpFestival.set(Calendar.YEAR, Year);
        tmGregorianSpFestival.set(Calendar.MONTH, LunarCalendar.MonthForSpringFestival() - 1);
        tmGregorianSpFestival.set(Calendar.DAY_OF_MONTH, LunarCalendar.DayForSpringFestival());
        tmGregorianSpFestival.setTime(tmGregorianSpFestival.getTime());

        if( (Month < tmGregorianSpFestival.get(Calendar.MONTH)) || ((Month == tmGregorianSpFestival.get(Calendar.MONTH)) && (Day < tmGregorianSpFestival.get(Calendar.DAY_OF_MONTH))) )
        {
            LunarYear.setValue(Year - 1);
            LunarCalendar = new LunarData(DateUtil.LunarCalendarTable[LunarYear.intValue() - 1]);
        }
        else
        {
            LunarYear.setValue(Year);
        }

        tmGregorian.clear();
        tmGregorian.set(Calendar.YEAR, Year);
        tmGregorian.set(Calendar.MONTH, Month);
        tmGregorian.set(Calendar.DAY_OF_MONTH, Day);
        tmGregorian.setTime(tmGregorian.getTime());
        
        dblDuration = (tmGregorian.getTimeInMillis() - tmGregorianSpFestival.getTimeInMillis()) / 1000;
        nDaysAfterSpringFestival = (int)(dblDuration/(24*3600));

        if( (Month < tmGregorianSpFestival.get(Calendar.MONTH)) || ((Month == tmGregorianSpFestival.get(Calendar.MONTH)) && (Day < tmGregorianSpFestival.get(Calendar.DAY_OF_MONTH))) )
        {
            int i = 1;
            if( LunarCalendar.LeapMon() != 0 )
            {
                i = 0;
            }
            LunarMonth.setValue(11);
            nDaysAfterSpringFestival *= -1;

            while( nDaysAfterSpringFestival > 0 )
            {
                if( (LunarCalendar.LargeMon() & (1 << i)) != 0 )
                {
                    nDayInMonth = 30;
                }
                else
                {
                    nDayInMonth = 29;
                }

                if( nDaysAfterSpringFestival > nDayInMonth )
                {
                    nDaysAfterSpringFestival -= nDayInMonth;
                    LunarMonth.decrement();
                }
                else if( nDaysAfterSpringFestival == nDayInMonth )
                {
                    LunarDay.setValue(1);
                    break;
                }
                else
                {
                    LunarDay.setValue(nDayInMonth - nDaysAfterSpringFestival + 1);
                    break;
                }

                i++;
            }
            if(LunarCalendar.LeapMon() != 0)
            {
                if(LunarCalendar.LeapMon() > (LunarMonth.intValue() + 1))
                {
                    LunarMonth.increment();
                    if((LunarMonth.intValue() + 1) == LunarCalendar.LeapMon())
                    {
                        isLeapMonth.setValue(true);
                    }
                }
                else if(LunarCalendar.LeapMon() == (LunarMonth.intValue() + 1))
                {
                    isLeapMonth.setValue(true);
                }
            }
        }
        else
        {
            int i = 0;
            LunarMonth.setValue(0);
            while( nDaysAfterSpringFestival >= 0 )
            {
                if( (LunarCalendar.LargeMon() & (1 << (12 - i))) != 0 )
                {
                    nDayInMonth = 30;
                }
                else
                {
                    nDayInMonth = 29;
                }

                if( nDaysAfterSpringFestival > nDayInMonth )
                {
                    nDaysAfterSpringFestival -= nDayInMonth;
                    LunarMonth.increment();
                }
                else if( nDaysAfterSpringFestival == nDayInMonth )
                {
                    LunarMonth.increment();
                    LunarDay.setValue(1);
                    break;
                }
                else
                {
                    LunarDay.setValue(nDaysAfterSpringFestival + 1);
                    break;
                }
                i++;
            }
            if( (LunarCalendar.LeapMon() != 0) && (LunarCalendar.LeapMon() < (LunarMonth.intValue() + 1)) )
            {
                LunarMonth.decrement();
                if((LunarMonth.intValue() + 1) == LunarCalendar.LeapMon())
                {
                    isLeapMonth.setValue(true);
                }
            }
        }
        
        LunarYear.add(1900);

        return(true);
    }
}

class FESTIVAL_DATA {

    public int month = 0;
    public int day = 0;
    public String festivalPrefix = "";
    public String festivalName = "";
    public String festivalPostfix = "";
    public boolean festivalPostMust = false;

    public FESTIVAL_DATA(int month, int day, String festivalPrefix,
            String festivalName, String festivalPostfix,
            boolean festivalPostMust) {
        this.month = month;
        this.day = day;
        this.festivalPrefix = festivalPrefix;
        this.festivalName = festivalName;
        this.festivalPostfix = festivalPostfix;
        this.festivalPostMust = festivalPostMust;
    }
}

class JieQiPatch {
    public final static int XiaoHan = 0;
    public final static int DaHan = 1;
    public final static int LiChun = 2;
    public final static int YuShui = 3;
    public final static int JinZhe = 4;
    public final static int ChunFen = 5;
    public final static int QinMing = 6;
    public final static int GuYu = 7;
    public final static int LiXia = 8;
    public final static int XiaoMan = 9;
    public final static int MangZhong = 10;
    public final static int XiaZhi = 11;
    public final static int XiaoShu = 12;
    public final static int DaShu = 13;
    public final static int LiQiu = 14;
    public final static int ChuShu = 15;
    public final static int BaiLu = 16;
    public final static int QiuFen = 17;
    public final static int HanLu = 18;
    public final static int ShuangJiang = 19;
    public final static int LiDong = 20;
    public final static int XiaoXue = 21;
    public final static int DaXue = 22;
    public final static int DongZhi = 23;

    public int year = 0;
    public int index = 0;
    public int day = 0;

    public JieQiPatch(int year, int index, int day) {
        this.year = year;
        this.index = index;
        this.day = day;
    }
}

class LunarData {
    int nYearData;
    
    public LunarData(int i) {
        this.nYearData = i;
    }
    
    public int DayForSpringFestival()
    {
        return(nYearData & 0x1f);
    }
    
    public int MonthForSpringFestival()
    {
        return((nYearData >> 5) & 0x3);
    }
    
    public int LargeMon()
    {
        return((nYearData >> 7) & 0x1fff);
    }
    
    public int LeapMon()
    {
        return((nYearData >> 20) & 0xf);
    }
}
