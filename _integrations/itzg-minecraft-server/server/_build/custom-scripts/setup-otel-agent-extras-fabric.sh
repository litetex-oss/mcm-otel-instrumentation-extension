#!/bin/bash

set -e

: "${USE_AGENT_OTEL_FABRIC_EXTENSION:=true}"

if [[ "$USE_AGENT_OTEL_FABRIC_EXTENSION" = true ]]; then

    : "${AGENT_DIR:=/data/agents}"
    : "${AGENT_OTEL_FABRIC_EXTENSION_DIR:=${AGENT_DIR}/otel-fabric}"
    : "${AGENT_OTEL_FABRIC_EXTENSION_JAR:=${AGENT_OTEL_FABRIC_EXTENSION_DIR}/otel-fabric-helper-extension.jar}"
    : "${AGENT_OTEL_FABRIC_EXTENSION_SETUP_DISABLE_CACHE:=}"

    echo "[OTEL Agent Fabric Ext] Setting up in $AGENT_OTEL_FABRIC_EXTENSION_DIR"

    mkdir -p "$AGENT_OTEL_FABRIC_EXTENSION_DIR"

    last_checked_file="$AGENT_OTEL_FABRIC_EXTENSION_DIR/last-checked.txt"
    last_checked=$(cat $last_checked_file || echo "0")
    time_since_last_check_in_sec=$(($(date +%s)-$last_checked))

    if (( time_since_last_check_in_sec > 600 )) || [[ "$AGENT_OTEL_FABRIC_EXTENSION_SETUP_DISABLE_CACHE" == "1" ]] || [ ! -f $AGENT_OTEL_FABRIC_EXTENSION_JAR ]; then
        # Download via Maven Central because GitHub cannot into IPv6
        remote_version=$(curl -sL https://repo.maven.apache.org/maven2/net/litetex/otel-fabric-helper-extension/maven-metadata.xml | grep -oP "(?<=<latest>)[^<]+" || echo "")
        local_version_file="$AGENT_OTEL_FABRIC_EXTENSION_DIR/current-version.txt"
        local_version=$(cat $local_version_file || echo "-")

        echo "[OTEL Agent Fabric Ext] Version check - Remote: $remote_version Local: $local_version"

        if [[ "$remote_version" == "" ]]; then
            echo "[OTEL Agent Fabric Ext] Failed to resolve remote extension version - Using cached extension if present"
        elif [[ "$remote_version" != "$local_version" ]] || [ ! -f $AGENT_OTEL_FABRIC_EXTENSION_JAR ]; then
            rm -f $AGENT_OTEL_FABRIC_EXTENSION_JAR || true

            download_url="https://repo.maven.apache.org/maven2/net/litetex/otel-fabric-helper-extension/${remote_version}/otel-fabric-helper-extension-${remote_version}.jar"
            echo "[OTEL Agent Fabric Ext] Downloading new extension from $download_url"
            curl -fsL -o $AGENT_OTEL_FABRIC_EXTENSION_JAR $download_url
            
            echo "[OTEL Agent Fabric Ext] Downloaded new extension to $AGENT_OTEL_FABRIC_EXTENSION_JAR"
            echo "$remote_version" > $local_version_file
        fi
        echo $(date +%s) > $last_checked_file
    else
        echo "[OTEL Agent Fabric Ext] Executed version check recently - Skipping"
    fi

    if [ -f $AGENT_OTEL_FABRIC_EXTENSION_JAR ]; then
        jvmOptToAdd="-Dotel.javaagent.extensions=$AGENT_OTEL_FABRIC_EXTENSION_JAR"
        JVM_OPTS="$jvmOptToAdd ${JVM_OPTS}"
        echo "[OTEL Agent Fabric Ext] Modified JVM_OPTS to contain $jvmOptToAdd"
    else
        echo "[OTEL Agent Fabric Ext] Extension $AGENT_OTEL_FABRIC_EXTENSION_JAR missing - Starting without it..."
    fi
fi

exec "$(dirname "$0")/start-setupRbac" "$@"
