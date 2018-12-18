#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

curl --user ${CIRCLE_TOKEN}: \
    --request POST \
    --form revision=$1 \
    --form config=@${DIR}/config.yml \
    --form notify=false \
        https://circleci.com/api/v1.1/project/github/smartthingsoss/smartthings-scala-toolkit/tree/master
