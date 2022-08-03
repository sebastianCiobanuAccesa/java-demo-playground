package com.mcserby.playground.javademoplayground.service;

import com.mcserby.playground.javademoplayground.dto.Currency;
import com.mcserby.playground.javademoplayground.dto.ExchangeRequest;
import com.mcserby.playground.javademoplayground.dto.ExchangeResult;
import com.mcserby.playground.javademoplayground.dto.Liquidity;
import com.mcserby.playground.javademoplayground.persistence.model.Agency;
import com.mcserby.playground.javademoplayground.persistence.model.ExchangePool;
import com.mcserby.playground.javademoplayground.persistence.model.Person;
import com.mcserby.playground.javademoplayground.persistence.model.Wallet;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.ExchangePoolRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.PersonRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DexTest {

    @Mock
    private AgencyRepository agencyRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private ExchangePoolRepository exchangePoolRepository;

    @Test
    void testSwapHappyFlow() {
        Dex dex = new Dex(agencyRepository, personRepository, walletRepository, exchangePoolRepository);
        ExchangeRequest request = ExchangeRequest.builder()
                .agencyId(1)
                .personId(1)
                .walletId(1)
                .value(100.0)
                .from("USD")
                .to("BTC")
                .build();

        ExchangePool usdBtc = ExchangePool.builder()
                .id(1L)
                .liquidityOne(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(1_000_000.0).build())
                .liquidityTwo(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Bitcoin").ticker("BTC").value(100_000.0).build())
                .build();
        Agency agency = Agency.builder()
                .id(1L)
                .exchangePools(List.of(usdBtc))
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .name("test-wallet-with-500USD")
                .liquidityList(List.of(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(500.0).build()))
                .build();

        when(agencyRepository.findById(1L)).thenReturn(Optional.of(agency));
        when(personRepository.findById(1L)).thenReturn(Optional.of(Person.builder().build()));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        ExchangeResult swap = dex.swap(request);

        assertTrue(swap.isSuccessful());
        assertEquals(400.0, wallet.getLiquidityList().get(0).getValue(), 0.1);
        assertEquals(9.9, wallet.getLiquidityList().get(1).getValue(), 0.1);
    }

    @Test
    void testSwapHappyFlowReverseSwap() {
        Dex dex = new Dex(agencyRepository, personRepository, walletRepository, exchangePoolRepository);
        ExchangeRequest request = ExchangeRequest.builder()
                .agencyId(1)
                .personId(1)
                .walletId(1)
                .from("USD")
                .to("BTC")
                .value(100.0)
                .build();

        ExchangePool usdBtc = ExchangePool.builder()
                .id(1L)
                .liquidityOne(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Bitcoin").ticker("BTC").value(100_000.0).build())
                .liquidityTwo(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(1_000_000.0).build())
                .build();
        Agency agency = Agency.builder()
                .id(1L)
                .exchangePools(List.of(usdBtc))
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .name("test-wallet-with-500USD")
                .liquidityList(List.of(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(500.0).build()))
                .build();

        when(agencyRepository.findById(1L)).thenReturn(Optional.of(agency));
        when(personRepository.findById(1L)).thenReturn(Optional.of(Person.builder().build()));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        ExchangeResult swap = dex.swap(request);

        assertTrue(swap.isSuccessful());
        assertEquals(400.0, wallet.getLiquidityList().get(0).getValue(), 0.1);
        assertEquals(9.9, wallet.getLiquidityList().get(1).getValue(), 0.1);
    }

    @Test
    void testSwapHappyFlowMultiplePurchases() {
        Dex dex = new Dex(agencyRepository, personRepository, walletRepository, exchangePoolRepository);
        ExchangeRequest request = ExchangeRequest.builder()
                .agencyId(1)
                .personId(1)
                .walletId(1)
                .from("USD")
                .to("BTC")
                .value(5000.0)
                .build();

        ExchangePool usdBtc = ExchangePool.builder()
                .id(1L)
                .liquidityOne(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(1_000_000.0).build())
                .liquidityTwo(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Bitcoin").ticker("BTC").value(100_000.0).build())
                .build();
        Agency agency = Agency.builder()
                .id(1L)
                .exchangePools(List.of(usdBtc))
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .name("test-wallet-with-500USD")
                .liquidityList(List.of(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(50_000.0).build()))
                .build();

        when(agencyRepository.findById(1L)).thenReturn(Optional.of(agency));
        when(personRepository.findById(1L)).thenReturn(Optional.of(Person.builder().build()));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        System.out.println("Graph: "+agency.getGraph());

        ExchangeResult swap1 = dex.swap(request);

        System.out.println("Graph: "+agency.getGraph());

        ExchangeResult swap2 = dex.swap(request);
        ExchangeResult swap3 = dex.swap(request);
        ExchangeResult swap4 = dex.swap(request);

        assertTrue(swap1.isSuccessful());
        assertTrue(swap2.isSuccessful());
        assertTrue(swap3.isSuccessful());
        assertTrue(swap4.isSuccessful());

        assertEquals(30_000.0, wallet.getLiquidityList().get(0).getValue(), 0.1);
        assertEquals(1960.78, wallet.getLiquidityList().get(1).getValue(), 0.1);
    }


    @Test
    void testSwapMultiStepPools() {
        Dex dex = new Dex(agencyRepository, personRepository, walletRepository, exchangePoolRepository);
        ExchangeRequest request = ExchangeRequest.builder()
                .agencyId(1)
                .personId(1)
                .walletId(1)
                .from("USD")
                .to("RON")
                .value(5000.0)
                .build();

        ExchangePool usdBtc = ExchangePool.builder()
                .id(1L)
                .liquidityOne(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(1_000_000.0).build())
                .liquidityTwo(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Bitcoin").ticker("BTC").value(100_000.0).build())
                .build();
        ExchangePool btcRon = ExchangePool.builder()
                .id(1L)
                .liquidityOne(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Bitcoin").ticker("BTC").value(10_000.0).build())
                .liquidityTwo(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Leu").ticker("RON").value(5_000_000.0).build())
                .build();
        Agency agency = Agency.builder()
                .id(1L)
                .exchangePools(List.of(usdBtc, btcRon))
                .build();

        Wallet wallet = Wallet.builder()
                .id(1L)
                .name("test-wallet-with-500USD")
                .liquidityList(List.of(com.mcserby.playground.javademoplayground.persistence.model.Liquidity
                        .builder().name("Dollar").ticker("USD").value(50_000.0).build()))
                .build();

        when(agencyRepository.findById(1L)).thenReturn(Optional.of(agency));
        when(personRepository.findById(1L)).thenReturn(Optional.of(Person.builder().build()));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        ExchangeResult swap1 = dex.swap(request);

        assertTrue(swap1.isSuccessful());
        // TODO assert expected values
    }
}