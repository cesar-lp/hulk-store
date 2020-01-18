INSERT INTO product_type (id, name) VALUES (1, 'Shirts');
INSERT INTO product_type (id, name) VALUES (2, 'Cups');
INSERT INTO product_type (id, name) VALUES (3, 'Comics');
INSERT INTO product_type (id, name) VALUES (4, 'Toys');
INSERT INTO product_type (id, name) VALUES (5, 'Accessories');

INSERT INTO product (id, name, product_type_id, price, stock) values (1, 'Batman Shirt', 1, 150.00, 5);
INSERT INTO product (id, name, product_type_id, price, stock) values (2, 'IronMan Shirt', 2, 25.00, 20);
INSERT INTO product (id, name, product_type_id, price, stock) values (3, 'Wonder Woman Shirt', 3, 50.00, 10);

-- TODO: create missing table and entity (contains dc, marvel, etc.)