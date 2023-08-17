delete from cart_items;
delete from order_items;
delete from orders;
delete from products;
delete from members;

insert into products(id, name, image_file_name, stock, description, price, created_at, last_modified_at)
values (1, '치킨', 'fried_chicken.png', 100, 'test_description', 20000, now(), now()),
       (2, '피자', 'pizza.png', 100, 'test_description', 25000, now(), now()),
       (3, '사케', 'sake.png', 100, 'test_description', 30000, now(), now());
