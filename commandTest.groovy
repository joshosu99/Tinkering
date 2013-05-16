def proc = "git branch -r".execute()
proc.waitFor()
def result = proc.in.text
println result.class
println "This got ${result}"
if (result.contains("4.1.0-release")) {
	println "WOOOOOOOOOOOOOOOOOOO"
} else {
	println "AWWWWWWWWWWWWWWWWWWWWWW"
}