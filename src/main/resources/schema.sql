CREATE TABLE IF NOT EXISTS members
(
    `id`               bigint auto_increment NOT NULL primary key,
    `email`            varchar(255)          NOT NULL unique,
    `password`         varchar(255)          NOT NULL,
    `nickname`         varchar(255)          NOT NULL,
    `gender`           varchar(255)          NOT NULL,
    `birth_date`       date                  NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    `id`               bigint auto_increment NOT NULL primary key,
    `member_id`        bigint                NOT NULL,
    `status`           varchar(255)          NOT NULL,
    `pay_type`         varchar(255)          NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS products
(
    `id`               bigint auto_increment NOT NULL primary key,
    `name`             varchar(255)          NOT NULL,
    `price`            bigint                NOT NULL,
    `image_file_name`  varchar(255)          NOT NULL,
    `stock`            int                   NOT NULL,
    `description`      text                  NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
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
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items
(
    `id`               bigint auto_increment NOT NULL primary key,
    `member_id`        bigint                NOT NULL,
    `product_id`       bigint                NOT NULL,
    `quantity`         int                   NOT NULL DEFAULT 1,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS pays
(
    `id`               bigint auto_increment NOT NULL primary key,
    `order_id`         bigint                NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS points
(
    `id`               bigint auto_increment NOT NULL primary key,
    `member_id`        bigint                NOT NULL,
    `point`            bigint                NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS rates
(
    `id`               bigint auto_increment NOT NULL primary key,
    `order_item_id`    bigint                NOT NULL,
    `score`            smallint              NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS rate_stat
(
    `id`               bigint auto_increment NOT NULL primary key,
    `product_id`       bigint                NOT NULL,
    `birth_year_range` varchar(255)          NOT NULL,
    `gender`           varchar(255)          NOT NULL,
    `total_score`      int                NOT NULL,
    `count`            int                   NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

CREATE TABLE IF NOT EXISTS order_stat
(
    `id`               bigint auto_increment NOT NULL primary key,
    `product_id`       bigint                NOT NULL,
    `birth_year_range` varchar(255)          NOT NULL,
    `gender`           varchar(255)          NOT NULL,
    `count`            int                   NOT NULL,
    `created_at`       datetime              NOT NULL,
    `last_modified_at` datetime              NOT NULL
);

create index idx_member_orders on orders (member_id);
create index idx_order_order_items on order_items (order_id);
create index idx_product_order_items on order_items (product_id);
create index idx_member_cart_items on cart_items (member_id);
create index idx_product_cart_items on cart_items (product_id);
create index idx_order_pays on pays (order_id);
create index idx_member_points on points (member_id);
create index idx_order_item_rates on rates (order_item_id);

create index idx_1 on order_items (product_id, quantity);
