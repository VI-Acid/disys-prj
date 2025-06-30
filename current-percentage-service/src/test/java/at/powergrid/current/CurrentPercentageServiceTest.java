package at.powergrid.current;

import at.powergrid.entity.CurrentPercentageEntity;
import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.CurrentPercentageRepository;
import at.powergrid.repository.EnergyUsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Unit-Test für den CurrentPercentageService, der die Berechnung der aktuellen Prozentsätze testet

@ExtendWith(MockitoExtension.class)
// Aktiviert in JUnit 5 den Mockito-Support. Spring-Mocks (@Mock) und die automatische Injektion in @InjectMocks werden so initialisiert
class CurrentPercentageServiceTest {

    @Mock
    // Erzeugt mit Mockito ein Dummy-Objekt für das jeweilige Interface/Klasse (hier CurrentPercentageRepository und EnergyUsageRepository)
    private CurrentPercentageRepository pctRepo;

    @Mock
    // Erstellt eine Instanz der Test-Klasse (CurrentPercentageService) und injiziert alle @Mock-Felder in ihren Konstruktor bzw. in @Autowired-Felder
    private EnergyUsageRepository usageRepo;

    @InjectMocks
    // Instanziiert CurrentPercentageService und injiziert die beiden obigen Mocks in dessen Konstruktor
    private CurrentPercentageService svc;

    @Test
        // Markiert die Methode als JUnit-Testfall (Test) - Jeder Test folgt dem AAA-Pattern: Arrange, Act, Assert)
    void shouldCalculateProperPercentages() throws Exception {
        // given: usage for hour 2025-06-24T11:00 with produced=2.0, used=1.0, grid=1.0
        LocalDateTime hour = LocalDateTime.of(2025, 6, 24, 11, 0);
        EnergyUsageEntity usage = new EnergyUsageEntity(hour, 2.0, 1.0, 1.0);
        // Wir konfigurieren den usageRepo-Mock so, dass er beim Aufruf von findById(hour) unser vordefiniertes usage zurückgibt

        // Stub findById(...) liefert eine vorgefertigte Entity zurück
        when(usageRepo.findById(hour)).thenReturn(Optional.of(usage));

        // when: an update arrives with a datetime within that hour
        String msg = "{\"datetime\":\"2025-06-24T11:15:00\"}";
        // Wir rufen die Service-Methode auf, die intern das Mock-Repository abfragt und dann eine neue CurrentPercentageEntity berechnen
        svc.receiveUpdate(msg);

        // then: calculate and save correct percentages
        ArgumentCaptor<CurrentPercentageEntity> cap = ArgumentCaptor.forClass(CurrentPercentageEntity.class);
        verify(pctRepo).save(cap.capture());
        CurrentPercentageEntity saved = cap.getValue();

        // communityDepleted = used / produced * 100 = 1.0 / 2.0 * 100 = 50%
        assertEquals(50.0, saved.getCommunityDepleted(), 1e-6);
        // gridPortion = gridUsed / (produced + gridUsed) * 100 = 1.0 / (2.0 + 1.0) * 100 ≈ 33.33%
        assertEquals(33.33, saved.getGridPortion(), 1e-2);

        // Mit ArgumentCaptor fangen wir das tatsächlich an pctRepo.save(...) übergebene Objekt ab und prüfen seine Werte
    }
}
