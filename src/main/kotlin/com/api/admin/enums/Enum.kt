package com.api.admin.enums

enum class ChainType{
    ETHEREUM_MAINNET,
    POLYGON_MAINNET,
    ETHREUM_GOERLI,
    ETHREUM_SEPOLIA,
    POLYGON_MUMBAI,
}


enum class AccountType{
    WITHDRAW, DEPOSIT
}

enum class TransferType {
    ERC20,ERC721
}

//enum class ChainType(val chainId: Long, val baseUrl: String) {
//    ETHEREUM_MAINNET(1L, "https://mainnet.infura.io/v3/YOUR_INFURA_PROJECT_ID"),
//    POLYGON_MAINNET(137L, "https://polygon-mainnet.infura.io/v3/YOUR_INFURA_PROJECT_ID"),
//    ETHEREUM_GOERLI(5L, "https://goerli.infura.io/v3/YOUR_INFURA_PROJECT_ID"),
//    ETHEREUM_SEPOLIA(11155111L, "https://sepolia.infura.io/v3/YOUR_INFURA_PROJECT_ID"),
//    POLYGON_MUMBAI(80001L, "https://polygon-mumbai.infura.io/v3/YOUR_INFURA_PROJECT_ID")
//}



