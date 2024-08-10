package domain;

import lombok.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 这是一个 JavaBean 和 weather 表对应
 * create table weather(
 *   id int not null, # 0 代表 今天，1 代表 明天，2 代表 后天
 *   cityId varchar(50), # 城市ID
 *   updateTime DATETIME not null, # 更新时间
 *   fxDate DATETIME not null, # 日期
 *   tempMax varchar(50) not null, # 最高温度 默认单位：摄氏度
 *   tempMin VARCHAR(50) not null, # 最低温度 默认单位：摄氏度
 *   textDay VARCHAR(50) not null, # 白天天气状况
 *   textNight VARCHAR(50) not null, # 晚上天气状况
 *   FOREIGN KEY (cityId) REFERENCES city(id) # 设置外键约束
 * ) charset = utf8;
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Weather {
    private Integer id;
    private String cityId;
    private LocalDateTime updateTime;
    private LocalDateTime fxDate;
    private String tempMax;
    private String tempMin;
    private String textDay;
    private String textNight;

    @Override
    public String toString() {
        return fxDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\t\t" +
                tempMax + "℃\t\t\t" +
                tempMin + "℃\t\t" +
                textDay + "\t\t\t" +
                textNight;
    }
}
