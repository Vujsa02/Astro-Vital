# AUTONOMNI SISTEM ZA ODRŽAVANjE USLOVA ŽIVOTA U SVEMIRU - AstroVital

**Studenti:**
Mihajlo Vujisić, SV 26/2021
Vesna Vasić, SV 78/2021

# 1. UVOD

Savremene svemirske misije dugog trajanja postavljaju visoke zahteve pred tehnologiju održavanja uslova života u zatvorenim i izolovanim okruženjima. U takvim uslovima, parametri kao što su temperatura, vlažnost, pritisak, kvalitet vazduha i dostupnost vode moraju biti neprekidno praćeni i kontrolisani, jer i mala odstupanja mogu imati ozbiljne posledice po bezbednost posade.

Zbog ograničene mogućnosti komunikacije i intervencije sa Zemlje, javlja se potreba za sistemima koji mogu delovati samostalno, bez stalnog oslanjanja na spoljnu podršku. Ovakvi sistemi moraju biti sposobni da u realnom vremenu analiziraju podatke iz različitih izvora, prepoznaju potencijalne rizike i preduzmu adekvatne mere.

Ovaj rad se bavi konceptom autonomnog sistema za upravljanje životnom podrškom u svemirskom staništu koji koristi ekspertski sistem zasnovan na pravilima (Drools) za integraciju i obradu podataka dobijenih sa senzora, nosivih uređaja i korisničkih unosa, sa ciljem da obezbedi kontinuiranu i pouzdanu podršku uslovima života u svemiru.

## 1.1 Motivacija

Dugotrajne misije u svemiru podrazumevaju rad u sredini gde i najmanji kvar ili odstupanje u uslovima može dovesti do ugrožavanja misije ili života članova posade. U takvim okolnostima, sistemi životne podrške moraju funkcionisati bez prekida, biti otporni na greške i sposobni da brzo reaguju na neočekivane situacije.

Autonomni sistemi koji primenjuju ekspertske metode donošenja odluka predstavljaju korak ka većoj samostalnosti posade i povećanju pouzdanosti infrastrukture u svemirskim misijama. Njihova sposobnost da kombinuju podatke iz više izvora, prepoznaju složene obrasce i reaguju u skladu sa definisanim pravilima, značajno smanjuje rizik i podiže nivo bezbednosti.

# 2. OPIS PROBLEMA

## 2.1 Nedostaci postojećih rešenja

Postojeći sistemi za upravljanje uslovima života u svemirskim staništima uglavnom su zasnovani na klasičnim SCADA i alarmnim mehanizmima koji imaju sledeća ograničenja:

- **Reaktivan pristup** – alarmi se aktiviraju tek nakon što parametri pređu kritične granice, bez mogućnosti predviđanja kvara na osnovu trendova.
- **Nedostatak integracije podataka** – zdravstveni podaci članova posade i tehnički parametri staništa se obrađuju odvojeno, što otežava dobijanje kompletne slike stanja.
- **Zavisnost od kontrole sa Zemlje** – složene odluke često donosi tim na Zemlji, što u uslovima kašnjenja komunikacije može odložiti reakciju.
- **Ograničena automatizacija** – sistemi nemaju mogućnost autonomnog izvođenja korektivnih mera, već se oslanjaju na ručnu intervenciju.
- **Nedostatak složene logike odlučivanja** – pravila su jednostavna i zasnovana samo na pojedinačnim prag vrednostima, bez kombinovanja više parametara ili analiza obrazaca događaja.

## 2.2 Ciljevi i inovativnost našeg rešenja

Cilj razvoja sistema **AstroVital** je da obezbedi sveobuhvatno, inteligentno i autonomno upravljanje uslovima života u svemirskom staništu, sa sledećim ključnim karakteristikama:

- **Integracija više izvora podataka** – spajanje informacija sa senzora, nosivih uređaja i ručnih unosa posade u jedinstvenu bazu znanja.
- **Primena naprednih metoda rezonovanja** – kombinacija forward chaining (sa najmanje 3 nivoa), backward chaining za dijagnostiku i CEP (Complex Event Processing) za prepoznavanje obrazaca tokom vremena.
- **Automatske i poluautomatske korektivne mere** – sistem može samostalno prilagoditi parametre (npr. temperaturu, protok vazduha, nivo vlage) ili predložiti akciju članu posade.
- **Interaktivnost sa korisnicima** – pružanje preporuka sa obrazloženjem "zašto" je doneta odluka, kao i mogućnost da korisnici unesu dodatne informacije koje utiču na rezonovanje.
- **Prilagodljivost i nadogradivost** – menadžer baze znanja može lako kreirati, menjati i testirati pravila, čime se sistem prilagođava novim uslovima misije.

# 3. FUNKCIONALNOSTI I KORISNICI

Sistem AstroVital je dizajniran sa tri jasno definisane korisničke uloge. Svakom korisniku je pre pristupa sistemu neophodna autentikacija putem prijave (login), čime se obezbeđuje sigurnost i kontrolisan pristup podacima. Svakoj ulozi dodeljen je set funkcionalnosti grupisan po oblasti primene, kako bi se obezbedila efikasna interakcija sa sistemom i optimalno korišćenje baze znanja.

## 3.1. Član posade (Crew Member)

- **Rad sa alarmima:**

  - Pregled aktivnih, kritičnih i informacionih alarma u svom modulu.
  - Potvrda alarma (acknowledge) uz evidentiranje vremena i identifikatora.
  - Pristup uputstvima za reagovanje, prilagođenim tipu alarma i ozbiljnosti situacije.

- **Zdravstveni nadzor:**

  - Unos zdravstvenih simptoma (izbor sa liste: vrtoglavica, glavobolja, kratkoća daha, umor, iritacija očiju itd.).
  - Pregled istorije unetih simptoma i povezanih preporuka sistema.
  - Dobijanje prilagođenih preporuka na osnovu kombinacije senzorskih i medicinskih podataka.

- **Upravljanje sistemom u modulu:**

  - Postavljanje vrednosti parametara u modulu (temperatura (°C), protok vazduha (%), vlažnost (%)...).
  - Pregled trenutnog stanja podsistema (ventilacija, klimatska kontrola, CO₂ skruber, generator kiseonika).
  - Pristup kratkim izveštajima o radu sistema u poslednjem periodu.

- **Izveštaji iz opservacije:**

  - Prijavljivanje vizuelnih nepravilnosti (kondezacija, led, oštećenja).
  - Prijavljivanje akustičnih signala (zviždanje, šištanje, lupkanje) i mirisa (dim, hemikalije).

## 3.2. Inženjer životne sredine (Life Support Engineer)

- **Rad sa alarmima:**

  - Pregled svih alarma u svim modulima, sa filterom po ozbiljnosti i vremenu.
  - Kreiranje novih alarma na osnovu rezultata inspekcije ili analize podataka.
  - Ažuriranje ili brisanje postojećih alarma koji nisu relevantni.

- **Nadzor i održavanje sistema:**

  - Unos rezultata vizuelnih inspekcija (filteri, cevovodi, konektori).
  - Evidentiranje zamene delova (filteri, pumpe, ventilatori) sa datumom i opisom.
  - Unos rezultata kalibracije senzora (datum, izlazna vrednost, odstupanje).
  - Procena preostalog kapaciteta sistema i potrebe za preventivnim održavanjem.

- **Upravljanje parametrima sistema:**

  - Podešavanje ciljne temperature u pojedinačnim ili svim modulima.
  - Podešavanje ciljnog pritiska unutar modula.
  - Regulisanje protoka vazduha i nivoa vlažnosti.

- **Analiza i izveštavanje:**

  - Pregled trendova rada podsistema i upoređivanje sa istorijskim podacima.
  - Generisanje izveštaja o statusu sistema za određeni vremenski period.
  - Dobijanje preporuka za održavanje na osnovu analize sistema.

## 3.3. Menadžer baze znanja (Knowledge Base Manager)

- **Upravljanje pravilima:**

  - Kreiranje novih pravila u Drools bazi znanja.
  - Izmena i optimizacija postojećih pravila na osnovu rezultata rada sistema.
  - Brisanje zastarelih ili neefikasnih pravila.

- **Podešavanje pragova i prioriteta:**

  - Definisanje pragova senzora za alarme i preporuke.
  - Podešavanje prioriteta alarma po tipu i kritičnosti.

- **Testiranje i simulacije:**

  - Pokretanje simulacija različitih scenarija rada sistema.
  - Analiza rezultata testova i merenje efikasnosti pravila.
  - Beleženje i arhiviranje rezultata radi kasnije analize.

- **Optimizacija sistema:**

  - Na osnovu rezultata simulacija i statistike alarma, sistem predlaže izmene pragova ili prioriteta.
  - Unošenje odobrenih izmena u bazu znanja i konfiguraciju sistema.

- **Upravljanje korisnicima:**
  - Registracija drugih korisnika sistema (članova posade i inženjera životne sredine).

# 4. METODOLOGIJA

Sistem AstroVital funkcioniše kao ekspertski sistem zasnovan na pravilima (rule-based engine), gde rezoner predstavlja centralni modul zadužen za prikupljanje i obradu ulaznih podataka, evaluaciju stanja i donošenje odluka na osnovu baze znanja.
Baza znanja sadrži formalizovana stručna pravila koja pokrivaju oblasti nadzora, održavanja i reakcije na kritične uslove u svemirskom staništu.

Rad sistema obuhvata:

1. Prikupljanje podataka iz više izvora – senzori, nosivi uređaji, ručni unosi korisnika.
2. Generisanje relevantnih izlaza – alarma, preporuka, izveštaja, korektivnih akcija.
3. Primena pravila iz baze znanja uz forward chaining za donošenje preporuka i automatskih akcija.
4. Prepoznavanje obrazaca tokom vremena korišćenjem CEP (Complex Event Processing) mehanizama.
5. Dijagnostičko rezonovanje uz backward chaining za utvrđivanje uzroka problema.

## 4.1. Ulazni podaci

Ulazni podaci predstavljaju sve informacije koje sistem prima radi obrade, procene stanja i donošenja odluka. Dele se u dve glavne grupe:

- **Automatski prikupljeni podaci** – dobijeni preko senzora, pametnih uređaja i sistema za nadzor.
- **Ručno uneti podaci** – koje unose korisnici sistema u skladu sa svojom ulogom.

### Automatski prikupljeni podaci

- **Podaci o čoveku (Član posade – Crew Member):**

  - Puls (bpm)
  - Krvni pritisak (mmHg)
  - Zasićenost kiseonikom – SpO₂ (%)
  - Respiratorna frekvencija (udisaja/min)
  - Telesna temperatura (°C)
  - Nivo aktivnosti (sedentarno, umereno, intenzivno)

- **Podaci o staništu (senzori i sistemi):**

  - Atmosferski uslovi:

    - Nivo kiseonika (O₂, %)
    - Nivo ugljen-dioksida (CO₂, ppm)
    - Nivo ugljen-monoksida (CO, ppm)
    - Temperatura (°C)
    - Vlažnost (%)
    - Atmosferski pritisak (kPa)
    - VOC nivo (ppm)
    - PM nivo (μg/m³)

  - Status podsistema:

    - Ventilacija
    - Klimatska kontrola
    - Sistem za reciklažu vode

  - Obrađeni trendovi:
    - Visoka vlažnost u periodu od 6h
    - Epizodično zagađenje vazduha u periodu od 24h

### Ručno uneti podaci

- **Član posade (Crew Member):**

  - Zdravstveni simptomi – izbor iz liste (vrtoglavica, glavobolja, kratkoća daha, umor, iritacija očiju.)

- **Menadžer baze znanja (Knowledge Base Manager):**

  - Kreiranje, izmena ili brisanje pravila u Drools bazi znanja
  - Podešavanje pragova senzora
  - Pokretanje i beleženje rezultata simulacija i testova

## 4.2 Činjenice sistema

„Činjenice sistema“ su jasno definisani, proverljivi iskazi o stanju okruženja, podsistema, posade i obrazaca u vremenu. One se koriste kao ulaz u pravila („ako–onda“) i mogu biti neposredno izmerene, prijavljene ili dobijene iz jednostavnih vremenskih prozora (trendovi).

**Činjenice vitalnih znakova i simptoma** — mereni parametri posade (SpO₂, puls, RR, T) i subjektivne prijave (vrtoglavica, glavobolja…).

| Činjenica                 | Kratak opis                 |
| ------------------------- | --------------------------- |
| SpO₂ nizak                | Slaba zasićenost kiseonikom |
| Puls povišen (HR)         | Ubrzan rad srca             |
| RR povišena               | Ubrzano disanje             |
| Telesna T povišena        | Povišena temperatura tela   |
| Drhtavica prijavljena     | Osećaj hladnoće/drhtanja    |
| Vrtoglavica prijavljena   | Nestabilnost/omaglica       |
| Kratkoća daha prijavljena | Teže disanje                |
| Glavobolja prijavljena    | Bol/pritisk u glavi         |
| Iritacija očiju           | Pečenje/suvoća očiju        |

**Činjenice okruženja** — stanje vazduha i uslova u modulu (O₂, CO₂, temperatura, vlažnost, pritisak, zagađivači).

| Činjenica            | Kratak opis                |
| -------------------- | -------------------------- |
| O₂ nizak             | Manjak kiseonika u vazduhu |
| CO₂ visok            | Povišen ugljen-dioksid     |
| Temperatura visoka   | Previše toplo u modulu     |
| Vlažnost niska       | Suv, iritirajući vazduh    |
| Pritisak naglo opada | Brz gubitak pritiska       |
| CO povišen           | Ugljen-monoksid detektovan |

**Činjenice podsistema** — operativni status opreme (ventilacija, O₂ generator, CO₂ skruber, senzori, filteri, voda/energija).

| Činjenica                  | Kratak opis               |
| -------------------------- | ------------------------- |
| Ventilacija degradirana    | Slab protok vazduha       |
| Filter vazduha zaprljan    | Povećan otpor/začepljenje |
| Reciklaža vode degradirana | Slab rad sistema vode     |

### 4.2.1 Pravila izvedena od činjenica

1. **O₂ nizak** ∨ **CO₂ visok** => **Rizik od hipoksije – faktor**
2. **Rizik od hipoksije** ∧ (**SpO₂ nizak** ∨ **Kratkoća daha prijavljena** ∨ **Vrtoglavica prijavljena**) => **Hipoksija potvrđena**
3. **CO₂ visok** ∧ (**CO₂ raste (30 min)** ∨ **CO₂ skruber zasićen**) => **Hiperkapnija verovatna**
4. **Ventilacija degradirana** ∧ **CO₂ visok** => **Potreban servis ventilacije**
5. **Pritisak naglo opada** ∨ **Pritisak opada (30 s)** => **Dekompresija – sumnja**
6. **Dekompresija – sumnja** ∧ (**Čuje se zviždanje** ∨ **Senzor „zaglavljen“ (pritisak)**) => **Dekompresija potvrđena**
7. **RH raste (6 h)** ∧ **Reciklaža vode degradirana** => **Rizik od kondenzacije**
8. **Filter vazduha zaprljan** ∧ **CO₂ visok** => **Potrebna zamena filtera**
9. **Temperatura visoka** ∧ (**Puls povišen (HR)** ∨ **RR povišena**) => **Toplotni stres**
10. **Senzor „zaglavljen“** ∨ **Senzor neispravan** => **Kalibracija senzora potrebna**

### 4.2.2 Forward Chaining

##### **Hipoksija**

1. Ako je **O₂ nizak** ∨ **CO₂ visok** => **Rizik od hipoksije – faktor (nova činjenica)**
2. Ako je **Rizik od hipoksije – faktor** ∧ (**SpO₂ nizak** ∨ **Kratkoća daha prijavljena** ∨ **Vrtoglavica prijavljena**) => **Hipoksija potvrđena (nova činjenica)**
3. Ako je **Hipoksija potvrđena** ∧ **Ventilacija degradirana** => **Uzrok hipoksije: nedovoljna ventilacija** (nova dijagnostička činjenica)

##### **Hemijsko zagađenje / CO incident (od lokalnog do kritičnog)**

1. **Ukoliko** su prisutni: **CO povišen** **i** **Iritacija očiju**, **izvodi se** **Hemijski iritansi prisutni**.
2. **Ukoliko** su **Hemijski iritansi prisutni** **i** **Ventilacija degradirana**, **izvodi se** **Opasan kvalitet vazduha u modulu**.
3. **Ukoliko** je **Opasan kvalitet vazduha u modulu** **i** **SpO₂ opada (2–6 h)**, **izvodi se** **Kritičan incident vazduha** (stanje koje eskalira protokol za zaštitu posade).

## 4.3 Izlazni podaci

Izlazni podaci predstavljaju informacije koje sistem generiše nakon obrade ulaznih podataka, primene pravila u Drools bazi znanja i izvršavanja predviđenih akcija. Oni se dostavljaju korisnicima u obliku notifikacija, vizuelnih prikaza, izveštaja i preporuka, u skladu sa dodeljenim ulogama.

### 4.3.1. Član posade (Crew Member)

- **Alarmi i obaveštenja**

  - Prikaz teksta upozorenja i prioriteta alarma.
  - Istorija alarma sa vremenom aktivacije i potvrde.

- **Prikaz statusa modula**

  - Trenutne vrednosti temperature, vlažnosti, pritiska i kvaliteta vazduha.
  - Pristup kratkim izveštajima o radu sistema u poslednjem periodu.

- **Povratna potvrda akcija**

  - Potvrda prijema alarma ili izvršene preporuke.
  - Zapis vremena reakcije.

### 4.3.2. Inženjer životne podrške (Life Support Engineer)

- **Centralizovani prikaz statusa**

  - Detaljni pregled svih modula i njihovih parametara u realnom vremenu.

- **Alarmi i statistika alarma**

  - Lista aktivnih alarma sa prioritetima.

### 4.3.3. Menadžer baze znanja (Knowledge Base Manager)

- **Analitički pregled sistema**

  - Konsolidovani pregled svih alarma, parametara i korisničkih interakcija.

- **Rezultati simulacija**

  - Prikaz ishoda testnih scenarija sa identifikovanim slabim tačkama.

## 4.4. Primeri rezonovanja

U ovoj tački prikazujemo kako sistem za autonomno upravljanje uslovima života u svemirskoj stanici primenjuje baze znanja i mehanizme rezonovanja u konkretnim situacijama.
Primeri pokazuju kompletan tok:

1. Prikupljanje podataka iz više izvora (senzori, nosivi uređaji, ručni unosi članova posade)
2. Prepoznavanje obrazaca u vremenu uz pomoć CEP mehanizama
3. Primenu pravila iz baze znanja kroz forward chaining
4. Dijagnostičko rezonovanje uz backward chaining
5. Generisanje alarma, preporuka i izveštaja

### 4.4.1 Glavni scenario: „Kondenzacija i mikrobni rizik u modulu“

#### CEP (vremenski obrasci koji kreiraju početne činjenice)

- **CEP-1:** Ako **relativna vlažnost raste najmanje 6 h** i **temperatura je blizu tačke rose**, onda => **Kondenzacija aktivna**.
- **CEP-2:** Ako **VOC ili PM** imaju **više epizoda/skokova u poslednja 24 h**, onda => **Epizodično zagađenje vazduha**.

_(Ove činjenice dalje „hrane“ forward chaining.)_

#### Forward chaining (jedan lanac od 3 koraka)

1. **Ako** je **Kondenzacija aktivna** **i** **Ventilacija degradirana**, **onda** => **Nakupljanje vlage**.
2. **Ako** je **Nakupljanje vlage** **i** (**Epizodično zagađenje vazduha** **ili** **VOC povišen**), **onda** => **Mikrobni uslovi pogodni**.
3. **Ako** su **Mikrobni uslovi pogodni** **i** (**Iritacija očiju** **ili** **Kašalj prijavljen**), **onda** => **Mikrobni rizik — visok prioritet**.

**Tok (sažeto):**  
CEP-1 => _Kondenzacija aktivna_ => (+ Ventilacija degradirana) => _Nakupljanje vlage_ => (+ VOC/PM) => _Mikrobni uslovi pogodni_ => (+ simptom) => **Mikrobni rizik — visok**.

##### Backward chaining (kako sistem „traži” potvrde i uzrok)

- **BC cilj 1 — „Postoji li mikrobni rizik — visok?“**  
   Sistem unazad proverava da li postoje:  
   (a) _Kondenzacija aktivna_ ili _Nakupljanje vlage_;  
   (b) _Epizodično zagađenje_ (VOC/PM) ili bar povišen VOC/PM;  
   (c) _simptomi_ (iritacija očiju ili kašalj).  
   Ako neki element nedostaje, sistem **zatraži baš taj podatak/merenja**. Kada (a)+(b)+(c) postoje, zaključuje **„Mikrobni rizik — visok prioritet“**.

- **BC cilj 2 — „Gde je izvor vlage?“** (hipoteze i dokazi)

  - **H1: Reciklaža vode/curenje** -> _Reciklaža vode degradirana_ **i** _vizuelno kondenzat/led na vodnim linijama_.

  - **H2: Nedovoljna ventilacija / hladne zone** -> _Ventilacija degradirana_ **i** _T blizu tačke rose_ na zidovima/panelima.

  - **H3: Kondenzacija na panelima/mostovi hladnoće** -> _termalne mape pokazuju hladne površine_ **i** _kondenzacija u tim zonama_.
    BC u **svakom modulu stanice** prolazi hipoteze redom (H1 → H2 → H3) i **traži ciljane dokaze**.
  - Ako se hipoteza potvrdi u modulu → pretraga se zaustavlja i vraća zaključak: **„Izvor vlage potvrđen (H1/H2/H3) u modulu X“**.
  - Ako nijedna hipoteza nije potvrđena → BC rekurzivno prelazi na sledeći modul i ponavlja isti postupak.
  - Ako se iscrpe svi moduli bez potvrde → vraća se zaključak: **„Izvor vlage nije potvrđen u dostupnim modulima“**.

6. **Template-ovi:**

   - Prag-pravila za pritisak, O₂, CO₂ i temperaturu generišu se iz tabele u Drools šablonu
   - Jedan šablon => desetine pravila (pokazuje skalabilnost)
   - Primeri:
     - „Ako je **O₂ < O2_nizak**(prag) ≥ 2 min, onda → **O₂ nizak**.“

   * „Ako je **CO₂ > CO2_visok**(prag) ≥ 5 min, onda → **CO₂ visok**.“
