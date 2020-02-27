package com.herostore.products.mapper.custom;

import com.herostore.products.constants.ProductStockCondition;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
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
