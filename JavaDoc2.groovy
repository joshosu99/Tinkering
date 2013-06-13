def mavenCommand

if (System.properties['os.name'].toLowerCase().contains('windows')) {
	mavenCommand = "mvn.bat -f \"my-app/pom.xml\" javadoc:javadoc".execute()
	//javaDocCommand.waitForProcessOutput()
	
} else {
	mavenCommand = "mvn -f \"my-app/pom.xml\" javadoc:javadoc".execute()
}

mavenCommand.in.eachLine { line -> println line}
mavenCommand.waitFor()

def filesToMove = [
	new File ("my-app/target/site/apidocs/allclasses-frame.html"),
	new File ("my-app/target/site/apidocs/java"),
	new File ("my-app/target/site/apidocs/resources"),
	new File ("my-app/target/site/apidocs/index.html")
]

File dirTemp = new File ("C:/users/jtrick/desktop/name") 

def filesMoved

filesToMove.each { file ->
	File newFile = new File (dirTemp, file.getName())
	file.renameTo(newFile)
	filesMoved.add(newFile)
}

def changeBranch = "git checkout gh-pages".execute()
changeBranch.in.eachLine { line -> println line}
changeBranch.waitFor()

def deleteDir = "rm my-app".execute()
deletDir.in.eachLine { line -> println line}
deletDir.waitFor()

File newDir = new File("my-app").mkdir()

filesMoved.each {file ->
	file.renameTo(new File (newDir, file.getName()))
}

def commit = "git commit -am".execute()
commit.in.eachLine {line -> println line}
commit.waitFor()

def push = "git push origin gh-pages".execute()
push.in.eachLine {line -> println line}
push.waitFor()

