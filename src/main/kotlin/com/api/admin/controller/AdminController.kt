package com.api.admin.controller

import com.api.admin.controller.dto.DepositRequest
import com.api.admin.controller.dto.WithdrawERC20Request
import com.api.admin.controller.dto.WithdrawERC721Request
import com.api.admin.enums.AccountType
import com.api.admin.service.TransferService
import com.api.admin.service.Web3jService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/admin")
class AdminController(
    private val transferService: TransferService,
    private val web3jService: Web3jService,
) {

    @PostMapping("/deposit")
    fun deposit(
        @RequestParam address: String,
        @RequestBody request: DepositRequest,
    ): Mono<ResponseEntity<Void>> {
        return transferService.getTransferData(address, request.chainType, request.transactionHash, AccountType.DEPOSIT, request.accountLogId)
            .then(Mono.just(ResponseEntity.ok().build<Void>()))
            .onErrorResume {
                Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())
            }
    }


    @PostMapping("/withdraw/erc20")
    fun withdrawERC20(
        @RequestParam address: String,
        @RequestBody request: WithdrawERC20Request,
    ): Mono<ResponseEntity<Void>> {
        println("Received withdraw request for address: $address with amount: ${request.amount}")
        return web3jService.createTransactionERC20(address, request.amount, request.chainType,request.accountLogId)
        .doOnSuccess { println("Transaction successful") }
        .then(Mono.just(ResponseEntity.ok().build()))
    }



    @PostMapping("/withdraw/erc721")
    fun withdrawERC721(
        @RequestParam address: String,
        @RequestBody request: WithdrawERC721Request,
    ): Mono<ResponseEntity<Void>> {
        return web3jService.createTransactionERC721(address, request.nftId,request.accountLogId)
            .then(Mono.just(ResponseEntity.ok().build()))
    }


}