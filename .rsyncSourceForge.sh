#!/bin/sh
set -eu
src=$1
dest=$2
shift 2
rsync -avizPn $* $src eldurloki@frs.sourceforge.net:$dest

