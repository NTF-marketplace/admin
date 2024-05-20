CREATE TABLE IF NOT EXISTS nft (
    id BIGINT PRIMARY KEY,
    token_id VARCHAR(255) NOT NULL,
    token_address VARCHAR(255) NOT NULL,
    chain_type varchar(100) NOT NULL,
    nft_name varchar(255) NOT NULL,
    collection_name varchar(500)
    );


CREATE TABLE IF NOT EXISTS transfer (
    id BIGINT PRIMARY KEY,
    wallet VARCHAR(255) NOT NULL,
    nft_id BIGINT REFERENCES nft(id),
    timestamp bigint not null,
    account_type VARCHAR(255) NOT NULL
);
