package no.nav.commons.foedselsnummer

import com.fasterxml.jackson.annotation.JsonValue
import no.nav.commons.foedselsnummer.FoedselsNr.Companion.tabeller.kontrollsiffer1
import no.nav.commons.foedselsnummer.FoedselsNr.Companion.tabeller.kontrollsiffer2
import java.time.LocalDate

data class FoedselsNr(@JsonValue val asString: String) {
    init {
        require("""\d{11}""".toRegex().matches(asString)) { "Ikke et gyldig fødselsnummer: $asString" }
        require(!(hNummer || fhNummer)) { "Impelemntasjonen støtter ikke H-nummer og FH-nummer" }
        require(gyldigeKontrollsiffer) { "Kontrollsifre må være gyldige" }
    }

    val kjoenn: Kjoenn
        get() {
            val kjoenn = asString.slice(8 until 9).toInt()
            return if(kjoenn % 2 == 0) Kjoenn.KVINNE else Kjoenn.MANN
        }

    val dNummer: Boolean
        get() {
            val mnd = asString[0].toString().toInt()
            return mnd in 4..7
        }

    private val hNummer: Boolean
        get() {
            return asString[2].toString().toInt() >= 4
        }

    private val fhNummer: Boolean
        get() {
            return when(asString[0]) {
                '8', '9' -> true
                else -> false
            }
        }

    val foedselsdato: LocalDate
        get() {
            val fnrMonth = asString.slice(2 until 4).toInt()

            val dayFelt = asString.slice(0 until 2).toInt()
            val fnrDay = if(dNummer) dayFelt - 40 else dayFelt

            return LocalDate.of(foedselsaar, fnrMonth, fnrDay)
        }

    private val gyldigeKontrollsiffer: Boolean
        get() {
            val ks1 = asString[9].toString().toInt()
            val ks2 = asString[10].toString().toInt()

            val c1 = checksum(kontrollsiffer1, asString)
            if(c1 == 10 || c1 != ks1) {
                return false
            }

            val c2 = checksum(kontrollsiffer2, asString)
            if(c2 == 10 || c2 != ks2) {
                return false
            }
            return true
        }

    private val foedselsaar: Int
        get() {
            val fnrYear = asString.slice(4 until 6)
            val individnummer = asString.slice(6 until 9).toInt()

            for((individSerie, aarSerie) in tabeller.serier) {
                val kandidat = (aarSerie.start.toString().slice(0 until 2) + fnrYear).toInt()
                if(individSerie.contains(individnummer) && aarSerie.contains(kandidat)) {
                    return kandidat
                }
            }
            throw IllegalStateException("Ugyldig individnummer: $individnummer")
        }

    companion object {
        object tabeller {
            // https://www.skatteetaten.no/person/folkeregister/fodsel-og-navnevalg/barn-fodt-i-norge/fodselsnummer/
            val serier: List<Pair<ClosedRange<Int>, ClosedRange<Int>>> = listOf(
                500..749 to 1854..1899,
                  0..499 to 1900..1999,
                900..999 to 1940..1999,
                500..999 to 2000..2039
            )

            val kontrollsiffer1: List<Int> = listOf(3,7,6,1,8,9,4,5,2)
            val kontrollsiffer2: List<Int> = listOf(5,4,3,2,7,6,5,4,3,2)
        }

        fun checksum(liste: List<Int>, str: String): Int {
            var sum = 0
            for((i, m) in liste.withIndex()) {
                sum += m * str[i].toString().toInt()
            }
            return 11 - (sum % 11)
        }
    }
}