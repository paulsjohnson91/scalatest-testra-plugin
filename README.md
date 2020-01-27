# scalatest-testra-reporter


Add to sbt
    Test / testOptions += Tests.Argument("-C", "org.scalatest.tools.JUnitReporter"),
    Test / testOptions += Tests.Argument("-DtestraApi=http://localhost:8083/api/v1", "-Dproject=CompanionService"),