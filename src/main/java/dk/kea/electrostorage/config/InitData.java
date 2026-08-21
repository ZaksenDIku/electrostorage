package dk.kea.electrostorage.config;

import dk.kea.electrostorage.model.*;
import dk.kea.electrostorage.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.time.LocalDate;

@Configuration
public class InitData {
    @Bean
    CommandLineRunner createDemoData(SupplierRepository suppliers, ComponentRepository components,
                                     PurchaseOrderRepository orders, AssemblyRepository assemblies) {
        return args -> {
            var nordic = suppliers.save(new Supplier("Nordic Components", "Industrivej 12, Lyngby"));
            var electro = suppliers.save(new Supplier("Electro Parts", "Teknikvej 7, Herlev"));
            var battery = suppliers.save(new Supplier("Battery House", "Energivej 4, Roskilde"));

            var led = components.save(new Component(1001L, "LED 5 mm, rød", "LED-RED-5", true, nordic));
            var resistor = components.save(new Component(1002L, "Modstand, 1 kOhm", "R-1K", true, nordic));
            var holder = components.save(new Component(1003L, "Batteriholder til 9 V", "BH-9V", true, nordic));
            var nineVolt = components.save(new Component(1004L, "9 V batteri", "BAT-9V", true, battery));
            var capacitor = components.save(new Component(1005L, "Kondensator, 100 uF", "CAP-100UF", true, electro));
            var transistor = components.save(new Component(1006L, "Transistor", "TR-NPN-01", true, electro));
            components.save(new Component(1007L, "Trykknap", "BTN-RED", true, electro));
            components.save(new Component(1008L, "Printplade", "PCB-50X70", true, electro));
            components.save(new Component(1009L, "Kobberledning", "WIRE-CU", true, nordic));
            components.save(new Component(1010L, "Sikring, 1 A", "FUSE-1A", true, nordic));
            var kit = components.save(new Component(2001L, "Lysende LED", null, false, null));

            orders.save(new PurchaseOrder(electro)); // En ordre, som stadig formuleres

            var active = new PurchaseOrder(electro);
            active.addLine(new OrderLine(capacitor, 10));
            active.addLine(new OrderLine(transistor, 10));
            active.addLine(new OrderLine(components.findById(1007L).orElseThrow(), 10));
            active.setSentDate(LocalDate.now().minusDays(2));
            active.setExpectedDate(LocalDate.now().plusDays(3));
            active.setTrackingCode("TRACK-12345");
            orders.save(active);

            var completed = new PurchaseOrder(nordic);
            completed.addLine(new OrderLine(led, 100));
            completed.setSentDate(LocalDate.now().minusDays(10));
            completed.setExpectedDate(LocalDate.now().minusDays(5));
            completed.setReceivedDate(LocalDate.now().minusDays(4));
            completed.setTrackingCode("DONE-98765");
            orders.save(completed);

            var assembly = new Assembly(kit);
            assembly.addLine(new AssemblyLine(led, 1));
            assembly.addLine(new AssemblyLine(resistor, 1));
            assembly.addLine(new AssemblyLine(holder, 1));
            assembly.addLine(new AssemblyLine(nineVolt, 1));
            assemblies.save(assembly);
        };
    }
}
