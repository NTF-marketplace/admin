package com.api.admin.controller

import com.api.admin.controller.dto.DepositRequest
import com.api.admin.enums.AccountType
import com.api.admin.service.TransferService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/admin")
class AdminController(
    private val transferService: TransferService,
) {

    @PostMapping("/deposit")
    fun deposit(
        @RequestParam address: String,
        @RequestBody request: DepositRequest,
    ): Mono<ResponseEntity<Void>> {
        return transferService.getTransferData(address, request.chainType, request.transactionHash, AccountType.DEPOSIT)
            .then(Mono.just(ResponseEntity.ok().build<Void>()))
            .onErrorResume {
                Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
            }
    }


}