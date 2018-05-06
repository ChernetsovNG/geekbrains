package entity;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class Measurment {

    @NotNull
    protected String name;

    @NotNull
    protected String symbol;
}
