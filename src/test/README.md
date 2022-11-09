We need good negative tests, because this project is all about testing (and automation, but more about testing).

Start the test with VM option

    -Denvironment=/path/to/your/exampleEnvironment.json

Example linux / ubuntu

    -Denvironment=exampleEnvironment.json
    -Dwebdriver.chrome.driver=../chromedriver

Example windows

    -Denvironment=exampleEnvironment.json
    -Dwebdriver.chrome.driver=../chromedriver.exe