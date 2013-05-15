#!/bin/bash
#
# Nick Joodi
# Spida Software
#
# This script will automatically format your java and groovy files. Then afterwords, the 
# script will stage all your modified files.

# Reformat all java and groovy files:
filenames=$(git diff --name-only)
for i in $filenames
do
    # Make sure the file extention is of type java
    if  [ "${i##*.}" = "java" -a -f $i ]; then
   		echo ""
    	echo "     reformatting ${i}...    "
    	echo ""
    	noticeJava="1"
    	# The command to reformat the file. Make sure the reformat folder is 
    	# located in the main folder of the repository. This hook should be 
    	# located in the hook directory.
		java -Dosgi.requiredJavaVersion=1.5 -client -Xms40m -Xmx256m -jar format/eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20120522-1813.jar -os win32 -ws gtk -arch x86 -launcher format/eclipse/eclipse.exe -name Eclipse --launcher.library format/org.eclipse.equinox.launcher.win32.win32.x86_64_1.1.200.v20120913-144807 -startup format/eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20120522-1813.jar -exitdata 84001b -application org.eclipse.jdt.core.JavaCodeFormatter -verbose -config format/org.eclipse.jdt.core.prefs $i -vm format/jdk1.7exe -vmargs -Dosgi.requiredJavaVersion=1.5 -client -Xms40m -Xmx256m -jar format/eclipse/plugins/org.eclipse.equinox.launcher_1.3.0.v20120522-1813.jar.0_21/bin/java.
    fi
    
    if  [ "${i##*.}" = "groovy" -a -f $i ]; then
    	echo ""
   		echo "     reformatting ${i}...    "
		echo ""
    	noticeGroovy="1"
    	export JAVA_OPTS=-Dantlr.ast=groovy
    	$(groovyc $i)
    	p="$i.pretty.groovy"
    	mv  $p $i
    	echo "Done."	    
    fi   
done



#Remove all modified class files resulting from the groovy formatting:
Modified=$(git diff --name-only)
for i in $Modified
do
    if [ "${i##*.}" = "class" -a -f $i ]; then
    	git checkout $i
    fi
done

#Remove all untracked class files resulting from the groovy formatting:
untracked=$(git ls-files --other --exclude-standard)
for i in $untracked
do
    if [ "${i##*.}" = "class" -a -f $i ]; then
    	git clean -f $i
    fi
done

#Stage all modified files for committing:
AllChanged=$(git diff --name-only)
for i in $AllChanged
do
if  [ -f $i ]; then
echo $i
    git add  $i

fi	    
done

echo ""
echo "*******************************************************"
echo "*        Your files were staged for commit...         *"        
echo "*******************************************************"
echo ""




