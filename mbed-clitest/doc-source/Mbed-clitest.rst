############
Mbed-clitest
############

`clitest` is the entry-point for test execution.

*************
Installation
*************

You can install Mbed-clitest with all required python dependencies
easily with this command:

`/> python setup.py install`

If everything goes well you can start tests from any location.
The test cases must be located under the `./testcases` subfolder,
OR you can set the testcase
root folder manually with Mbed-clitest option: `--tcdir`.

Dependencies
============

To use Mbed-clitest, make sure that you have the following tools
available in your computer. Python modules should be automatically
installed by Mbed-clitest installation.

* Python (2.7<)
* pip (python package manager)
 * download: https://bootstrap.pypa.io/get-pip.py
 * python get-pip.py
* pip modules
 * pyserial version > 2.5(`pip install pyserial>2.5`
   or https://pypi.python.org/pypi/pyserial)
 * jsonmerge (`pip install jsonmerge`)
 * pyshark (`pip install pyshark`)
 * yattag (`pip install yattag`)
 * prettytable (`pip install prettytable`)
 * requests (`pip install requests`)
 * coverage (`pip install coverage`), for unit tests.
 * mock  (`pip install mock`), for unit tests.
 * psutil (`pip install psutil`)
 * coloredlogs (`pip install coloredlogs`), optional
 * semver (`pip install semver`)
 * six (`pip install six`)
 * mbed-ls (`pip install mbed-ls`)
 * netifaces (`pip install netifaces`), for unit tests
* Mbed-flasher (available in github at
  https://github.com/ARMmbed/mbed-flasher)

*************
Folder structure
*************

| /mbed-clitest> tree
| ├───doc         // these documents
| │   └───UML     // UML diagrams describing different parts of the software
| ├───doc-source  // these documents in rst format, for use with sphinx
| ├───examples    // some test case examples
| ├───log         // default location for test execution logs, when running clitest directly from GIT repository root
| ├───mbed_clitest    // clitest -libraries
| │   ├───PacketShark       // PacketShark libraries
| │   ├───Plugins           // Plugin implementation and default plugins
| │   ├───Randomize         // Random seed generator implementation
| │   ├───DeviceConnectors  // DUT connectors
| │   ├───ExtApps           // Backwards compatibility module for previous plugins
| │   ├───Extensions        // Backwards compatibility module for previous plugins
| │   ├───build             // build object implementation
| │   ├───Events            // Event system implementation
| │   ├───Reports           // Reporters
| │   ├───TestBench         // Test bench implementation
| │   ├───tools             // tools and helper libraries
| │   ├───ResourceProvider  // Allocators and ResourceProvider
| │   ├───TestSuite         // Test runner implementation
| │   └───nanosimulator     // Simulator helper library
| ├───test_regression  // Regression tests
| └───test         // unit tests

*******
Example
*******

|  /my-testcases> tree
|  ├───testcases
|  │   ├───folder
|  │   │   ├ test3.py
|  │   ├ test1.py
|  │   └ test2.py
|  ├───folder2
|  │   ├ test22.py
|  └ test.py
| /my-testcases> clitest --tc test3
| ...
| /my-testcases> clitest --tcdir folder2 --tc test22
| ...

*******
Development
*******

Install Mbed-clitest in development mode:

    /> python setup.py develop

This allows you to modify the source code and debug easily.

***********************
Command line parameters
***********************

Command line parameters can be given both over the command line
as well as from a configuration file. The file must be a text file,
which is formatted with one or more parameters per line,
and it can be given to Mbed-clitest with command line
parameter --cfg_file. Example configuration file is available in
`examples. <../examples/example_cli_config_file>`_

**Note**: If you put --cfg_file argument inside a file used as
--cfg_file, don't try to load the same file.
This will cause infinite recursion.
We try to check if the file names match and delete the new --cfg_file
argument if they match, but the check might not be foolproof.::

    -h, --help            show this help message and exit
    --list                List of available testcases(nothing else)
    --listsuites          List of available suites
    --tc TC               execute testcase. Give test index, name, list of
                          indices/names, or all to execute all testcases
    --suite SUITE         Run tests from suite json file <suite>. Can be
                          absolute path to suite file or path relative to
                          --suitedir.
    --version             Show version
    --raas RAAS           RaaS server address and port, e.g. --raas
                          127.0.0.1:8000 or omitting port for default (8000)
                          --raas 127.0.0.1
    --allocator ALLOCATOR
                          Allocator to be used for allocating resources. Default
                          is LocalAllocator
    --allocator_cfg       File that contains configuration for the allocator used.
    --env_cfg ENV_CFG     Use user specific environment configuration file
    --repeat REPEAT       Repeat testcases N times
    --stop_on_failure     Stop testruns/repeation on first failed TC
    --clean               Clean old logs
    --connector CONNECTOR
                          Connector credentials for selecting and/or generating
                          endpoint certificates. Format should be domain[:token]
                          where token is optional. Eg. --connector
                          this_is_some_domain:this_is_my_token
    --failure_return_value
                          Sets clitest to return a failing code to caller if one
                          or more tests fail during the run. Default is False
    --color               Indicates if console logs are printed plain or with
                          colours. Default is False for plainlogs.
    --check_version       Enables version checks for test cases.
    --ignore_invalid_params
                          Disables checks for invalid parameters.
    --parallel_flash [PARALLEL_FLASH]
                          Enables parallel flash. Defaults to True, expects
                          boolean values.
    --disable_log_truncate
                          Disable long log line truncating. Over 10000characters
                          long lines are truncated by default.
    --cm CM               name of module that is to be used to send results to a
                          cloud service.
    --json                Output results of --list as json instead of a table.
    --export SUITE_FILE_NAME
                          Export list into suite template file.
    --sync_start          Use echo-command to try and make sure duts have
                          started before proceeding with test.
    --valgrind_text       Output as Text. Default: xml format
    --valgrind_console    Output as Text to console. Default: xml format
    --gdb DUT             Run specific simulate node with gdb (debugger). e.g.
                          --gdb 1
    --gdbs DUT            Run specific simulate node with gdbserver (debugger).
                          e.g. --gdbs 1
    --vgdb DUT            Run specific simulate node with vgdb (debugger under
                          valgrind). e.g. --vgdb 1
    --forceflash          Force flashing of hardware device if binary is given.
                          Defaults to False. Mutually exclusive with forceflash and skip_flash
    --forceflash_once     Force flashing of hardware device if binary is given,
                          but only once. Defaults to False. Mutually exclusive with forceflash
                          and skip_flash
    --skip_flash          Skip flashing binaries for duts. Defaults to False.
                          Mutually exclusive with forceflash and forceflash_once.

    Filter arguments:
      Arguments used for filtering tc:s

      --status STATUS       Run all testcases with status <status>
      --group GROUP         Run all testcases that have all items in
                            <group/subgroup> or <group,group2> in their group
                            path.
      --testtype TESTTYPE   Run all testcases with type <testtype>
      --subtype SUBTYPE     Run all testcases with subtype <subtype
      --component COMPONENT
                            Run all testcases with component <component>
      --feature FEATURE     Run all testcases with feature <feature>
      --platform PLATFORM   Run all testcases that allow platform <platform>

    Run information:
      Information of run, such as job id and git or build information.

      --jobId JOBID         Job Unique ID
      --gitUrl GITURL       Set application used git url for results
      --branch BRANCH       Set used build branch for results
      --commitId COMMITID   Set used commit ID for results
      --buildDate BUILDDATE
                            Set build date
      --toolchain TOOLCHAIN
                            Set toolchain for results
      --buildUrl BUILDURL   Set build url for results
      --campaign CAMPAIGN   Set campaign name for results

    Paths:
      Directory and file paths for various clitest features.

      --tcdir TCDIR         Search for testcases in directory <path>
      --suitedir SUITEDIR   Search for suites in directory <path>
      --simdir SIMDIR       Location of nanosimulator libraries
      --cfg_file CFG_FILE   Load cli parameters from file. This will overwrite
                            parameters given before --cfg_file, but results of
                            this will be overwritten by parameters given after
                            this one
      --plugin_path PLUGIN_PATH
                            location of file called plugins_to_load, where custom
                            plugins are imported.

    RAAS parameters:
      Information needed to use RAAS.

      --raas_pwd RAAS_PWD   RaaS server login password. If not specified,
                            environment variable RAAS_PASSWORD is used.
      --raas_user RAAS_USER
                            RaaS server login user name. If not specified,
                            environment variable RAAS_USERNAME is used.
      --raas_token RAAS_TOKEN
                            Optional RaaS server access token when usr/pwd is not
                            used.
      --raas_queue          Enables RAAS daemon side queue for allocating devices.
      --raas_queue_timeout RAAS_QUEUE_TIMEOUT
                            Sets the timeout for allocation on the daemon side
                            queue.
      --raas_share_allocs   Enables sharing raas resources between testcases.

    Test case arguments:
      --log LOG             Store logs to specific path. Filename will be
                            <path>/<testcase>_D<dutNumber>.log
      -s, --silent          Silent mode, only prints results and WARNING or ERROR
                            levels. Has priority over -v argument (see below)
      -v, --verbose         increase output verbosity, max 2 times.
      -w                    Store results to a cloud service.
      --with_logs           Store bench.log to cloud db after run.
      --reset [RESET]       reset device before executing test cases
      --iface IFACE         Used NW sniffer interface name
      --tc_cfg TC_CFG       Testcase Configuration file
      --ch CHANNEL          Use specific rf channel
      --type {hardware,process,simulate,serial,mbed}
                            Overrides DUT type.
      --platform_name PLATFORM_NAME
                            Overrides used platform. Must be found in
                            allowed_platforms in dut configuration if
                            allowed_platforms is defined and non-empty.
      --putty               Open putty after TC executed
      --skip_setup          Skip TC setUp phase
      --skip_case           Skip TC body phase
      --skip_teardown       Skip TC tearDown phase
      --valgrind            Analyse nodes with valgrind (linux only)
      --valgrind_tool {memcheck,callgrind,massif}
                            Valgrind tool to use.
      --valgrind_extra_params VALGRIND_EXTRA_PARAMS
                            Additional command line parameters to valgrind.
      --valgrind_track_origins
                            Show origins of undefined values. Default: false; Used
                            only if the Valgrind tool is memcheck
      --use_sniffer         Use Sniffer as defined in tc requirements. If not defined, define and use default sniffer.
      --my_duts MY_DUTS     Use only some of duts. e.g. --my_duts 1,3
      --pause_ext           Pause when external device command happens
      --nobuf NOBUF         do not use stdio buffers in simulated node process
      --gdbs-port GDBS_PORT
                            select gdbs port
      --pre-cmds PRE_CMDS   Send extra commands right after DUT connection
      --post-cmds POST_CMDS
                            Send extra commands right before terminating dut
                            connection.
      --baudrate BAUDRATE   Use user defined serial baudrate (when serial device
                            is in use)
      --serial_timeout SERIAL_TIMEOUT
                            User defined serial timeout (default 0.01)
      --serial_xonxoff      Use software flow control
      --serial_rtscts       Use Hardware flow control
      --serial_ch_size SERIAL_CH_SIZE
                            use chunck mode with size N when writing to serial
                            port. (default N=-1: use pre-defined mode, N=0:
                            normal, N<0: chunk-mode with size N
      --serial_ch_delay CH_MODE_CH_DELAY
                            User defined delay between characters. Used only when
                            serial_ch_size>0. (default 0.01)
      --kill_putty          Kill old putty/kitty processes
      --simulator_event_log
                            Enable simulator event output log into a file
      --interface INTERFACE
                            Network interface used in tests, unless the testcase
                            specifies which one to use. Defaults to eth0
      --simulator_step_len SIMULATOR_STEP_LEN
                            Simulator step length in microseconds. Default 1
      --bin BIN             Used specific binary for DUTs, when process/simulate
                            is in use. NOTE: Does not affect duts which specify
                            their own binaries.

*******
Running
*******
To run tests you first need to have the test cases in valid python modules.
There are two ways to select which test cases to run: suites or filters.
When using suites Mbed-clitest will search for test cases based on their name
as described in the suite file. This is described in more detail in
`suite_api.md <suite_api.html>`_.

Mbed-clitest also supports filtering test cases by their metadata.
All the available filters are described in the table above.
The filters are provided on the command line in string format and
they support basic boolean logic using keywords "and, or, not" and
grouping by parenthesis. If you want to use filter values with
multiple words, surround them with single quotes (').
Example: --feature "feature1 and 'feature2 subfeature2'"

*******
Results
*******
Mbed-clitest creates the following kinds of results after execution:

junit
=====
  * common xml format suitable for use with Jenkins
    `test_results_analyzer <https://github.com/jenkinsci/test-results-analyzer-plugin)>`_ -plugin
    (for example)
  * location: `log/<timestamp>/result.junit.xml`
  * format is::

    <testsuite failures="0" tests="1" errors="0" skipped="0">
    <testcase classname="<test-name>.<platform>" name="<toolchain>" time="12.626"></testcase>
    </testsuite>

| **NOTE**
The JUnit file is generated slightly differently
from the other reports due to CI.
If the run used the Mbed-clitest retry mechanism to retry failed or
inconclusive test cases, only the final attempt is displayed
in the JUnit report. The failed tries are displayed in the other
reports as normal. This functionality can be configured using the
retryReason parameter in the suite.
See `suite_api.md <suite_api.html>`_ for more info.

HTML result summary
===================

  * simple summary view of results
  * location: `log/<timestamp>/result.html`
  * features collapsible test case containers with links to
    relevant logs
      * **Note**: Some of the logs are only visible under
        the first test case, since they are common for all test cases
        run during the execution.

Console results
===================
Console results look like this::

  +--------------+---------+-------------+-------------+-----------+----------+---------+
  | Testcase     | Verdict | Fail Reason | Skip Reason | Platforms | Duration | Retried |
  +--------------+---------+-------------+-------------+-----------+----------+---------+
  | test_cmdline |   pass  |             |             |  process  | 0.946598 |    No   |
  +--------------+---------+-------------+-------------+-----------+----------+---------+
  +----------------------------+----------------+
  |          Summary           |                |
  +----------------------------+----------------+
  |       Final Verdict        |      PASS      |
  |           count            |       1        |
  |          passrate          |    100.00 %    |
  | passrate excluding retries |    100.00 %    |
  |            pass            |       1        |
  |          Duration          | 0:00:00.946598 |
  +----------------------------+----------------+

************************
Bash command completion
************************

Initial support for bash command completion is
provided in file `bash_completion/clitest`

You can include this file from your `.bashrc` or `.bash_profile`
files like this::

  if [ -f ~/src/mbed-clites/bash_completion/clitest ]; then
    source ~/src/mbed-clites/bash_completion/clitest
  fi


**********
Exit codes
**********

ClitestManager can return four different
kinds of return codes to the command line.
These are EXIT_SUCCESS (0), EXIT_ERROR (1),
EXIT_FAIL(2) and EXIT_INCONC(3).

EXIT_SUCCESS is the default return code when the test run
completed successfully, even if there were failed testcases.
This behaviour can be modified
by setting the --failure_return_value flag. This will cause Mbed-clitest
to return EXIT_FAIL if one or more testcase in the run failed.
When using the --failure_return_value flag and
at least one inconclusive result was collected
and no failed results were found, the return code
will be set to EXIT_INCONC.
Inconclusive results are generated by errors that are not
related to the actual test case, such as environment or
configuration errors.

If an error was encountered during the test run and
the error caused the execution to cease, EXIT_ERROR is returned.
