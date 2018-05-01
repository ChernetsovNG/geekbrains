package entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Blob;
import java.sql.Clob;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Item {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToMany
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

    @Lob
    protected Blob imageBlob;

    @Lob
    protected Clob description;

    @NotNull
    @org.hibernate.annotations.Type(
        type = "monetary_amount_usd"
    )
    @org.hibernate.annotations.Columns(columns = {
        @Column(name = "BUYNOWPRICE_AMOUNT"),
        @Column(name = "BUYNOWPRICE_CURRENCY", length = 3)
    })
    protected MonetaryAmount buyNowPrice;

    @NotNull
    @org.hibernate.annotations.Type(
        type = "monetary_amount_eur"
    )
    @org.hibernate.annotations.Columns(columns = {
        @Column(name = "INITIALPRICE_AMOUNT"),
        @Column(name = "INITIALPRICE_CURRENCY", length = 3)
    })
    protected MonetaryAmount initialPrice;

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

    public MonetaryAmount getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(MonetaryAmount buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public MonetaryAmount getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(MonetaryAmount initialPrice) {
        this.initialPrice = initialPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
            "id=" + id +
            ", bids=" + bids +
            ", name='" + name + '\'' +
            ", auctionEnd=" + auctionEnd +
            ", buyNowPrice=" + buyNowPrice +
            ", initialPrice=" + initialPrice +
            '}';
    }
}
