package PACKAGE_NAME;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.lang.ArchRule;
import org.jfoundry.test.archunit.HexagonalAdapterPackageConvention;
import org.jfoundry.test.archunit.JFoundryRules;

@AnalyzeClasses(packages = "PACKAGE_NAME")
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchTests jfoundryRules = JFoundryRules.hexagonalStrict();

    @ArchTest
    static final ArchRule adapterPackages = JFoundryRules.hexagonalAdapterPackageConvention(
            HexagonalAdapterPackageConvention.IN_OUT);

    @ArchTest
    static final ArchTests jmoleculesDddRules = JFoundryRules.jmoleculesDdd();

    @ArchTest
    static final ArchTests aggregateRepositoryRules = JFoundryRules.aggregateRepositoryConventions();
}
