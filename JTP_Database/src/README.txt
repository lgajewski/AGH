1. Stworz plik NAZWA.info i umiesc w nim dane osobowe wed³ug wzorca:

template=MyTemplate;
name=YourName;
surname=YourSurname;
imageFile=true/false;
descriptionFile=true/false;


2. W pierwszych dwoch polach (w miejsce YourName oraz YourSurname) 
	wpisz swoje imie oraz nazwisko.
	
     W dwoch kolejnych wpisz wartosc boolean (true lub false)
	w zaleznosci od tego czy chcesz wczytywac swoje zdjecie oraz opis.

3. W przypadku, gdy wybierzesz opcje ze zdjeciem lub opisem powinienes
	dolaczyc je przed zakonczeniem monitorowania katalogu. W przeciwnym
	razie program nie wczyta Twoich danych. 

     Pliki powinny zostac zapisane jako:
	NAZWA.jpg lub NAZWA.txt, odpowiednio dla zdjecia i opisu.



przyk³adowy plik nazwa.info

template=MyTemplate;
name=Jan;
surname=Kowalski;
imageFile=false;
descriptionFile=true;