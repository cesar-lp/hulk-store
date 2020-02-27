package com.herostore.products.utils;

import com.herostore.products.exception.error.FieldValidationError;
import com.herostore.products.exception.error.ValidationError;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.IntStream;

import static com.herostore.products.utils.SerializationUtils.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

public class ResponseBodyMatchers {

    private ResponseBodyMatchers() {
    }

    public static <T> ResultMatcher responseContainsJsonObject(Object expectedObject, Class<T> targetClass) {
        return mvcResult -> {
            var json = mvcResult.getResponse().getContentAsString();
            var actualObject = objectMapper.readValue(json, targetClass);
            assertFieldByField(actualObject, expectedObject);
        };
    }

    public static <T> ResultMatcher responseContainsJsonCollection(List<T> expectedList, Class<T> targetClass) {
        return mvcResult -> {
            var json = mvcResult.getResponse().getContentAsString();
            var javaType = objectMapper.getTypeFactory().constructParametricType(List.class, targetClass);
            List<T> actualList = objectMapper.readValue(json, javaType);

            IntStream.range(0, expectedList.size())
                    .boxed()
                    .forEachOrdered(i -> assertFieldByField(actualList.get(i), expectedList.get(i)));
        };
    }

    public ResultMatcher containsValidationErrors(FieldValidationError... expectedErrors) {
        return mvcResult -> {
            var json = mvcResult.getResponse().getContentAsString();
            var validationError = objectMapper.readValue(json, ValidationError.class);

            assertThat(validationError.getError()).isEqualTo("Validation Error");
            assertThat(validationError.getStatusCode()).isEqualTo(422);
            assertThat(validationError.getFieldValidationErrors()).hasSize(expectedErrors.length);
            assertThat(validationError.getFieldValidationErrors()).containsExactlyInAnyOrder(expectedErrors);
        };
    }

    public static ResponseBodyMatchers response() {
        return new ResponseBodyMatchers();
    }

    private static <T> void assertFieldByField(T actualObject, T expectedObject) {
        assertThat(actualObject).isEqualToComparingFieldByField(expectedObject);
    }
}
