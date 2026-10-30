FROM itzg/minecraft-server:stable-java25-alpine

# GNU grep is required for proper pattern matching support
RUN apk add --no-cache grep

COPY --chmod=755 custom-scripts/setup-otel-agent.sh /image/scripts/setup-otel-agent.sh
COPY --chmod=755 custom-scripts/setup-otel-agent-extras-fabric.sh /image/scripts/setup-otel-agent-extras-fabric.sh
RUN sed -i -e 's/start-setupRbac/setup-otel-agent.sh/g' /image/scripts/start-setupEnvVariables
