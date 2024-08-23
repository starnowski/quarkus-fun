package com.github.starnowski.quarkus.fun.liquid;

import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTestProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.mockito.Mockito;

public class TestProfiles {

    public static class MockedTemplateSupplierTestProfile implements QuarkusTestProfile {

        @Mock
        @ApplicationScoped
        @Produces
        public TemplateSupplier mockTemplateSupplier() {
            return Mockito.mock(TemplateSupplier.class);
        }
    }
}
