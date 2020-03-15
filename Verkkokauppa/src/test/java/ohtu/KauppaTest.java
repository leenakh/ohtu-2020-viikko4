
package ohtu;

import ohtu.verkkokauppa.Kauppa;
import ohtu.verkkokauppa.Pankki;
import ohtu.verkkokauppa.Tuote;
import ohtu.verkkokauppa.Varasto;
import ohtu.verkkokauppa.Viitegeneraattori;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class KauppaTest {
    
    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;
    Kauppa kauppa;
    
    @Before
    public void setUp() {
        pankki = mock(Pankki.class);

        viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(42);

        varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10); 
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kalja", 1));
        when(varasto.saldo(3)).thenReturn(0);
        when(varasto.haeTuote(3)).thenReturn(new Tuote(3, "kukkaruukku", 1));

        kauppa = new Kauppa(varasto, pankki, viite);
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaArvoilla() {
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq("33333-44455"), eq(5));
    }
    
    @Test
    public void kahdenEriTuotteenLisaamisenJalkeenPankinMetodiaTilisiirtoKutsutaanOikeillaArvoilla() {
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(2);
        kauppa.tilimaksu("leena", "56789");
        
        verify(pankki).tilisiirto(eq("leena"), eq(42), eq("56789"), eq("33333-44455"), eq(6));
    }
    
    @Test
    public void kahdenSamanTuotteenLisaamisenJalkeenPankinMetodiaTilisiirtoKutsutaanOikeillaArvoilla() {
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(1);
        kauppa.tilimaksu("leena", "56789");
        
        verify(pankki).tilisiirto(eq("leena"), eq(42), eq("56789"), eq("33333-44455"), eq(10));
    }
    
    @Test
    public void varastossaOlevanJaVarastostaLoppuneenTuotteenLisaamisenJalkeenPankinMetodiaTilisiirtoKutsutaanOikeillaArvoilla() {
        kauppa.aloitaAsiointi();
        kauppa.lisaaKoriin(1);
        kauppa.lisaaKoriin(3);
        kauppa.tilimaksu("leena", "56789");
        
        verify(pankki).tilisiirto(eq("leena"), eq(42), eq("56789"), eq("33333-44455"), eq(5));
    }
}
