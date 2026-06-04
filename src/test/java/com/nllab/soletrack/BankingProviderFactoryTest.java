package com.nllab.soletrack;

import com.nllab.soletrack.service.BankingProviderFactory;
import com.nllab.soletrack.service.OpenBankingProvider;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BankingProviderFactoryTest {

    @Test
    public void testGetProvider_returnsConfigured() {
        OpenBankingProvider p = mock(OpenBankingProvider.class);
        var providers = Map.of("my", p);

        BankingProviderFactory factory = new BankingProviderFactory(providers, "my");
        OpenBankingProvider result = factory.getProvider();

        assertSame(p, result);
    }

    @Test
    public void testGetProvider_fallbackToFirst() {
        OpenBankingProvider p = mock(OpenBankingProvider.class);
        var providers = Map.of("one", p);

        BankingProviderFactory factory = new BankingProviderFactory(providers, "unknown");
        OpenBankingProvider result = factory.getProvider();

        assertSame(p, result);
    }

    @Test
    public void testGetAllProviders() {
        OpenBankingProvider p = mock(OpenBankingProvider.class);
        var providers = Map.of("one", p);

        BankingProviderFactory factory = new BankingProviderFactory(providers, "one");
        assertEquals(1, factory.getAllProviders().size());
    }
}