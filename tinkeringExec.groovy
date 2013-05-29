void exec (processString) {
 println processString
 //println new File(".")
 def process = processString.execute(null, new File("."))
 process.in.eachLine { line -> println line }
 //process.consumeProcessOutput(sout, serr)
 process.waitFor()
 //println '' + serr
 //println '' + sout // => test text
}

exec ("git status")