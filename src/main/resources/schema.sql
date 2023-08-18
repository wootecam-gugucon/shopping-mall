CREATE TABLE IF NOT EXISTS members
(
    `id`               bigint auto_increment NOT NULL primary key,
    `email`            varchar(255)          NOT NULL unique,
    `password`         varchar(255)          NOT NULL,
    `nickname`         varchar(255)          NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    `id`               bigint auto_increment NOT NULL primary key,
    `member_id`        bigint                NOT NULL,
    `status`           varchar(255)          NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL,
    foreign key (member_id) references members (id)
);

CREATE TABLE IF NOT EXISTS order_items
(
    `id`               bigint auto_increment NOT NULL primary key,
    `order_id`         bigint,
    `product_id`       bigint                NOT NULL,
    `name`             varchar(255)          NOT NULL,
    `price`            bigint                NOT NULL,
    `image_file_name`  varchar(255)          NOT NULL,
    `quantity`         int                   NOT NULL DEFAULT 1,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL,
    foreign key (order_id) references orders (id)
);

CREATE TABLE IF NOT EXISTS products
(
    `id`               bigint auto_increment NOT NULL primary key,
    `name`             varchar(255)          NOT NULL,
    `price`            bigint                NOT NULL,
    `image_file_name`  varchar(255)          NOT NULL unique,
    `stock`            int                   NOT NULL,
    `description`      text                  NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items
(
    `id`               bigint auto_increment NOT NULL primary key,
    `member_id`        bigint                NOT NULL,
    `product_id`       bigint                NOT NULL,
    `quantity`         int                   NOT NULL DEFAULT 1,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL,
    foreign key (product_id) references products (id),
    foreign key (member_id) references members (id)
);

CREATE TABLE IF NOT EXISTS pays
(
    `id`               bigint auto_increment NOT NULL primary key,
    `order_id`         bigint                NOT NULL,
    `order_name`       varchar(255)          NOT NULL,
    `price`            bigint                NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL,
    foreign key (order_id) references orders (id)
);
