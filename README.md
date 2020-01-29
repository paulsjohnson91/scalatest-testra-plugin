# TestraReporter

TestraReporter is a scalatest plugin used to report test results to a testra api

## Installation

In your gradle file in testCompile
```gradle
'io.testra:plugins_2.12:0.1.1'
```
In sbt
```sbt
libraryDependencies += "io.testra" % "plugins_2.12" % "0.1.1",
```

## Usage
Example gradle task
```gradle
task scalaTestRunner(dependsOn: ['testClasses'], type: JavaExec) {
    main = 'org.scalatest.tools.Runner'
    args = ['-R', 'build/classes/scala/test'
            "-C", "io.testra.plugins.TestraReporter",
            "-Dtestra=true","-DtestraApi=http://localhost:8083/api/v1",
            "-Dproject=ScalaTestProject"]

    classpath = sourceSets.test.runtimeClasspath
}
```
Example in build.sbt
```sbt
    Test / testOptions += Tests.Argument("-C", "io.testra.plugins.TestraReporter"),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
     Test / testOptions += Tests.Argument("-Dtestra=true",
       "-DtestraApi=http://localhost:8083/api/v1",
       "-Dproject=ScalaTestProject", "-Dhost=myhost",
       "-Ddomain=mydomain","-Dbranch=mybranch",
       "-Denv=myenv", "-DbuildRef=mybuildref",
       "-Dtags=tags"),
```
Running gradle scalaTestRunner or sbt test will now submit your test results to the project name ScalaTestProject

Please see [here](https://github.com/paulsjohnson91/testra-k8s) for deployment yamls for testra
