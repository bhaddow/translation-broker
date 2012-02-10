#!/usr/bin/env python
# coding=utf8

#
# Translates using the google api style interface
#

import urllib

def main():
    urls = ["http://accept:motelone@accept.statmt.org/demo/translate.php"]
    for url in urls:
        print url
        source = "en"
        target = "fr"
        input_text = "I clearly stated in my earlier post this is what the tech guy did - and I reported his exact steps ."
        params = urllib.urlencode({'v' : '1.0', 'ie' : 'UTF8', \
            'langpair' : '%s|%s' % (source,target), 'q' : input_text})
        f = urllib.urlopen(url,params)
        for line in f:
            print line[:-1]




if __name__ == "__main__":
    main()
