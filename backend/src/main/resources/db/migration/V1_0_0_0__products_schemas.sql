CREATE TABLE IF NOT EXISTS product_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE CONSTRAINT name_not_empty CHECK (name <> '')
);

CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL CONSTRAINT name_not_empty CHECK (name <> ''),
    stock INTEGER NOT NULL CONSTRAINT stock_not_negative CHECK (stock >= 0),
    price NUMERIC(15, 6) NOT NULL CONSTRAINT price_not_negative CHECK (price >= 0),
    product_type_id INTEGER NOT NULL,
    CONSTRAINT product_product_type_id_fk FOREIGN KEY (product_type_id)
        REFERENCES product_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS product_order (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    total NUMERIC(15, 6) NOT NULL CONSTRAINT total_not_negative CHECK (total >= 0)
);

CREATE TABLE IF NOT EXISTS product_order_line (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    product_name varchar(128) NOT NULL CONSTRAINT product_name_not_empty CHECK (product_name <> ''),
    product_price NUMERIC(15, 6) NOT NULL CONSTRAINT product_price_not_negative CHECK (product_price >= 0),
    quantity INTEGER NOT NULL CONSTRAINT quantity_not_negative CHECK (quantity >= 0),
    total NUMERIC(15, 6) NOT NULL CONSTRAINT total_not_negative CHECK (total >= 0),
    product_order_id INTEGER NOT NULL,
    CONSTRAINT product_order_line_product_id_fk FOREIGN KEY (product_id)
        REFERENCES product (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT product_order_line_product_order_id_fk FOREIGN KEY (product_order_id)
        REFERENCES product_order (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);
