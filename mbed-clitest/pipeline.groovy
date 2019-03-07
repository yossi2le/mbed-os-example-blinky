def setBuildStatus(String state, String context, String message) {
    step([
        $class: "GitHubCommitStatusSetter",
        reposSource: [
            $class: "ManuallyEnteredRepositorySource",
            url: "https://github.com/ARMmbed/mbed-clitest.git"
        ],
        contextSource: [
            $class: "ManuallyEnteredCommitContextSource",
            context: context
        ],
        errorHandlers: [[
            $class: "ChangingBuildStatusErrorHandler",
            result: "UNSTABLE"
        ]],
        commitShaSource: [
            $class: "ManuallyEnteredShaSource",
            sha: env.GIT_COMMIT_HASH
        ],
        statusResultSource: [
            $class: 'ConditionalStatusResultSource',
            results: [
                [
                    $class: 'AnyBuildResult',
                    message: message,
                    state: state
                ]
            ]
        ]
     ])
}


def smokeBuild() {
    dir ('mbed-clitest'){
    }

    echo "Copying artifacts from nanosimulator-private-make-merge build"

    def buildSelector = [$class: 'StatusBuildSelector', stable: true]
    if (env.CLINODE_BUILD_NUMBER) {
        buildSelector = [$class: 'SpecificBuildSelector', buildNumber: env.CLINODE_BUILD_NUMBER]
    }

    // copy artifacts
    step([
        $class: 'CopyArtifact',
        fingerprintArtifacts: true,
        flatten: true,
        projectName: "nanosimulator-private-make-merge",
        selector: buildSelector,
        target: 'nanosimulator/'
    ])

    String buildName = 'smoke test with nanosimulator'
    setBuildStatus('PENDING', "${buildName}", 'build start')
    try {
        stage("Execute smoke script") {
            sh "mbed-clitest/smoke.sh"
        }
        setBuildStatus('SUCCESS', "${buildName}", "build and generate report done")
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${buildName}", "some failures")
        currentBuild.result = 'FAILURE'
    } finally {
        stage("smoke post build actions") {
            catchError {
                step([
                    $class: 'WarningsPublisher',
                    consoleParsers: [
                        [
                            parserName: 'GNU Make + GNU C Compiler (gcc)'
                        ]
                    ]
                ])
            }

            catchError {
                execute "cp -R mbed-clitest/log/ log_smoke/"
            }

            catchError {
                // archive the artifacts
                archiveArtifacts artifacts: "log_smoke/**/*.*"
            }

             /* not yet supported by pipeline
             *step([
             *   $class: 'TextFinderPublisher',
             *   fileSet: "mbed-clitest/log/result.html",
             *   regexp: "fail<",
             *   succeedIfFound: False,
             *   unstableIfFound: True,
             *   alsoCheckConsoleOutput: False
             *])
            */

            // publish HTML reports
            catchError {
                publishHTML(target: [
                    allowMissing: false,
                    alwayLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: "log_smoke",
                    reportFiles: "latest.html",
                    reportName: "Smoke Build HTML Results"
                ])
            }

            /*
            *step([
            *    $class: 'JUnitResultArchiver'
            *    testResults: 'mbed-clitest/log/result.junit.xml',
            *    keepLongStdio: False,
            *    healthScaleFactor: 1.0
            *    testDataPublishers: [
            *        [$class: 'AttachmentPublisher'],
            *        [$class: 'StabilityTestDataPublisher']
            *    ]
            *])
            */

            //Todo: add testDataPublishers, healthScaleFactor: 1.0
            catchError {
                junit testResults: "log_smoke/**/result.junit.xml", healthScaleFactor: 1.0
            }

            //Todo: add trigger parameterized build on other projects
        }
    }
}


def create_linux_venv() {
     stage ("create linux venv") {
        sh """
            set -e
            echo "linux python2 venv installation starts"
            virtualenv --python=../usr/bin/python py2venv --no-site-packages
            . py2venv/bin/activate
            id
            pip install coverage mock astroid==1.5.3 pylint==1.7.2
            pip freeze
            deactivate
            echo "linux python2 venv installation success"
        """

        sh """
            set -e
            echo "linux python3 venv installation starts"
            virtualenv --python=\$(which python3) py3venv --no-site-packages
            . py3venv/bin/activate
            pip install coverage mock lxml
            id
            pip freeze
            deactivate
            echo "linux python3 venv installation success"
        """
     }
}


def create_windows_venv() {
     stage ("create windows venv") {
        /*
        bat """
            echo "windows python3 venv installation starts"
            c:\\Python36\\python.exe -m venv py3venv || goto :error
            echo "Activating venv"
            call py3venv\\Scripts\\activate.bat || goto :error
            pip install coverage mock || goto :error
            if %errorlevel% neq 0 exit /b %errorlevel%
            pip freeze
            deactivate
            :error
            echo "Failed with error %errorlevel%"
            exit /b %errorlevel%
        """
        */

        // IOTSYSTOOL-1071, workaround: pip install pyshark-legacy
        bat """
            where virtualenv
            echo "windows python2 venv installation starts"
            virtualenv --python=C:\\mbed_tools\\Python27\\python.exe py2venv --no-site-packages || goto :error
            echo "Activating venv"
            call py2venv\\Scripts\\activate.bat || goto :error
            pip install coverage mock pyshark-legacy || goto :error
            pip freeze
            deactivate
            :error
            echo "Failed with error %errorlevel%"
            exit /b %errorlevel%
        """
    }
}


def baseBuild(String platform, String pythonVersion) {
    execute 'make'

    def venvName = "py2venv"

    if (pythonVersion == "python3") {
        venvName = "py3venv"
    }

    unittest(platform, pythonVersion, venvName)

    pluginTest(platform, pythonVersion, venvName)

    createCoverageReport(platform, pythonVersion, venvName)

    raas_e2e_test(platform, pythonVersion, venvName)
}


def unittest(String platform, String pythonVersion, String venvName) {
    // run clitest unittest
    String buildName = "${platform} - ${pythonVersion} - unittest"

    setBuildStatus('PENDING', "${buildName}", 'unittest start')
    try {
        stage("${buildName}") {
            if(isUnix()) {
                sh """
                    set -e
                    . ${venvName}/bin/activate
                    python setup.py install
                    coverage run --parallel-mode -m unittest discover -s test
                    deactivate
                """
            } else {
                bat """
                    call ${venvName}\\Scripts\\activate.bat || goto :error
                    python setup.py install || goto :error
                    coverage run --parallel-mode -m unittest discover -s test || goto :error
                    deactivate

                    :error
                    echo "Failed with error %errorlevel%"
                    exit /b %errorlevel%
                """
            }
        }
        setBuildStatus('SUCCESS', "${buildName}", 'unittest success')
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${buildName}", "unittests didn't pass")
        currentBuild.result = 'FAILURE'
    }
}


def pluginTest(String platform, String pythonVersion, String venvName) {
    // run plugin tests
    String pluginBuildName = "${platform} - ${pythonVersion} - plugin tests"

    setBuildStatus('PENDING', "${pluginBuildName}", 'plugin tests start')
    try {
        stage("${pluginBuildName}") {
            if(isUnix()) {
                sh """
                    set -e
                    . ${venvName}/bin/activate
                    cd ${venvName}
                    git clone https://github.com/ARMmbed/raas-pyclient.git
                    cd raas-pyclient
                    python setup.py install
                    cd ../../
                    coverage run --parallel-mode -m unittest discover -s mbed_clitest/Plugin/plugins/plugin_tests -v
                    deactivate
                """
            } else {
                bat """
                    call ${venvName}\\Scripts\\activate.bat || goto :error
                    git clone https://github.com/ARMmbed/raas-pyclient.git
                    cd raas-pyclient
                    python setup.py install
                    cd ..
                    coverage run --parallel-mode -m unittest discover -s mbed_clitest/Plugin/plugins/plugin_tests -v || goto:error
                    deactivate

                    :error
                    echo "Failed with error %errorlevel%"
                    exit /b %errorlevel%
                """
            }
        }
        setBuildStatus('SUCCESS', "${pluginBuildName}", 'plugin tests success')
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${pluginBuildName}", "plugin tests didn't pass")
        currentBuild.result = 'FAILURE'
    }
}


def createCoverageReport(String platform, String pythonVersion, String venvName){
    if (pythonVersion == "python2") {
        // Generate Coverage report
        if(isUnix()) {
            sh """
                set -e
                . ${venvName}/bin/activate
                coverage combine --append
                coverage html --include='*mbed_clitest*' --directory=log_${platform}
                coverage xml --include='*mbed_clitest*' -o log_${platform}/coverage.xml
                deactivate
            """
        } else {
            bat """
                call ${venvName}\\Scripts\\activate.bat || goto :error
                coverage combine --append || goto :error
                coverage html --include='*mbed_clitest*' --directory=log_${platform} || goto :error
                coverage xml --include='*mbed_clitest*' -o log_${platform}/coverage.xml || goto :error
                deactivate

                :error
                echo "Failed with error %errorlevel%"
                exit /b %errorlevel%
            """
        }

        postBuild(platform)
    }
}


def postBuild(String platform) {
    // Archive artifacts
    catchError {
        archiveArtifacts artifacts: "log_${platform}/*.*"
    }


    if (platform == 'linux') {
        catchError {
            // pylint check
            pylint_linux_check()

            // publish warnings checked for console log and pylint log
            warningPublisher('PyLint', '**/pylint.log')
            archiveArtifacts artifacts: "**/pylint.log"
        }

        catchError {
            // Publish cobertura
            step([
                $class: 'CoberturaPublisher',
                coberturaReportFile: 'log_linux/coverage.xml'
            ])
        }
    }


    // Publish HTML reports
    publishHTML(target: [
        allowMissing: false,
        alwayLinkToLastBuild: false,
        keepAll: true,
        reportDir: "log_${platform}",
        reportFiles: "index.html",
        reportName: "${platform} Build HTML Report"
    ])
}


def raas_e2e_test(String platform, String pythonVersion, String venvName) {
    // run raas-e2e tests
    String pluginBuildName = "${platform} - ${pythonVersion} - raas-e2e tests"

    setBuildStatus('PENDING', "${pluginBuildName}", 'raas-e2e tests start')
    try {
        stage("${pluginBuildName}") {
            if(isUnix()) {
                sh """
                    set -e
                    . ${venvName}/bin/activate
                    pip install --upgrade opentmi-client
                    python test_regression/test_regression.py \
                    --raas https://ruka.mbedcloudtesting.com \
                    --raas_user "user" \
                    --raas_pwd "user"
                    deactivate
                """
            } else {
                bat """
                    call ${venvName}\\Scripts\\activate.bat || goto :error
                    pip install --upgrade opentmi-client
                    python test_regression\\test_regression.py \
                    --raas https://ruka.mbedcloudtesting.com/ \
                    --raas_user "user" --raas_pwd "user" || goto :error
                    deactivate

                    :error
                    echo "Failed with error %errorlevel%"
                    exit /b %errorlevel%
                """
            }
        }
        setBuildStatus('SUCCESS', "${pluginBuildName}", 'plugin tests success')
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${pluginBuildName}", "plugin tests didn't pass")
        currentBuild.result = 'FAILURE'
    }


}
def buildExampleApp() {
    // build app
    String buildName = "build app"
    setBuildStatus('PENDING', "${buildName}", 'start')
    try{
        dir ("examples/cliapp/mbed-os5") {
            sh "mkdir example-app"
            sh "mbed deploy -v"
            sh "mbed compile -t GCC_ARM -m K64F --build BUILD/output | tee example-app/build.log"
            sh "cp BUILD/output/mbed-os5.elf example-app/"
            sh "cp BUILD/output/mbed-os5.bin example-app/"
            archiveArtifacts artifacts: "example-app/**/*"
        }
        setBuildStatus('SUCCESS', "${buildName}", 'success')
    } catch (err) {
        // set build fail
        setBuildStatus('FAILURE', "${buildName}", "fail")
        currentBuild.result = 'FAILURE'
    }
}


def pylint_linux_check() {
    // run pylint check
    String pylintBuildName = "pylint check"

    setBuildStatus('PENDING', "${pylintBuildName}", 'start')
    try {
        echo "REST OF THESE ARE FOR PYLINT"
        sh 'pylint --version'
        sh 'pylint ./setup.py ./clitest.py ./mbed_clitest ./test ./examples > pylint.log'
        setBuildStatus('SUCCESS', "${pylintBuildName}", 'done')
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${pylintBuildName}", '')
        currentBuild.result = 'FAILURE'
    }
}


def warningPublisher(String parser, String pattern) {
    step([
        $class: 'WarningsPublisher',
        consoleParsers: [
            [
                parserName: 'GNU Make + GNU C Compiler (gcc)'
            ]
        ],
        parserConfigurations: [
            [
                parserName: parser,
                pattern: pattern
            ]
        ]
    ])

}


def runLinuxHwTests(String pythonVersion){
    // Run hardware e2e tests with local devices
    String buildName = "e2e-local-hw-tests - linux - ${pythonVersion}"

    setBuildStatus('PENDING', "${buildName}", 'start')
    try {
        sh "ykushcmd -u a"
        stage("${buildName}") {
            if (pythonVersion == "python3") {
                sh """
                    set -e
                    virtualenv -p python3 py3venv --no-site-packages
                    . py3venv/bin/activate
                    python -m pip install --upgrade pip
                    pip install coverage mock
                    python setup.py install
                    ykushcmd -u a
                    sleep 1
                    python test_regression/test_regression.py
                    deactivate
                """
            } else {
                sh """
                    set -e
                    virtualenv --python=../usr/bin/python py2venv --no-site-packages
                    . py2venv/bin/activate
                    pip install coverage mock lxml
                    python setup.py install
                    ykushcmd -u a
                    sleep 1
                    python test_regression/test_regression.py
                    deactivate
                """
            }
        }
        setBuildStatus('SUCCESS', "${buildName}", 'success')
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${buildName}", "didn't pass")
        currentBuild.result = 'FAILURE'
    }
}

def runWinHwTests(String pythonVersion){
    // Run e2e-local-hw-tests on win-nuc
    String buildName = "e2e-local-hw-tests - windows - ${pythonVersion}"
    // IOTSYSTOOL-1071, workaround: pip install pyshark-legacy
    setBuildStatus('PENDING', "${buildName}", 'start')
    try {
        bat "c:\\32a31_ykushcmd_rev1.1.0\\ykushcmd\\bin\\ykushcmd.exe -u a"
        stage("${buildName}") {
            if (pythonVersion == "python3") {
                bat """
                    c:\\Python36\\python.exe -m venv py3venv || goto :error
                    echo "Activating venv"
                    call py3venv\\Scripts\\activate.bat || goto :error
                    pip install coverage mock lxml|| goto :error
                    pip freeze
                    python setup.py install  || goto :error
                    python test_regression/test_regression.py || goto :error
                    deactivate

                    :error
                    echo "Failed with error %errorlevel%"
                    exit /b %errorlevel%
                """
            } else {
                bat """
                    virtualenv --python=c:\\Python27\\python.exe py2venv --no-site-packages || goto :error
                    echo "Activating venv"
                    call py2venv\\Scripts\\activate.bat || goto :error
                    pip install coverage mock lxml pyshark-legacy|| goto :error
                    pip freeze
                    python setup.py install  || goto :error
                    python test_regression/test_regression.py || goto :error
                    deactivate

                :error
                echo "Failed with error %errorlevel%"
                exit /b %errorlevel%
                """
            }
        }
        setBuildStatus('SUCCESS', "${buildName}", 'success')
    } catch (Exception e) {
        // set build fail
        setBuildStatus('FAILURE', "${buildName}", "didn't pass")
        currentBuild.result = 'FAILURE'
    }
}

return this;
