:<<"::CMDLITERAL"
@ECHO OFF
GOTO :CMDSCRIPT
::CMDLITERAL

set -eux
root="$(cd "$(dirname "$0")/.."; pwd)"
exec "$root/platform/jps-bootstrap/jps-bootstrap.sh" "$@" "$root" intellij.idea.community.build OpenSourceCommunityInstallersBuildTarget
:CMDSCRIPT

call "%~dp0\..\platform\jps-bootstrap\jps-bootstrap.cmd" %* edument.perl6.comma.build CommaCommunityInstallersBuildTarget
EXIT /B %ERRORLEVEL%
