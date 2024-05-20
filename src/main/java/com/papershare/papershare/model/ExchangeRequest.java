package com.papershare.papershare.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy = "exchangeRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    @Column(name = "request_book_owner_read_messages")
    private boolean requestBookOwnerReadMessages;

    @Column(name = "offer_book_owner_read_messages")
    private boolean offerBookOwnerReadMessages;

    public ExchangeRequest() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.requestBookOwnerReadMessages = true;
        this.offerBookOwnerReadMessages = true;
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isRequestBookOwnerReadMessages() {
        return requestBookOwnerReadMessages;
    }

    public void setRequestBookOwnerReadMessages(boolean requestBookOwnerReadMessages) {
        this.requestBookOwnerReadMessages = requestBookOwnerReadMessages;
    }

    public boolean isOfferBookOwnerReadMessages() {
        return offerBookOwnerReadMessages;
    }

    public void setOfferBookOwnerReadMessages(boolean offerBookOwnerReadMessages) {
        this.offerBookOwnerReadMessages = offerBookOwnerReadMessages;
    }
}
