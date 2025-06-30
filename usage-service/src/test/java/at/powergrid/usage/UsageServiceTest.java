package at.powergrid.usage;

import at.powergrid.entity.EnergyUsageEntity;
import at.powergrid.repository.EnergyUsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
// Aktiviert Mockito in JUnit-5, damit die @Mock- und @InjectMocks-Annotationen verarbeitet werden

class UsageServiceTest {

    @Mock
    EnergyUsageRepository usageRepo;
    // Erzeugt ein Mock-Objekt für das Repository, damit wir Aufrufe prüfen und Stub-Werte zurückliefern können

    @Mock
    RabbitTemplate rabbitTemplate;
    // Mock für RabbitTemplate, damit wir prüfen können, dass update-Nachricht verschickt wird

    @InjectMocks
    UsageService usageService;
    // Instanziiert UsageService und injiziert die beiden obigen Mocks in dessen Konstruktor


    @Test
        // markiert eine Methode als Testmethode für JUnit (jeder Test folgt AAA-Pattern)
    void whenProducerMessage_thenCommunityProducedIncrements() throws Exception {
        // Arrange (Vorbereitung)
        LocalDateTime hour = LocalDateTime.of(2025,6,24,10,0);
        // Simuliere die volle Stunde, in der wir aggregieren wollen

        EnergyUsageEntity existing = new EnergyUsageEntity(hour, 1.0, 0.5, 0.0);
        // Erstelle eine Entity mit bereits 1.0 kWh Produktion und 0.5 kWh Verbrauch

        when(usageRepo.findById(hour)).thenReturn(Optional.of(existing));
        // Stub findById(...) liefert eine vorgefertigte Entity zurück


        String msg = "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\"," +
                "\"kwh\":\"0.2\",\"datetime\":\"2025-06-24T10:15:00\"}";


        // Act (Ausführung)
        usageService.handleMessage(msg);
        // Die Methode unter Test: parst die Nachricht, erhöht communityProduced und speichert

        // Assert (Verifikation)
        // 1) Prüfen, dass save(...) aufgerufen wurde und die Werte korrekt sind
        ArgumentCaptor<EnergyUsageEntity> cap = ArgumentCaptor.forClass(EnergyUsageEntity.class);
        verify(usageRepo).save(cap.capture());
        EnergyUsageEntity saved = cap.getValue();
        // saved ist das Objekt, das tatsächlich an usageRepo.save() übergeben wurde

        // 1e-6 Toleranz -> sorgt dafür, dass Test bei winzigen Rundungsunterschieden nicht unnötig fehlschlägt
        assertEquals(1.2, saved.getCommunityProduced(), 1e-6);
        // 1.0 + 0.2 muss 1.2 ergeben

        assertEquals(0.0, saved.getGridUsed(), 1e-6);
        // Weil Verbrauch (0.5) < Produktion (1.2) → gridUsed bleibt 0


        // 2) Prüfen, dass eine Update-Nachricht an RabbitMQ geschickt wurde
        verify(rabbitTemplate).convertAndSend("updateQueue", msg);
    }

    @Test
    void whenUserMessage_exceedsProduction_thenGridUsedIncrements() throws Exception {
        // Arrange: stunde 10:00, bisher 0.5 kWh prod und 0.4 kWh verbrauch
        LocalDateTime hour = LocalDateTime.of(2025,6,24,10,0);
        EnergyUsageEntity existing = new EnergyUsageEntity(hour, 0.5, 0.4, 0.0);


        when(usageRepo.findById(hour)).thenReturn(Optional.of(existing));
        // Stub findById(...) liefert eine vorgefertigte Entity zurück

        // Simulierte USER-Nachricht über 0.3 kWh
        String msg = "{\"type\":\"USER\",\"association\":\"COMMUNITY\"," +
                "\"kwh\":\"0.3\",\"datetime\":\"2025-06-24T10:20:00\"}";

        // Act: Aufruf der Methode, die die Nachricht verarbeitet
        usageService.handleMessage(msg);

        // Assert: Verbrauch erhöht sich auf 0.7, Rest (0.7−0.5)=0.2 aus dem Netz
        ArgumentCaptor<EnergyUsageEntity> cap = ArgumentCaptor.forClass(EnergyUsageEntity.class);
        verify(usageRepo).save(cap.capture());
        EnergyUsageEntity saved = cap.getValue();

        // 1e-6 Toleranz -> sorgt dafür, dass Test bei winzigen Rundungsunterschieden nicht unnötig fehlschlägt
        assertEquals(0.7, saved.getCommunityUsed(), 1e-6);
        assertEquals(0.2, saved.getGridUsed(),     1e-6);

        // Auch hier: Update-Nachricht wurde verschickt
        verify(rabbitTemplate).convertAndSend("updateQueue", msg);
    }
}

