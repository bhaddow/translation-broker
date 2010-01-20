#!/usr/bin/env python
# coding=utf8


#
# Check that the moses server is running
#

import re
import sys
import urllib

url_prod = "http://demo.statmt.org/index.php";
url_ec = "http://demo.statmt.org/ec/index.php";

systems = [\
    (url_prod,"en-de", "Zinc is a metallic chemical element with atomic number 30."),\
    (url_prod, "de-en", "In Paris kommt der Mathematiker Augustin Louis Cauchy, ein Pionier der Analysis, zur Welt. "),\
    (url_prod, "fr-en","Le centre commercial attire quinze millions de visiteurs par an pour un chiffre d’affaires de 259 M€ en 2008,"),\
    (url_prod, "es-en", "En un viaje de visita a Dinamarca contrajo matrimonio con una sobrina."),
    (url_ec, "fr-en", "Règlement (CE) no 753/2009 du Conseil du 27 juillet 2009 portant modification du règlement (CE) no 43/2009 en ce qui concerne les possibilités de pêche et les conditions associées applicables à certains stocks halieutiques"),\
    (url_ec, "it-en", "Decisione EUMM Georgia/1/2009 del Comitato politico e di sicurezza, del 31 luglio 2009, relativa alla proroga del mandato del capo della missione di vigilanza dell'Unione europea in Georgia (EUMM Georgia)"),
    (url_ec, "nl-el", "Richtlijn 2009/45/EG van het Europees Parlement en de Raad van 6 mei 2009 inzake veiligheidsvoorschriften en -normen voor passagiersschepen (Herschikking)"),
    (url_ec, "de-fr", "Bekanntmachung des bevorstehenden Außerkrafttretens bestimmter Antidumpingmaßnahmen"),
    (url_ec, "en-lt", "Acts adopted under the EC Treaty/Euratom Treaty whose publication is not obligatory"),
    (url_ec, "hu-ro", "A LexAlert értesítési szolgáltatás jelenleg kidolgozás alatt áll, az interneten keresztül történő regisztrációra még nincs lehetőség."),
    (url_ec, "es-en", "Decisión del Consejo, de 27 de julio de 2009, relativa a la conclusión del procedimiento de consulta con la República de Guinea en virtud del artículo 96 del Acuerdo de Cotonú")]

spaces = re.compile(r"\s+")

def main():
    for url,sysid,input in systems:
        print "Testing %s system %s" % (url,sysid)
        print "SENDING: %s" % input
        params = urllib.urlencode({'sysid' : sysid, 'input' : input})
        f = urllib.urlopen(url, params)
        output = None
        for line in f:
            if line.startswith("<TEXTAREA"):
                pos = line.find(">")
                if pos>=0:
                    output = line[pos+1:-1]
        if output:
            print "RECEIVED: ", output
        else:
            print "ERROR: Could not find output in response"
            sys.exit(1) 
        output = spaces.sub("",output).lower()
        input = spaces.sub("",input).lower()
        if output == input:
            print "ERROR: Not translated"
            sys.exit(1)

if __name__ == "__main__":
    main()
