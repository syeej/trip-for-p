package team.seventhmile.tripforp.domain.plan.entity;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum Area {
    SEOUL("서울특별시"),
    BUSAN("부산광역시"),
    DAEGU("대구광역시"),
    INCHEON("인천광역시"),
    GWANGJU("광주광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    SEJONG("세종특별자치시"),
    GYEONGGI("경기도"),
    GANGWON("강원도"),
    CHUNGCHEONGBUK("충청북도"),
    CHUNGCHEONGNAM("충청남도"),
    JEOLLABUK("전라북도"),
    JEOLLANAM("전라남도"),
    GYEONGSANGBUK("경상북도"),
    GYEONGSANGNAM("경상남도"),
    JEJU("제주도");

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
