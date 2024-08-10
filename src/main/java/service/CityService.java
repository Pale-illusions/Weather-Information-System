package service;

import DAO.CityDAO;
import domain.City;

import java.util.List;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote
 */
public class CityService {
    private CityDAO cityDAO = new CityDAO();

    /**
     * 返回所有城市列表
     * @return 返回所有城市列表
     */
    public List<City> list() {
        return cityDAO.multipleQuery("select * from city", City.class);
    }

    /**
     * 根据城市名字返回城市对象
     * @param name 城市名字
     * @return 城市对象
     */
    public City getCityByName(String name) {
        return cityDAO.singleQuery("select * from city where name = ?", City.class, name);
    }

}
