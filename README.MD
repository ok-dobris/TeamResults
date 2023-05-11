### Počítání výsledků přeboru škol v OB

Program počítá výsledky přeboru škol podle pravidel z https://obrozvoj.cz/Pages/PreborSkol/Pravidla.aspx

#### Systémové požadavky

Program je velmi nenáročný, stačí mu Java 8, měl by běžet na libovolném operačním systému, kde je Java. Pokud Java dosud instalovaná není, lze ji stáhnout a nainstalovat z https://www.java.com/en/download/.

#### Jak použít

- výsledky jednotlivců dejte běžným způsobem do ORISu
- stáhněte si verzi z https://github.com/ok-dobris/TeamResults/releases (jar soubor)
- otevřete si v ORISu výsledky závodu a pod tlačítkem **Exporty** vyberte **Export XML 3.0**
  - tohle je opravdu potřeba. I když pořádací program také produkují IOF XML 3.0 výsledky, pro vyhodnocení přeboru
    obvykle nestačí, ORIS některé potřebné informace přidá (hlavně o družstvech)
- stažený XML soubor (bude se jmenovat třeba export_xml_NNNN.xml) umístěte vedle JAR souboru
  - pozn.: pokud program najde více xml souborů, zpracuje je všechny. Staré soubory, které už nechcete znovu
    zpracovávat, tedy raději mažte.
- spusťte JAR soubor aplikace (ve Windows stačí poklepat)
  - pokud je vše v pořádku, měly by se vytvořit soubory s koncovkami  **.html** a **.csv**. CSV soubor je vhodný,
    pokud chcete výsledky dále zpracovávat, HTML soubor je vhodný k prohlížení / tisku a zveřejnění

#### Konfigurace

- pokud potřebujete změnit bodování nebo kategorie, vedle JAR souboru můžete umístit soubor s názvem `application.conf`. S jeho pomocí můžete změnit některé parametry toho, jak se výsledky počítají:

```
scoring-first: 2 # Počet bodujících v každém družstvu

categories: { # Skupiny kategorií
  "DH3+DH5": [D3, H3, D5, H5, DI, HI, DII, HII]
  "DH7+DH9": [D7, H7, D9, H9, DIII, HIII, DIV, HIV]
  "DS+HS": [DS, HS, DV, HV]
}
```
