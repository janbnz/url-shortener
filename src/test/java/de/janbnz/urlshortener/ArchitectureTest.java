package de.janbnz.urlshortener;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    private static final String MAIN_PACKAGE = "de.janbnz.urlshortener";

    @Test
    @DisplayName("domain classes should not depend on infrastructure")
    void shouldNotDependOnInfrastructure() {
        Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain")
                .definedBy("..domain..")
                .layer("Infrastructure")
                .definedBy("..infrastructure..")
                .whereLayer("Domain")
                .mayNotAccessAnyLayer()
                .check(new ClassFileImporter().importPackages(MAIN_PACKAGE));
    }
}
