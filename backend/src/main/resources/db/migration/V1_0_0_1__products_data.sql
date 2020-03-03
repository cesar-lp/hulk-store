insert into product_type (name) values
('Shirts'),
('Cups'),
('Comics'),
('Toys'),
('Accessories');

insert into product (name, product_type_id, stock, price) values
('Batman Shirt', 1, 20, 150.00),
('IronMan Cup', 2, 20, 25.00),
('Wonder Woman Comic', 3, 20, 50.00);

insert into product_order (created_at, total) values
('2020-02-28T09:37:15.939362', 450.00),
('2020-02-28T09:50:15.939362', 500.00),
('2020-02-28T14:50:15.939362', 100.00);

insert into product_order_line (product_id, product_name, product_price, quantity, total, product_order_id) values
(1, 'Batman Shirt', 150.00, 2, 300.00, 1),
(2, 'IronMan Cup', 25.00, 2, 50.00, 1),
(3, 'Wonder Woman Comic', 50.00, 2, 100.00, 1),
(1, 'Batman Shirt', 150.00, 2, 300.00, 2),
(2, 'IronMan Cup', 25.00, 2, 50.00, 2),
(3, 'Wonder Woman Comic', 50.00, 2, 100.00, 2),
(1, 'Batman Shirt', 150.00, 2, 300.00, 3),
(2, 'IronMan Cup', 25.00, 2, 50.00, 2),
(3, 'Wonder Woman Comic', 50.00, 2, 100.00, 3);