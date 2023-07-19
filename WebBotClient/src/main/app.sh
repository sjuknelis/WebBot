#!/usr/bin/env bash

cd $(dirname "$0") || exit 1

source select_option.sh

echo -e "\033[2J\033[H\033[0;103m\033[1;30m WEBBOT for FTC \033[0m"
echo -e "Select an Op Mode to run or open the WebBot REPL (arrow keys + enter):"
echo

options=()
for f in $(ls -1 java/com/webbot/teamcode | grep ".java"); do options+=("${f%.*}"); done
options+=("REPL")

# Adapted from below-described work to use relevant options
# Title: Answer to Unix & Linux Stack Exchange Question "Arrow key/Enter menu"
# Author: Unix & Linux Stack Exchange User Alexander Klimetschek
# Source URL: https://unix.stackexchange.com/a/415155
# Licensed under CC-BY-SA 3.0 (https://creativecommons.org/licenses/by-sa/3.0/)

select_option "${options[@]}"
choice=$?

startup_file=$(mktemp)
cat << EOF > "$startup_file"
import com.webbot.client.*
import com.webbot.client.hardware.*
import com.webbot.client.util.*
import com.webbot.teamcode.*
EOF

if [ "${options[$choice]}" != "REPL" ]; then
  cat << EOF >> "$startup_file"
if ( RobotClient.getInstance().confirmConnection() ) {
  LinearOpMode opMode = new ${options[$choice]}();
  opMode.runOpMode();
}
/exit
EOF
else
  cat << EOF >> "$startup_file"
HardwareMap hardwareMap;
Gamepad gamepad1;
if ( RobotClient.getInstance().confirmConnection() ) {
  hardwareMap = HardwareMap.getInstance();
  gamepad1 = Gamepad.getInstance();
}
EOF
  echo -e "\033[2J\033[H\033[0;103m\033[1;30mWebBot: You can reference the following variables: hardwareMap, gamepad1\nWebBot: Use /exit to exit\033[0m"
fi

jshell --class-path "$(dirname $0)/../../build/libs/WebBotClient.jar:$(dirname $0)/../../libs/jna-5.13.0.jar:$(dirname $0)/../../libs/purejavahidapi.jar" --startup "$startup_file"

rm "$startup_file"