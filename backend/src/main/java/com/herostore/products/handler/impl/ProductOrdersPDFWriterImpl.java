package com.herostore.products.handler.impl;

import com.herostore.products.dto.response.ProductOrderResponse;
import com.herostore.products.exception.ServiceException;
import com.herostore.products.handler.ProductOrdersPDFWriter;
import com.herostore.products.utils.DateUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Stream;

import static com.herostore.products.utils.NumberUtils.toCurrencyFormat;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrdersPDFWriterImpl implements ProductOrdersPDFWriter {

    Document document;
    List<ProductOrderResponse> productOrders;

    Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void createDocument(OutputStream outputStream) {
        document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
        } catch (DocumentException e) {
            logger.error("Couldn't open PDF document", e);
            throw new ServiceException("Couldn't open PDF document", e);
        }
    }

    @Override
    public void setProductOrders(List<ProductOrderResponse> productOrders) {
        this.productOrders = productOrders;
    }

    @Override
    public void openDocument() {
        document.open();
    }

    @Override
    public boolean isDocumentOpen() {
        return document != null && document.isOpen();
    }

    @Override
    public void writeDocument() {
        writeDocumentTitle();

        for (var productOrder : productOrders) {
            writeProductOrderTableTitle(productOrder);
            writeProductOrderTable(productOrder);
            writeProductOrderTableTotal(productOrder);
        }
    }

    @Override
    public void closeDocument() {
        document.close();
        document = null;
    }

    private void writeDocumentTitle() {
        var titleFont = FontFactory.getFont(FontFactory.COURIER, 22, BaseColor.BLACK);
        var title = new Paragraph(new Phrase(new Chunk("Hero Store", titleFont)));

        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(40);

        try {
            document.add(title);
        } catch (DocumentException e) {
            logger.error("Couldn't write PDF document title", e);
            throw new ServiceException("Couldn't write PDF document title", e);
        }
    }

    private void writeProductOrderTableTitle(ProductOrderResponse productOrder) {
        var titleFont = FontFactory.getFont(FontFactory.COURIER, 13, BaseColor.BLACK);
        var tableTitle = new Paragraph(new Phrase("Product Order: " + productOrder.getId(), titleFont));

        var productOrderCell = new PdfPCell(tableTitle);
        productOrderCell.setPadding(0);
        productOrderCell.setBorder(Rectangle.NO_BORDER);
        productOrderCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        var text = "Date: " + DateUtils.toLocalizedDateTime(productOrder.getCreatedAt());
        var paymentOrderDate = new Paragraph(new Phrase(text, titleFont));
        var productOrderCreationDateCell = new PdfPCell(paymentOrderDate);
        productOrderCreationDateCell.setPadding(0);
        productOrderCreationDateCell.setBorder(Rectangle.NO_BORDER);
        productOrderCreationDateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        var table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(productOrderCell);
        table.addCell(productOrderCreationDateCell);

        try {
            document.add(table);
        } catch (DocumentException e) {
            logger.error("Couldn't write product order number and creation date to PDF document", e);
            throw new ServiceException("Couldn't write product order number and creation date to PDF document", e);
        }
    }

    private void writeProductOrderTable(ProductOrderResponse productOrder) {
        var table = new PdfPTable(new float[]{.5f, 2, 1, .5f, 1});
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addTableHeaders(table);
        addProductOrderLinesToTable(productOrder, table);

        try {
            document.add(table);
        } catch (DocumentException e) {
            logger.error("Couldn't write product order table to PDF document", e);
            throw new ServiceException("Couldn't write product order table to PDF document", e);
        }
    }

    private void writeProductOrderTableTotal(ProductOrderResponse productOrder) {
        var totalFont = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        var text = "Total: " + toCurrencyFormat(productOrder.getTotal());
        var total = new Paragraph(new Phrase(new Chunk(text, totalFont)));

        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingAfter(30);

        try {
            document.add(total);
        } catch (DocumentException e) {
            logger.error("Couldn't write product order total to PDF document", e);
            throw new ServiceException("Couldn't write product order total to PDF document", e);
        }
    }

    private void addTableHeaders(PdfPTable table) {
        Stream.of("Order ID", "Product", "Price", "Quantity", "Order total")
                .forEachOrdered(column -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    header.setPaddingTop(10);
                    header.setPaddingBottom(10);
                    header.setPhrase(new Phrase(column));
                    table.addCell(header);
                });
    }

    private void addProductOrderLinesToTable(ProductOrderResponse productOrder, PdfPTable table) {
        for (var productOrderLine : productOrder.getProductOrderLines()) {
            var cellText = new Phrase(productOrderLine.getId().toString());
            table.addCell(getRightAlignedCell(cellText));

            table.addCell(productOrderLine.getProductName());

            cellText = new Phrase(toCurrencyFormat(productOrderLine.getProductPrice()));
            table.addCell(getRightAlignedCell(cellText));

            cellText = new Phrase(productOrderLine.getQuantity().toString());
            table.addCell(getRightAlignedCell(cellText));

            cellText = new Phrase(toCurrencyFormat(productOrderLine.getTotal()));
            table.addCell(getRightAlignedCell(cellText));
        }
    }

    private static PdfPCell getRightAlignedCell(Phrase phrase) {
        var quantityCell = new PdfPCell(phrase);
        quantityCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return quantityCell;
    }
}
