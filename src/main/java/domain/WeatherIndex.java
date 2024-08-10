package domain;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 这是一个 JavaBean 和 weatherIndex 表对应
 * create table weatherIndex(
 *   cityId varchar(50), # 城市ID
 *   updateTime DATETIME not null, # 更新时间
 *   date DATETIME not null, # 日期
 *   type int not null, # 类型ID
 *   name varchar(50) not null, # 类型名称
 *   level int not null, # 等级
 *   category varchar(50) not null, # 级别
 *   text varchar(50), # 描述
 *   FOREIGN KEY (cityId) REFERENCES city(id) # 设置外键约束
 * ) charset = utf8;
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherIndex {
    private String cityId;
    private LocalDateTime updateTime;
    private LocalDateTime date;
    private Integer type;
    private String name;
    private Integer level;
    private String category;
    private String text;

    @Override
    public String toString() {
        return "日期：" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "\n" +
                "level：" + level + "\n" +
                "级别：" + category + "\n" +
                "建议：" + text;
    }
}
