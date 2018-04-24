package entity;

public class Book {
    private String title;
    private Float price;
    private String descriptor;
    private String number;

    public Book(String title, Float price, String descriptor) {
        this.title = title;
        this.price = price;
        this.descriptor = descriptor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "entity.Book{" +
            "title='" + title + '\'' +
            ", price=" + price +
            ", descriptor='" + descriptor + '\'' +
            ", number='" + number + '\'' +
            '}';
    }
}
