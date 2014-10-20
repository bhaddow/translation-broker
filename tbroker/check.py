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
url_accept = "http://accept:motelone@accept.statmt.org/demo.2014/index.php";

systems = [\
    (url_prod,"en-de", "Zinc is a metallic chemical element with atomic number 30."),\
    (url_prod, "de-en", "In Paris kommt der Mathematiker Augustin Louis Cauchy, ein Pionier der Analysis, zur Welt. "),\
    (url_prod, "fr-en","Le centre commercial attire quinze millions de visiteurs par an pour un chiffre d’affaires de 259 M€ en 2008,"),\
    (url_prod, "es-en", "En un viaje de visita a Dinamarca contrajo matrimonio con una sobrina."),
    (url_prod, "cs-en", "Historie  je zkoumání minulosti se zaměřením na písemné zprávy týkající se činnosti lidstva během jeho existence."),
    (url_accept, "en-fr-sym", "it doesn 't even look professional or promising or anything since I guess that was your aim - it simply looks ugly and creepy ."),
    (url_accept, "en-de-sym", "it doesn 't even look professional or promising or anything since I guess that was your aim - it simply looks ugly and creepy ."),
    (url_accept, "fr-en-sym", "Mais si tu as téléchargé le fichier je ne sais pas pourquoi tu as cette demande."),
    (url_accept, "fr-en-twb", "Mais si tu as téléchargé le fichier je ne sais pas pourquoi tu as cette demande."),
#    (url_accept, "sb-fr-en-1009-13", "Mais si tu as téléchargé le fichier je ne sais pas pourquoi tu as cette demande."),
#    (url_accept, "tb-en-fr", "Careful: there is no real winner in this activity: the aim is not to find out which option had really been chosen by MSF at that time."),
#    (url_accept,"tb-fr-en", "À MSF, il y a des cycles réguliers d'innovations et de remise en question de ces innovations."),
#    (url_accept, "tb13-en-fr", "Careful: there is no real winner in this activity: the aim is not to find out which option had really been chosen by MSF at that time."),
#    (url_accept,"tb13-fr-en", "À MSF, il y a des cycles réguliers d'innovations et de remise en question de ces innovations.")
    ]

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
