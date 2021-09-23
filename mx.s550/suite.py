suite = {
  "mxversion" : "5.175.4",
  "name" : "s550",
  "versionConflictResolution" : "latest",

  "javac.lint.overrides" : "none",

  "licenses" : {
    "GPLv3" : {
      "name" : "GNU General Public License, version 3",
      "url" : "https://opensource.org/licenses/GPL-3.0",
    }
  },

  "defaultLicense" : "GPLv3",

  "projects" : {

    "org.hackyourlife.s550" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "checkstyle" : "org.hackyourlife.s550",
      "javaCompliance" : "1.8+",
      "workingSets" : "s550",
      "license" : "GPLv3",
    },

    "org.hackyourlife.s550.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "org.hackyourlife.s550",
        "mx:JUNIT",
      ],
      "checkstyle" : "org.hackyourlife.s550",
      "javaCompliance" : "1.8+",
      "workingSets" : "s550",
      "license" : "GPLv3",
    }

  },

  "distributions" : {
    "S550" : {
      "path" : "build/s550.jar",
      "subDir" : "s550",
      "sourcesPath" : "build/s550.src.zip",
      "mainClass" : "org.hackyourlife.s550.ui.MainWindow",
      "dependencies" : [
        "org.hackyourlife.s550"
      ],
    },

    "S550_TEST" : {
      "path" : "build/s550_test.jar",
      "subDir" : "s550",
      "sourcesPath" : "build/s550_test.src.zip",
      "dependencies" : [
        "org.hackyourlife.s550.test"
      ],
      "exclude" : [
        "mx:JUNIT"
      ],
      "distDependencies" : [
        "s550:S550"
      ]
    }
  }
}
