package service;

import DAO.WeatherIndexDAO;
import domain.WeatherIndex;
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
public class WeatherIndexService {
    private WeatherIndexDAO weatherIndexDAO = new WeatherIndexDAO();

    /**
     * 通过城市id和天气信息id查询天气指数
     * @param cityId 城市id
     * @param type 天气指数id
     * @return 天气指数
     */
    public WeatherIndex getWeatherIndexInfo(String cityId, int type) {
        return weatherIndexDAO.singleQuery("select * from weatherindex where cityId = ? and type = ?", WeatherIndex.class, cityId, type);
    }

    /**
     * 更新城市今日天气指数
     * 如果查询时间处于缓存范围内(1h), 直接输出信息
     * 如果是第一次查询或查询时间超出缓存时间(1h), 调用API查询信息，更新数据库，最后输出
     * 特判：上一次更新时间位于23点-0点，此次查询时间为0点-1点，即判断更新时间和查询时间是否为同一天，那么即使位于缓存范围内，仍需更新数据库
     * @param cityId 城市id
     * @param queryTime 查询时间
     */
    public void updateWeatherIndexByCityId(String cityId, LocalDateTime queryTime) {
        List<WeatherIndex> weatherIndices = weatherIndexDAO.multipleQuery("select * from weatherindex where cityId = ?", WeatherIndex.class, cityId);
        // 判断是否是第一次查询
        if (weatherIndices.isEmpty()) {
            // 请求网页数据
            List<WeatherIndex> infoList = OKHttpAPI.getWeatherIndexInfo(cityId);
            // 添加数据库
            if (!insertWeatherIndexInfo(cityId, infoList)) {
                throw new RuntimeException("添加失败！");
            }
            return;
        }
        // 判断是否超出缓存时间 && 判断是否在同一天
        if (!DateUtils.isWithinOneHour(queryTime, weatherIndices.getFirst().getUpdateTime())
                || !DateUtils.isSameDay(queryTime, weatherIndices.getFirst().getUpdateTime())) {
            // 请求网页数据
            List<WeatherIndex> infoList = OKHttpAPI.getWeatherIndexInfo(cityId);
            // 更新数据库
            if (!updateWeatherIndexInfo(cityId, infoList)) {
                throw new RuntimeException("更新失败！");
            }
        }
    }

    /**
     * 添加新的天气指数信息到数据库
     * @param cityId 城市id
     * @param weatherIndices 天气指数
     * @return 添加是否成功
     */
    private boolean insertWeatherIndexInfo(String cityId, List<WeatherIndex> weatherIndices) {
        boolean success = true;
        for (WeatherIndex weatherIndex : weatherIndices) {
            int update = weatherIndexDAO.update("insert into weatherindex values(?,?,?,?,?,?,?,?)",
                    cityId,
                    weatherIndex.getUpdateTime(),
                    weatherIndex.getDate(),
                    weatherIndex.getType(),
                    weatherIndex.getName(),
                    weatherIndex.getLevel(),
                    weatherIndex.getCategory(),
                    weatherIndex.getText());
            success = update > 0;
        }
        return success;
    }

    /**
     * 更新新的天气指数信息到数据库
     * @param cityId 城市id
     * @param weatherIndices 天气指数
     * @return 更新是否成功
     */
    private boolean updateWeatherIndexInfo(String cityId, List<WeatherIndex> weatherIndices) {
        boolean success = true;
        for (WeatherIndex weatherIndex : weatherIndices) {
            int update = weatherIndexDAO.update("update weatherindex set updateTime = ?, date = ?, type = ?, name = ?, level = ?, category = ?, text = ? where cityId = ? and type = ?",
                    weatherIndex.getUpdateTime(),
                    weatherIndex.getDate(),
                    weatherIndex.getType(),
                    weatherIndex.getName(),
                    weatherIndex.getLevel(),
                    weatherIndex.getCategory(),
                    weatherIndex.getText(),
                    cityId,
                    weatherIndex.getType());
            success = update > 0;
        }
        return success;
    }
}
