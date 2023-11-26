CREATE TABLE file_data (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255),
    upload_date VARCHAR(255),
    change_date VARCHAR(255),
    file_url VARCHAR(255),
    file_size BIGINT,
    file_content BYTEA
);