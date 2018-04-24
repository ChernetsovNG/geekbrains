package constraint;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

public interface ZipCodeChecker {
    boolean isZipCodeValid(String zipCode);
}
