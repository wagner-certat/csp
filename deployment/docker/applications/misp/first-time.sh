#!/usr/bin/env bash

docker volume create MISPConfigVolume
docker volume create MISPGnuVolume
docker volume create MISPAdapterDatavolume
docker volume create MYSQLDatavolume
docker volume create MISPSharedDatavolume
docker volume create MISPStateDatavolume


docker volume  ls --format '{{.Name}}' | grep MISPDatavolume  > /dev/null
if [[ "$?" -eq 0 ]] ; then
    echo ">>  Found Docker volume MISPDatavolume"
    docker run -d --rm -v MISPDatavolume:/old -v MISPConfigVolume:/var/www/MISP/app/Config thanosa75/alpine-jdk8:slim sh -c "cp /old/app/Config/* /var/www/MISP/app/Config/"
    docker run -d --rm -v MISPDatavolume:/old -v MISPGnuVolume:/var/www/MISP/.gnupg thanosa75/alpine-jdk8:slim sh -c "cp /old/.gnupg/* /var/www/MISP/.gnupg/"
fi

docker ps -a --format '{{.Names}}' | grep csp-misp  > /dev/null
if [[ "$?" -eq 0 ]] ; then
    echo ">> Found old MISP Installation"
    docker rm csp-misp
fi

echo ">> sleeping for 5"
sleep 5

echo ">> sleeping for 5 more"
sleep 5

docker volume  ls --format '{{.Name}}' | grep MISPDatavolume  > /dev/null
if [[ "$?" -eq 0 ]] ; then
    echo ">>  Found old Docker volume MISPDatavolume"
    docker volume rm MISPDatavolume
fi

mkdir -p /opt/csp/logs_misp/
chown -R 33.33 /opt/csp/logs_misp/
