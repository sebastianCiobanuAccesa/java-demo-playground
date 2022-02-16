package com.mcserby.playground.javademoplayground.integration;

import com.mcserby.playground.javademoplayground.persistence.model.Agency;
import com.mcserby.playground.javademoplayground.persistence.model.Liquidity;
import com.mcserby.playground.javademoplayground.persistence.model.Person;
import com.mcserby.playground.javademoplayground.persistence.model.Wallet;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.PersonRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.WalletRepository;
import com.mcserby.playground.javademoplayground.service.RandomEntityGenerator;
import com.mcserby.playground.javademoplayground.service.SwapSimulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class SwapSimulatorTestIT {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private SwapSimulator swapSimulator;

    @Test
    void testSwap() throws InterruptedException {
        Random random = new Random();
        generatePersons(100);
        Agency agency1 = RandomEntityGenerator.generateRandomAgency(20, 1_000_000.0, random);
        agencyRepository.save(agency1);

        Agency agency2 = RandomEntityGenerator.generateRandomAgency(20, 1_000_000.0, random);
        agencyRepository.save(agency2);

        swapSimulator.startSwap(1000);
        Thread.sleep(120_000);
        swapSimulator.stopSwap();
        Thread.sleep(5_000);
    }

    private void generatePersons(int number) {
        Random random = new Random();
        IntStream.range(0, number).forEach(i -> createPerson(RandomEntityGenerator.generateRandomString(20, random)));
    }

    private Person createPerson(String name) {
        Wallet wallet = Wallet.builder()
                .name(name + "-main-wallet")
                .liquidityList(List.of(
                        Liquidity.builder().value(100.0).name("Leu").ticker("RON").build(),
                        Liquidity.builder().value(5.0).name("Bitcoin").ticker("BTC").build(),
                        Liquidity.builder().value(500.0).name("Euro").ticker("EUR").build(),
                        Liquidity.builder().value(1000.0).name("Solana").ticker("SOL").build()))
                .build();
        List<Wallet> wallets = List.of(wallet);
        Person person = Person.builder()
                .name(name)
                .wallets(wallets)
                .address("Cluj")
                .photo("profilePic".getBytes(StandardCharsets.UTF_8))
                .build();
        wallets.forEach(w -> w.setPerson(person));
        return personRepository.save(person);
    }
}