package com.curty.libraryAPI.api.resource;

import com.curty.libraryAPI.api.dto.BookDOT;
import com.curty.libraryAPI.exception.BusinessException;
import com.curty.libraryAPI.model.entity.Book;
import com.curty.libraryAPI.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureWebMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    private BookDOT createNewBook() {
        return BookDOT.builder().author("Fulano").title("Titulo").isbn("12345").build();
    }

    @Test
    @DisplayName("Must successfully create book")
    public void createBookTest() throws Exception{
        //scenario
        BookDOT dto = createNewBook();
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willReturn(Book.builder()
                        .id((long) 10)
                        .author("Fulano")
                        .title("Titulo")
                        .isbn("12345").build());

        //execution
        String json = new ObjectMapper().writeValueAsString(dto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        //verifications
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("should return validation error when book creation data is insufficient")
    public void createInvalidBookTest() throws Exception {
        //scenario
        //execution
        String json = new ObjectMapper().writeValueAsString(new BookDOT());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        //JSON is Null
        //verifications
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("should return validation error when isbn book is duplicated")
    public void createBookWithDuplicatedIsbn() throws Exception{
        //scenario
        BookDOT dto = createNewBook();
        String msgError = "Isbn already filled";
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(msgError));
        //execution
        String json = new ObjectMapper().writeValueAsString(dto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verifications
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]")
                        .value(msgError));
    }
}
