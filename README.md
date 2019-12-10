# Praca-magisterska--mobile-app

Repozytorium zawiera kompletny kod aplikacji mobilnej zaprojektowanej i zaimplementowanej w ramach pracy magisterskiej "Moduł prezentacji informacji z urządzeń Internetu Rzeczy z wykorzystaniem rozszerzonej rzeczywistości". Na całość systemu skład się rownież aplikacja serwerowa, której kod znajduje się w osobnym repozytorium, oraz urządzenia IoT. 

Działanie aplikacji opiera się na agregacji danych pochodzących z urządń Internetu Rzeczy. Na potrzeby realizacji pracy magisterskiej wydzielony zostały dwa typy urządzeń IoT. Pierwsze z nich komunikują się z urządzeniem mobilnym za pośrednictwem Bluetooth, natomiast drugie są abstrakcją, a ich dane przechowywane są na serwerze. Takie koncepcyjne rozwiązanie obrazuje jego skalowalność, ponieważ niezależnie od typu urządeń IoT, urządnie mobilne pobiera dane z serwera. 

Użytkownik w celu komunikacji dokonuje procesu autoryzacji z serwerem. Istnieje również możliwość pracy "jako gość", w takim wypadku agregowane będą tylko dane pochodzące z urządzeń IoT Bluetooth. Jeśli urządzenie mobilne posiada czytnik linii papilarnych, może go wykorzystywać do autoryzacji.

Użytkownik do swojej dyspozycji ma widok skaneru urządzeń IoT Bluetooth oraz posiada możliowść zarządzania zapamiętaną listą urządzeń Iot. To samo tyczy się urządeń, których odczyty znajdują się na serwerze. Opracowany został widok mapy, na którą naniesione zostały urządzenia IoT. Użytkownik może definiować obszar na mapie, z którego mają być zbierany odczyty. 

Ostatnim, ale najważniejszym elementem aplikacji jest widok Rozszerzonej Rzeczywistości. Wszystkie wybrane urządzenia wyświetlane są poprzez widok z kamery. Zbudowana scena 3d pozwala na umieszczenie symobli reprezentujących stan urządzeń IoT. Po naciśnięciu na dane urządzenia wyświetlane zostają dane pochodzące z nich. 

Widoki Aplikacji globAR

<img src="https://user-images.githubusercontent.com/34090166/70506236-19951b00-1b2a-11ea-8dbc-5c0f028e5038.jpg" width="200">

