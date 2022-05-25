### Počítání výsledků přeboru škol v OB

Program počítá výsledky přeboru škol podle pravidel z https://obrozvoj.cz/Pages/PreborSkol/Pravidla.aspx

#### Systémové požadavky

Program je velmi nenáročný, stačí mu Java 8, měl by běžet na libovolné operačním systému, kde je Java.

#### Jak použít

- výsledky jednotlivců dejte běžným způsobem do ORISu
- stáhněte si verzi z https://github.com/ok-dobris/TeamResults/releases (jar soubor)
- otevřete si v ORISu výsledky závodu a pod tlačítkem **Exporty** vyberte **Export XML 3.0**  
- stažený XML soubor (bude se jmenovat třeba export_xml_NNNN.xml) umístěte ho vedle JAR souboru
  - pozn.: pokud program najde více xml souborů, zpracuje je všechny. Staré soubory, které už nechcete znovu
    zpracovávat, tedy raději mažte.
- spusťte JAR soubor aplikace (ve Windows stačí poklepat)
  - pokud je vše v pořádku, měly by se vytvořit soubory s koncovkami  **.html** a **.csv**. CSV soubor je vhodný,
    pokud chcete výsledky dále zpracovávat, HTML soubor je vhodný k prohlížení / tisku a zveřejnění
