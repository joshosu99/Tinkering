/**********************************************************************
 * Script to increment calc JAR files.
 *
 * Enter original and new to replace all version numbers.
 * Enter nothing to go from SNAPSHOT to number ending .0, or
 * enter nothing to increment final decimal number.
 *
 * 1. Change pom.xml file version numbers
 * 2. Change launch4j.xml file version numbers
 *
 * Works with Groovy 1.8.4
 **********************************************************************/

/**********************************************************************
 * Setup variables
 **********************************************************************/

println "Incrementing calc version numbers..."

def oldVersion = "."
def newVersion = "."
def newBranch = "."
def changeOption = true

def pomFiles = [
        new File("Test/pom.xml"),
        new File("Test/Test2/pom.xml")
]

def launch4jFiles = [
        new File("Test/launch4j.xml"),
        new File("Test/Test2/launch4j.xml")
]

//// list of all pom.xml files to be changed
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
//// list of launch4j.xml files to be changed
//def launch4jFiles = [
//        new File("calc-launch4j.xml"),
//        new File("editor-launch4j.xml")
//]

def separator = "*"*70

/**********************************************************************
 * Check ARGS and get oldVersion and newVersion
 **********************************************************************/

if(args.size()== 1){
    // use to input to get new branch name
    newBranch = args[0]

    // get newVersion from the input depending on if it is a number or ends with x
    // Need to change newVersion to end in -SNAPSHOT if it ends with an x for jar files
    newVersion = args[0]
    if (newVersion.endsWith(".x")) {
        newVersion = newVersion.replace(".x", "-SNAPSHOT")
    }

    // get oldVersion number from builder/pom.xml
    oldVersion = null
    new File("Test/pom.xml").eachLine(){line->
        if(line.contains("<version>") && !oldVersion){
            oldVersion = line.toString().replace("<version>", "").replace("</version>", "").trim()
        }
    }
    println "Old version $oldVersion will be updated to $newVersion"
}else {
    println "[Error]     No changes made.  Enter 1 parameter - version numbers will be changed to this."
    return
}

/**********************************************************************
 * If git branch doesn't exist make branch and go to it, else fail
 **********************************************************************/

def getBranches = "git branch -r".execute()
getBranches.waitFor()
def branches = getBranches.in.text
if (branches.contains(newBranch)) {
    println "[Error]     Branch $newBranch already exists."
    println "[Error]     Either delete the preexisting branch or change to another version number."
    return
} else {
    def makeBranch = "git checkout -b $newBranch".execute()
    makeBranch.waitFor()
    println "New branch $newBranch was made and moved to."
}

/**********************************************************************
 * 1. Change pom.xml file version numbers
 **********************************************************************/

pomFiles.each{file->
    println "updating $file..."
    def firstChanged = false
    def lines = file.readLines()
    file.withWriter{w->
        lines.each{line->
            if (!firstChanged) {
                //these should be all the matching version numbers for the pom files
                def lineAfter = line.replace("<version>"+oldVersion+"</version>","<version>"+newVersion+"</version>")
                if(line!=lineAfter) {
                    def originLine = line.trim()
                    def changedLine = lineAfter.trim()
                    println "     $originLine -> $changedLine"
                    firstChanged = true
                }
                w.writeLine(lineAfter)
            } else {
                w.writeLine(line)
            }
        }
    }
}

///**********************************************************************
// * Print pom.xml finish
// **********************************************************************/
//
//print "\n"
//println separator
//println "Changed pom.xml project file versions"
//println separator
//print "\n"

/**********************************************************************
 * 2. Change launch4j.xml file version numbers
 **********************************************************************/

launch4jFiles.each{file->
    println "updating $file..."
    def firstChanged = false
    def lines = file.readLines()
    file.withWriter{w->
        lines.each{line->
            if (!firstChanged) {
                //these should be all the matching version numbers in the launch4j.xml files
                def lineAfter = line.replace("<jar>jars/launcher-"+oldVersion+".jar</jar>","<jar>jars/launcher-"+newVersion+".jar</jar>")
                if(line!=lineAfter) {
                    def originLine = line.trim()
                    def changedLine = lineAfter.trim()
                    println "     $originLine -> $changedLine"
                    firstChanged = true
                }
                w.writeLine(lineAfter)
            } else {
                w.writeLine(line)
            }
        }
    }
}

///**********************************************************************
// * Print launch4j.xml finish
// **********************************************************************/
//
//print "\n"
//println separator
//println "Changed launch4j.xml file versions"
//println separator

/**********************************************************************
 * Run launch4j to make new executables
 **********************************************************************/



/**********************************************************************
 * Make a commit
 **********************************************************************/
def commit = "git commit -a -m Version $newVersion".execute()
commit.waitFor()
println "Commit made"

/**********************************************************************
 * Push new branch to origin
 **********************************************************************/
def push = "git push origin $newBranch".execute()
push.waitFor()
print "\n"
println separator
println "[Success]     Your branch was committed and pushed to the origin."
println separator
print "\n"
