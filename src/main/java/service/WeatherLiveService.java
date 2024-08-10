package service;

import DAO.WeatherLiveDAO;
import domain.WeatherLive;
import utils.DateUtils;
import utils.OKHttpAPI;

import java.time.LocalDateTime;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class WeatherLiveService {
    private WeatherLiveDAO weatherLiveDAO = new WeatherLiveDAO();

    /**
     * 查询城市实时天气信息
     * @param cityId 城市ID
     * @param queryTime 查询时间
     * 如果查询时间处于缓存范围内(10min), 直接输出信息
     * 如果是第一次查询或查询时间超出缓存时间(10min), 调用API查询信息，更新数据库，最后输出
     */
    public WeatherLive getWeatherLiveInfoByCityId(String cityId, LocalDateTime queryTime) {
        WeatherLive weatherLive = weatherLiveDAO.singleQuery("select * from weatherlive where cityId = ?", WeatherLive.class, cityId);
        // 判断是否是第一次查询
        if (weatherLive == null) {
            // 请求网页数据
            WeatherLive info = OKHttpAPI.getWeatherLiveInfo(cityId);
            // 添加数据库
            if (!insertWeatherLiveInfo(cityId, info)) {
                throw new RuntimeException("添加失败！");
            }
            return info;
        }
        // 判断查询时间是否处于缓存范围内
        if (!DateUtils.isWithinTenMinutes(queryTime, weatherLive.getUpdateTime())) {
            // 请求网页数据
            WeatherLive info = OKHttpAPI.getWeatherLiveInfo(cityId);
            // 更新数据库
            if (!updateWeatherLiveInfo(cityId, info)) {
                throw new RuntimeException("更新失败！");
            }
            return info;
        }
        return weatherLive;
    }

    /**
     * 添加新的实时天气信息到数据库
     * @param cityId 城市id
     * @param weatherLive 实时天气信息对象
     * @return 添加是否成功
     */
    private boolean insertWeatherLiveInfo(String cityId, WeatherLive weatherLive) {
        return weatherLiveDAO.update("insert into weatherlive values(?,?,?,?,?,?)",
                cityId,
                weatherLive.getUpdateTime(),
                weatherLive.getObsTime(),
                weatherLive.getTemp(),
                weatherLive.getFeelsLike(),
                weatherLive.getText()) > 0;
    }

    /**
     * 更新新的实时天气信息到数据库
     * @param cityId 城市id
     * @param weatherLive 实时天气信息对象
     * @return 更新是否成功
     */
    private boolean updateWeatherLiveInfo(String cityId, WeatherLive weatherLive) {
        return weatherLiveDAO.update("update weatherlive set updateTime = ?, obsTime = ?, temp = ?, feelsLike = ?, text = ? where cityId = ?",
                weatherLive.getUpdateTime(),
                weatherLive.getObsTime(),
                weatherLive.getTemp(),
                weatherLive.getFeelsLike(),
                weatherLive.getText(),
                cityId) > 0;
    }
}
