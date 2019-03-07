# Jenkins Build and Log Reference

This doc explains reference of finding jobs build result from CI.

Please note, for each PR there is only one CI job each time, which has multiple parallel nodes and tasks:

number | CI-slave-node-name | github-status-label | task | function | env config
--- | --- | --- | --- | --- | ---
1 | mesh-test | unittest in linux | run python 2 unittest in linux | `baseBuild("linux")` | N/A
2 | windows | unittest in windows | run python 2 unittest in windows  | `baseBuild("windows")` | N/A
3 | mesh-test | Py3 unittest in Linux | run python 3 unittest in linux | `py3LinuxBuild()` | python 3 virtualenv
4 | mesh-test | plugin tests in linux | run python 2 plugin tests in linux | `baseBuild("linux")` | N/A
5 | windows | plugin tests in windows | run python 2 plugin tests in windows | `baseBuild("windows")` | N/A
6 | oul_ext_lin_nuc | e2e-local-hw-tests in linux | run e2e local hardware tests in linux | `runLinuxHwTests()` | python 2 virtualenv
7 | oul_ext_win_flasher_nuc | e2e-local-hw-tests in windows | run e2e local hardware tests in windows | `runWinHwTests()` | python 2 virtualenv
8 | arm-none-eabi-gcc | build app | build example mbed_cliapp with mbed-os5 | `buildExampleApp()` | N/A
9 | mesh-test | smoke test with nanosimulator | run smoke tests with nanosimulator collected from teams on linux | `smokeBuild()` | download nanosimulator
10 | mesh-test | pylint check | run python 2 pylint check | `pylint_linux_check()` | N/A
11 | N/A | continuous-integration/jenkins/pr-head | CI job result in general | N/A

**Note: continuous-integration/jenkins/branch**

- general build result
- if one of listed above tasks result failed, this one would be failed
- if all of listed above taks result are success, but this one is failed, it means some of post build
actions, like archiving files, publish HTML results, publish junit result, etc is/are failed.


## Key Functions

1. `smokeBuild()`: download nanosimulator, run smoke tests with nanosimulator, publish warnings, archive logs and publish
HTML report.
2. `baseBuild(String platform)`
    - platform value: `windows` or `linux`: means run task on OS linux or windows
    - This function: run unittest, run plugin tests, create coverage report, publish HTML report
3. `buildExampleApp()`: build example cliapp
4. `pylint_linux_check()`: check global installed python version and run pylint check
5. `py3LinuxBuild()` will call `runPy3Unittests()`: run python 3 unittest
6. `runWinHwTests()`: run e2e local hardware tests on windows python 2 virtualenv
7. `runLinuxHwTests()`: run e2e local hardware tests on linux python 2 virtualenv
    - **Note:** becuase of the CI slave is shared
with other jobs, ykush power switch might be turned off, so at here, turn on ykush first, and sleep 1 second to wait power
switch on
8. `setBuildStatus()`: function for set github status label and build result


## Check Logs

In `Build Artifacts`, it collects all the logs needed:

    * example_app   : save example binary and build log
    * log_linux/    : all the html results for unittest and plugin tests run on Linux
    * log_windows/  : all the html results for unittest and plugin tests run on Windows
    * log_smoke/    : test logs for smoke test with nanosimulator
    * pylint.log    : check result of python 2 code style on linux


## HTML Reports

There are HTML reports for tests on each node, which you can find the button below job console output.

 * Windows Build HTML Results
 * Linux Build HTML Report
 * Smoke Build HTML Results
