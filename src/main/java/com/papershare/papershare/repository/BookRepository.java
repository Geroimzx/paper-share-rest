package com.papershare.papershare.repository;

import com.papershare.papershare.model.Book;
import com.papershare.papershare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    public Optional<Book> findById(Long id);

    public List<Book> findAll();

    public List<Book> findByTitle(String title);

    public List<Book> findByAuthor(String author);

    public List<Book> findByGenre(String genre);

    public List<Book> findByIsbn(String isbn);

    public List<Book> findByOwner(User owner);

    public List<Book> findByAvailable(boolean available);
}
