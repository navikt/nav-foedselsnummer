package no.nav.commons.foedselsnummer.testutils

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import java.time.LocalDate

class FoedselsnummerGenerator {
    private var lopenrMann = -1
    private var lopenrKvinne = -2

    fun foedselsnummer(
        foedselsdato: LocalDate = LocalDate.now(),
        kjoenn: Kjoenn = Kjoenn.MANN,
        dNummer: Boolean = false
    ): FoedselsNr {
        var fnr: String? = null
        var day = foedselsdato.dayOfMonth
        if(dNummer) day += 40

        while(fnr == null) {
            fnr = korrigerKontrollsiffer(String.format("%02d%02d%s%03d00",
                day, foedselsdato.monthValue, foedselsdato.year.toString().takeLast(2), individnummer(foedselsdato, kjoenn)))
        }

        val resultat = FoedselsNr(fnr)
        assert(resultat.foedselsdato == foedselsdato)
        assert(resultat.kjoenn == kjoenn)
        assert(resultat.dNummer == dNummer)
        return resultat
    }

    private fun individnummer(foedselsdato: LocalDate, kjoenn: Kjoenn): Int {
        for ((individSerie, aarSerie) in FoedselsNr.Companion.tabeller.serier) {
            if (aarSerie.contains(foedselsdato.year)) {
                val lopenr: Int = when(kjoenn) {
                    Kjoenn.MANN -> {
                        lopenrMann += 2
                        lopenrMann
                    }
                    Kjoenn.KVINNE -> {
                        lopenrKvinne += 2
                        lopenrKvinne
                    }
                }
                return individSerie.start + lopenr
            }
        }
        throw IllegalArgumentException("Fødselsdato må være mellom år 1854 og 2039")
    }
}

fun korrigerKontrollsiffer(fnrMedFeilKontrollsiffer: String): String? {
    require(fnrMedFeilKontrollsiffer.length == 11) { "Fødselsnummer må være 11 siffer" }

    var fnr = fnrMedFeilKontrollsiffer.take(9)

    val k1 = FoedselsNr.checksum(FoedselsNr.Companion.tabeller.kontrollsiffer1, fnr)
    fnr += k1

    val k2 = FoedselsNr.checksum(FoedselsNr.Companion.tabeller.kontrollsiffer2, fnr)
    fnr += k2

    if(k1 >= 10 || k2 >= 10) {
        return null
    }

    return fnr
}