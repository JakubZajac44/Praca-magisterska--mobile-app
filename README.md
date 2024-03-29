# Praca-magisterska--mobile-app

Repozytorium zawiera kompletny kod aplikacji mobilnej zaprojektowanej i zaimplementowanej w ramach pracy magisterskiej "Moduł prezentacji informacji z urządzeń Internetu Rzeczy z wykorzystaniem rozszerzonej rzeczywistości". Na całość systemu skład się rownież aplikacja serwerowa, której kod znajduje się w osobnym repozytorium, oraz urządzenia IoT. 

Działanie aplikacji opiera się na agregacji danych pochodzących z urządń Internetu Rzeczy. Na potrzeby realizacji pracy magisterskiej wydzielony zostały dwa typy urządzeń IoT. Pierwsze z nich komunikują się z urządzeniem mobilnym za pośrednictwem Bluetooth, natomiast drugie są abstrakcją, a ich dane przechowywane są na serwerze. Takie koncepcyjne rozwiązanie obrazuje jego skalowalność, ponieważ niezależnie od typu urządeń IoT, urządnie mobilne pobiera dane z serwera. 

Użytkownik w celu komunikacji dokonuje procesu autoryzacji z serwerem. Istnieje również możliwość pracy "jako gość", w takim wypadku agregowane będą tylko dane pochodzące z urządzeń IoT Bluetooth. Jeśli urządzenie mobilne posiada czytnik linii papilarnych, może go wykorzystywać do autoryzacji.

Użytkownik do swojej dyspozycji ma widok skaneru urządzeń IoT Bluetooth oraz posiada możliowść zarządzania zapamiętaną listą urządzeń Iot. To samo tyczy się urządeń, których odczyty znajdują się na serwerze. Opracowany został widok mapy, na którą naniesione zostały urządzenia IoT. Użytkownik może definiować obszar na mapie, z którego mają być zbierany odczyty. 

Ostatnim, ale najważniejszym elementem aplikacji jest widok Rozszerzonej Rzeczywistości. Wszystkie wybrane urządzenia wyświetlane są poprzez widok z kamery. Zbudowana scena 3d pozwala na umieszczenie symobli reprezentujących stan urządzeń IoT. Po naciśnięciu na dane urządzenia wyświetlane zostają dane pochodzące z nich. 

Widoki Aplikacji globAR

<img src="https://user-images.githubusercontent.com/34090166/70507371-7bef1b00-1b2c-11ea-9f1d-c3f0b47462ef.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507075-ece20300-1b2b-11ea-972e-1d45eb84e5ad.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507084-f23f4d80-1b2b-11ea-8b28-958805fcaee7.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507099-f79c9800-1b2b-11ea-80f1-1d7dd07003e9.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507141-0daa5880-1b2c-11ea-8c8b-e82bd740df42.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507210-33cff880-1b2c-11ea-884d-11f18f84d6f4.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507222-37fc1600-1b2c-11ea-9f2b-c84a1e6333c8.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507253-43e7d800-1b2c-11ea-936b-21e02b81885a.jpg" width="200"><img src="https://user-images.githubusercontent.com/34090166/70507262-46e2c880-1b2c-11ea-8276-31c9550e3489.jpg" width="200">

<img src="https://user-images.githubusercontent.com/34090166/70507298-59f59880-1b2c-11ea-8c2f-e87011b27ac6.jpg" width="600">
<img src="https://user-images.githubusercontent.com/34090166/70507337-6b3ea500-1b2c-11ea-938e-26c136408f35.jpg" width="600">

W czasie projektowania aplikacji wykorzystany został wzorzec MVP, który tuż obok MVVM jest rekomendowanym wzorem projektowania na platformę android. Poniższe diagramy obrazują architekturę systemu: 


Diagram komponentów:

<img src="https://user-images.githubusercontent.com/34090166/70507935-ac838480-1b2d-11ea-8e80-48a8e32dead2.jpg" width="600">

Diagram wdrożenia:

<img src="https://user-images.githubusercontent.com/34090166/70507947-b1483880-1b2d-11ea-9059-33e5f14c9899.jpg" width="600">

Diagram przypadków użycia:

<img src="https://user-images.githubusercontent.com/34090166/70507960-b86f4680-1b2d-11ea-8de6-258a072ef9e8.jpg" width="600">

Diagramy aktywności:

<img src="https://user-images.githubusercontent.com/34090166/70507969-befdbe00-1b2d-11ea-93a0-fd2ed45eb969.jpg" width="600">
<img src="https://user-images.githubusercontent.com/34090166/70507965-b9a07380-1b2d-11ea-8df4-c36c39ac9218.jpg" width="600">
<img src="https://user-images.githubusercontent.com/34090166/70507977-c45b0880-1b2d-11ea-901f-034d07ccc504.jpg" width="600">

Aplikacja mobilna korzysta z wzorca programowania reaktywnego: RxJava, podczas wykonywania operacji na obiektowej bazy danych, autoryzacji użytkownia oraz nawiązywaniu połączenia z urządzeniami IoT Bluetooth. Wykorzystany został również Dagger 2 do wstrzykiwania zależności między komponentami.

W celu wyświetlania widoku AR zbudowana została scena w OpenGL, a następnie nałożony został na nią widok z kamery. W celu określenia położenia w przestrzeni wykorzystan został fuzja czujników: żyroskop, akcelerometr oraz czujnik magnetyczny. 

Link do filmiku obrazującego działanie aplikacji:

https://www.youtube.com/watch?v=vPt4YS6j7Nw&list=PLVLDfQrBl6KjVxy1UFPc_prghKsUCQtxm
