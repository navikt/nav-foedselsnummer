# Klasse for fødselsnummer

Dette repoet inneholder en klasse for å holde på og validere fødselsnummer.

Ut i fra et fødselsnummer kan den utlede fødselsdato, kjønn og hvorvidt det er et D-nummer.

Repoet inneholder også en testmodul som kan brukes til å generere fødselsnummer og D-nummer i unittester,
 basert på fødselsdato og kjønn.

## Gradle konfigurasjon
For å laste ned pakker fra GitHub Package Registry (GPR) trenger du et access token med scope `read:packages`. Dette må
 legges til i `~/.gradle/gradle.properties`:
~~~
gpr.key=<token>
~~~

Legg deretter til GPR repository og dependencies i `build.gradle.tks`:

~~~kotlin
repositories {
    mavenCentral()
    maven {
        name = "Github"
        url = uri("https://maven.pkg.github.com/navikt/nav-foedselsnummer")
        credentials {
            username = "x-access-token"
            password = System.getenv("GPR_API_KEY") ?: project.findProperty("gpr.key") as String?
        }
    }
}

dependencies {
    implementation("nav-foedselsnummer:core:1.0-SNAPSHOT.3")
    testImplementation("nav-foedselsnummer:testutils:1.0-SNAPSHOT.3")
}
~~~

## Eksempel

~~~kotlin
package no.nav.commons.foedselsnummer.testutils.eksempel

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate

class EksempelTest {
    @Test
    fun foedselsnummer() {
        val fnr = FoedselsNr("01015450572")
        assertThat(fnr.foedselsdato).isEqualTo(LocalDate.of(1854, 1, 1))
        assertThat(fnr.kjoenn).isEqualTo(Kjoenn.MANN)
        assertThat(fnr.dNummer).isFalse()
    }

    @Test
    fun genererUnikeFoedselsnummer() {
        val generator = FoedselsnummerGenerator()
        val a: FoedselsNr = generator.foedselsnummer()
        val b: FoedselsNr = generator.foedselsnummer()
        assertThat(a).isNotEqualTo(b)
    }

    @Test
    fun genererFodeselsnummerMedParametre() {
        val generator = FoedselsnummerGenerator()
        val fnr = generator.foedselsnummer(
            foedselsdato = LocalDate.of(2019, 1, 2),
            kjoenn = Kjoenn.KVINNE,
            dNummer = false)

        assertThat(fnr.asString).startsWith("020119")
        assertThat(fnr.kjoenn).isEqualTo(Kjoenn.KVINNE)
        assertThat(fnr.dNummer).isFalse()
    }
}
~~~
