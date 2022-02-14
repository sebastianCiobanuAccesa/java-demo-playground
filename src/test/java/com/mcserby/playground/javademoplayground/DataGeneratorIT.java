package com.mcserby.playground.javademoplayground;


import com.mcserby.playground.javademoplayground.persistence.model.*;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class DataGeneratorIT {

    private final Logger logger = LoggerFactory.getLogger(DataGeneratorIT.class);

    private static final int NUMBER_OF_RANDOM_MARKET_ACTORS = 1_000;
    private static final double PROPORTION_PERSON_AGENCY = 0.001;
    private static final int MAX_WALLETS_PER_AGENCY = 1;
    private static final int MAX_WALLETS_PER_PERSON = 3;
    private static final int MAX_EXCHANGE_POOLS = 10;
    private static final double MAX_LIQUIDITY_VALUE = 100_000_000_000.0;

    private final static Map<String, String> CURRENCIES;

    static {
        CURRENCIES = Map.of("EUR", "Euro",
                "USD", "Dollar",
                "RON", "leu",
                "BTC", "Bitcoin",
                "ETH", "Ethereum",
                "DOGE", "Dogecoin",
                "EGLD", "eGold",
                "SOL", "Solana",
                "MATIC", "Polygon",
                "ATOM", "Cosmos");
    }

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Test
    void generateRandomMarketActors() {
        Random random = new Random();
        for (int i = 0; i < NUMBER_OF_RANDOM_MARKET_ACTORS; i++) {
            generateRandomMarketActor(random);
        }
    }

    private void generateRandomMarketActor(Random random) {
        if (random.nextDouble() < PROPORTION_PERSON_AGENCY) {
            Agency agency = generateRandomAgency(random);
            logger.info("agency: " + agency.getName());
            agencyRepository.save(agency);
        } else {
            Person person = generateRandomPerson(random);
            logger.info("person: " + person.getName());
            personRepository.save(person);
        }
    }

    private Person generateRandomPerson(Random random) {
        List<Wallet> wallets = IntStream.range(0, random.nextInt(MAX_WALLETS_PER_PERSON) + 1)
                .mapToObj(i -> generateRandomWallet(random))
                .collect(Collectors.toList());
        return Person.builder()
                .name(generateRandomString(20, random))
                .wallets(wallets)
                .address(generateRandomString(50, random))
                .photo(generateRandomString(200, random).getBytes(StandardCharsets.UTF_8))
                .build();
    }

    private Agency generateRandomAgency(Random random) {
        List<Wallet> wallets = IntStream.range(0, random.nextInt(MAX_WALLETS_PER_AGENCY) + 1)
                .mapToObj(i -> generateRandomWallet(random))
                .collect(Collectors.toList());
        List<ExchangePool> exchangePools = IntStream.range(0, random.nextInt(MAX_EXCHANGE_POOLS)).mapToObj(
                i-> generateRandomExchangePool(random)).collect(Collectors.toList());
        Agency agency = Agency.builder()
                .name(generateRandomString(20, random))
                .wallets(wallets)
                .cui(generateRandomString(50, random))
                .exchangePools(exchangePools)
                .build();
        agency.setExchangePoolReferences();
        return agency;
    }

    private ExchangePool generateRandomExchangePool(Random random) {

        List<String> keys = new ArrayList<>(CURRENCIES.keySet());
        Collections.shuffle(keys);
        Liquidity firstCurrency = Liquidity.builder()
                .value(random.nextDouble() * MAX_LIQUIDITY_VALUE)
                .name(keys.get(0))
                .ticker(CURRENCIES.get(keys.get(0)))
                .build();
        Liquidity secondCurrency = Liquidity.builder()
                .value(random.nextDouble() * MAX_LIQUIDITY_VALUE)
                .name(keys.get(1))
                .ticker(CURRENCIES.get(keys.get(1)))
                .build();

        return ExchangePool.builder()
                .liquidityOne(firstCurrency)
                .liquidityTwo(secondCurrency)
                .build();
    }


    private Wallet generateRandomWallet(Random random) {
        List<String> keys = new ArrayList<>(CURRENCIES.keySet());
        Collections.shuffle(keys);
        List<Liquidity> liquidityList = keys.subList(0, random.nextInt(10))
                .stream()
                .map(k -> Liquidity.builder()
                        .value(random.nextDouble() * MAX_LIQUIDITY_VALUE)
                        .name(k)
                        .ticker(CURRENCIES.get(k))
                        .build())
                .collect(Collectors.toList());

        return Wallet.builder()
                .name(generateRandomString(15, random))
                .liquidityList(liquidityList)
                .build();
    }

    private String generateRandomString(int maxLength, Random random){
        int leftLimit = 48;
        int rightLimit = 122;
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(maxLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
