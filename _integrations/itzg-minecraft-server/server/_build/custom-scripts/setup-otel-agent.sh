#!/bin/bash

set -e

: "${USE_AGENT_OTEL:=true}"

if [[ "$USE_AGENT_OTEL" = true ]]; then

    : "${AGENT_DIR:=/data/agents}"
    : "${AGENT_OTEL_DIR:=${AGENT_DIR}/otel}"
    : "${AGENT_OTEL_JAR:=${AGENT_OTEL_DIR}/agent.jar}"
    : "${AGENT_OTEL_SETUP_DISABLE_CACHE:=}"

    echo "[OTEL Agent] Setting up in $AGENT_OTEL_DIR"

    mkdir -p "$AGENT_OTEL_DIR"

    last_checked_file="$AGENT_OTEL_DIR/last-checked.txt"
    last_checked=$(cat $last_checked_file || echo "0")
    time_since_last_check_in_sec=$(($(date +%s)-$last_checked))

    if (( time_since_last_check_in_sec > 600 )) || [[ "$AGENT_OTEL_SETUP_DISABLE_CACHE" == "1" ]] || [ ! -f $AGENT_OTEL_JAR ]; then
        # Download via Maven Central because GitHub cannot into IPv6
        remote_version=$(curl -sL https://repo.maven.apache.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/maven-metadata.xml | grep -oP "(?<=<latest>)[^<]+" || echo "")
        local_version_file="$AGENT_OTEL_DIR/current-version.txt"
        local_version=$(cat $local_version_file || echo "-")

        echo "[OTEL Agent] Version check - Remote: $remote_version Local: $local_version"

        if [[ "$remote_version" == "" ]]; then
            echo "[OTEL Agent] Failed to resolve remote agent version - Using cached agent if present"
        elif [[ "$remote_version" != "$local_version" ]] || [ ! -f $AGENT_OTEL_JAR ]; then
            rm -f $AGENT_OTEL_JAR || true

            download_url="https://repo.maven.apache.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/${remote_version}/opentelemetry-javaagent-${remote_version}.jar"
            echo "[OTEL Agent] Downloading new agent from $download_url"
            curl -fsL -o $AGENT_OTEL_JAR $download_url
            
            echo "[OTEL Agent] Downloaded new agent to $AGENT_OTEL_JAR"
            echo "$remote_version" > $local_version_file
        fi
        echo $(date +%s) > $last_checked_file
    else
        echo "[OTEL Agent] Executed version check recently - Skipping"
    fi

    if [ -f $AGENT_OTEL_JAR ]; then
        jvmOptToAdd="-javaagent:$AGENT_OTEL_JAR"
        JVM_OPTS="$jvmOptToAdd ${JVM_OPTS}"
        echo "[OTEL Agent] Modified JVM_OPTS to contain $jvmOptToAdd"

        echo "[OTEL Agent] Checking if extras need to be applied"
        case "${TYPE^^}" in
            *FABRIC)
                exec "${SCRIPTS:-/}setup-otel-agent-extras-fabric.sh" "$@"
            ;;

            *)
                echo "Nothing needs to be applied"
                exec "${SCRIPTS:-/}start-setupRbac" "$@"
        ;;

        esac
    else
        echo "[OTEL Agent] Agent $AGENT_OTEL_JAR missing - Starting without it..."
        exec "${SCRIPTS:-/}start-setupRbac" "$@"
    fi
else
    exec "${SCRIPTS:-/}start-setupRbac" "$@"
fi
