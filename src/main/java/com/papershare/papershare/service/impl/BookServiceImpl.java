package com.papershare.papershare.service.impl;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.repository.BookRepository;
import com.papershare.papershare.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.orElse(null);
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public boolean deleteBook(Long id) {
        if(bookRepository.findById(id).isPresent()) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

}