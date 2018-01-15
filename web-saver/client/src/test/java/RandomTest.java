
import org.junit.jupiter.api.Test;
import ru.geekbrains.client.utils.RandomString;

public class RandomTest {

    @Test
    public void testRandomString() {
        RandomString randomString = new RandomString(10);
        System.out.println(randomString.nextString());
    }

    @Test
    public void testRandomString2() {
        RandomString randomString = new RandomString(10);
        System.out.println(randomString.nextString());
    }
}
