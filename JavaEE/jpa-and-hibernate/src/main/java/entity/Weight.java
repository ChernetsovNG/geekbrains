package entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Embeddable
@AttributeOverrides({
    @AttributeOverride(name = "name",
        column = @Column(name = "WEIGHT_NAME")),
    @AttributeOverride(name = "symbol",
        column = @Column(name = "WEIGHT_SYMBOL"))
})
public class Weight extends Measurment {

    @NotNull
    @Column(name = "WEIGHT")
    protected BigDecimal value;
}
