package com.bookstore.jpa.repositories;

import com.bookstore.jpa.models.BookModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookModel, UUID> {
    BookModel findBookModelByTitle(String title);

    @Query(value = """
            Select * FROM tb_book WHERE published_id = :id
            """, nativeQuery = true)
    List<BookModel> findBooksByPublisherId(@Param("id") UUID id);
}
