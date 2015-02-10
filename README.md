This is the code used to run the demo.statmt.org website. It's not really packaged for distribution
but I make it available in case it provides useful inspiration for others.

Here is some instructions I wrote for a colleague once:

The architecture consists of three parts:
- a web frontend, stored in the web directory in svn
- a  translation broker (tbroker) which handles the routing of translation
requests
-  moses servers.

The web frontend is quite simple - just  a couple of php scripts and some
style  sheets. index.php runs the snippet demo, and when it is loaded it
communicates  with the tbroker server at the configured port, using
mt_functions.php. Much of the complexity in this php script is about logging
corrections, so you can probably ignore this.

The tbroker is written in java, and its source files are under tbroker/src. It
should compile with ant and the given build.xml, and depends on a few
apache classes, which are checked in to svn. The scripts I use to start the
tbroker are in svn, as are its configuration files (in tbroker/config). The best
example is thor-prod.xml.

You can see that this consists of three parts - specification of directories,
specification of tools, and toolchains. The tools are things like tokenizers and
truecasers, and of course moses. The toolchains are a series of tools that
make a translation system.

To start up the demo server, I first start the moses server(s), then the
tbroker server, then accessing index.php should give you a list of systems.
The moses.*.sh are the scripts to start/stop the moses servers, and the
tbroker.*.sh are used to start the tbroker.

There's also a couple of useful command line scripts check.py is the one I use
to check that the website is still up wwlclient.py is the script that access the
'google translate' style api, implemented using translate.php - these are in
the web directory.

