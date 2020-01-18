insert into product_type (id, name) values (1, 'Shirts');
insert into product_type (id, name) values (2, 'Cups');
insert into product_type (id, name) values (3, 'Comics');
insert into product_type (id, name) values (4, 'Toys');
insert into product_type (id, name) values (5, 'Accessories');

insert into product (id, name, product_type_id, price, stock) values (1, 'Batman Shirt', 1, 150.00, 5);
insert into product (id, name, product_type_id, price, stock) values (2, 'IronMan Shirt', 2, 25.00, 20);
insert into product (id, name, product_type_id, price, stock) values (3, 'Wonder Woman Shirt', 3, 50.00, 10);

insert into product_inventory (id, product_id, stock) values (1, 1, 10);
insert into product_inventory (id, product_id, stock) values (2, 2, 10);
insert into product_inventory (id, product_id, stock) values (3, 3, 10);
