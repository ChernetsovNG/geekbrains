package entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item {
    @Id
    @GeneratedValue
    private Long id;

    protected Set<Bid> bids = new HashSet<>();

    @NotNull
    @Size(
        min = 2,
        max = 255,
        message = "Name is required, maximum 255 characters."
    )
    protected String name;

    @Future
    protected ZonedDateTime auctionEnd;

    public void addBid(Bid bid) {
        if (bid == null) {
            throw new NullPointerException("Can't add null Bid");
        }
        if (bid.getItem() != null) {
            throw new IllegalStateException("Bid is already assigned to an Item");
        }
        getBids().add(bid);
        bid.setItem(this);
    }

    public Set<Bid> getBids() {
        return Collections.unmodifiableSet(bids);
    }

    public void setBids(Set<Bid> bids) {
        this.bids = bids;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getAuctionEnd() {
        return auctionEnd;
    }

    public void setAuctionEnd(ZonedDateTime auctionEnd) {
        this.auctionEnd = auctionEnd;
    }
}
