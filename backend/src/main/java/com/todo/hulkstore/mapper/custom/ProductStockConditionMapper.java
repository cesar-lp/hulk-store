package com.todo.hulkstore.mapper.custom;

import com.todo.hulkstore.constants.ProductStockCondition;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class ProductStockConditionMapper implements Converter<String, ProductStockCondition> {

    @Override
    public ProductStockCondition convert(String source) {
        var stockCondition = Arrays.stream(ProductStockCondition.values())
                .filter(condition -> condition.getValue().equals(source))
                .findFirst();

        if (stockCondition.isEmpty()) {
            return ProductStockCondition.ALL;
        }

        return stockCondition.get();
    }
}
