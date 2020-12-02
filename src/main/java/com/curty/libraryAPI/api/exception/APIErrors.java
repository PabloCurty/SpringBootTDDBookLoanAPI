package com.curty.libraryAPI.api.exception;

import com.curty.libraryAPI.exception.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIErrors {

    private List<String> errors;
    public APIErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult
                .getAllErrors()
                .forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public APIErrors(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public APIErrors(ResponseStatusException ex) {
        this.errors = Arrays.asList(ex.getReason());
    }

    public List<String> getErrors() {
        return errors;
    }
}
