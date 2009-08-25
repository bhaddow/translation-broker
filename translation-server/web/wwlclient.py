#!/usr/bin/env python
# coding=utf8

#
# Translates using the google api style interface
#

import urllib

def main():
    urls = [" http://ajax.googleapis.com/ajax/services/language/translate",  "http://demo.statmt.org/translate.php"]
    for url in urls:
        print url
        source = "fr"
        target = "en"
        #input_text = "Je ne sais pas."
        input_text = "C’est une vache de couleur froment vif, plus claire sous le ventre et autour des yeux et du mufle, avec des muqueuses rose clair."
        params = urllib.urlencode({'v' : '1.0', 'ie' : 'UTF8', \
            'langpair' : '%s|%s' % (source,target), 'q' : input_text})
        f = urllib.urlopen(url,params)
        for line in f:
            print line[:-1]




if __name__ == "__main__":
    main()
