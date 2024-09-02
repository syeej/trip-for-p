package team.seventhmile.tripforp.domain.plan.entity;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum Area {
    SEOUL("서울"),
    BUSAN("부산"),
    DAEGU("대구"),
    INCHEON("인천"),
    GWANGJU("광주"),
    DAEJEON("대전"),
    ULSAN("울산"),
    SEJONG("세종"),
    GYEONGGI("경기"),
    GANGWON("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    JEOLNBUK("전북"),
    JEONNAM("전남"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    JEJU("제주");

    private final String name;

    private static final Map<String, Area> NAME_TO_ENUM_MAP = new HashMap<>();

    Area(String name) {
        this.name = name;
    }

    static {
        for (Area area : Area.values()) {
            NAME_TO_ENUM_MAP.put(area.getName(), area);
        }
    }

    // 한글 이름을 통해 Enum을 반환하는 메서드
    public static Area fromName(String name) {
        return NAME_TO_ENUM_MAP.get(name);
    }
}
