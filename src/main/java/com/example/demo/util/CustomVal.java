package com.example.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomVal {

    /**
     * @Valid 를 통해 Form 내부 데이터의 유효성 검사
     */
//    public ResponseEntity validateForm(BindingResult bindingResult, Object service){
//        Map<String, String> validatorResult = memberService.validateHandling(bindingResult);
//
//        return new ResponseEntity<>();
//    }

    @Transactional(readOnly = true)
    public  ResponseEntity validateHandling(BindingResult bindingResult, HashMap<String,Object> response) {

        Map<String, String> validatorResult = new HashMap<>();

        for(FieldError error : bindingResult.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        response.put("val_result",validatorResult);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
