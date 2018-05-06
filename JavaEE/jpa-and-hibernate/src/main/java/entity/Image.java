package entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Image {

    @Column(nullable = false)
    protected String title;

    @Column(nullable = false)
    protected String filename;

    protected int width;

    protected int height;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return width == image.width &&
            height == image.height &&
            Objects.equals(title, image.title) &&
            Objects.equals(filename, image.filename);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, filename, width, height);
    }
}
