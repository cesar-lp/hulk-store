insert into product_type (id, name) values (1, 'Shirts');
insert into product_type (id, name) values (2, 'Cups');
insert into product_type (id, name) values (3, 'Comics');
insert into product_type (id, name) values (4, 'Toys');
insert into product_type (id, name) values (5, 'Accessories');

insert into product (id, name, product_type_id, stock, price) values (1, 'Batman Shirt', 1, 20, 150.00);
insert into product (id, name, product_type_id, stock, price) values (2, 'IronMan Cup', 2, 20, 25.00);
insert into product (id, name, product_type_id, stock, price) values (3, 'Wonder Woman Comic', 3, 20, 50.00);

insert into product_order (id, created_at, total) values (1, '2020-02-28T09:37:15.939362', 450.00);
insert into product_order (id, created_at, total) values (2, '2020-02-28T09:50:15.939362', 500.00);
insert into product_order (id, created_at, total) values (3, '2020-02-28T14:50:15.939362', 100.00);

insert into product_order_line (id, product_id, product_name, product_price, quantity, total, payment_id)
values (1, 1, 'Batman Shirt', 150.00, 2, 300.00, 1),
(2, 2, 'IronMan Cup', 25.00, 2, 50.00, 1),
(3, 3, 'Wonder Woman Comic', 50.00, 2, 100.00, 1),
(4, 1, 'Batman Shirt', 150.00, 2, 300.00, 2),
(5, 2, 'IronMan Cup', 25.00, 2, 50.00, 2),
(6, 3, 'Wonder Woman Comic', 50.00, 2, 100.00, 2),
(7, 1, 'Batman Shirt', 150.00, 2, 300.00, 3),
(8, 2, 'IronMan Cup', 25.00, 2, 50.00, 2),
(9, 3, 'Wonder Woman Comic', 50.00, 2, 100.00, 3);
