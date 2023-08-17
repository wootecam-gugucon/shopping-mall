insert into products(id, name, image_file_name, stock, description, price, created_at, last_modified_at)
values (default, '치킨', 'fried_chicken.png', 100, 'test_description', 20000, now(), now());
insert into products(id, name, image_file_name, stock, description, price, created_at, last_modified_at)
values (default, '피자', 'pizza.png', 100, 'test_description', 25000, now(), now());
insert into products(id, name, image_file_name, stock, description, price, created_at, last_modified_at)
values (default, '사케', 'sake.png', 100, 'test_description', 30000, now(), now());
insert into products(id, name, image_file_name, stock, description, price, created_at, last_modified_at)
values (default, '품절된 치킨', 'null.png', 0, 'test_description', 20000, now(), now());

insert into members(id, email, password, nickname, created_at, last_modified_at)
values (default, 'test_email@woowafriends.com', 'test_password!', 'tester1', now(), now());
insert into members(id, email, password, nickname, created_at, last_modified_at)
values (default, 'other_test_email@woowafriends.com', 'test_password!', 'tester2', now(), now());
