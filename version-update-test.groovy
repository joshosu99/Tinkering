/**********************************************************************
 * Script to test that calcIncrementVersion incremented correctly
 * Enter nothing to compare all version numbers
 * Enter "version number" to test if all versions match this given number
 * Enter "list" if you want the list of all files that are checked
 * Enter "version number" and "list" if you want to check all of the files
 *      for a given version number and you want the list of all files checked
 *
 * 1. Checks that all pom.xml files have the correct version number
 * 2. Checks that all launch4j.xml files have the correct version number
 *
 * Works with Groovy 1.8.4
 **********************************************************************/

/**********************************************************************
 * Setup variables
 **********************************************************************/

println "Checking version numbers..."

def checkVersion = "."
def presentVersion = "."
def listOptionOn = false
def launch4jStatus = true
def pomStatus = true

def pomFiles = [
        new File("Test/pom.xml"),
        new File("Test/Test2/pom.xml")
]

def launch4jFiles = [
        new File("Test/launch4j.xml"),
        new File("Test/Test2/launch4j.xml")
]

//// list of all pom.xml files to be checked
//def pomFiles = [
//        new File("../../../../../builder/pom.xml"),
//        new File("../../../../../calc/pom.xml"),
//        new File("../../../../../commons/pom.xml"),
//        new File("../../../../../editor/pom.xml"),
//        new File("../../../../../engine/pom.xml"),
//        new File("../../../../../gps/pom.xml"),
//        new File("../../../../../jagatoo/pom.xml"),
//        new File("../../../../../launcher/pom.xml"),
//        new File("../../../../../mappingproxy/pom.xml"),
//        new File("../../../../../mapviewer/pom.xml"),
//        new File("../../../../../project/pom.xml"),
//        new File("../../../../../rendering/pom.xml"),
//        new File("../../../../../xith3d/pom.xml")
//]
//
//// list of all launch4j.xml files to be checked
//def launch4jFiles = [
//        new File("calc-launch4j.xml"),
//        new File("editor-launch4j.xml")
//]

def separator = "*"*70

/**********************************************************************
 * Check ARGS
 **********************************************************************/
if(args.size() == 1 && args[0] != "list"){
    checkVersion = args[0]
    println "Checking for $checkVersion"
} else if (args.size() == 2 && args[1] == "list") {
    checkVersion = args[0]
    println "Checking for $checkVersion"
    listOptionOn = true
} else if (args.size() == 0 || (args.size() == 1 && args[0] == "list")) {
    println "Checking that all version numbers are the same"
    checkVersion = null
    // Gets checkVersion from builder/pom.xml
    new File("Test/pom.xml").eachLine(){line->
        if(line.contains("<version>") && !checkVersion){
            checkVersion = line.toString().replace("<version>", "").replace("</version>", "").trim()
        }
    }
    if (!checkVersion) {
        println "[Error] Could not find a version number in build/pom.xml"
        checkVersion = "..........."
    }
    if (args.size() == 1 && args[0] == "list") {
        listOptionOn = true
    }
} else {
    println "[Error]  Only input one version number to check."
}

/**********************************************************************
 * 1. Checks that all pom.xml files have the correct version number
 **********************************************************************/
if (checkVersion != "...........") {
    println "Checking pom.xml files..."
    def problemPomFiles = [:];
    pomFiles.each{file->
        def checkedFirst = false
        presentVersion = "."
        def lines = file.readLines()
        lines.each{line->
            if(line.contains("<version>") && !checkedFirst){
                presentVersion = line.toString().replace("<version>", "").replace("</version>", "").trim()
                checkedFirst = true
            }
        }
        if (presentVersion != checkVersion) {
            pomStatus = false
            problemPomFiles.put(file, presentVersion)
        }
        if (listOptionOn) {
            println "     $file"
        }
    }
    if (pomStatus) {
        println "All pom.xml files have the same version number:  $checkVersion"
    } else {
        println "[Error]  All pom.xml files do not have the same version number:  $checkVersion"
        println "[Error]  Problem files are:"
        problemPomFiles.any() {file, version ->
            println "               $file version:  $version"
        }
    }
}

/**********************************************************************
 * 2. Checks that all launch4j.xml files have the correct version number
 **********************************************************************/
if (checkVersion != "...........") {
    println "Checking launch4j.xml files..."
    def problemLaunch4jFiles = [:];
    launch4jFiles.each{file->
        def checkedFirst = false
        presentVersion = "."
        def lines = file.readLines()
        lines.each{line->
            if(line.contains("<jar>") && !checkedFirst){
                presentVersion = line.toString().replace("<jar>jars/launcher-", "").replace(".jar</jar>", "").trim()
                checkedFirst = true
            }
        }
        if (presentVersion != checkVersion) {
            launch4jStatus = false
            problemLaunch4jFiles.put(file, presentVersion)
        }
        if (listOptionOn) {
            println "     $file"
        }
    }
    if (launch4jStatus) {
        println "All launch4j.xml files have the same version number:  $checkVersion"
    } else {
        println "[Error]  All launch4j.xml files do not have the same version number:  $checkVersion"
        println "[Error]  Problem files are:"
        problemLaunch4jFiles.any() {file, version ->
            println "               $file version:  $version"
        }
    }
}

/**********************************************************************
 * Print result
 **********************************************************************/

print "\n"
println separator
println "All appropriate files checked"
if (checkVersion == "...........") {
    println "[Error]  Could not find a version number in build/pom.xml"
} else if (pomStatus && launch4jStatus) {
    println "[Succeeded]  All version numbers are the same"
} else if (pomStatus) {
    println "[Error]  All launch4j.xml files do not have the same version number"
} else if (launch4jStatus) {
    println "[Error]  All pom.xml files do not have the same version number"
} else {
    println "[Error]  Neither pom.xml nor launch4j.xml files have the same version numbers"
}
println separator