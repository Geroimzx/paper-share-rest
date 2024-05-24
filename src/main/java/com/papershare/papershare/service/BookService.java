package com.papershare.papershare.service;

import com.papershare.papershare.model.Book;

import java.util.List;


public interface BookService {

    List<Book> getAllBooks();

    Book getBookById(Long id);

    Book save(Book book);

    long getCountOfAllAvailableBooks();
}