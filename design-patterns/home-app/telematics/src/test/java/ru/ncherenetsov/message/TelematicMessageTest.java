package ru.ncherenetsov.message;

import org.junit.jupiter.api.Test;
import ru.ncherenetsov.geo.GeoPoint;
import ru.ncherenetsov.geo.TSPos;

import java.util.Map;

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.ncherenetsov.message.ObjectKind.VEHICLE;

class TelematicMessageTest {

    @Test
    void createMessageTest() {
        Map<String, Object> header = Map.of(
            "utc", System.currentTimeMillis(),
            "object_kind", VEHICLE,
            "object_id", 123);

        TSPos tsPos = TSPos.newBuilder()
            .setPoint(new GeoPoint(37.0, 55.0))
            .setSpd(60.0 / 36.0)
            .setDir(90.0)
            .setNtm(System.currentTimeMillis())
            .setVld(true)
            .build();

        TelematicMessage telematicMessage = TelematicMessage.newBuilder()
            .setHeader(header)
            .setBody(null)
            .setTSPos(tsPos)
            .build();

        assertEquals(123, telematicMessage.getHeader().get("object_id"));
        assertEquals(VEHICLE, telematicMessage.getHeader().get("object_kind"));
        assertEquals(37.0, telematicMessage.getTsPos().getPoint().getLon(), 1e-6);
        assertEquals(NaN, telematicMessage.getTsPos().getPoint().getAlt());
    }
}
