package service;

import DAO.WeatherDAO;
import domain.Weather;
import utils.DateUtils;
import utils.OKHttpAPI;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class WeatherService {
    private WeatherDAO weatherDAO = new WeatherDAO();

    /**
     * 通过城市id和天气信息id查询天气信息
     * @param cityId 城市id
     * @param id 天气信息id
     * @return 天气信息
     */
    public Weather getWeatherInfo(String cityId, int id) {
        return weatherDAO.singleQuery("select * from weather where cityId = ? and id = ?", Weather.class, cityId, id);
    }

    /**
     * 更新城市3日天气信息
     * 如果查询时间处于缓存范围内(1h), 直接输出信息
     * 如果是第一次查询或查询时间超出缓存时间(1h), 调用API查询信息，更新数据库，最后输出
     * 特判：上一次更新时间位于23点-0点，此次查询时间为0点-1点，即判断更新时间和查询时间是否为同一天，那么即使位于缓存范围内，仍需更新数据库
     * @param cityId 城市id
     * @param queryTime 查询时间
     */
    public void update3DaysWeatherByCityId(String cityId, LocalDateTime queryTime) {
        List<Weather> weatherList = weatherDAO.multipleQuery("select * from weather where cityId = ?", Weather.class, cityId);
        // 判断是否是第一次查询
        if (weatherList.isEmpty()) {
            // 请求网页数据
            List<Weather> list = OKHttpAPI.getWeatherInfo(cityId);
            // 添加数据库
            if (!insert3DaysWeatherInfo(cityId, list)) {
                throw new RuntimeException("添加失败");
            }
            return;
        }
        // 判断是否超出缓存时间 && 判断是否在同一天
        if (!DateUtils.isWithinOneHour(queryTime, weatherList.getFirst().getUpdateTime())
                || !DateUtils.isSameDay(queryTime, weatherList.getFirst().getUpdateTime())) {
            // 请求网页数据
            List<Weather> list = OKHttpAPI.getWeatherInfo(cityId);
            // 更新数据库
            if (!update3DaysWeatherInfo(cityId, list)) {
                throw new RuntimeException("更新失败");
            }
        }
    }

    /**
     * 添加新的天气信息到数据库
     * @param cityId 城市id
     * @param weatherList 3日天气信息
     * @return 添加是否成功
     */
    private boolean insert3DaysWeatherInfo(String cityId, List<Weather> weatherList) {
        boolean success = true;
        for (Weather weather : weatherList) {
            int update = weatherDAO.update("insert into weather values(?,?,?,?,?,?,?,?)",
                    weather.getId(),
                    cityId,
                    weather.getUpdateTime(),
                    weather.getFxDate(),
                    weather.getTempMax(),
                    weather.getTempMin(),
                    weather.getTextDay(),
                    weather.getTextNight());
            success = update > 0;
        }
        return success;
    }

    /**
     * 更新新的天气信息到数据库
     * @param cityId 城市ID
     * @param weatherList 新的天气信息
     * @return 更新是否成功
     */
    private boolean update3DaysWeatherInfo(String cityId, List<Weather> weatherList) {
        boolean success = true;
        for (Weather weather : weatherList) {
            int update = weatherDAO.update("update weather set updateTime = ?, fxDate = ?, tempMax = ?, tempMin = ?, textDay = ?, textNight = ? where cityId = ? and id = ?",
                    weather.getUpdateTime(),
                    weather.getFxDate(),
                    weather.getTempMax(),
                    weather.getTempMin(),
                    weather.getTextDay(),
                    weather.getTextNight(),
                    cityId,
                    weather.getId());
            success = update > 0;
        }
        return success;
    }
}
