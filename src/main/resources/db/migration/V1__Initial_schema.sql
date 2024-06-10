CREATE TYPE chain_type AS ENUM (
    'ETHEREUM_MAINNET',
    'LINEA_MAINNET',
    'LINEA_SEPOLIA',
    'POLYGON_MAINNET',
    'ETHEREUM_HOLESKY',
    'ETHEREUM_SEPOLIA',
    'POLYGON_AMOY'
    );

CREATE TYPE account_type AS ENUM (
    'WITHDRAW', 'DEPOSIT'
);

CREATE TYPE transfer_type AS ENUM (
    'ERC20', 'ERC721'
    );


CREATE TABLE IF NOT EXISTS nft (
    id BIGINT PRIMARY KEY,
    token_id VARCHAR(255) NOT NULL,
    token_address VARCHAR(255) NOT NULL,
    chain_type chain_type NOT NULL
    );


CREATE TABLE IF NOT EXISTS transfer (
    id SERIAL PRIMARY KEY,
    wallet VARCHAR(255) NOT NULL,
    nft_id BIGINT REFERENCES nft(id),
    timestamp bigint not null,
    account_type account_type NOT NULL,
    balance DECIMAL(19, 4),
    transfer_type transfer_type NOT NULL,
    transaction_hash VARCHAR(255) NOT NULL,
    chain_type chain_type
);
