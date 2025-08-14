-- DROP DATABASE restaurant_maestro;
CREATE DATABASE restaurant_maestro;
USE restaurant_maestro;
CREATE TABLE employee (
    employee_id int auto_increment PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    username VARCHAR(50) ,
    password_hash VARCHAR(256),
    salary INT,
    shift ENUM('Lunch', 'Dinner', 'Both'),
    gender ENUM('male','female','other')
);

CREATE TABLE waiter (
    employee_id int,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE cook (
    employee_id int,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE manager (
    employee_id int,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE delivery_boy (
    employee_id int,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

CREATE TABLE menu_item (
    item_name VARCHAR(50),
    item_id int PRIMARY KEY auto_increment,
    price int,
    item_availability ENUM('no','yes'),
    item_type ENUM('veg','non-veg')
);

-- item type lite

CREATE TABLE restaurant_order (
    order_id int PRIMARY KEY auto_increment,
    bill_amount DECIMAL(8,2),
    order_time DATETIME,
    completion_time DATETIME,
    order_status ENUM('active', 'completed'),
    discount int,
    order_type ENUM('dine in', 'delivery')
);

CREATE TABLE invoice (
    invoice_id int PRIMARY KEY auto_increment,
    waiter_rating int not null,
    invoice_time DATETIME not null,
    mode_of_payment ENUM('cash','card','UPI')
);

CREATE TABLE customer_information (
    -- invoice_time DATETIME,
    invoice_id int,
    cfirst_name VARCHAR(50),
    clast_name VARCHAR(50),
    FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id)
);

CREATE TABLE restaurant_table (
    table_number int PRIMARY KEY auto_increment,
    res_first_name VARCHAR(50),
    res_last_name VARCHAR(50),
    res_phone_number CHAR(10),
    table_status ENUM('Reserved', 'Available', 'Occupied')
);

CREATE TABLE cusine_cook (
    cusine_name VARCHAR(50),
    item_id int,
    employee_id int,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (item_id) REFERENCES menu_item(item_id),
    PRIMARY KEY (cusine_name, item_id)
);

CREATE TABLE ordered_item (
    order_id int,
    FOREIGN KEY (order_id) REFERENCES restaurant_order(order_id),
    item_id int,
    FOREIGN KEY (item_id) REFERENCES menu_item(item_id),
    comment VARCHAR(500),
    quantity_ordered int,
    item_status ENUM('sent','preparing', 'served', 'delivered'),
    PRIMARY KEY (order_id, item_id)
);

CREATE TABLE order_invoice (
    order_id int,
    FOREIGN KEY (order_id) REFERENCES restaurant_order(order_id),
    invoice_id int,
    FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id)
);

CREATE TABLE delivered_by (
	employee_id int,
    FOREIGN KEY (employee_id) REFERENCES delivery_boy(employee_id),
    order_id int,
    FOREIGN KEY (order_id) REFERENCES restaurant_order(order_id),
    address VARCHAR(50)
);

CREATE TABLE seated_at (
    order_id int,
    FOREIGN KEY (order_id) REFERENCES restaurant_order(order_id),
    table_number int,
    FOREIGN KEY (table_number) REFERENCES restaurant_table(table_number)
);

CREATE TABLE assign_to (
    table_number int,
    FOREIGN KEY (table_number) REFERENCES restaurant_table(table_number),
    employee_id int,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    PRIMARY KEY (table_number, employee_id)
);