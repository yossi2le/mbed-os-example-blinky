# clitest

`clitest` is the entry-point for test execution.

## Installation

You can install `clitest` with all required
dependencies easily with this command:

`/> python setup.py install`

If everything goes well you can start tests from any location.
The test cases must be located under the `./testcases`
subfolder, OR you can set the testcase root folder
manually with `clitest` option: `--tcdir`.

### Dependencies

To use `clitest`, make sure that you have the following tools
available in your computer. They should be installed automatically.

* Python (2.7.X or 3.5 and later)
* pip (python package manager)
 * download: https://bootstrap.pypa.io/get-pip.py
 * python get-pip.py
* pip modules
 * pyserial version > 2.5(`pip install pyserial>2.5`
 or https://pypi.python.org/pypi/pyserial)
 * jsonmerge (`pip install jsonmerge`)
 * pyshark (`pip install pyshark`) for Python 3
 * pyshark-legacy(`pip install pyshark-legacy`) for Python 2.7
 * yattag (`pip install yattag`)
 * prettytable (`pip install prettytable`)
 * requests (`pip install requests`)
 * coverage (`pip install coverage`) for unit tests
 * mock  (`pip install mock`) for unit tests
 * psutil (`pip install psutil`)
 * coloredlogs (`pip install coloredlogs`) optional
 * semver (`pip install semver`)
 * six (`pip install six`)
 * mbed-ls (`pip install mbed-ls`)
 * netifaces (`pip install netifaces`) for unit tests
 * pydash (`pip install pydash`)
 * transitions (`pip install transitions`)
 * Mbed-flasher (`pip install mbed-flasher`)

## Folder structure

```
/mbed-clitest> tree
├───doc         // these documents
│   └───UML     // UML diagrams describing different parts of the software
├───doc-source  // these documents in rst format, for use with sphinx
├───examples    // some test case examples
├───log         // default location for test execution logs, when running clitest directly from GIT repository root
├───mbed_clitest    // clitest -libraries
│   ├───PacketShark       // PacketShark libraries
│   ├───Plugins           // Plugin implementation and default plugins
│   ├───Randomize         // Random seed generator implementation
│   ├───DeviceConnectors  // DUT connectors
│   ├───ExtApps           // Backwards compatibility module for previous plugins
│   ├───Extensions        // Backwards compatibility module for previous plugins
│   ├───build             // build object implementation
│   ├───Events            // Event system implementation
│   ├───Reports           // Reporters
│   ├───TestBench         // Test bench implementation
│   ├───tools             // tools and helper libraries
│   ├───ResourceProvider  // Allocators and ResourceProvider
│   ├───TestSuite         // Test runner implementation
│   └───nanosimulator     // Simulator helper library
├───test_regression  // Regression tests
└───test         // unit tests
```

## Example

```
/my-testcases> tree
├───testcases
│   ├───folder
│   │   ├ test3.py
│   ├ test1.py
│   └ test2.py
├───folder2
│   ├ test22.py
└ test.py
/my-testcases> clitest --tc test3
...
/my-testcases> clitest --tcdir folder2 --tc test22
...
```

## Development

Install `clitest` in development mode:

`/> python setup.py develop`

This allows you to modify the source code and debug easily.

## Command line parameters
Command line parameters can be given both over the command line
as well as from a configuration file. The file must be a text file,
which is formatted with one or more parameters per line,
and it can be given to mbed-clitest with command line
parameter --cfg_file. Example configuration file is available in
[examples.](../examples/example_cli_config_file)

**Note**: If you put --cfg_file argument inside a file used as
--cfg_file, don't try to load the same file.
This will cause infinite recursion.
We try to check if the file names match and delete the new --cfg_file
argument if they match, but the check might not be foolproof.

Supported cli parameters are described below:

| Name | Description | Allowed values | Default values | Additional info |
| --- | --- | --- | --- | --- |
| -h | Show help message and exit |  |  |  |
| --listsuites | List all suites found in location set to suitedir (see --suitedir) | | |  |
| --list | List all test cases found in test case search path (see --tcdir) | | |  |
| --json | Print test case list as json instead of a table. Json contains all tc metadata. | | False |  |
| --export | Export listed test cases into a suite file.  | File path | None |  |
| --tc | Filter test cases by test case name. | Test case name as string. If you want to provide several test cases, separate them with a comma. |  |  |
| --suite | Name of suite to run |  |  |  |
| --version | Print mbed-clitest version and exit|  |  |  |
| --clean | Empty the log directory before starting run (see --log) |  | False |  |
| --status | Filter test cases by test status |  |  |  |
| --group | Filter test cases by test group (folder) |  |  |  |
| --testtype | Filter test cases by test type | See [test case api description](tc_api.md) |  |  |
| --subtype | Filter test cases by test subtype | See [test case api description](tc_api.md) |  |  |
| --component | Filter test cases by test component-under-test  |  |  |  |
| --feature | Filter test cases by test feature|  |  |  |
| --platform | Filter test cases by allowed platforms|  |  |  |
| --jobId | job unique id|  |  |  |
| --gitUrl | Set application used git url for results |  |  |  |
| --branch | Set used build branch for results |  |  |  |
| --commitId | Set used commit ID for results |  |  |  |
| --buildDate | Set build date |  |  |  |
| --toolchain | Set toolchain for results |  |  |  |
| --buildUrl | Set build url for results |  |  |  |
| --campaign | Set campaign name for results |  |  |  |
| --tcdir | Test case search directory | Any valid directory path | ./testcases |  |
| --suitedir | Test suite search directory | Any valid directory path | ./testcases/suites |  |
| --simdir | Location of NanoSimulator libraries | Any valid directory path | < parent directory of mbed-clitest >/nanosimulator | |
| --cfg_file | Read command line parameters from file | Any valid path to a configuration file |  |  |
| --env_cfg | Use user specific environment configuration file | Valid file name or path |  |  |
| --repeat | Run test cases N times | Integer | 1 |  |
| --stop_on_failure | Stop run on first failed test case |  |  |  |
| --raas | RaaS server address and port |  | If port is not present in address, 8000 is used |  |
| --raas_pwd | Password for RaaS |  |  |  |
| --raas_user | Username for RaaS |  |  |  |
| --raas_token | Optional access token for RaaS|  |  |  |
| --raas_queue | Use RaaS allocation queue |  |  |  |
| --raas_queue_timeout | Timeout value for RaaS allocation queue in seconds | Integer | 5 |
| --raas_share_allocs | Share allocated RaaS resources between test cases of this run |  |  |
| --allocator | Allocator to be used for allocating resources. | | "LocalAllocator" | |
| --allocator_cfg | File that contains configuration for the allocator used. | | | |
| --plugin_path | Location of file claled plugins_to_load, where custom plugins are imported. | | | |
| --connector | Connector credentials for selecting and/or generating endpoint certificates. |  |  | Format should be domain[:token] where token is optional. Eg. --connector this_is_some_domain:this_is_my_token |
| --failure_return_value | Set mbed-clitest to return a failing code to caller if one or more tests fail during the run. Otherwise return value will always be 0 |  |  |  |
| --ignore_invalid_params | Ignore parameters mbed-clitest cannot parse instead of stopping the run (for backwards compatibility) |  |  |  |
| --parallel_flash | Enable parallel flashing of devices | "True" or "False" | "True". "False" if simulator is used. |  |
| --disable_log_truncate | Disable long log lines truncating. Over 10000 characters long lines are truncated by default. |  |  |  |
| --log | Store logs to a specific path. Filename will be <path>/<testcase>_D<dutNumber>.log | Any valid directory | ./log |  |
| -s, --silent | Enable silent-mode (only results and WARNING/ERROR levels will be printed to console) |  |  |  |
| -v | Control verbosity of console logging. Use -v to move internal loggers from INFO to DEBUG and external loggers from WARNING to INFO. Use -vv to increase external loggers to DEBUG also.|  |  |  |
| -w | Store results to the cloud |  |  |  |
| --cm | Used cloud provider | | opentmi-client |
| --with_logs | Send bench.log file to the cloud when -w is used. |  |  |  |
| --reset | Reset device before executing test cases | Hard, soft or can be left empty | If left empty defaults to soft |  |
| --iface | Used NW sniffer interface name |  |  |  |
| --bin | Used binary for DUTs when process/hardware is used. NOTE: Does not affect duts which specify their own binaries | Valid file name or path |  |  |
| --skip_flash | Skip flashing duts.|  | Mutually exclusive with forceflash and forceflash_once |
| --tc_cfg | Test case configuration file, loaded into the configuration last at the start of the test. | Valid file name or path |  |  |
| --ch | Use specific rf channel |  |  |  |
| --type | Overrides DUT type | hardware, process, simulate, serial or mbed |  |  |
| --platform_name | Overides used platform. Must be found in allowed_platforms in dut configuration if allowed_platforms is defined and non-empty |  |  |  |
| --putty | Open putty after TC executed |  |  |  |
| --skip_setup | Skip test case setup phase |  |  |  |
| --skip_case | Skip test case case function|  |  |  |
| --skip_teardown | Skip test case teardown phase |  |  |  |
| --valgrind | Analyze nodes with valgrind (linux only) |  |  |  |
| --valgrind_tool | Valgrind tool to use | memcheck, callgrind, massif |  |  |
| --valgrind_extra_params | Additional command line parameters for Valgrind |  |  |  |
| --valgrind_text | Output as text.|  |  | Mutually exclusive with --valgrind_console |
| --valgrind_console | Output as text to console.|  |  | Mutually exclusive with --valgrind_text |
| --valgrind_track_origins | Show origins of undefined values. |  |  | Used only if the Valgrind tool is memcheck |
| --use_sniffer | Use network sniffers defined in tc requirements. If none are specified, create default sniffer. |  |  |  |
| --my_duts | Use only some of the duts | Dut index numbers separated by commas |  |  |
| --pause_ext | Pause when external device command happens |  |  |  |
| --nobuf | Do not use stdio buffers in simulated node process |  |  |  |
| --gdb | Run specific simulate node with gdb debugger| Integer |  | Mutually exclusive with --gdbs and --vgdb |
| --gdbs | Run specific simulate node with gdb server | Integer |  | Mutually exclusive with --gdb and --vgdb  |
| --vgdb | Run specific simulate node with vgdb (debugger under Valgrind) | Integer |  | Mutually exclusive with --gdb and --gdbs  |
| --gdbs-port | Select gdbs port | Integer |  | 2345 |
| --pre-cmds | Send extra commands right after dut connection |  |  |  |
| --post-cmds | Send extra commands right before terminating dut connection |  |  |  |
| --baudrate | Use user defined serial baudrate when serial device is in use. | Integer |  |  |
| --serial_timeout | User defined serial timeout | Float | 0.01 |  |
| --serial_xonxoff | Use software flow control |  |  | |
| --serial_rtscts | Use hardware flow control |  |  | |
| --serial_ch_size | Use chunk mode with size N when writing to serial port  | Integer, -1 for pre-defined mode, N=0 for normal mode, N>0 chunk mode with size N |  |  |
| --serial_ch_delay | Use defined delay between characters. Used only when serial_ch_size > 0  | Float | 0.01 |  |
| --kill_putty | Kill old putty/kitty processes |  |  |  |
| --simulator_event_log | Enable simulator event output log into a file |  |  |  |
| --forceflash | Force flashing of hardware devices if binary is given. |  |  | Mutually exclusive with forceflash_once |
| --forceflash_once | Force flashing of hardware devices if binary is given, but only once. |  |  | Mutually exclusive with forceflash and skip_flash |
| --interface | Network interface used in tests, unless the test case specifies which one to use. |  | eth0 |  |
| --simulator_step_len | Simulator step length in microseconds | Integer | 1 |  |
| --sync_start | Make sure dut applications have started using 'echo' command. | Boolean | False |  |

## Running
To run tests you first need to have the test cases in valid python modules.
There are two ways to select which test cases to run: suites or filters.
When using suites Mbed-clitest will search for test cases based on their name
as described in the suite file. This is described in more detail in
[suite_api.md](suite_api.md).

Mbed-clitest also supports filtering test cases by their metadata.
All the available filters are described in the table above.
The filters are provided on the command line in string format and
they support basic boolean logic using keywords "and, or, not" and
grouping by parenthesis. If you want to use filter values with
multiple words, surround them with single quotes (').
Example: --feature "feature1 and 'feature2 subfeature2'"

## Results
Clitest creates junit, html and console reports on the run. These are
described in more detail below. HTML and JUnit results are located
in the log directory of the run.

**NOTE**: If you run clitest instances in parallel using the same
location for logging, the links to the latest result files might not
be very useful, as they might with bad luck point to different runs,
depending on scheduling and other timing factors.

### junit

  * common xml format which are suitable for example with Jenkins
  [test_results_analyzer](https://github.com/jenkinsci/test-results-analyzer-plugin)
  -plugin
  * location: `log/<timestamp>/result.junit.xml`
  * format is:
    ```
    <testsuite failures="0" tests="1" errors="0" skipped="0">
        <testcase classname="<test-name>.<platform>" name="<toolchain>" time="12.626"></testcase>
    </testsuite>
    ```

**NOTE**

The JUnit file is generated slightly differently
from the other reports due to CI.
If the run used the clitest retry mechanism to retry failed
or inconclusive test cases, only the final attempt
is displayed in the JUnit report. The failed tries are displayed
in the other reports as normal.
This functionality can be configured using the
retryReason parameter in the suite.
See [suite api](suite_api.md) for more info.

### html result summary
  * simple summary view of results
  * location: `log/<timestamp>/result.html`

### console results
  ```
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
  ```

## Bash command completion

Initial support for bash command completion is provided in file
`bash_completion/clitest`

You can include this file from your `.bashrc` or
`.bash_profile` files like this:

~~~
if [ -f ~/src/mbed-clites/bash_completion/clitest ]; then
  source ~/src/mbed-clites/bash_completion/clitest
fi
~~~

## Exit codes

ClitestManager can return four different kinds
of return codes to the command line. These are EXIT_SUCCESS (0),
EXIT_ERROR (1), EXIT_FAIL(2) and EXIT_INCONC(3).

EXIT_SUCCESS is the default return code when the test run
completed successfully, even if there were failed testcases.
This behaviour can be modified by setting the --failure_return_value
flag. This will cause clitest to return EXIT_FAIL if one
or more testcase in the run failed.
When using the --failure_return_value flag and only passed or
inconclusive results appear in the final results,
the return code will be set to EXIT_INCONC.
Inconclusive results are generated by errors that are not related
to the actual test case, such as environment or configuration errors.

If an error was encountered during the test run and the error
caused the execution to cease, EXIT_ERROR is returned.
