package com.papershare.papershare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String index(Model model) {
        // Симуляція отримання списку книг з бази даних або іншого джерела
        List<Book> books = getSampleBooks();

        // Передача списку книг на сторінку через Thymeleaf
        model.addAttribute("books", books);

        // Повертаємо назву HTML файлу, який ми хочемо відобразити
        return "index";
    }

    // Метод для симуляції отримання списку книг
    private List<Book> getSampleBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Книга 1", "Автор 1"));
        books.add(new Book("Книга 2", "Автор 2"));
        books.add(new Book("Книга 3", "Автор 3"));
        // Додайте інші книги за необхідності
        return books;
    }

    // Клас представляє сутність "Книга"
    private static class Book {
        private String title;
        private String author;

        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}
