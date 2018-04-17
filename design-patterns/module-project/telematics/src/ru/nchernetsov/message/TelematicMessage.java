package ru.nchernetsov.message;

import ru.nchernetsov.geo.TSPos;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Телематическое сообщение
 */
public class TelematicMessage {
    /**
     * Уникальный идентификатор сообщения
     */
    private final UUID id;
    /**
     * Заголовок сообщения
     */
    private Map<String, Object> header;
    /**
     * Тело сообщения
     */
    private Map<String, Object> body;
    /**
     * Географическое положение точки посылки сообщения
     */
    private TSPos tsPos;

    private TelematicMessage() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public TSPos getTsPos() {
        return tsPos;
    }

    public static Builder newBuilder() {
        return new TelematicMessage().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder setHeader(Map<String, Object> header) {
            TelematicMessage.this.header = header;
            return this;
        }

        public Builder setBody(Map<String, Object> body) {
            TelematicMessage.this.body = body;
            return this;
        }

        public Builder setTSPos(TSPos tsPos) {
            TelematicMessage.this.tsPos = tsPos;
            return this;
        }

        public TelematicMessage build() {
            return TelematicMessage.this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelematicMessage that = (TelematicMessage) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(header, that.header) &&
            Objects.equals(body, that.body) &&
            Objects.equals(tsPos, that.tsPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, body, tsPos);
    }
}
