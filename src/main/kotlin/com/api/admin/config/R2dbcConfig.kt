package com.api.admin.config

import com.api.admin.enums.AccountType
import com.api.admin.enums.ChainType
import com.api.admin.enums.TransferType
import com.api.admin.util.AccountTypeConvert
import com.api.admin.util.ChainTypeConvert
import com.api.admin.util.StringToEnumConverter
import com.api.admin.util.TransferTypeConvert
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.codec.EnumCodec
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import java.util.ArrayList

@Configuration
@EnableR2dbcRepositories
class R2dbcConfig : AbstractR2dbcConfiguration() {
    @Bean
    override fun connectionFactory(): PostgresqlConnectionFactory {
        val configuration = PostgresqlConnectionConfiguration.builder()
            .host("localhost")
            .port(5435)
            .database("admin")
            .username("admin")
            .password("admin")
            .codecRegistrar(
                EnumCodec.builder()
                    .withEnum("chain_type", ChainType::class.java)
                    .withEnum("transfer_type", TransferType::class.java)
                    .withEnum("account_type", AccountType::class.java)
                    .build()
            )
            .build()
        return PostgresqlConnectionFactory(configuration)
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters: MutableList<Converter<*, *>?> = ArrayList<Converter<*, *>?>()
        converters.add(ChainTypeConvert(ChainType::class.java))
        converters.add(StringToEnumConverter(ChainType::class.java))
        converters.add(AccountTypeConvert(AccountType::class.java))
        converters.add(StringToEnumConverter(AccountType::class.java))
        converters.add(TransferTypeConvert(TransferType::class.java))
        converters.add(StringToEnumConverter(TransferType::class.java))
        return R2dbcCustomConversions(storeConversions, converters)
    }

    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory?): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory!!)
    }

    @Bean
    fun r2dbcEntityTemplate(connectionFactory: ConnectionFactory?): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory!!)
    }
}