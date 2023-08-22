package com.gugucon.shopping.point.dto.response;

import com.gugucon.shopping.point.domain.Point;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PointResponse {

    private Long point;

    public static PointResponse from(final Point point) {
        return new PointResponse(point.getPoint().getValue());
    }
}
