#!/bin/sh
set -eu
mvn clean package assembly:single -DskipTests
upload=./../.rsyncSourceForge.sh
cd target
incl=include-rsync.tmp
readme=README.md
cat > $readme << EOF
# README

- - -
* **Files in this folder are NOT necessary the latest snapshot**
* check also <http://jwbf.sf.net> for snapshots and releases
- - -
The files in this folder contains all libraries for useing and developing jwbf.
All runtime and test related dependecies are included.

EOF
rm -f $incl
echo $readme >> $incl
echo "jwbf*SNAPSHOT*.tar.gz" >> $incl
$upload . /home/frs/project/jwbf/snapshots/ --include-from=$incl --exclude=* --delete-after

