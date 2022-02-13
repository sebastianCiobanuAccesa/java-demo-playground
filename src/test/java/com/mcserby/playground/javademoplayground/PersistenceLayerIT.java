package com.mcserby.playground.javademoplayground;

import com.mcserby.playground.javademoplayground.persistence.model.Currency;
import com.mcserby.playground.javademoplayground.persistence.model.Person;
import com.mcserby.playground.javademoplayground.persistence.model.Wallet;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.CurrencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.ExchangePoolRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class PersistenceLayerIT {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private ExchangePoolRepository exchangePoolRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testPersonIsPersistedCorrectlyPersistenceLayer() {

        Currency ron = currencyRepository.save(Currency.builder().name("Leu").ticker("RON").build());
        Currency btc = currencyRepository.save(Currency.builder().name("Bitcoin").ticker("BTC").build());
        Currency eur = currencyRepository.save(Currency.builder().name("EUro").ticker("EUR").build());

        Wallet wallet = Wallet.builder()
                .name("main-wallet")
                .currencies(Map.of(ron, 100.0, btc, 5.0, eur, 500.0))
                .build();
        List<Wallet> wallets = List.of(wallet);
        Person person = Person.builder()
                .name("Serby")
                .wallets(wallets)
                .address("Cluj")
                .photo("profilePic".getBytes(StandardCharsets.UTF_8))
                .build();
        Person saved = personRepository.save(person);
        long personId = saved.getId();
        Optional<Person> maybeQueriedPerson = personRepository.findById(personId);
        assertTrue(maybeQueriedPerson.isPresent());
        Person queriedPerson = maybeQueriedPerson.get();
        assertEquals(person.getName(), queriedPerson.getName());

        assertEquals(3, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, "currency"));
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, "person"));
        assertEquals(1, JdbcTestUtils.countRowsInTable(this.jdbcTemplate, "wallet"));

    }
}
