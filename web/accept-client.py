#!/usr/bin/env python
# coding=utf8

#
# Translates using the google api style interface
#

import json
import urllib

def main():
    urls = ["http://accept:motelone@accept.statmt.org/demo/translate.php"]
    for url in urls:
        print url
        source = "en"
        target = "fr"
        input_text = "I clearly stated in my earlier post this is what the tech guy did - and I reported his exact steps. I clearly stated in my earlier post this is what the tech guy did - and I reported his exact steps ."
        params = urllib.urlencode({'v' : '1.0', 'ie' : 'UTF8', \
            'langpair' : '%s|%s' % (source,target),\
            'system' : 'sb', 'q' : input_text})
        f = urllib.urlopen(url,params)
        line = f.readline()
        response = json.loads(line)
        if not response['responseData']:
            print "Error: ", response['responseDetails']
        else: 
            print response['responseData']['translatedText']




if __name__ == "__main__":
    main()
