package converter;

import javax.persistence.AttributeConverter;

public class ZipcodeConverter implements AttributeConverter<Zipcode, String> {
    @Override
    public String convertToDatabaseColumn(Zipcode attribute) {
        return attribute.getValue();
    }

    @Override
    public Zipcode convertToEntityAttribute(String s) {
        if (s.length() == 6) {
            return new RussianZipcode(s);
        } else if (s.length() == 5) {
            return new GermanZipcode(s);
        } else if (s.length() == 4) {
            return new SwissZipcode(s);
        }
        throw new IllegalArgumentException("Unsupported zipcode in database: " + s);
    }
}
