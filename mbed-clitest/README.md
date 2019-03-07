## Mbed-clitest test framework
*Mbed-clitest* is an automated testing framework for Mbed development.
It automates the process of flashing Mbed boards,
running tests and accumulating test results into reports.
Developers use it for local development as well as for automation in a
Continuous Integration Environment.

When testing [`mbed-OS`](https://www.mbed.com/en/platform/mbed-os/)
`Mbed-clitest` allows you to execute commands remotely
via the command line interface in board (`DUT`).
The interface between the test framework and `DUT` can be (for example)
UART, sockets or for example stdio (process `DUT`).

More detailed documentation on the tool is available
[here in rst format](https://github.com/ARMmbed/mbed-clitest/tree/master/doc-source)
and [here in markdown format](https://github.com/ARMmbed/mbed-clitest/tree/master/doc).
A more detailed description of the *mbed-clitest*
concept is available [here](doc/README.md).

### Prerequisites
Mbed-clitest supports Linux (Ubuntu preferred), Windows and OS X. Our main target is Linux.
We support Python 2.7 and 3.5-3.6. Some OS specific prerequisites below:

* Linux
    * python-dev and python-lxml
        `sudo apt-get install python-dev python-lxml`
    * In order to run test cases with hardware in Linux without sudo rights:
        ```
        sudo usermod -a -G dialout username
        Log out & log in back to Linux
        ```
        This command will add the user 'username' to the 'dialout' group and
        grant the required permissions to the USB ports.
* OS X
    * [XCode developer tools](http://osxdaily.com/2014/02/12/install-command-line-tools-mac-os-x/)
    * [MacPorts](https://www.macports.org/install.php)
    * [lxml](http://lxml.de/installation.html#installation):
        `STATIC_DEPS=true sudo pip install lxml`
* Windows
    * python-lxml installation is problematic on Windows since
    it usually requires build tools. It can however be installed
    from pre-built binaries.
        * Search for a binary for you system from the internet.
        * Navigate to the directory where you downloaded the
        binary and install it with `pip install <insert_file_name>`
    * You can follow instructions [here](http://lxml.de/installation.html#installation)
    instead.
    * Some python modules might require [Visual C++ for Python](https://www.microsoft.com/en-us/download/details.aspx?id=44266)

### Installation

`> python setup.py install`

When installing clitest, installing to
a [virtualenv](https://virtualenv.pypa.io/en/stable/installation/)
is a good idea.

#### Dependencies
Dependencies are listed in [setup.py](setup.py).

#### Optional dependencies

* If you wish to decorate your console log with all kinds of colors,
install the coloredlogs module using pip. `pip install coloredlogs`
    * There have been issues with coloredlogs installation on Windows.
    We might switch to a different module at some point
    to enable colored logging on Windows as well.
* If you want to use RAAS: [raas-pyclient](https://github.com/ARMmbed/raas-pyclient)
* To use the DeviceServer, Connector or RCC ExtApps you will need
  the [mdsrestclient](https://github.com/ARMmbed/mds-rest-testing) module.
* [valgrind](http://valgrind.org)
* [gdb](https://www.gnu.org/software/gdb/)


#### Nanosimulator
To use nanosimulator with clitest, you need to install
the nanosimulator library available in
[nanomesh-applications](https://github.com/ARMmbed/nanomesh-applications).
Clitest is also a part of this repository.
To build nanosimulator for mbed-clitest you need to first
clone the nanomesh-applications repository and then run
make in the nanosimulator folder.

Finally you need to build the clinode object,
which can be done from the nanomesh-applications repository root
by running make on the Makefile.simulator.
Instructions for this are available in the makefile Makefile.simulator.

### Documentation
To build documentation please install Sphinx (see [installation](#installation)).
We have provided a small script to automate building the documentation
to html. To run this script call:
`python build_docs.py`

This command will build the documentation from doc-source to doc/html.

### Usage

To print the help page:

`clitest --help`

To list all local testcases from the examples subfolder:

`clitest --list --tcdir examples`

To print Mbed-clitest version:

`clitest --version`


#### Typical use

All of the commands described below might also need other options,
depending on the test case.

**Running test cases using the tc argument**

`> clitest --tc <test case name> --tcdir <test case search path>`

To run all existing test cases from the `examples` folder:

`> clitest --tc all --tcdir examples`

**Running an example test case with hardware**

In this example, we assume that a compatible board has been connected
to the computer and an application binary for the board is available.
The referred test case is available in [the Mbed-clitest github repository](https://github.com/ARMmbed/mbed-clitest/blob/master/examples/test_cmdline.py).

`> clitest --tc test_cmdline --tcdir examples --type hardware --bin <path to a binary>`

**Using metadata filters**

To run all test cases with testtype regression in the metadata:

`> clitest --testtype regression --tcdir <test case search path>`

The following metadata filters are available:
* test type (--testtype)
* test subtype (--subtype)
* feature (--feature)
* test case name (--tc)
* tested component (--component)
* test case folder (--group)

**Running a premade suite**

Mbed-clitest supports a suite file that describes a suite of test cases
in `json` format.

`> clitest --suite <suite file name> --tcdir <test case search path> --suitedir <path to suite directory>`

**Enabling debug level logging**

Use -v or -vv arguments to control logging levels. -v increases the frameworks logging level
to debug (default is info) and the level of logging in
certain plugins and external components to info (default is warning).
--vv increases the level of logging on all Mbed-clitest loggers to debug.

**Further details**

For further details on any of the features see our documentation.

#### Creating a test case
Mbed-clitest test cases are implemented as Python classes that inherit the Bench object available in `mbed_clitest.bench` module.
The test case needs to have an initialization function that defines the metadata and a case function that implements the test sequence.
There are two optional functions, setup and teardown. More information is available in our documentation.

An example test case is shown below:

```
from mbed_clitest.bench import Bench


class Testcase(Bench):
    def __init__(self):
        Bench.__init__(self,
                       name="example_test",
                       title="Example test",
                       status="development",
                       purpose="Show example of a test",
                       component=["examples"],
                       type="smoke",
                       requirements={
                           "duts": {
                               '*': {
                                    "count": 1,
                                    "type": "hardware"
                               }
                           }
                       }
                       )

    def setup(self):
        # nothing for now
        pass


    def case(self):
        self.command(1, "echo hello world", timeout=5)
        self.command("help")

    def teardown(self):
        # nothing for now
        pass
```

### License
See the [license](LICENSE) agreement.
