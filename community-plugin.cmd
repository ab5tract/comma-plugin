:<<"::CMDLITERAL"
@ECHO OFF
GOTO :CMDSCRIPT
::CMDLITERAL

set -eux
exec "$(cd "$(dirname "$0")"; pwd)/../platform/jps-bootstrap/jps-bootstrap.sh" "$@" edument.perl6.comma.build CommaCommunityPluginBuildTarget
:CMDSCRIPT

call "%~dp0\..\platform\jps-bootstrap\jps-bootstrap.cmd" %* edument.perl6.comma.build CommaCommunityPluginBuildTarget
EXIT /B %ERRORLEVEL%