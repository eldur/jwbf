#!/bin/sh
set -eu
cd target
upload=./../.rsyncSourceForge.sh

remoteWebPath=/home/project-web/jwbf/htdocs
remoteDocs=$remoteWebPath/doc/
# remoteTests=$remoteWebPath/tests/
$upload apidocs/ $remoteDocs --delete




