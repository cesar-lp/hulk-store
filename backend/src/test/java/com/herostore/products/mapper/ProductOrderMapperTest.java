package com.herostore.products.mapper;

import com.herostore.products.domain.ProductOrder;
import com.herostore.products.domain.ProductOrderLine;
import com.herostore.products.domain.embedded.ProductDetail;
import com.herostore.products.dto.ProductOrderLineDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderMapperTest {

    @Autowired
    ProductOrderMapper mapper;

    @Test
    void shouldMapProductOrderToProductResponse() {
        var productOrder = ProductOrder.builder()
                .id(1L)
                .productOrderLines(asList(mockIronManCupOrder(), mockBatmanCupOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(450.00))
                .build();

        var productOrderResponse = mapper.toProductOrderResponse(productOrder);

        Assertions.assertEquals(productOrder.getId(), productOrderResponse.getId());
        Assertions.assertEquals(productOrder.getCreatedAt(), productOrderResponse.getCreatedAt());
        Assertions.assertEquals(productOrder.getTotal(), productOrderResponse.getTotal());
        assertProductOrders(productOrder.getProductOrderLines().get(0), productOrderResponse.getProductOrderLines().get(0));
        assertProductOrders(productOrder.getProductOrderLines().get(1), productOrderResponse.getProductOrderLines().get(1));
    }

    @Test
    void shouldMapProductOrderListToProductOrderDTOList() {
        var ironManCupOrder = mockIronManCupOrder();
        var batmanCupOrder = mockBatmanCupOrder();

        var productOrderList = asList(ironManCupOrder, batmanCupOrder);
        var productOrderDTOList = mapper.toProductOrderLineDTOList(productOrderList);

        assertEquals(2, productOrderDTOList.size());
        assertProductOrders(ironManCupOrder, productOrderDTOList.get(0));
        assertProductOrders(batmanCupOrder, productOrderDTOList.get(1));
    }

    @Test
    void shouldMapProductOrderToProductOrderDTO() {
        var productOrder = mockIronManCupOrder();
        var productOrderDTO = mapper.toProductOrderLineDTO(productOrder);
        assertProductOrders(productOrder, productOrderDTO);
    }

    @Test
    void shouldMapProductOrderListToProductOrderResponseList() {
        var list = mapper.toProductOrderResponseList(emptyList());
        assertTrue(list.isEmpty());

        list = mapper.toProductOrderResponseList(null);
        assertTrue(list.isEmpty());

        var firstProductOrder = ProductOrder.builder()
                .id(1L)
                .productOrderLines(singletonList(mockIronManCupOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(225.00))
                .build();

        var secondProductOrder = ProductOrder.builder()
                .id(2L)
                .productOrderLines(singletonList(mockBatmanCupOrder()))
                .createdAt(LocalDateTime.now())
                .total(BigDecimal.valueOf(225.00))
                .build();

        var productOrderResponse = mapper.toProductOrderResponseList(asList(firstProductOrder, secondProductOrder));

        assertEquals(2, productOrderResponse.size());

        var paymentOrder = productOrderResponse.get(0);
        {
            assertEquals(firstProductOrder.getId(), paymentOrder.getId());
            assertEquals(firstProductOrder.getCreatedAt(), paymentOrder.getCreatedAt());
            assertProductOrders(firstProductOrder.getProductOrderLines().get(0), paymentOrder.getProductOrderLines().get(0));
            assertEquals(firstProductOrder.getTotal(), paymentOrder.getTotal());
        }

        paymentOrder = productOrderResponse.get(1);
        {
            assertEquals(secondProductOrder.getId(), paymentOrder.getId());
            assertEquals(secondProductOrder.getCreatedAt(), paymentOrder.getCreatedAt());
            assertProductOrders(secondProductOrder.getProductOrderLines().get(0), paymentOrder.getProductOrderLines().get(0));
            assertEquals(secondProductOrder.getTotal(), paymentOrder.getTotal());
        }
    }

    private void assertProductOrders(ProductOrderLine source, ProductOrderLineDTO target) {
        assertEquals(source.getId(), target.getId());
        Assertions.assertEquals(source.getProductDetail().getId(), target.getProductId());
        Assertions.assertEquals(source.getProductDetail().getName(), target.getProductName());
        Assertions.assertEquals(source.getProductDetail().getPrice(), target.getProductPrice());
        assertEquals(source.getQuantity(), target.getQuantity());
        assertEquals(source.getTotal(), target.getTotal());
    }

    private ProductOrderLine mockIronManCupOrder() {
        var productDetail = ProductDetail.builder()
                .id(1L)
                .name("Iron Man Cup")
                .price(BigDecimal.valueOf(75.00))
                .build();

        return ProductOrderLine.builder()
                .id(1L)
                .productDetail(productDetail)
                .quantity(3)
                .total(productDetail.getPrice().multiply(BigDecimal.valueOf(3)))
                .build();
    }

    private ProductOrderLine mockBatmanCupOrder() {
        var batmanCupDetail = ProductDetail.builder()
                .id(2L)
                .name("Batman Cup")
                .price(BigDecimal.valueOf(45.00))
                .build();

        return ProductOrderLine.builder()
                .id(2L)
                .productDetail(batmanCupDetail)
                .quantity(5)
                .total(batmanCupDetail.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();
    }
}
