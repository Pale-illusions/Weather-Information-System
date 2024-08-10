package domain;

import lombok.*;

/**
 * @author 苍镜月
 * @version 1.0
 * @implNote 这是一个 JavaBean 和 City 表对应
 * create table city(
 *     id int PRIMARY KEY,
 *     name varchar(50) not null default ''
 * ) charset = utf8;
 *
 * INSERT into city(id, name) values
 *   ('101230101', '福州'),
 *   ('101210101', '杭州'),
 *   ('101020100', '上海'),
 *   ('101010100', '北京');
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {
    // 城市ID
    private String id;
    // 城市名字
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
