/**********************************************************************
 * Script to test that version-update worked correctly
 * Enter nothing to compare all version numbers
 * Enter "version number" to test if all versions match this given number
 *
 * 1. Checks that all pom.xml files have the correct version number
 * 2. Checks that all launch4j.xml files have the correct version number
 * 3. Checks updated .exe applications
 * 4. Checks created branches (2) and current branch
 * 5. Checks commit
 *
 * Works with Groovy 1.8.4
 **********************************************************************/

/**********************************************************************
 * Setup variables
 **********************************************************************/

println "Running version-update-test..."

def checkVersion = "."
def presentVersion = "."
def launch4jStatus = true
def pomStatus = true
def branchStatus = true
def commitStatus = true
def exeStatus = true

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
if(args.size() == 1){
    checkVersion = args[0]
    println "Checking for $checkVersion..."
} else if (args.size() == 0) {
    println "Checking that all version numbers are the same..."
    checkVersion = null
    // Gets checkVersion from builder/pom.xml
    new File("Test/pom.xml").eachLine(){line->
        if(line.contains("<version>") && !checkVersion){
            checkVersion = line.toString().replace("<version>", "").replace("</version>", "").trim()
        }
    }
    if (!checkVersion) {
        println "[Error] Could not find a version number in build/pom.xml"
        return
    }
} else {
    println "[Error]  Only input one version number to check."
    return
}

/**********************************************************************
 * 1. Checks that all pom.xml files have the correct version number
 **********************************************************************/

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
    println "     $file"
}
if (pomStatus) {
    println "All pom.xml files have the same version number:  $checkVersion"
} else {
    println "[Error]  All pom.xml files do not have the same version number:  $checkVersion"
    println "[Error]  Problem files:"
    problemPomFiles.any() {file, version ->
        println "[Error]        $file   version:  $version"
    }
}


/**********************************************************************
 * 2. Checks that all launch4j.xml files have the correct version number
 **********************************************************************/

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
    println "     $file"
}
if (launch4jStatus) {
    println "All launch4j.xml files have the same version number:  $checkVersion"
} else {
    println "[Error]  All launch4j.xml files do not have the same version number:  $checkVersion"
    println "[Error]  Problem files:"
    problemLaunch4jFiles.any() {file, version ->
        println "[Error]        $file   version:  $version"
    }
}

/**********************************************************************
 * Print version number change results
 **********************************************************************/

print "\n"
println separator
if (pomStatus && launch4jStatus) {
    println "[Succeeded]  All version numbers are the same: $presentVersion"
} else if (pomStatus) {
    println "[Error]  All launch4j.xml files do not have the same version number"
} else if (launch4jStatus) {
    println "[Error]  All pom.xml files do not have the same version number"
} else {
    println "[Error]  Neither pom.xml nor launch4j.xml files have the same version numbers"
}
println separator
print "\n"

/**********************************************************************
 *  3. Checking updated .exe applications
 **********************************************************************/

println "Checking .exe files for changes..."
def checkExeFiles = "git diff master".execute()
checkExeFiles.waitFor()
def exeResults = checkExeFiles.in.text

def calcResult = "[Succeeded]"
def editorResult = "[Succeeded]"
if (!exeResults.contains("/calc.exe differ")) {
    calcResult = "[Error]"
    exeStatus = false
}
if (!exeResults.contains("/editor.exe differ")) {
    editorResult = "[Error]"
    exeStatus = false
}
println "Checked .exe files for changes."
print "\n"
println separator
println "$calcResult  calc.exe"
println "$editorResult editor.exe"
println separator
print "\n"

/**********************************************************************
 * 4. Check created branches (2) and current branch
 **********************************************************************/

println "Checking git branches..."
def checkLocalBranches = "git branch".execute()
checkLocalBranches.waitFor()
def localBranches = checkLocalBranches.in.text
// get branch name from version number
def branchName
if (!checkVersion.contains("SNAPSHOT")) {
    branchName = checkVersion
} else {
    branchName = checkVersion.replace("-SNAPSHOT", ".x")
}
// check local branch
if (localBranches.contains(branchName)){
    println "Local branch $branchName exists."
} else {
    branchStatus = false
    println "[Error]  Local branch $branchName does not exist."
}
// check current branch is correct
if (localBranches.contains("* $branchName")) {
    println "Correctly moved to $branchName."
} else {
    branchStatus = false
    println "[Error]  Did not move to correct branch $branchName."
}

// check remote branch exists
def checkRemoteBranches = "git branch -r".execute()
checkRemoteBranches.waitFor()
def remoteBranches = checkRemoteBranches.in.text
if (remoteBranches.contains("origin/$branchName")) {
    println "Remote branch origin/$branchName was created."
} else {
    branchStatus = false
    println "[Error]  Did not create the appropriate remote branch origin/$branchName."
}

/**********************************************************************
 * 5. Check commit
 **********************************************************************/

println "Checking commit..."
def checkCommit = "git status".execute()
checkCommit.waitFor()
def commitResult = checkCommit.in.text
if (commitResult.contains("nothing to commit")) {
    println "Commit was made successfully."
} else {
    commitStatus = false
    println "[Error]  Commit was not made successfully."
}

/**********************************************************************
 * Print branch and commit results
 **********************************************************************/

print "\n"
println separator
if (branchStatus && commitStatus) {
    println "[Succeeded]  Branches created and commit made."
} else if (branchStatus) {
    println "[Error]  Commit was not made successfully."
} else if (commitStatus) {
    println "[Error] Branches were not created correctly."
} else {
    println "[Error]  Neither the branches nor the commit are correct."
}
println separator

/**********************************************************************
 * Print all results
 **********************************************************************/

// getting printable results
def pomResult2 = "[Succeeded]"
def launch4jResult2 = "[Succeeded]"
def exeResult2 = "[Succeeded]"
def branchResult2 = "[Succeeded]"
def commitResult2 = "[Succeeded]"
if (!pomStatus) {
    pomResult2 = "[Error]"
}
if (!launch4jStatus) {
    launch4jResult2 = "[Error]"
}
if (!exeStatus) {
    exeResult2 = "[Error]"
}
if (!branchStatus) {
    branchResult2 = "[Error]"
}
if (!commitStatus) {
    commitResult2 = "[Error]"
}

print "\n"
println separator
println "$pomResult2  pom file version numbers: $checkVersion"
println "$launch4jResult2  launch4j file version numbers: $checkVersion"
println "$exeResult2 calc.exe and editor.exe"
println "$branchResult2  branches: $branchName and origin/$branchName"
println "$commitResult2  commit"
println separator
