# TW
Concurrency theory - AGH

## Exercises

### Task #1

**Zadanie 1.1**
Zaimplementuj "niebezpieczną" klasę Counter (licznik) z metodami increment() i decrement() odpowiednio modyfikującymi wewnętrzny stan licznika. (2 pkt.)

**Zadanie 1.2**
Zaimplementuj klasę SynchronizedCounter z metodami jak w poprzednim zadaniu, ale opatrzonymi słowem kluczowym synchronized. (2 pkt.)

**Zadanie 1.3**
Zaimplementuj 2 współbieżne wątki: jeden wielokrotnie inkrementujący, a drugi wielokrotnie dekrementujący współdzielony licznik. Porównaj wynik działania programu dla obu klas liczników. (2 pkt.)

**Zadanie 1.4**
(Termin oddania 13 marca) Napisz program porównujący czas działania obu wersji programu z poprzedniego zadania. (2 pkt.)

### Task #2

**Zadanie 2.1**
Zaimplementuj klasę AtomicCounter bezpiecznego licznika wykorzystującą klasy z pakietu java.lang.concurrent.atomic. Zmodyfikuj kod z zadania 1.4 do porównania czasu działania AtomicCounter i SynchronizedCounter. (2 pkt.)

**Zadanie 2.2**
Zaimplementuj problem producenta-konsumenta z ograniczonym buforem przy pomocy monitorów. (5 pkt.)

**Zadanie 2.3**
(Termin oddania 17 marca) Jakie niekorzystne efekty wywoła zastąpienie instrukcji while przez if w realizacji oczekiwania na warunek w zadaniu 2.2? A jakie zastąpienie notifyAll() przez notify()? Odpowiedzi należy umieścić w pliku tekstowym. (4 pkt.)

### Task #3

**Zadanie 3.1**
Zaimplementuj semafor binarny i licznikowy korzystając ze standardowych monitorów. (4 pkt.)

**Zadanie 3.2**
Zaimplementuj problem producenta i konsumenta z ograniczonym buforem wykorzystujący semafory z zadania 3.1. (4 pkt.)

**Zadanie 3.3**
Porównaj czas działania implementacji problemu producenta i konsumenta z zadań 2.2 (czyste monitory) i 3.2 (semafory zrealizowane przy pomocy monitorów). (2 pkt.)

### Task #4

**Zadanie 4.1**
Zaimplementuj problem producenta i konsumenta z ograniczonym buforem wykorzystując zamki i zmienne warunkowe. Nie wolno korzystać z interfejsu ReadWriteLock i klasy ReentrantReadWriteLock. (3 pkt.)

### Task #5

**Zadanie 5.1**
Zaimplementuj rozwiązanie trzeciego problemu czytelników i pisarzy. (3 pkt. + 1 pkt. za wykorzystanie monitorów/zamków do czegoś innego niż realizacja semafora)

**Zadanie 5.2**
Zaimplementuj dowolne rozwiązanie problemu 5 filozofów (oczywiście bez zakleszczenia). (2 pkt.)

### Task #6

**Zadanie 6.1**
Zaimplementuj listę, w której każdy element składa się z wartości typu java.lang.Object, referencji do następnego elementu i zamka. Zakładamy, że lista nie akceptuje wartości pustych (null). Należy zastosować blokowanie drobnoziarniste w następujących metodach:

```java
boolean add(Object o)
boolean remove(Object o)
boolean contains(Object o)
```
(5 pkt.)

**Zadanie 6.2**
Porównaj wydajność listy z zadania 6.1 z listą blokowaną w całości jednym zamkiem. Wykonaj pomiary w zależności od liczby wątków operujących na liście - przygotuj odpowiednie wykresy. (5 pkt.)

---

### Author: *Lukasz Gajewski*