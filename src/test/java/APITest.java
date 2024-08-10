import com.alibaba.fastjson2.JSONObject;
import domain.Weather;
import domain.WeatherIndex;
import org.junit.jupiter.api.Test;
import utils.OKHttpAPI;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class APITest {
    @Test
    public void testDateFormat() throws ParseException {
//        String time = "{\"time\" : \"2021-11-15T16:35+08:00\"}";
//        JSONObject jsonObject = JSONObject.parseObject(time);
//
//        // 提取时间字符串
//        String timeString = jsonObject.getString("time");
//
//        // 定义时间格式
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//
//        // 解析为 LocalDateTime
//        LocalDateTime localDateTime = LocalDateTime.parse(timeString, formatter);
//
////        LocalDateTime.parse(jsonObject.getString("time"), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
//        // 输出结果
//        System.out.println(localDateTime);


        String dateString = "2021-11-15";

        // 定义日期格式，不包括时间部分
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 解析为 LocalDateTime，手动添加时间部分
        LocalDateTime localDateTime = LocalDateTime.parse(dateString + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // 输出结果
        System.out.println(localDateTime);

    }

    @Test
    public void testGetWeatherInfo() {
        List<Weather> list = OKHttpAPI.getWeatherInfo("101210101");
        for (Weather weather : list) {
            System.out.println(weather);
        }
    }

    @Test
    public void testGetWeatherLiveInfo() {
        System.out.println(OKHttpAPI.getWeatherLiveInfo("101210101"));
    }

    @Test
    public void testGetWeatherIndexInfo() {
        List<WeatherIndex> list = OKHttpAPI.getWeatherIndexInfo("101210101");
        for (WeatherIndex weatherIndex : list) {
            System.out.println(weatherIndex);
        }
    }
}
