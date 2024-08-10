package view;

import domain.City;
import domain.Weather;
import domain.WeatherIndex;
import domain.WeatherLive;
import service.CityService;
import service.WeatherIndexService;
import service.WeatherLiveService;
import service.WeatherService;
import utils.OKHttpAPI;
import utils.Utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class View {
    public static void main(String[] args) {
        new View().mainMenu();
    }

    // 控制循环
    private boolean loop = true;
    // 接收用户输入
    private String key = "";
    // 定义一个 CityService 对象
    private CityService cityService = new CityService();
    // 定义一个 WeatherService 对象
    private WeatherService weatherService = new WeatherService();
    // 定义一个 WeatherLiveService 对象
    private WeatherLiveService weatherLiveService = new WeatherLiveService();
    // 定义一个 WeatherIndexService 对象
    private WeatherIndexService weatherIndexService = new WeatherIndexService();

    public void mainMenu() {
        while (loop) {
            System.out.println("\n========================天气信息系统========================");
            System.out.println("\t\t 1 查询城市3日天气");
            System.out.println("\t\t 2 查询城市实时天气");
            System.out.println("\t\t 3 查询今日天气指数");
            System.out.println("\t\t 9 退出");
            System.out.print("请输入你的选择：");
            key = Utility.readString(1);
            switch (key) {
                case "1":
                    System.out.print("请输入你要查询的城市名(-1退出)：");
                    key = Utility.readString(50);
                    if (key.equals("-1")) break;
                    list3DaysWeather(key);
                    break;
                case "2":
                    System.out.print("请输入你要查询的城市名(-1退出)：");
                    key = Utility.readString(50);
                    if (key.equals("-1")) break;
                    listWeatherLiveInfo(key);
                    break;
                case "3":
                    System.out.print("请输入你要查询的城市名(-1退出)：");
                    key = Utility.readString(50);
                    if (key.equals("-1")) break;
                    listWeatherIndexInfo(key);
                    break;
                case "9":
                    loop = false;
                    System.out.println("退出");
                    break;
                default:
                    System.out.println("输入有误，请重新输入！");
            }
        }
    }

    /**
     * 查询城市今日天气指数
     * 如果查询时间处于缓存范围内(1h), 直接输出信息
     * 如果是第一次查询或查询时间超出缓存时间(1h), 调用API查询信息，更新数据库，最后输出
     * 特判：上一次更新时间位于23点-0点，此次查询时间为0点-1点，即判断更新时间和查询时间是否为同一天，那么即使位于缓存范围内，仍需更新数据库
     * @param name 城市名
     */
    public void listWeatherIndexInfo(String name) {
        City city = cityService.getCityByName(name);
        // 判断城市是否在数据库中
        if (!inDataBase(city)) return;
        weatherIndexService.updateWeatherIndexByCityId(city.getId(), LocalDateTime.now());
        System.out.println("\n========================"+city.getName()+"市今日天气指数========================");
        int[] types = {1,3,8,15};
        for (int type : types) {
            WeatherIndex weatherIndexInfo = weatherIndexService.getWeatherIndexInfo(city.getId(), type);
            System.out.println("\n========================"+weatherIndexInfo.getName()+"========================");
            System.out.println(weatherIndexInfo);
            System.out.println("====================================================");
        }
        System.out.println("\n========================显示完毕========================");
    }

    /**
     * 查询城市实时天气信息
     * @param name 城市名
     * 如果查询时间处于缓存范围内(10min), 直接输出信息
     * 如果是第一次查询或查询时间超出缓存时间(10min), 调用API查询信息，更新数据库，最后输出
     */
    public void listWeatherLiveInfo(String name) {
        City city = cityService.getCityByName(name);
        // 判断城市是否在数据库中
        if (!inDataBase(city)) return;
        WeatherLive weatherLive = weatherLiveService.getWeatherLiveInfoByCityId(city.getId(), LocalDateTime.now());
        System.out.println("\n========================"+city.getName()+"市实时天气========================");
        System.out.println("\t当前时间\t\t\t室外温度(℃)\t\t体感温度(℃)\t\t天气状况");
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")) + "\t\t" +
                weatherLive);
        System.out.println("========================显示完毕========================");
    }

    /**
     * 查询城市3日天气信息
     * 如果查询时间处于缓存范围内(1h), 直接输出信息
     * 如果是第一次查询或查询时间超出缓存时间(1h), 调用API查询信息，更新数据库，最后输出
     * 特判：上一次更新时间位于23点-0点，此次查询时间为0点-1点，即判断更新时间和查询时间是否为同一天，那么即使位于缓存范围内，仍需更新数据库
     * @param name 城市名
     */
    public void list3DaysWeather(String name) {
        City city = cityService.getCityByName(name);
        // 判断城市是否在数据库中
        if (!inDataBase(city)) return;
        weatherService.update3DaysWeatherByCityId(city.getId(), LocalDateTime.now());
        System.out.println("\n========================"+city.getName()+"市3日天气预报========================");
        System.out.println("日期\t\t\t最高温度(℃)\t\t最低温度(℃)\t\t白天天气状况\t\t晚间天气状况");
        for (int i = 0; i < 3; i++) {
            System.out.println(weatherService.getWeatherInfo(city.getId(), i));
        }
        System.out.println("========================显示完毕========================");
    }

    /**
     * 判断城市是否在数据库中
     * @param city 城市名
     * @return 城市是否在数据库中
     */
    private boolean inDataBase(City city) {
        if (city == null) {
            System.out.println("该城市不支持查询！");
            System.out.println("支持查询的城市有：");
            List<City> list = cityService.list();
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) System.out.print("[" + list.get(i) + ", ");
                else if (i == list.size() - 1) System.out.println(list.get(i) + "]");
                else System.out.print(list.get(i) + ", ");
            }
            return false;
        }
        return true;
    }
}
