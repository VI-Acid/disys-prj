# disys-prj – Energie-Daten Viewer

## Technologien
- Java 21 (Zulu FX)
- JavaFX mit FXML (GUI / - SceneBuilder)
- Spring Boot (REST-API)
- Maven


## Projektstruktur
- `rest-api` → REST-Service mit `/current`, `/history` (Backend mit Spring Boot (liefert Energiedaten))
- `ui-app` → JavaFX-Anwendung mit Button zur Datenabfrage Frontend mit JavaFX (zeigt ein einfaches Fenster an)

## Milestone
### Must Haves
- Every component can be started independently - DONE
- System can be build and run with no errors - DONE
- Spring Boot used for REST API - DONE
- JavaFX used for GUI - DONE
- GitHub repository link in submission - DONE

### REST API
The REST API will be implemented using Spring Boot and designed to provide example data for testing and
demonstration purposes. It will include two endpoints: one to retrieve the data of the current hour, and
another to filter historic data. The API will focus solely on returning structured example data to simulate
real-world usage scenarios without relying on a persistent data store or handling complex business logic.	

### GUI
The GUI will be developed using JavaFX, providing an intuitive and interactive interface for users to interact
with the REST API. The application will include buttons and input fields to send requests to the API, such as
fetching the hour and historic energy data. Retrieved data will be displayed dynamically within the application
using visual components like tables, labels, or text areas. The design will focus on simplicity and clarity,
ensuring a seamless user experience while demonstrating the integration of JavaFX with a REST API for real-time
data interaction.



