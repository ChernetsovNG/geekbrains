import annotation.Loggable;
import annotation.ThirteenDigits;
import entity.Book;
import number.NumberGenerator;

import javax.inject.Inject;

@Loggable
public class BookService {
    @Inject @ThirteenDigits
    private NumberGenerator numberGenerator;

    public Book createBook(String title, Float price, String description) {
        Book book = new Book(title, price, description);
        book.setIsbn(numberGenerator.generateNumber());
        return book;
    }
}
