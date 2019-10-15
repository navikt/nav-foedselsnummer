package no.nav.commons.foedselsnummer.testutils

import no.nav.commons.foedselsnummer.Kjoenn
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate

internal class FoedselsnummerGeneratorTest {
    @Test
    fun korrigerKontrollsiffer() {
        System.err.println(no.nav.commons.foedselsnummer.testutils.korrigerKontrollsiffer("00000000200"))
    }

    @Test
    fun generer() {
        val generator = FoedselsnummerGenerator()

        val dato = LocalDate.of(2000, 10, 15)

        val mann = generator.foedselsnummer(kjoenn = Kjoenn.MANN, foedselsdato = dato)
        val kvinne = generator.foedselsnummer(kjoenn = Kjoenn.KVINNE, foedselsdato = dato)
        val dnummer = generator.foedselsnummer(foedselsdato = dato, dNummer = true)

        assertThat(mann.foedselsdato).isEqualTo(dato)
        assertThat(kvinne.foedselsdato).isEqualTo(dato)
        assertThat(mann.kjoenn).isEqualTo(Kjoenn.MANN)
        assertThat(kvinne.kjoenn).isEqualTo(Kjoenn.KVINNE)

        assertThat(dnummer.foedselsdato).isEqualTo(dato)
        assertThat(dnummer.dNummer).isTrue()
    }
}