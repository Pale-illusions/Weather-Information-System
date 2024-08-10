package domain;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 这是一个 JavaBean 和 weatherLive 表对应
 * create table weatherLive(
 *   cityId varchar(50), # 城市ID
 *   updateTime DATETIME not null, # 更新时间
 *   obsTime DATETIME not null, # 观测时间
 *   temp varchar(50) not null, # 当前温度 默认单位：摄氏度
 *   feelsLike VARCHAR(50) not null, # 体感温度 默认单位：摄氏度
 *   text VARCHAR(50) not null, # 天气状况
 *   FOREIGN KEY (cityId) REFERENCES city(id) # 设置外键约束
 * ) charset = utf8;
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherLive {
    private String cityId;
    private LocalDateTime obsTime;
    private LocalDateTime updateTime;
    private String temp;
    private String feelsLike;
    private String text;

    @Override
    public String toString() {
        return temp + "℃\t\t\t" + feelsLike + "℃\t\t\t" + text;
    }
}
