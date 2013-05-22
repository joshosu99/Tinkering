def proc = "git diff master".execute()
proc.waitFor()
def result = proc.in.text
println result.class
//println "This got ${result}"
if (result.contains("/calc.exe differ")) {
	println "Got it"
} else {
	println "AWWWWWWWWWWWWWWWWWWWWWW"
}