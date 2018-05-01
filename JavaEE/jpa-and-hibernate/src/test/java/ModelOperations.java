import entity.Item;
import org.junit.Test;

import javax.validation.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ModelOperations {

    @Test
    public void itemValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setName("Some Item");
        item.setAuctionEnd(ZonedDateTime.of(LocalDateTime.of(2018, Month.APRIL, 1, 12, 0), ZoneId.of("Europe/Moscow")));

        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        assertEquals(1, violations.size());

        ConstraintViolation<Item> violation = violations.iterator().next();

        String failedPropertyName = violation.getPropertyPath().iterator().next().getName();

        assertEquals(failedPropertyName, "auctionEnd");

        if (Locale.getDefault().getLanguage().equals("en")) {
            assertEquals(violation.getMessage(), "must be in the future");
        }

        Class<? extends Item> itemClass = item.getClass();
        ClassLoader itemClassLoader = itemClass.getClassLoader();

        System.out.println(itemClass);
    }
}
