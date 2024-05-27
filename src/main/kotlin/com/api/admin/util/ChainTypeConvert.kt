package com.api.admin.util

import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.enums.TransferType
import org.springframework.data.r2dbc.convert.EnumWriteSupport

data class ChainTypeConvert<T: Enum<T>>(private val enumType: Class<T>): EnumWriteSupport<ChainType>()

data class TransferTypeConvert<T: Enum<T>>(private val enumType: Class<T>): EnumWriteSupport<TransferType>()

data class AccountTypeConvert<T: Enum<T>>(private val enumType: Class<T>): EnumWriteSupport<AccountType>()
