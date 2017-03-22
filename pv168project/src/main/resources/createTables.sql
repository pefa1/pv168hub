CREATE TABLE book (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL
);

CREATE TABLE customer (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

CREATE TABLE rent (
   id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
   bookId INT REFERENCES books(id) on delete cascade,
   customerId INT REFERENCES customers(id) on delete cascade,
   startDate DATE,
   expectedEnd DATE,
   realEnd TIMESTAMP
);