package ru.nchernetsov.test;

import ru.nchernetsov.geo.GeoPoint;
import ru.nchernetsov.geo.TSPos;
import ru.nchernetsov.message.TelematicMessage;

import java.util.Map;

import static java.lang.Double.NaN;
import static org.junit.Assert.assertEquals;
import static ru.nchernetsov.message.ObjectKind.VEHICLE;

public class TelematicMessageTest {

    public static void main(String[] args) {
        TelematicMessageTest telematicMessageTest = new TelematicMessageTest();
        telematicMessageTest.createMessageTest();
    }

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
        assertEquals(NaN, telematicMessage.getTsPos().getPoint().getAlt(), 1e-6);
    }
}
