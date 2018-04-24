package constraint;

import annotation.USA;

@USA
public class ZipCodeCheckerImpl implements ZipCodeChecker {
    @Override
    public boolean isZipCodeValid(String zipCode) {
        return true;
    }
}
