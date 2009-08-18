#!/usr/bin/env python


#
# Check that the moses server is running
#

import sys
import urllib

systems = [("http://demo.statmt.org/index.php","en-de")]

def main():
    for url,sysid in systems:
        print "Testing %s" % url
        params = urllib.urlencode({'sysid' : sysid, 'input' : "Are you alive?"})
        f = urllib.urlopen(url, params)
        output = None
        for line in f:
            if line.startswith("<TEXTAREA"):
                pos = line.find(">")
                if pos>=0:
                    output = line[pos+1:-1]
        if output:
            print "RECEIVED: ", output
            sys.exit(0)
        else:
            print "ERROR: Could not find output in response"
            sys.exit(1) 

if __name__ == "__main__":
    main()
