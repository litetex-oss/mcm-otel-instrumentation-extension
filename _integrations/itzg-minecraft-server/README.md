# Integration with [``itzg/minecraft-server``](https://github.com/itzg/docker-minecraft-server)

## Setup

1. Ensure that [``monitoring dev infra``](../../_monitoring_dev_infra/) is started
2. Copy [``secrets.env.template``](./server/secrets.env.template) to ``secrets.env`` and fill it out

## Start & Stop

Start it with ``docker compose up`` (add ``--build`` if you changed the scripts)<br/>
and stop it with ``docker compose down``.
