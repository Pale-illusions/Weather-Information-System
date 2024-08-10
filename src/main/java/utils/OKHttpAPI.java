package utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import domain.Weather;
import domain.WeatherIndex;
import domain.WeatherLive;
import exception.ErrorStatusException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class OKHttpAPI {
    private static OkHttpClient client = new OkHttpClient();

    private static String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * 根据城市id 获得3日天气信息
     * @param cityId 城市id
     * @return 包含3日天气信息对象的集合
     */
    public static List<Weather> getWeatherInfo(String cityId) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(run("https://devapi.qweather.com/v7/weather/3d?key=712816261be84e6a9835cf10731237ad&location=" + cityId));
            // 判断请求是否成功
            String code = jsonObject.getString("code");
            // 如果失败，输出错误信息
            if (!code.equals("200")) {
                throw ErrorStatusException.fromCode(code);
            }
            // 解析 JSON 数据
            LocalDateTime updateTime = LocalDateTime.parse(jsonObject.getString("updateTime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME); // 最新一次更新时间
            List<Weather> weathers = new ArrayList<>();
            JSONArray daily = jsonObject.getJSONArray("daily");
            for (int i = 0; i < daily.size(); i++) {
                JSONObject day = daily.getJSONObject(i);
                Weather weather = Weather
                        .builder()
                        .id(i)
                        .fxDate(LocalDateTime.parse(day.getString("fxDate") + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .tempMax(day.getString("tempMax"))
                        .tempMin(day.getString("tempMin"))
                        .textDay(day.getString("textDay"))
                        .textNight(day.getString("textNight"))
                        .cityId(cityId)
                        .updateTime(updateTime)
                        .build();
                weathers.add(weather);
            }
            return weathers;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ErrorStatusException e) {
            System.out.println(e);
        }
        return null;
    }

    public static WeatherLive getWeatherLiveInfo(String cityId) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(run("https://devapi.qweather.com/v7/weather/now?key=712816261be84e6a9835cf10731237ad&location=" + cityId));
            // 判断请求是否成功
            String code = jsonObject.getString("code");
            // 如果失败，输出错误信息
            if (!code.equals("200")) {
                throw ErrorStatusException.fromCode(code);
            }
            // 解析 JSON 数据
            JSONObject now = jsonObject.getJSONObject("now");
            WeatherLive weatherLive = WeatherLive
                    .builder()
                    .updateTime(LocalDateTime.parse(jsonObject.getString("updateTime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .obsTime(LocalDateTime.parse(now.getString("obsTime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                    .temp(now.getString("temp"))
                    .feelsLike(now.getString("feelsLike"))
                    .text(now.getString("text"))
                    .cityId(cityId)
                    .build();
            return weatherLive;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ErrorStatusException e) {
            System.out.println(e);
        }
        return null;
    }

    public static List<WeatherIndex> getWeatherIndexInfo(String cityId) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(run("https://devapi.qweather.com/v7/indices/1d?key=712816261be84e6a9835cf10731237ad&location=" + cityId + "&type=1,3,8,15"));
            // 判断请求是否成功
            String code = jsonObject.getString("code");
            // 如果失败，输出错误信息
            if (!code.equals("200")) {
                throw ErrorStatusException.fromCode(code);
            }
            // 解析 JSON 数据
            LocalDateTime updateTime = LocalDateTime.parse(jsonObject.getString("updateTime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME); // 最新一次更新时间
            List<WeatherIndex> weatherIndices = new ArrayList<>();
            JSONArray daily = jsonObject.getJSONArray("daily");
            for (int i = 0; i < daily.size(); i++) {
                JSONObject day = daily.getJSONObject(i);
                WeatherIndex weatherIndex = WeatherIndex
                        .builder()
                        .updateTime(updateTime)
                        .date(LocalDateTime.parse(day.getString("date") + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .type(day.getInteger("type"))
                        .name(day.getString("name"))
                        .level(day.getInteger("level"))
                        .category(day.getString("category"))
                        .text(day.getString("text"))
                        .cityId(cityId)
                        .build();
                weatherIndices.add(weatherIndex);
            }
            return weatherIndices;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ErrorStatusException e) {
            System.out.println(e);
        }
        return null;
    }
}
