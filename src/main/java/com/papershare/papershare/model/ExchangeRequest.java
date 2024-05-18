package com.papershare.papershare.model;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "exchange_requests")
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_book_id")
    private Book offeredBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_book_id")
    private Book requestedBook;

    @Enumerated(EnumType.STRING)
    private ExchangeRequestStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    public ExchangeRequest() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public Book getOfferedBook() {
        return offeredBook;
    }

    public void setOfferedBook(Book offeredBook) {
        this.offeredBook = offeredBook;
    }

    public Book getRequestedBook() {
        return requestedBook;
    }

    public void setRequestedBook(Book requestedBook) {
        this.requestedBook = requestedBook;
    }

    public ExchangeRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ExchangeRequestStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
